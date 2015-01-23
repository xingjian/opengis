package com.gi.giengine.map;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.oracle.OracleDataStoreFactory;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.FeatureSourceMapLayer;
import org.geotools.map.GraphicEnhancedMapContext;
import org.geotools.map.MapLayer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.giengine.geometry.BufferEngine;
import com.gi.giengine.geometry.ProjectEngine;
import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.desc.LayerDesc;
import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.param.FindParam;
import com.gi.giengine.map.param.IdentifyParam;
import com.gi.giengine.map.param.QueryParam;
import com.gi.giengine.map.param.RenderParam;
import com.gi.giengine.map.query.FeatureResult;
import com.gi.giengine.map.query.IdentifyType;
import com.gi.giengine.map.query.SpatialFilterType;
import com.gi.giengine.map.style.DefaultStyleManager;
import com.gi.giengine.map.util.MapUtil;
import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giengine.util.PathUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.PNGImageEncoder;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class MapEngine {
	private MapDesc mapDesc = null;
	private GraphicEnhancedMapContext mapContext = null;

	public MapDesc getMapDesc() {
		return mapDesc;
	}

	public GraphicEnhancedMapContext getMapContext() {
		return mapContext;
	}

	// ----------------------------------------------

	public MapEngine() {
	}

	public MapEngine(MapDesc mapDesc) throws Exception {
		loadMapDesc(mapDesc);
	}

	public void dispose() {
		if (mapContext != null) {
			MapLayer[] mapLayers = mapContext.getLayers();
			if (mapLayers != null) {
				int count = mapLayers.length;
				for (int i = 0; i < count; i++) {
					MapLayer layer = mapLayers[i];
					layer.getFeatureSource().getDataStore().dispose();
				}
			}
		}
	}

	public void newMapDesc(String mapDirPath) throws Exception {
		this.mapContext = null;
		this.mapDesc = new MapDesc();
		this.mapDesc.setMapDescFilePath(mapDirPath);
	}

	public void loadMapDesc(MapDesc mapDesc) throws Exception {
		this.mapContext = null;
		this.mapDesc = mapDesc;

		if (this.mapDesc != null) {
			String wkid = this.mapDesc.getWkid();
			CoordinateReferenceSystem crs = SpatialReferenceEngine.wkidToCRS(
					wkid, true);
			if (crs != null) {
				mapContext = new GraphicEnhancedMapContext(crs);

				int count = this.mapDesc.getLayerDescs().size();
				for (int i = 0; i < count; i++) {
					try {
						LayerDesc layerDesc = this.mapDesc.getLayerDesc(i);
						MapLayer layer = this.initLayer(layerDesc);
						if (layer != null) {
							layer.setSelected(layerDesc.isVisible());
							mapContext.addLayer(0, layer);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				ExtentDesc extentDesc = this.mapDesc.getInitialExtentDesc();
				ReferencedEnvelope env = new ReferencedEnvelope(extentDesc
						.getXmin(), extentDesc.getXmax(), extentDesc.getYmin(),
						extentDesc.getYmax(), crs);
				mapContext.setAreaOfInterest(env);
			}

		}
	}

	private MapLayer initLayer(LayerDesc layerDesc) throws Exception {
		MapLayer layer = null;

		if (layerDesc != null) {
			String dsType = layerDesc.getDataSourceType().toLowerCase();
			String dsSource = layerDesc.getDataSource();
			String styleSource = layerDesc.getStyle();
			if ("file".equals(dsType)) {
				// Check absolute file path
				String fakeFilePath = dsSource;
				File file = new File(PathUtil.fakePath2Real(fakeFilePath));
				if (!file.exists()) {
					// Check relative file path
					fakeFilePath = MapUtil.getMapDir(this.mapDesc
							.getMapDescFilePath())
							+ dsSource;
					file = new File(PathUtil.fakePath2Real(fakeFilePath));
				}
				layer = initLayerByFile(file, styleSource);
			} else if ("wfs".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = initLayerByWFS(sources[0], sources[1], styleSource);
			} else if ("postgis".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = this.initLayerByPostGIS(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], sources[6], styleSource);
			} else if ("mysql".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = this.initLayerByMySQL(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			} else if ("arcsde".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = this.initLayerByArcSDE(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			} else if ("oracle".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = this.initLayerByOracle(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			}
		}

		if (layer != null) {
			String layerName = layerDesc.getName();
			layer.setTitle(layerName);
		}

		return layer;
	}

	private MapLayer initLayerByFile(File file, String styleSource)
			throws Exception {
		String lowFileName = file.getName().toLowerCase();

		if (lowFileName.endsWith(".shp") || lowFileName.endsWith(".gml")
				|| lowFileName.endsWith(".xml")) {
			// Vector
			DataStore ds = DataSourceEngine.getDataStoreFromFile(file);
			String typeNames[] = ds.getTypeNames();
			String typeName = typeNames[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
					.getFeatureSource(typeName);
			Class<?> geometryType = featureSource.getSchema()
					.getGeometryDescriptor().getType().getBinding();
			Style style = initFeatureLayerStyle(styleSource, geometryType);
			return new FeatureSourceMapLayer(featureSource, style);
		} else if (lowFileName.endsWith(".jpg")
				|| lowFileName.endsWith(".jpeg")
				|| lowFileName.endsWith(".png") || lowFileName.endsWith(".gif")
				|| lowFileName.endsWith(".tif")
				|| lowFileName.endsWith(".tiff")) {
			// Raster
			AbstractGridCoverage2DReader reader = DataSourceEngine.getGridCoverage2DReaderFromFile(file);
			Style style = initRasterLayerStyle(styleSource, reader);
			return new DefaultMapLayer(reader, style);
		} else {
			return null;
		}
	}

	private MapLayer initLayerByWFS(String url, String typeName,
			String styleSource) throws Exception {
		DataStore ds = DataSourceEngine.getDataStoreFromWFS(url);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = initFeatureLayerStyle(styleSource, geometryType);
		return new FeatureSourceMapLayer(featureSource, style);
	}

	private MapLayer initLayerByPostGIS(String host, int port, String user,
			String passwd, String database, String schema, String typeName,
			String styleSource) throws Exception {
		DataStore ds = DataSourceEngine.getDataStoreFromPostGIS(host,port, user,
				passwd, database, schema);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = initFeatureLayerStyle(styleSource, geometryType);
		return new FeatureSourceMapLayer(featureSource, style);
	}

	private MapLayer initLayerByMySQL(String host, int port, String user,
			String passwd, String database, String typeName, String styleSource)
			throws Exception {
		DataStore ds = DataSourceEngine.getDataStoreFromMySQL(host, port, user, passwd, database);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = initFeatureLayerStyle(styleSource, geometryType);
		return new FeatureSourceMapLayer(featureSource, style);
	}

	private MapLayer initLayerByArcSDE(String server, int port,
			String instance, String user, String passwd, String typeName,
			String styleSource) throws Exception {
		DataStore ds = DataSourceEngine.getDataStoreFromArcSDE(server, port, instance, user, passwd);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = initFeatureLayerStyle(styleSource, geometryType);
		return new FeatureSourceMapLayer(featureSource, style);
	}

	private MapLayer initLayerByOracle(String host, int port, String user,
			String passwd, String instance, String typeName, String styleSource)
			throws Exception {
		DataStore ds = DataSourceEngine.getDataStoreFromOracle(host, port, user, passwd, instance);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = initFeatureLayerStyle(styleSource, geometryType);
		return new FeatureSourceMapLayer(featureSource, style);
	}

	private Style getStyleFromStyleSource(String styleSource) throws Exception {
		Style style = null;

		// Check absolute file path
		String fakeFilePath = styleSource;
		String filePath = PathUtil.fakePath2Real(fakeFilePath);
		if (filePath != null && !filePath.trim().equals("")) {
			File file = new File(filePath);
			if (!file.exists()) {
				// Check relative file path
				fakeFilePath = MapUtil.getMapDir(this.mapDesc
						.getMapDescFilePath())
						+ styleSource;
				file = new File(PathUtil.fakePath2Real(fakeFilePath));
			}
			if (file.exists() && file.isFile()) {
				StyleFactory styleFactory = new StyleFactoryImpl();
				SLDParser sldParser = new SLDParser(styleFactory, file);
				Style[] styles = sldParser.readXML();
				style = styles[0];
			}
		}

		return style;
	}

	private Style initFeatureLayerStyle(String styleSource,
			Class<?> geometryType) throws Exception {
		Style style = getStyleFromStyleSource(styleSource);

		String low = "";
		if (styleSource != null) {
			low = styleSource.toLowerCase();
		}

		// Use internal default styles
		if (style == null) {
			if ("point".equals(low) || geometryType.equals(Point.class)
					|| geometryType.equals(MultiPoint.class)) {
				style = DefaultStyleManager.getDefaultFeatureStyle("point");
			} else if ("line".equals(low)
					|| geometryType.equals(LineString.class)
					|| geometryType.equals(MultiLineString.class)) {
				style = DefaultStyleManager.getDefaultFeatureStyle("line");
			} else if ("polygon".equals(low)
					|| geometryType.equals(Polygon.class)
					|| geometryType.equals(MultiPolygon.class)) {
				style = DefaultStyleManager.getDefaultFeatureStyle("polygon");
			}
		}

		return style;
	}

	private Style initRasterLayerStyle(String styleSource,
			AbstractGridCoverage2DReader reader) throws Exception {
		Style style = getStyleFromStyleSource(styleSource);

		// Use internal default styles
		if (style == null) {
			style = DefaultStyleManager.getDefaultRasterStyle(reader);
		}

		return style;
	}

	/**************************************************************
	 * Map Operations
	 **************************************************************/

	public MapLayer getLayer(int layerId) {
		MapLayer mapLayer = null;

		int layerCount = mapContext.getLayerCount();
		int index = layerCount - layerId - 1;
		if (index < layerCount) {
			mapLayer = mapContext.getLayer(index);
		}

		return mapLayer;
	}

	public ReferencedEnvelope getFullExtent() throws Exception {
		return mapContext.getLayerBounds();
	}

	public FeatureSource<? extends FeatureType, ? extends Feature> getFeatureLayerSource(
			int layerId) {
		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = null;

		try {
			MapLayer layer = getLayer(layerId);
			featureSource = layer.getFeatureSource();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return featureSource;
	}

	// Render map to image
	public BufferedImage render(RenderParam renderParam) throws Exception {
		BufferedImage image = null;

		// Handle visible layerIds
		int layerCount = mapContext.getLayerCount();
		MapLayer layer = null;
		for (int i = 0; i < layerCount; i++) {
			layer = getLayer(i);
			layer.setVisible(false);
		}
		List<String> visibleLayerIds = renderParam.getVisibleLayerIds();
		HashMap<String, String> layerDefs = renderParam.getLayerDefs();
		if (visibleLayerIds == null) {
			visibleLayerIds = new ArrayList<String>();
			for (int i = 0; i < layerCount; i++) {
				visibleLayerIds.add(String.valueOf(i));
			}
		}
		int visibleLayerCount = visibleLayerIds.size();
		for (int i = 0; i < visibleLayerCount; i++) {
			int layerId = Integer.parseInt(visibleLayerIds.get(i));
			layer = getLayer(layerId);
			layer.setVisible(true);

			// Handle layerDefs
			String key = String.valueOf(layerId);
			if (layerDefs != null && layerDefs.containsKey(key)) {
				String def = layerDefs.get(key);
				Filter filter = ECQL.toFilter(def);
				DefaultQuery query = new DefaultQuery();
				query.setFilter(filter);
				layer.setQuery(query);
			} else {
				layer.setQuery(Query.ALL);
			}
		}

		// Check bbox
		ExtentDesc mapExtentDesc = mapDesc.getInitialExtentDesc();
		ExtentDesc bbox = renderParam.getBbox();
		double xmax = bbox.getXmax();
		double xmin = bbox.getXmin();
		double ymax = bbox.getYmax();
		double ymin = bbox.getYmin();
		if ((xmax - xmin) * (ymax - ymin) == 0) {
			xmin = mapExtentDesc.getXmin();
			xmax = mapExtentDesc.getXmax();
			ymin = mapExtentDesc.getYmin();
			ymax = mapExtentDesc.getYmax();
		}

		// Create BufferedImage
		int w = renderParam.getImageWidth();
		int h = renderParam.getImageHeight();
		if (renderParam.isTransparent()) {
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		} else {
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}

		Graphics2D g = image.createGraphics();

		if (!renderParam.isTransparent()) {
			g.setBackground(mapDesc.getBackgroundColor());
			g.clearRect(0, 0, w, h);
		}

		Rectangle paintArea = new Rectangle();
		paintArea.setSize(w, h);

		CoordinateReferenceSystem bboxCRS = SpatialReferenceEngine.wkidToCRS(
				renderParam.getBboxSR(), true);
		ReferencedEnvelope env = new ReferencedEnvelope(xmin, xmax, ymin, ymax,
				bboxCRS);
		env = ProjectEngine.projectReferencedEnvelope(env, renderParam
				.getImageSR());
		ReferencedEnvelope mapArea = MapUtil.adjustEnvelopeToSize(env, w, h);

		final StreamingRenderer renderer = new StreamingRenderer();

		HashMap<Key, Object> hintsMap = new HashMap<Key, Object>();
		// Handle antiAlias
		if (renderParam.isAntiAlias()) {
			hintsMap.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		hintsMap.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hintsMap.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		RenderingHints hints = new RenderingHints(hintsMap);

		renderer.setJava2DHints(hints);
		renderer.setContext(mapContext);

		renderer.addRenderListener(new RenderListener() {
			public void featureRenderer(SimpleFeature feature) {
			}

			public void errorOccurred(final Exception ex) {
				renderer.stopRendering();
			}
		});

		try {
			renderer.paint(g, paintArea, mapArea);
		} finally {
			g.dispose();
		}

		return image;
	}

	public byte[] export(RenderParam renderParam, String format, int dpi)
			throws Exception {
		byte[] result = null;

		// Get low case format
		String lowFormat = format.toLowerCase();

		// Check if export format support transparent
		boolean supportTransparent = false;
		if ("png".equals(lowFormat)) {
			supportTransparent = true;
		}
		RenderParam fixedRenderParam = renderParam.clone();
		fixedRenderParam.setTransparent(renderParam.isTransparent()
				&& supportTransparent);

		// Create BufferedImage
		BufferedImage image = this.render(fixedRenderParam);

		// Output as byte[]
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if ("png".equals(lowFormat)) {
				PNGEncodeParam pngParam = PNGEncodeParam
						.getDefaultEncodeParam(image);
				pngParam.setPhysicalDimension(dpi * 3937, dpi * 3937, 100);
				PNGImageEncoder pngEncoder = new PNGImageEncoder(outputStream,
						pngParam);
				pngEncoder.encode(image);
			} else if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
				JPEGEncodeParam jpgParam = JPEGCodec
						.getDefaultJPEGEncodeParam(image);
				jpgParam.setXDensity(dpi);
				jpgParam.setYDensity(dpi);
				jpgParam.setDensityUnit(JPEGDecodeParam.DENSITY_UNIT_DOTS_INCH);
				JPEGImageEncoder jpgEncoder = JPEGCodec.createJPEGEncoder(
						outputStream, jpgParam);
				jpgEncoder.encode(image);
			} else {
				ImageIO.write(image, format, outputStream);
			}

			result = outputStream.toByteArray();
		} finally {
			outputStream.close();
		}

		return result;
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			int layerId, QueryParam queryParam) throws Exception {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		FeatureSource<? extends FeatureType, ? extends Feature> featureSource = getFeatureLayerSource(layerId);

		if (featureSource != null) {
			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools
					.getDefaultHints());

			Filter geometryFilter = null;
			Geometry geometry = queryParam.getGeometry();
			String inSR = queryParam.getInSR();
			SpatialFilterType spatialFilterType = queryParam
					.getSpatialFilterType();
			if (geometry != null) {
				String mapSR = mapDesc.getWkid();
				if (!inSR.equals(mapSR)) {
					geometry = ProjectEngine.project(geometry, inSR, mapSR);
				}

				String geomtryField = featureSource.getSchema()
						.getGeometryDescriptor().getLocalName();
				PropertyName geometryPropertyName = ff.property(geomtryField);
				if (spatialFilterType.equals(SpatialFilterType.INTERSECTS)) {
					geometryFilter = ff.intersects(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.CONTAINS)) {
					geometryFilter = ff.contains(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.CROSSES)) {
					geometryFilter = ff.crosses(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType
						.equals(SpatialFilterType.ENVELOPE_INTERSECTS)) {
					// TODO
					geometryFilter = ff.intersects(ff.literal(geometry
							.getEnvelope()), geometryPropertyName);
				} else if (spatialFilterType
						.equals(SpatialFilterType.INDEX_INTERSECTS)) {
					// TODO
					geometryFilter = ff.intersects(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.OVERLAPS)) {
					geometryFilter = ff.overlaps(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.TOUCHES)) {
					geometryFilter = ff.touches(ff.literal(geometry),
							geometryPropertyName);
				} else if (spatialFilterType.equals(SpatialFilterType.WITHIN)) {
					geometryFilter = ff.within(ff.literal(geometry),
							geometryPropertyName);
				}
			}

			Filter attributesFilter = null;
			String where = queryParam.getWhere();
			if (where != null && !"".equals(where.trim())
					&& !"1=1".equals(where.trim())) {
				try {
					attributesFilter = ECQL.toFilter(where);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (geometryFilter != null && attributesFilter == null) {
				result = featureSource.getFeatures(geometryFilter);
			} else if (geometryFilter == null && attributesFilter != null) {
				result = featureSource.getFeatures(attributesFilter);
			} else if (geometryFilter == null && attributesFilter == null) {
				// I like 1=1 :)
				if ("1=1".equals(where)) {
					result = featureSource.getFeatures();
				} else {
					result = null;
				}
			} else {
				result = featureSource.getFeatures(geometryFilter)
						.subCollection(attributesFilter);
			}
		}

		return result;
	}

	public ArrayList<FeatureResult> identify(ArrayList<String> layerIds,
			IdentifyParam identifyParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		String mapSR = mapDesc.getWkid();

		// Handle geometry
		Geometry geometry = identifyParam.getGeometry();
		int tolerance = identifyParam.getTolerance();
		double resolution = identifyParam.getResolution();
		if (tolerance > 0) {
			double distance = tolerance * resolution;
			geometry = BufferEngine.buffer(geometry, mapSR, mapSR, mapSR,
					distance);
		}

		IdentifyType identifyType = identifyParam.getIdentifyType();

		// Handle layers
		ArrayList<String> handleLayerIds = null;
		if (identifyType.equals(IdentifyType.TOP)) {
			handleLayerIds = layerIds;
		} else if (identifyType.equals(IdentifyType.VISIBLE)) {
			handleLayerIds = new ArrayList<String>();
			int layerCount = mapContext.getLayerCount();
			for (int i = 0; i < layerCount; i++) {
				MapLayer layer = getLayer(i);
				if (layer.isVisible()) {
					handleLayerIds.add(String.valueOf(i));
				}
			}
		} else if (identifyType.equals(IdentifyType.ALL)) {
			handleLayerIds = new ArrayList<String>();
			int layerCount = mapContext.getLayerCount();
			for (int i = 0; i < layerCount; i++) {
				handleLayerIds.add(String.valueOf(i));
			}
		}

		int layerCount = handleLayerIds.size();
		for (int i = 0; i < layerCount; i++) {
			int layerId = Integer.parseInt(handleLayerIds.get(i));
			String layerName = getLayer(i).getTitle();

			try {
				QueryParam queryParam = new QueryParam();
				queryParam.setGeometry(geometry);
				queryParam.setInSR(mapSR);
				queryParam.setSpatialFilterType(SpatialFilterType.INTERSECTS);
				FeatureCollection<? extends FeatureType, ? extends Feature> layerFeatureCollection = query(
						layerId, queryParam);
				if (layerFeatureCollection != null
						&& !layerFeatureCollection.isEmpty()) {
					FeatureIterator<? extends Feature> featureIterator = layerFeatureCollection
							.features();
					try {
						Feature feature = null;
						FeatureResult featureResult = null;
						while (featureIterator.hasNext()) {
							feature = featureIterator.next();
							featureResult = new FeatureResult();
							featureResult.setLayerId(layerId);
							featureResult.setLayerName(layerName);
							featureResult.setFeature(feature);
							result.add(featureResult);
						}
					} finally {
						featureIterator.close();
					}

					if (identifyType.equals(IdentifyType.TOP)) {
						break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public ArrayList<FeatureResult> find(ArrayList<String> layerIds,
			FindParam findParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		String[] searchFields = findParam.getSearchFields();
		boolean contains = findParam.isContains();
		String searchText = findParam.getSearchText();
		if (searchFields != null) {
			// Generate where
			String where = "";
			int fieldCount = searchFields.length;
			String field = null;
			for (int i = 0; i < fieldCount; i++) {
				field = searchFields[i];
				if (!"".equals(where)) {
					where += " OR ";
				}
				if (contains) {
					where += field + " like '%" + searchText + "%'";
				} else {
					where += field + "='" + searchText + "'";
				}
			}

			int layerCount = layerIds.size();
			for (int i = 0; i < layerCount; i++) {
				int layerId = Integer.parseInt(layerIds.get(i));
				String layerName = getLayer(i).getTitle();

				try {
					QueryParam queryParam = new QueryParam();
					queryParam.setWhere(where);
					FeatureCollection<? extends FeatureType, ? extends Feature> layerFeatureCollection = query(
							layerId, queryParam);
					if (layerFeatureCollection != null
							&& !layerFeatureCollection.isEmpty()) {
						FeatureIterator<? extends Feature> featureIterator = layerFeatureCollection
								.features();
						try {
							Feature feature = null;
							FeatureResult featureResult = null;
							while (featureIterator.hasNext()) {
								feature = featureIterator.next();
								featureResult = new FeatureResult();
								featureResult.setLayerId(layerId);
								featureResult.setLayerName(layerName);
								featureResult.setFeature(feature);
								result.add(featureResult);
							}
						} finally {
							featureIterator.close();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean insert(int layerId, FeatureCollection featureCollection) {
		boolean result = false;

		if (mapDesc.getLayerDesc(layerId).isEditable()) {
			if (featureCollection != null) {
				FeatureSource<? extends FeatureType, ? extends Feature> featureSource = getFeatureLayerSource(layerId);
				if (featureSource != null
						&& featureSource instanceof FeatureStore<?, ?>) {
					// String transactionId = "insert|" + index + "|" +
					// layerId + "|" + featureCollection.hashCode();
					// DefaultTransaction transaction = new
					// DefaultTransaction(transactionId);
					FeatureStore<? extends FeatureType, ? extends Feature> featureStore = (FeatureStore<? extends FeatureType, ? extends Feature>) featureSource;
					// featureStore.setTransaction(transaction);
					try {
						featureStore.addFeatures(featureCollection);
						// transaction.commit();
						result = true;
					} catch (Exception ex) {
						// transaction.rollback();
					} finally {
						// transaction.close();
					}
				}
			}
		}

		return result;
	}

	public Feature[] delete(int layerId, String where) throws Exception {
		Feature[] result = null;

		if (mapDesc.getLayerDesc(layerId).isEditable()) {
			// Handle where
			Filter filter = null;
			if (where != null && !"".equals(where.trim())) {
				filter = ECQL.toFilter(where);
			}

			FeatureSource<? extends FeatureType, ? extends Feature> featureSource = getFeatureLayerSource(layerId);
			if (featureSource != null
					&& featureSource instanceof FeatureStore<?, ?>) {
				if (filter != null) {
					// String transactionId = "delete|" + index + "|" +
					// layerId + "|" + where;
					// DefaultTransaction transaction = new
					// DefaultTransaction(transactionId);
					try {
						FeatureStore<? extends FeatureType, ? extends Feature> featureStore = (FeatureStore<? extends FeatureType, ? extends Feature>) featureSource;
						// featureStore.setTransaction(transaction);
						featureStore.removeFeatures(filter);
						// transaction.commit();
						FeatureCollection<? extends FeatureType, ? extends Feature> deleteFeatureCollection = featureSource
								.getFeatures(filter);
						Feature[] toDeleteFeatures = new Feature[deleteFeatureCollection
								.size()];
						deleteFeatureCollection.toArray(toDeleteFeatures);
						result = toDeleteFeatures;
					} catch (Exception ex) {
						result = null;
						// transaction.rollback();
					} finally {
						// transaction.close();
					}
				}
			}
		}

		return result;
	}

	public Feature[] modify(int layerId, String where, Geometry geometry,
			HashMap<String, Object> attributes) throws Exception {
		Feature[] result = null;

		if (mapDesc.getLayerDesc(layerId).isEditable()) {
			// Handle where
			Filter filter = null;
			if (where != null && !"".equals(where.trim())) {
				filter = ECQL.toFilter(where);
			}

			FeatureSource<? extends FeatureType, ? extends Feature> featureSource = getFeatureLayerSource(layerId);
			if (featureSource != null
					&& featureSource instanceof FeatureStore<?, ?>) {
				if (filter != null) {
					// String transactionId = "modify|" + index + "|" +
					// layerId + "|" + where + "|" + geometry.hashCode() +
					// "+" + attributes.hashCode();
					// DefaultTransaction transaction = new
					// DefaultTransaction(transactionId);
					FeatureStore<? extends FeatureType, ? extends Feature> featureStore = (FeatureStore<? extends FeatureType, ? extends Feature>) featureSource;
					// featureStore.setTransaction(transaction);
					try {
						if (geometry != null) {
							featureStore.modifyFeatures(featureSource
									.getSchema().getGeometryDescriptor(),
									geometry, filter);
						}

						if (attributes != null) {
							Set<String> keys = attributes.keySet();
							Iterator<String> itKey = keys.iterator();
							while (itKey.hasNext()) {
								String key = itKey.next();
								Object value = attributes.get(key);
								featureStore
										.modifyFeatures(
												(AttributeDescriptor) featureSource
														.getSchema()
														.getDescriptor(key),
												value, filter);
							}

							// transaction.commit();
							FeatureCollection<? extends FeatureType, ? extends Feature> modifyFeatureCollection = featureSource
									.getFeatures(filter);
							Feature[] modifyFeatures = new Feature[modifyFeatureCollection
									.size()];
							modifyFeatureCollection.toArray(modifyFeatures);
							result = modifyFeatures;
						}
					} catch (Exception ex) {
						result = null;
						// transaction.rollback();
					} finally {
						// transaction.close();
					}
				}
			}
		}
		return result;
	}

}
