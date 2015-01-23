package com.gi.server.core.service.mapservice;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.engine.carto.FeatureLayer;
import com.gi.engine.carto.FeatureResult;
import com.gi.engine.carto.FindParam;
import com.gi.engine.carto.IdentifyParam;
import com.gi.engine.carto.IdentifyType;
import com.gi.engine.carto.Layer;
import com.gi.engine.carto.Map;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.carto.MapImage;
import com.gi.engine.carto.QueryParam;
import com.gi.engine.carto.RenderParam;
import com.gi.engine.carto.SpatialFilterType;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.server.service.TileInfo;
import com.gi.engine.server.service.TileLodInfo;
import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.engine.util.ArchitectureUtil;
import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.i18n.ResourceBundleManager;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.pool.AbstractInstance;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.PNGImageEncoder;
import com.vividsolutions.jts.geom.Geometry;

public class MapServiceInstance extends AbstractInstance {
	static Logger logger = Logger.getLogger(MapServiceInstance.class);

	private String serviceName = null;// Service Name needed by logger
	private Map map = null;

	public MapServiceInstance(MapServicePool pool) {
		super(pool);

		try {
			MapService mapService = (MapService) pool.getParentService();
			serviceName = mapService.getServiceName();

			MapDesc mapDesc = mapService.getMapDesc();
			map = new Map();
			map.initByMapDesc(mapDesc, mapService.getServiceDir()
					+ ArchitectureUtil.MAP_DESC_FILE_NAME);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.CREATE_INSTANCE"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		}
	}

	public void dispose() {
		if (map != null) {
			map.dispose();
		}
	}

	public byte[] export(RenderParam renderParam, String format, int dpi)
			throws Exception {
		byte[] result = null;
		ByteArrayOutputStream outputStream = null;

		try {
			this.startTimeoutMonitor();

			// Get low case format
			String lowFormat = format.toLowerCase();

			// Check if export format support transparent
			boolean supportTransparent = false;
			if (lowFormat.startsWith("png")) {
				supportTransparent = true;
			}
			RenderParam fixedRenderParam = renderParam.clone();
			fixedRenderParam.setTransparent(renderParam.isTransparent()
					&& supportTransparent);

			// Create BufferedImage
			BufferedImage image = this.render(fixedRenderParam);

			// Output as byte[]
			outputStream = new ByteArrayOutputStream();
			if (lowFormat.startsWith("png")) {
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
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.EXPORT"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception ex) {
			}

			this.stopTimeoutMonitor();
		}

		return result;
	}

	public ArrayList<FeatureResult> find(ArrayList<String> layerIds,
			FindParam findParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		try {
			this.startTimeoutMonitor();

			String[] searchFields = findParam.getSearchFields();
			boolean contains = findParam.isContains();
			String searchText = findParam.getSearchText();
			if (searchFields != null) {
				// Generate where
				String where = "";
				String field = null;
				for (int i = 0, count = searchFields.length; i < count; i++) {
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

				for (int i = 0, count = layerIds.size(); i < count; i++) {
					int layerId = Integer.valueOf(layerIds.get(i));
					Layer layer = map.getLayer(i);
					if (layer != null && layer instanceof FeatureLayer) {
						FeatureLayer featureLayer = (FeatureLayer) layer;
						try {
							QueryParam queryParam = new QueryParam();
							queryParam.setWhere(where);
							FeatureCollection<? extends FeatureType, ? extends Feature> layerFeatureCollection = featureLayer
									.query(queryParam);
							if (layerFeatureCollection != null
									&& !layerFeatureCollection.isEmpty()) {
								for (FeatureIterator<? extends Feature> itr = layerFeatureCollection
										.features(); itr.hasNext();) {
									Feature feature = itr.next();
									FeatureResult featureResult = new FeatureResult();
									featureResult.setLayerId(layerId);
									featureResult.setLayer(layer);
									featureResult.setFeature(feature);
									result.add(featureResult);
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.FIND"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		} finally {
			this.stopTimeoutMonitor();
		}

		return result;
	}

	public ReferencedEnvelope getFullExtent() {
		ReferencedEnvelope result = null;

		if (map != null) {
			try {
				this.startTimeoutMonitor();

				result = map.getFullExtent();
			} catch (IOException e) {
				StringBuilder sb = new StringBuilder();
				sb.append(ResourceBundleManager
						.getResourceBundleMapServiceLog().getString(
								"ERROR.GET_FULLEXTENT"));
				sb.append("<");
				sb.append(serviceName);
				sb.append(">");
				logger.error(sb.toString(), e);
			} finally {
				this.stopTimeoutMonitor();
			}
		}

		return result;
	}

	public Map getMap() {
		return map;
	}

	public byte[] getOrCreateTile(int level, int row, int col) {
		byte[] result = null;

		try {
			this.startTimeoutMonitor();

			// Try to get tile
			result = doGetExplodedTile(level, row, col);

			if (result == null) {
				MapService mapService = (MapService) this.getParentPool()
						.getParentService();
				MapTilesCreatingMonitor monitor = mapService
						.getMapTilesCreatingMonitor();

				if (monitor.isMapTileCreating(level, row, col)) {
					while (monitor.isMapTileCreating(level, row, col)) {
						Thread.sleep(500);
					}
				} else {
					// Now we should create tiles which are not exist

					// Get tileInfo
					TileInfo tileInfo = mapService.getMapServiceDesc()
							.getTileInfo();

					// Get tile create spread and span
					int createSpread = tileInfo.getCreateSpread();

					int rowMin = (row / createSpread) * createSpread;
					int colMin = (col / createSpread) * createSpread;

					// Mark these tiles are creating
					for (int i = rowMin; i < rowMin + createSpread; i++) {
						for (int j = colMin; j < colMin + createSpread; j++) {
							monitor.setMapTileCreating(level, i, j);
						}
					}

					String sr = mapService.getMapDesc().getWkid();
					int width = tileInfo.getWidth();
					int height = tileInfo.getHeight();
					double originX = tileInfo.getOriginX();
					double originY = tileInfo.getOriginY();
					String format = tileInfo.getFormat();

					// Get suffix by format
					// Use png for default
					String suffix = "png";
					String lowFormat = format.toLowerCase();
					if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
						suffix = "jpg";
					}

					// Check if export format support transparent
					boolean supportTransparent = false;
					if ("png".equals(lowFormat)) {
						supportTransparent = true;
					}

					// Calculate the spread tile bbox
					TileLodInfo tileLodInfo = tileInfo.getTileLodInfo(level);
					double resolution = tileLodInfo.getResolution();
					double deltaX = width * resolution;
					double deltaY = height * resolution;
					double xmin = originX + deltaX * colMin;
					double ymax = originY - deltaY * rowMin;
					double xmax = xmin + deltaX * createSpread;
					double ymin = ymax - deltaY * createSpread;

					RenderParam renderParam = new RenderParam();
					renderParam.setAntiAlias(mapService.getMapDesc()
							.isAntiAlias());
					CoordinateReferenceSystem crs = SpatialReferenceManager
							.wkidToCRS(sr, true);
					ReferencedEnvelope extent = new ReferencedEnvelope(xmin,
							xmax, ymin, ymax, crs);
					renderParam.setExtent(extent);
					renderParam.setImageHeight(createSpread * height);
					renderParam.setImageWidth(createSpread * width);
					renderParam.setTransparent(supportTransparent);

					// Get spread image
					MapImage mapImage = map.render(renderParam);
					BufferedImage image = mapImage.getImage();

					for (int i = rowMin; i < rowMin + createSpread; i++) {
						for (int j = colMin; j < colMin + createSpread; j++) {
							ImageFilter cropFilter = new CropImageFilter(
									(j - colMin) * width,
									(i - rowMin) * height, width, height);
							Image destImage = Toolkit.getDefaultToolkit()
									.createImage(
											new FilteredImageSource(image
													.getSource(), cropFilter));

							BufferedImage tileImage = new BufferedImage(width,
									height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = tileImage.createGraphics();
							g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);
							g.drawImage(destImage, 0, 0, null);

							// Output as byte[]
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							ImageIO.write(tileImage, format, outputStream);
							byte[] out = outputStream.toByteArray();
							outputStream.close();

							String mapDir = ServiceManager.getMapServicesDir()
									.getAbsolutePath()
									+ File.separator
									+ PathUtil.nameToRealPath(serviceName);
							String tileFilePath = ArchitectureUtil
									.getExplodedTileFilePath(mapDir, level, i,
											j, suffix);
							File tileFile = new File(tileFilePath);
							if (out != null) {
								String tileDir = tileFilePath.substring(0,
										tileFilePath
												.lastIndexOf(File.separator));
								File tileDirFile = new File(tileDir);
								if (!tileDirFile.exists()) {
									tileDirFile.mkdirs();
								}
								if (tileFile.createNewFile()) {
									FileOutputStream outputStreamFile = new FileOutputStream(
											tileFilePath);
									outputStreamFile.write(out);
									outputStreamFile.flush();
									outputStreamFile.close();
								}
							}

							monitor.removeMapTileCreating(level, i, j);
						}
					}
				}

				result = doGetExplodedTile(level, row, col);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_CREATE_TILE"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		} finally {
			this.stopTimeoutMonitor();
		}

		return result;
	}

	public byte[] getTile(int level, int row, int col) {
		byte[] result = null;

		try {
			this.startTimeoutMonitor();

			MapService mapService = (MapService) this.getParentPool()
					.getParentService();
			TileInfo tileInfo = mapService.getMapServiceDesc().getTileInfo();
			if (tileInfo.isReadCompact()) {
				result = doGetCompactTile(level, row, col);
			} else {
				result = doGetExplodedTile(level, row, col);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_TILE"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		} finally {
			this.stopTimeoutMonitor();
		}

		return result;
	}

	private byte[] doGetExplodedTile(int level, int row, int col)
			throws Exception {
		byte[] result = null;

		MapService mapService = (MapService) this.getParentPool()
				.getParentService();
		TileInfo tileDesc = mapService.getMapServiceDesc().getTileInfo();
		String format = tileDesc.getFormat();

		// Get suffix by format
		// Use png for default
		String suffix = "png";
		String lowFormat = format.toLowerCase();
		if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
			suffix = "jpg";
		}

		String fakeMapDir = ServiceManager.getMapServicesDir()
				.getAbsolutePath()
				+ File.separator + PathUtil.nameToRealPath(serviceName);
		String mapDir = PathUtil.fakePathToReal(fakeMapDir);
		String tileFilePath = ArchitectureUtil.getExplodedTileFilePath(mapDir,
				level, row, col, suffix);
		File tileFile = new File(tileFilePath);
		if (tileFile.exists()) {
			FileInputStream is = new FileInputStream(tileFile);
			result = new byte[(int) tileFile.length()];
			is.read(result);
			is.close();
		}

		return result;
	}

	private byte[] doGetCompactTile(int level, int row, int col)
			throws Exception {
		byte[] result = null;

		try {
			String fakeMapDir = ServiceManager.getMapServicesDir()
					.getAbsolutePath()
					+ File.separator + PathUtil.nameToRealPath(serviceName);
			String mapDir = PathUtil.fakePathToReal(fakeMapDir);
			String tileFilePath = ArchitectureUtil.getCompactTileFilePath(
					mapDir, level, row, col);
			String bundlxFileName = tileFilePath + ".bundlx";
			String bundleFileName = tileFilePath + ".bundle";

			int rGroup = 128 * (row / 128);
			int cGroup = 128 * (col / 128);

			int index = 128 * (col - cGroup) + (row - rGroup);
			FileInputStream isBundlx = new FileInputStream(bundlxFileName);
			isBundlx.skip(16 + 5 * index);
			byte[] buffer = new byte[5];
			isBundlx.read(buffer);
			isBundlx.close();
			long offset = (long) (buffer[0] & 0xff) + (long) (buffer[1] & 0xff)
					* 256 + (long) (buffer[2] & 0xff) * 65536
					+ (long) (buffer[3] & 0xff) * 16777216
					+ (long) (buffer[4] & 0xff) * 4294967296L;

			FileInputStream isBundle = new FileInputStream(bundleFileName);
			isBundle.skip(offset);
			byte[] lengthBytes = new byte[4];
			isBundle.read(lengthBytes);
			int length = (int) (lengthBytes[0] & 0xff)
					+ (int) (lengthBytes[1] & 0xff) * 256
					+ (int) (lengthBytes[2] & 0xff) * 65536
					+ (int) (lengthBytes[3] & 0xff) * 16777216;
			result = new byte[length];
			isBundle.read(result);
			isBundle.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public ArrayList<FeatureResult> identify(ArrayList<String> layerIds,
			IdentifyParam identifyParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		try {
			this.startTimeoutMonitor();

			MapService mapService = (MapService) this.getParentPool()
					.getParentService();
			MapDesc mapDesc = mapService.getMapDesc();

			String mapSR = mapDesc.getWkid();

			// Handle geometry
			Geometry geometry = identifyParam.getGeometry();
			int tolerance = identifyParam.getTolerance();
			double resolution = identifyParam.getResolution();
			if (tolerance > 0) {
				double distance = tolerance * resolution;
				geometry = GeometryToolkit.buffer(geometry, mapSR, mapSR,
						mapSR, distance);
			}

			IdentifyType identifyType = identifyParam.getIdentifyType();

			// Handle layers
			ArrayList<String> handleLayerIds = null;
			if (identifyType.equals(IdentifyType.TOP)) {
				handleLayerIds = layerIds;
			} else if (identifyType.equals(IdentifyType.VISIBLE)) {
				handleLayerIds = new ArrayList<String>();
				for (int i = 0, count = map.getMapContext().getLayerCount(); i < count; i++) {
					MapLayer layer = map.getLayer(i).getMapLayer();
					if (layer.isVisible()) {
						handleLayerIds.add(String.valueOf(i));
					}
				}
			} else if (identifyType.equals(IdentifyType.ALL)) {
				handleLayerIds = new ArrayList<String>();
				for (int i = 0, count = map.getMapContext().getLayerCount(); i < count; i++) {
					handleLayerIds.add(String.valueOf(i));
				}
			}

			for (int i = 0, count = handleLayerIds.size(); i < count; i++) {
				Layer layer = map.getLayer(i);

				QueryParam queryParam = new QueryParam();
				queryParam.setGeometry(geometry);
				queryParam.setSpatialFilterType(SpatialFilterType.INTERSECTS);
				FeatureCollection<? extends FeatureType, ? extends Feature> layerFeatureCollection = layer
						.query(queryParam);
				if (layerFeatureCollection != null
						&& !layerFeatureCollection.isEmpty()) {
					for (FeatureIterator<? extends Feature> itr = layerFeatureCollection
							.features(); itr.hasNext();) {
						Feature feature = itr.next();
						FeatureResult featureResult = new FeatureResult();
						featureResult.setLayer(layer);
						featureResult.setFeature(feature);
						result.add(featureResult);
					}

					if (identifyType.equals(IdentifyType.TOP)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.IDENTIFY"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		} finally {
			this.stopTimeoutMonitor();
		}

		return result;
	}

	// Render map to image
	private BufferedImage render(RenderParam renderParam) throws Exception {
		BufferedImage image = null;

		try {
			// Handle visible layerIds
			int layerCount = map.getMapContext().getLayerCount();
			for (int i = 0; i < layerCount; i++) {
				MapLayer layer = map.getLayer(i).getMapLayer();
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
			for (int i = 0, count = visibleLayerIds.size(); i < count; i++) {
				int layerId = Integer.parseInt(visibleLayerIds.get(i));
				MapLayer layer = map.getLayer(i).getMapLayer();
				layer.setVisible(true);

				// Handle layerDefs
				String key = String.valueOf(layerId);
				if (layerDefs != null && layerDefs.containsKey(key)) {
					String def = layerDefs.get(key);
					if (def != null && !"".equals(def)) {
						try {
							Filter filter = ECQL.toFilter(def);
							DefaultQuery query = new DefaultQuery();
							query.setFilter(filter);
							layer.setQuery(query);
						} catch (Exception e) {
							StringBuilder sb = new StringBuilder();
							sb.append(ResourceBundleManager
									.getResourceBundleMapServiceLog()
									.getString("ERROR.LAYER_DEFS"));
							sb.append("<");
							sb.append(serviceName);
							sb.append("-");
							sb.append(String.valueOf(layerId));
							sb.append(">");
							logger.error(sb.toString(), e);
						}
					} else {
						layer.setQuery(Query.ALL);
					}
				}
			}

			MapImage mapImage = map.render(renderParam);
			image = mapImage.getImage();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.RENDER"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return image;
	}
}
