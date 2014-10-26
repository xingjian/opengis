package com.pbgis.engine.carto;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GraphicEnhancedMapContext;
import org.geotools.map.MapLayer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.pbgis.engine.geometry.GeometryToolkit;
import com.pbgis.engine.spatialreference.SpatialReferenceManager;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.PNGImageEncoder;
import com.vividsolutions.jts.geom.Envelope;

public class Map {

	private GraphicEnhancedMapContext mapContext = null;
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	private Color backgroundColor = null;

	/**
	 * Computer scale by extent and image size/dpi
	 * 
	 * @param extent
	 * @param imageWidth
	 * @param imageHeight
	 * @param dpi
	 * @return
	 * @throws Exception
	 */
	public double computeScale(ReferencedEnvelope extent, int imageWidth,
			int imageHeight, int dpi) throws Exception {
		double result = Double.NaN;

		/*
		 * ReferencedEnvelope adjustExtent =
		 * MapUtil.adjustEnvelopeToSize(extent, imageWidth, imageHeight);
		 */
		ReferencedEnvelope adjustExtent = extent;
		String inSR = SpatialReferenceManager.crsToWkid(adjustExtent
				.getCoordinateReferenceSystem());
		String outSR = "4326";// WGS 84
		Envelope degreeEnv = GeometryToolkit.project(adjustExtent, inSR, outSR);
		double meterWidth = degreeEnv.getWidth() * 111194.6462427292;// 1
		// degree
		// =
		// 111194.65
		// meters
		// (Calculate
		// from
		// ArcGIS)
		double meterResolution = meterWidth / imageWidth;
		result = meterResolution * dpi / 0.02539999918;// 1 inch = 2.54 cm

		return result;
	}

	public double computeResolution(ReferencedEnvelope env, int imageWidth,
			int imageHeight) throws Exception {
		double result = Double.NaN;

		double widthEnv = env.getMaxX() - env.getMinX();
		double heightEnv = env.getMaxY() - env.getMinY();
		double whEnv = widthEnv / heightEnv;
		double whSize = 1.0 * imageWidth / imageHeight;
		if (whEnv > whSize) {
			result = widthEnv / imageWidth;
		} else {
			result = heightEnv / imageHeight;
		}

		return result;
	}

	public void dispose() {
		if (mapContext != null) {
			MapLayer[] mapLayers = mapContext.getLayers();
			if (mapLayers != null) {
				for (int i = 0, count = mapLayers.length; i < count; i++) {
					MapLayer layer = mapLayers[i];
					try {
						layer.getFeatureSource().getDataStore().dispose();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public byte[] exportImage(RenderParam renderParam, String format, int dpi)
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

		// Render to MapImage first
		MapImage mapImage = render(fixedRenderParam);
		BufferedImage image = mapImage.getImage();

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

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ReferencedEnvelope getFullExtent() throws IOException {
		return mapContext.getLayerBounds();
	}

	public ReferencedEnvelope getExtent() throws IOException {
		return mapContext.getAreaOfInterest();
	}

	public Layer getLayer(int layerId) {
		Layer layer = null;

		int layerCount = layers.size();
		int index = layerCount - layerId - 1;
		if (index < layerCount && index > -1) {
			layer = layers.get(index);
		}

		return layer;
	}

	public GraphicEnhancedMapContext getMapContext() {
		return mapContext;
	}

	/**
	 * Initialize Map by MapDesc
	 * 
	 * @param mapDesc
	 * @return
	 * @throws Exception
	 */
	public boolean initByMapDesc(MapDesc mapDesc, String realMapDescFilePath)
			throws Exception {
		boolean result = false;

		removeAllLayers();
		if (mapDesc != null) {
			String wkid = mapDesc.getWkid();
			CoordinateReferenceSystem crs = null;
			try {
				crs = SpatialReferenceManager.wkidToCRS(wkid, true);
			} catch (Exception e) {
				crs = SpatialReferenceManager.wkidToCRS("4326", true);
			}
			if (crs != null) {
				mapContext = new GraphicEnhancedMapContext(crs);

				File mapDescFile = new File(realMapDescFilePath);
				LayerFactory layerFactory = new LayerFactory(mapDescFile
						.getParentFile().getAbsolutePath());
				for (int i = 0, count = mapDesc.getLayerInfos().size(); i < count; i++) {
					try {
						LayerInfo layerInfo = mapDesc.getLayerInfo(i);
						Layer layer = layerFactory.createLayer(layerInfo);
						if (layer != null) {
							/*
							 * mapContext for geotools function layers for layer
							 * informations They should by synchronized
							 */
							mapContext.addLayer(0, layer.getMapLayer());
							layers.add(0, layer);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				Envelope env = mapDesc.getInitialExtent();
				if (env != null) {
					if (env.getWidth() > 0 && env.getHeight() > 0) {
						ReferencedEnvelope refenv = new ReferencedEnvelope(env,
								crs);
						mapContext.setAreaOfInterest(refenv);
					} else {
						mapContext.setAreaOfInterest(mapContext
								.getLayerBounds());
					}
				}
				backgroundColor = mapDesc.getBackgroundColor();
				if (backgroundColor != null) {
					mapContext.setBgColor(backgroundColor);
				}

				result = true;
			}
		}

		return result;
	}

	public void removeAllLayers() {
		for (Iterator<Layer> itr = layers.iterator(); itr.hasNext();) {
			Layer layer = itr.next();
			try {
				layer.getMapLayer().getFeatureSource().getDataStore().dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		layers.clear();

		if (mapContext != null) {
			mapContext.clearLayerList();
		}
	}

	/**
	 * Render map to image
	 * 
	 * @param renderParam
	 * @return
	 * @throws Exception
	 */
	public MapImage render(RenderParam renderParam) throws Exception {
		MapImage result = new MapImage();

		BufferedImage image = null;

		// Handle visible layerIds
		for (int i = 0, count = layers.size(); i < count; i++) {
			Layer layer = getLayer(i);
			layer.getMapLayer().setVisible(false);
		}
		List<String> visibleLayerIds = renderParam.getVisibleLayerIds();
		HashMap<String, String> layerDefs = renderParam.getLayerDefs();
		if (visibleLayerIds == null) {
			visibleLayerIds = new ArrayList<String>();
			for (int i = 0, count = layers.size(); i < count; i++) {
				visibleLayerIds.add(String.valueOf(i));
			}
		}
		for (int i = 0, count = visibleLayerIds.size(); i < count; i++) {
			int layerId = Integer.parseInt(visibleLayerIds.get(i));
			Layer layer = getLayer(layerId);
			layer.getMapLayer().setVisible(true);

			// Handle layerDefs
			String key = String.valueOf(layerId);
			if (layerDefs != null && layerDefs.containsKey(key)) {
				String def = layerDefs.get(key);
				Filter filter = ECQL.toFilter(def);
				DefaultQuery query = new DefaultQuery();
				query.setFilter(filter);
				layer.getMapLayer().setQuery(query);
			} else {
				layer.getMapLayer().setQuery(Query.ALL);
			}
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
			g.setBackground(backgroundColor);
			g.clearRect(0, 0, w, h);
		}

		Rectangle paintArea = new Rectangle();
		paintArea.setSize(w, h);

		ReferencedEnvelope mapArea = renderParam.getExtent();
		result.setExtent(mapArea);

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
			public void errorOccurred(final Exception ex) {
				renderer.stopRendering();
			}

			public void featureRenderer(SimpleFeature feature) {
			}
		});

		try {
			renderer.paint(g, paintArea, mapArea);
		} finally {
			g.dispose();
		}

		result.setImage(image);

		return result;
	}

}
