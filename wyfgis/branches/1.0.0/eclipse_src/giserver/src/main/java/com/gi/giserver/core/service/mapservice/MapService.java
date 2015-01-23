package com.gi.giserver.core.service.mapservice;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.param.FindParam;
import com.gi.giengine.map.param.IdentifyParam;
import com.gi.giengine.map.param.QueryParam;
import com.gi.giengine.map.param.RenderParam;
import com.gi.giengine.map.query.FeatureResult;
import com.gi.giserver.core.i18n.ResourceManager;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.vividsolutions.jts.geom.Geometry;

public class MapService {
	static Logger logger = Logger.getLogger(MapService.class);

	private boolean started = false;
	private MapDesc mapDesc = null;
	private MapServiceDesc mapServiceDesc = null;
	private MapServicePool mapServicePool = null;

	public MapService(MapDesc mapDesc, MapServiceDesc mapServiceDesc) {
		this.mapDesc = mapDesc;
		this.mapServiceDesc = mapServiceDesc;
		if (mapServiceDesc.isAutoStart()) {
			start();
		}
	}

	public boolean start() {
		if (!started) {
			try {
				mapServicePool = new MapServicePool(this);
				started = true;
			} catch (Exception ex) {
				logger.error(ResourceManager.getResourceBundleMapServiceLog()
						.getString("ERROR.START_MAP_SERVICE"), ex);
			}
		}

		return started;
	}

	public boolean stop() {
		if (started) {
			try {
				if (mapServicePool != null) {
					mapServicePool.disposeAllInstances();
					mapServicePool = null;
				}
				started = false;
			} catch (Exception ex) {
				logger.error(ResourceManager.getResourceBundleMapServiceLog()
						.getString("ERROR.STOP_MAP_SERVICE"), ex);
			} finally {
				Runtime r = Runtime.getRuntime();
				r.gc();
			}
		}

		return !started;
	}

	public boolean isStarted() {
		return started;
	}

	public MapDesc getMapDesc() {
		return mapDesc;
	}

	public MapServiceDesc getMapServiceDesc() {
		return mapServiceDesc;
	}

	public MapServicePool getMapServicePool() {
		return mapServicePool;
	}

	// --------------------------------------------------------------

	public byte[] export(RenderParam renderParam, String format, int dpi) {
		byte[] result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.export(renderParam, format, dpi);
		}

		return result;
	}

	public ArrayList<FeatureResult> find(ArrayList<String> layerIds,
			FindParam findParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.find(layerIds, findParam);
		}

		return result;
	}

	public ArrayList<FeatureResult> identify(ArrayList<String> layerIds,
			IdentifyParam identifyParam) {
		ArrayList<FeatureResult> result = new ArrayList<FeatureResult>();

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.identify(layerIds, identifyParam);
		}

		return result;
	}

	public FeatureCollection<? extends FeatureType, ? extends Feature> query(
			int layerId, QueryParam queryParam) {
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.query(layerId, queryParam);
		}

		return result;
	}

	public MapLayer getLayer(int layerId) {
		MapLayer result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.getLayer(layerId);
		}

		return result;
	}

	public boolean insert(int layerId, FeatureCollection featureCollection) {
		boolean result = false;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.insert(layerId, featureCollection);
		}

		return result;
	}

	public Feature[] delete(int layerId, String where) {
		Feature[] result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.delete(layerId, where);
		}

		return result;
	}

	public Feature[] modify(int layerId, String where, Geometry geometry,
			HashMap<String, Object> attributes) throws Exception {
		Feature[] result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.modify(layerId, where, geometry,
					attributes);
		}

		return result;
	}

	public byte[] getTile(int level, int row, int col) {
		byte[] result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.getTile(level, row, col);
		}

		return result;
	}

	public byte[] getOrCreateTile(int level, int row, int col) {
		byte[] result = null;

		MapServiceInstance mapServiceInstance = mapServicePool
				.getIdleInstance();
		if (mapServiceInstance != null) {
			result = mapServiceInstance.getOrCreateTile(level, row, col);
		}

		return result;
	}

}
