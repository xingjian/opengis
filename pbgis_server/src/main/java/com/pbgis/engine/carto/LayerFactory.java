package com.pbgis.engine.carto;

import java.io.File;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.FeatureSourceMapLayer;
import org.geotools.map.MapLayer;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.pbgis.engine.datasource.DataStoreFactory;
import com.pbgis.engine.datasource.GridCoverage2DReaderFactory;
import com.pbgis.engine.util.common.PathUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * MapLayerFactory helps for create MapLayer by LayerDesc
 * 
 * @author wuyf
 * 
 */
public class LayerFactory {

	private String realMapDescDirectoryPath = null;

	public LayerFactory() {
		// realMapDescDirectoryPath=null, relative file path can not be used
	}

	public LayerFactory(String realMapDescDirectoryPath) {
		this.realMapDescDirectoryPath = realMapDescDirectoryPath;
	}

	/**
	 * Create Layer by LayerDesc.
	 * 
	 * @param layerInfo
	 * @return
	 * @throws Exception
	 */
	public Layer createLayer(LayerInfo layerInfo) throws Exception {
		Layer layer = null;

		if (layerInfo != null) {
			String dsType = layerInfo.getDataSourceType().toLowerCase();
			String dsSource = layerInfo.getDataSource();
			String dsCharset = layerInfo.getCharset();
			String styleSource = layerInfo.getStyle();
			if ("file".equals(dsType)) {
				// Check absolute file path
				String fakeFilePath = dsSource;
				File file = new File(PathUtil.fakePathToReal(fakeFilePath));
				if (!file.exists() && realMapDescDirectoryPath != null) {
					// Check relative file path
					if (!realMapDescDirectoryPath.endsWith(File.separator)) {
						realMapDescDirectoryPath += File.separator;
					}
					fakeFilePath = realMapDescDirectoryPath + dsSource;
					file = new File(PathUtil.fakePathToReal(fakeFilePath));
				}
				layer = createLayerByFile(file, dsCharset, styleSource);
			} else if ("wfs".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = createLayerByWFS(sources[0], sources[1], styleSource);
			} else if ("postgis".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = createLayerByPostGIS(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], sources[6], styleSource);
			} else if ("mysql".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = createLayerByMySQL(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			} else if ("arcsde".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = createLayerByArcSDE(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			} else if ("oracle".equals(dsType)) {
				String[] sources = dsSource.split(",");
				layer = createLayerByOracle(sources[0], Integer
						.parseInt(sources[1]), sources[2], sources[3],
						sources[4], sources[5], styleSource);
			}

			if (layer != null) {
				layer.getMapLayer().setTitle(layerInfo.getName());
				layer.getMapLayer().setSelected(layerInfo.isVisible());
				layer.setEditable(layerInfo.isEditable());
			}
		}

		return layer;
	}

	private Layer createLayerByFile(File file, String charset,
			String styleSource) throws Exception {
		Layer layer = null;
		String lowFileName = file.getName().toLowerCase();

		if (lowFileName.endsWith(".shp") || lowFileName.endsWith(".gml")
				|| lowFileName.endsWith(".xml")) {
			// Vector
			DataStore ds = DataStoreFactory.getDataStoreFromFile(file, charset);
			String typeNames[] = ds.getTypeNames();
			String typeName = typeNames[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
					.getFeatureSource(typeName);
			Class<?> geometryType = featureSource.getSchema()
					.getGeometryDescriptor().getType().getBinding();
			Style style = getFeatureLayerStyle(styleSource, geometryType);
			MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
			layer = new FeatureLayer();
			layer.setMapLayer(mapLayer);
		} else if (lowFileName.endsWith(".jpg")
				|| lowFileName.endsWith(".jpeg")
				|| lowFileName.endsWith(".png") || lowFileName.endsWith(".gif")
				|| lowFileName.endsWith(".tif")
				|| lowFileName.endsWith(".tiff")) {
			// Raster
			AbstractGridCoverage2DReader reader = GridCoverage2DReaderFactory
					.getGridCoverage2DReaderFromFile(file);
			Style style = getRasterLayerStyle(styleSource, reader);
			MapLayer mapLayer = new DefaultMapLayer(reader, style);
			layer = new RasterLayer();
			layer.setMapLayer(mapLayer);
			layer.setEditable(false);
		}

		return layer;
	}

	private Layer createLayerByWFS(String url, String typeName,
			String styleSource) throws Exception {
		DataStore ds = DataStoreFactory.getDataStoreFromWFS(url);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
		Layer layer = new FeatureLayer();
		layer.setMapLayer(mapLayer);
		return layer;
	}

	private Layer createLayerByPostGIS(String host, int port, String user,
			String passwd, String database, String schema, String typeName,
			String styleSource) throws Exception {
		DataStore ds = DataStoreFactory.getDataStoreFromPostGIS(host, port,
				user, passwd, database, schema);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
		Layer layer = new FeatureLayer();
		layer.setMapLayer(mapLayer);
		return layer;
	}

	private Layer createLayerByMySQL(String host, int port, String user,
			String passwd, String database, String typeName, String styleSource)
			throws Exception {
		DataStore ds = DataStoreFactory.getDataStoreFromMySQL(host, port, user,
				passwd, database);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
		Layer layer = new FeatureLayer();
		layer.setMapLayer(mapLayer);
		return layer;
	}

	private Layer createLayerByArcSDE(String server, int port, String instance,
			String user, String passwd, String typeName, String styleSource)
			throws Exception {
		DataStore ds = DataStoreFactory.getDataStoreFromArcSDE(server, port,
				instance, user, passwd);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
		Layer layer = new FeatureLayer();
		layer.setMapLayer(mapLayer);
		return layer;
	}

	private Layer createLayerByOracle(String host, int port, String user,
			String passwd, String instance, String typeName, String styleSource)
			throws Exception {
		DataStore ds = DataStoreFactory.getDataStoreFromOracle(host, port,
				user, passwd, instance);
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
				.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema()
				.getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		MapLayer mapLayer = new FeatureSourceMapLayer(featureSource, style);
		Layer layer = new FeatureLayer();
		layer.setMapLayer(mapLayer);
		return layer;
	}

	private Style createStyleFromStyleSource(String styleSource)
			throws Exception {
		Style style = null;

		// Check absolute file path
		String fakeFilePath = styleSource;
		String filePath = PathUtil.fakePathToReal(fakeFilePath);
		if (filePath != null && !filePath.trim().equals("")) {
			File file = new File(filePath);
			if (!file.exists() && realMapDescDirectoryPath != null) {
				// Check relative file path
				if (realMapDescDirectoryPath.endsWith(File.separator)) {
					realMapDescDirectoryPath += File.separator;
				}
				fakeFilePath = realMapDescDirectoryPath + styleSource;
				file = new File(PathUtil.fakePathToReal(fakeFilePath));
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

	private Style getFeatureLayerStyle(String styleSource, Class<?> geometryType)
			throws Exception {
		Style style = createStyleFromStyleSource(styleSource);

		String low = "";
		if (styleSource != null && !"".equals(styleSource)) {
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

	private Style getRasterLayerStyle(String styleSource,
			AbstractGridCoverage2DReader reader) throws Exception {
		Style style = createStyleFromStyleSource(styleSource);

		// Use internal default styles
		if (style == null) {
			style = DefaultStyleManager.getDefaultRasterStyle(reader);
		}

		return style;
	}

}
