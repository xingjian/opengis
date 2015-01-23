package com.gi.giserver.core.service.mapservice;

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
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.gi.giengine.map.MapEngine;
import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.param.FindParam;
import com.gi.giengine.map.param.IdentifyParam;
import com.gi.giengine.map.param.QueryParam;
import com.gi.giengine.map.param.RenderParam;
import com.gi.giengine.map.query.FeatureResult;
import com.gi.giserver.core.i18n.ResourceManager;
import com.gi.giserver.core.service.concurrent.Instance;
import com.gi.giserver.core.service.mapservice.desc.TileDesc;
import com.gi.giserver.core.service.mapservice.desc.TileLodDesc;
import com.gi.giserver.core.util.MapServiceUtil;
import com.vividsolutions.jts.geom.Geometry;

public class MapServiceInstance implements Instance {
	static Logger logger = Logger.getLogger(MapServiceInstance.class);

	private MapService mapService = null;
	private MapEngine mapEngine = null;

	public void dispose() {
		if (mapEngine != null) {
			mapEngine.dispose();

			String name = mapEngine.getMapDesc().getMapName();
			logger.info(ResourceManager.getResourceBundleMapServiceLog()
					.getString("INFO.INSTANCE_DISPOSE")
					+ "<" + name + ">");
		}
	}

	public void setIdle() {
		if (mapService != null) {
			MapServicePool mapServicePool = mapService.getMapServicePool();
			mapServicePool.setInstanceIdle(this);
			mapServicePool.doNotifyAll();
		}
	}

	// --------------------

	public MapServiceInstance(MapService mapService) throws Exception {
		this.mapService = mapService;
		mapEngine = new MapEngine(mapService.getMapDesc());
	}

	public MapLayer getLayer(int layerId) {
		MapLayer result = null;

		try {
			result = mapEngine.getLayer(layerId);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_LAYER")
					+ "<"
					+ mapService.getMapDesc().getMapName()
					+ ":"
					+ layerId + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public ReferencedEnvelope getFullExtent() {
		ReferencedEnvelope result = null;

		try {
			result = mapEngine.getFullExtent();
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_FULLEXTENT")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public byte[] export(RenderParam renderParam, String format, int dpi) {
		byte[] result = null;

		try {
			result = mapEngine.export(renderParam, format, dpi);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.EXPORT")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public ArrayList<FeatureResult> find(ArrayList<String> layerIds,
			FindParam findParam) {
		ArrayList<FeatureResult> result = null;

		try {
			result = mapEngine.find(layerIds, findParam);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.FIND")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public ArrayList<FeatureResult> identify(ArrayList<String> layerIds,
			IdentifyParam identifyParam) {
		ArrayList<FeatureResult> result = null;

		try {
			result = mapEngine.identify(layerIds, identifyParam);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.IDENTIFY")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			int layerId, QueryParam queryParam) {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		try {
			result = mapEngine.query(layerId, queryParam);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.QUERY")
					+ "<"
					+ mapService.getMapDesc().getMapName()
					+ ":"
					+ layerId + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public boolean insert(int layerId, FeatureCollection featureCollection) {
		boolean result = false;

		try {
			result = mapEngine.insert(layerId, featureCollection);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.INSERT")
					+ "<"
					+ mapService.getMapDesc().getMapName()
					+ ":"
					+ layerId + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public Feature[] delete(int layerId, String where) {
		Feature[] result = null;

		try {
			result = mapEngine.delete(layerId, where);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.DELETE")
					+ "<"
					+ mapService.getMapDesc().getMapName()
					+ ":"
					+ layerId + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public Feature[] modify(int layerId, String where, Geometry geometry,
			HashMap<String, Object> attributes) {
		Feature[] result = null;

		try {
			result = mapEngine.modify(layerId, where, geometry, attributes);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.MODIFY")
					+ "<"
					+ mapService.getMapDesc().getMapName()
					+ ":"
					+ layerId + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public byte[] getTile(int level, int row, int col) {
		byte[] result = null;

		try {
			TileDesc tileDesc = mapService.getMapServiceDesc().getTileDesc();
			String mapName = mapService.getMapDesc().getMapName();
			String format = tileDesc.getFormat();

			// Get suffix by format
			// Use png for default
			String suffix = "png";
			String lowFormat = format.toLowerCase();
			if ("jpg".equals(lowFormat) || "jpeg".equals(lowFormat)) {
				suffix = "jpg";
			}

			String tileFilePath = MapServiceUtil.getTileFilePath(mapName,
					level, row, col, suffix);
			File tileFile = new File(tileFilePath);
			if (tileFile.exists()) {
				FileInputStream stream = new FileInputStream(tileFile);
				result = new byte[(int) tileFile.length()];
				stream.read(result);
				stream.close();
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_TILE")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}

	public byte[] getOrCreateTile(int level, int row, int col) {
		byte[] result = null;

		try {
			// Try to get tile
			result = this.getTile(level, row, col);
			if (result == null) {
				// Get sr
				String sr = mapService.getMapDesc().getWkid();

				// Get tileInfo
				TileDesc tileDesc = mapService.getMapServiceDesc()
						.getTileDesc();
				int width = tileDesc.getWidth();
				int height = tileDesc.getHeight();
				double originX = tileDesc.getOriginX();
				double originY = tileDesc.getOriginY();
				String format = tileDesc.getFormat();

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

				// Calculate the tile bbox
				TileLodDesc tileLodDesc = tileDesc.getTileLodDesc(level);
				double resolution = tileLodDesc.getResolution();
				double deltaX = width * resolution;
				double deltaY = height * resolution;
				double xmin = originX + deltaX * col;
				double ymax = originY - deltaY * row;
				double xmax = xmin + deltaX;
				double ymin = ymax - deltaY;
				
				// Get  tile create spread and span
				int createSpread = tileDesc.getCreateSpread();
				int tileSpan = 2*createSpread+1;

				// Expand tile bbox for labels
				double xmin2 = xmin - createSpread * deltaX;
				double xmax2 = xmax + createSpread * deltaX;
				double ymin2 = ymin - createSpread * deltaY;
				double ymax2 = ymax + createSpread * deltaY;

				RenderParam renderParam = new RenderParam();
				renderParam.setAntiAlias(mapEngine.getMapDesc().isAntiAlias());
				renderParam.setBbox(new ExtentDesc(xmin2, xmax2, ymin2, ymax2));
				renderParam.setBboxSR(sr);
				renderParam.setImageHeight(tileSpan * height);
				renderParam.setImageWidth(tileSpan * width);
				renderParam.setImageSR(sr);
				renderParam.setTransparent(supportTransparent);
				BufferedImage image = mapEngine.render(renderParam);
				ImageFilter cropFilter = new CropImageFilter(createSpread * width,
						createSpread * height, width, height);
				Image destImage = Toolkit.getDefaultToolkit().createImage(
						new FilteredImageSource(image.getSource(), cropFilter));

				BufferedImage tileImage = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = tileImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(destImage, 0, 0, null);

				// Output as byte[]
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(tileImage, format, outputStream);
				result = outputStream.toByteArray();
				outputStream.close();

				String mapName = mapService.getMapDesc().getMapName();
				String tileFilePath = MapServiceUtil.getTileFilePath(mapName,
						level, row, col, suffix);
				File tileFile = new File(tileFilePath);

				if (result != null) {
					String tileDir = tileFilePath.substring(0, tileFilePath
							.lastIndexOf(File.separator));
					File tileDirFile = new File(tileDir);
					if (!tileDirFile.exists()) {
						tileDirFile.mkdirs();
					}
					if (tileFile.createNewFile()) {
						FileOutputStream outputStreamFile = new FileOutputStream(
								tileFilePath);
						outputStreamFile.write(result);
						outputStreamFile.flush();
						outputStreamFile.close();
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_CREATE_TILE")
					+ "<" + mapService.getMapDesc().getMapName() + ">", ex);
		} finally {
			setIdle();
		}

		return result;
	}
}
