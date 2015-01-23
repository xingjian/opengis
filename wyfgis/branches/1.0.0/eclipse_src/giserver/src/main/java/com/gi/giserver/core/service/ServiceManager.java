package com.gi.giserver.core.service;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.util.MapUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.util.MapServiceUtil;

public class ServiceManager {
	static Logger logger = Logger.getLogger(ServiceManager.class);

	private static HashMap<String, MapService> mapServices = new HashMap<String, MapService>();

	public static MapService getMapService(String mapName) {
		MapService result = null;

		try {
			if (mapServices.containsKey(mapName)) {
				result = mapServices.get(mapName);
			} else {
				String mapDir = MapServiceUtil.getMapDir(mapName);
				MapDesc mapDesc = new MapDesc(MapUtil
						.getMapDescFilePath(mapDir));
				MapServiceDesc mapServiceDesc = new MapServiceDesc(MapUtil
						.getMapServiceDescFilePath(mapDir));
				result = new MapService(mapDesc, mapServiceDesc);
				mapServices.put(mapName, result);
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_MAP_SERVICE")
					+ "<" + mapName + ">", ex);
		}

		return result;
	}

	public static void loadMapServices(String mapServiceDir) {
		try {
			File file = null;
			file = new File(mapServiceDir);
			if (file.isDirectory()) {
				String[] strs = file.list();
				int strCount = strs.length;
				String str = null;
				for (int i = 0; i < strCount; i++) {
					try {
						str = strs[i];
						if (!str.startsWith(".")) {
							file = new File(mapServiceDir + str);
							if (file.isDirectory()) {
								getMapService(str);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean reloadMapService(String mapName) {
		boolean result = false;

		try {
			if (mapServices.containsKey(mapName)) {
				MapService mapService = mapServices.get(mapName);
				mapService.getMapServicePool().disposeAllInstances();
				mapServices.remove(mapName);
			}

			MapService mapService = getMapService(mapName);
			if (mapService != null) {
				result = true;
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.RELOAD_MAP_SERVICE")
					+ "<" + mapName + ">", ex);
		}

		return result;
	}

	public static MapService[] getMapServices() {
		MapService[] result = null;

		try {
			int count = mapServices.size();
			result = new MapService[count];
			mapServices.values().toArray(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
