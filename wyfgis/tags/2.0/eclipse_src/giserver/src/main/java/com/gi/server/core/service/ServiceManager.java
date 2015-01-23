package com.gi.server.core.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gi.engine.util.ArchitectureUtil;
import com.gi.engine.util.common.FileNoPrefixFilter;
import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.i18n.ResourceBundleManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServicePool;

public class ServiceManager {
	static Logger logger = Logger.getLogger(ServiceManager.class);

	private static File mapServicesDir = null;
	private static HashMap<String, MapService> mapServices = new HashMap<String, MapService>();

	public static File getMapServicesDir() {
		return mapServicesDir;
	}

	public static void setMapServicesDir(File mapServicesDir) {
		ServiceManager.mapServicesDir = mapServicesDir;
	}

	/**
	 * 
	 * @param serviceRelativePath
	 *            like "dir1/dir2/name"
	 * @return
	 */
	public static MapService loadMapService(String serviceRelativePath) {
		MapService result = null;

		try {
			result = getMapService(serviceRelativePath);

			if (result == null) {
				result = new MapService(serviceRelativePath);
				String serviceName = PathUtil
						.realPathToName(serviceRelativePath);
				mapServices.put(serviceName, result);
			}
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.LOAD_MAP_SERVICE"));
			sb.append("<");
			sb.append(serviceRelativePath);
			sb.append(">");
			logger.error(sb.toString(), ex);
		}

		return result;
	}

	public static MapService getMapService(String serviceName) {
		MapService result = null;

		try {
			if (mapServices.containsKey(serviceName)) {
				result = mapServices.get(serviceName);
			}
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_MAP_SERVICE"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), ex);
		}

		return result;
	}

	/**
	 * If we have set map services directory, we can load map services.
	 */
	public static void loadMapServices() {
		if (mapServicesDir != null) {
			removeMapServices();
			loadMapServicesUnderDir(mapServicesDir);
		}
	}

	public static void removeMapServices() {
		for (Iterator<Entry<String, MapService>> itr = mapServices.entrySet()
				.iterator(); itr.hasNext();) {
			Entry<String, MapService> entry = itr.next();
			MapService mapService = entry.getValue();
			MapServicePool mapServicePool = mapService.getMapServicePool();
			mapServicePool.dispose();
		}

		mapServices.clear();
	}

	/**
	 * Load map services from this directory, will not scan services is sub
	 * directories.
	 * 
	 * @param dir
	 */
	private static void loadMapServicesUnderDir(File dir) {		
		if (dir.isDirectory() && dir.canRead()) {
			FileNoPrefixFilter filter = new FileNoPrefixFilter(".");
			File[] files = dir.listFiles(filter);
			for (int i = 0, count = files.length; i < count; i++) {
				File file = files[i];
				try {
					if (file.isDirectory()) {
						if (ArchitectureUtil.isMapServiceDirectory(file)) {
							// Get directory relative path as service's full
							// name.
							String name = file.getName();
							File parent = file.getParentFile();
							while (!parent.getAbsolutePath().equals(
									mapServicesDir.getAbsolutePath())) {
								name = parent.getName() + File.separator + name;
								parent = parent.getParentFile();
							}

							loadMapService(name);
						} else {
							loadMapServicesUnderDir(file);
						}
					}
				} catch (Exception ex) {
					StringBuilder sb = new StringBuilder();
					sb.append(ResourceBundleManager
							.getResourceBundleMapServiceLog().getString(
									"ERROR.LOAD_MAP_SERVICE"));
					sb.append("<");
					sb.append(file.getAbsolutePath());
					sb.append(">");
					logger.error(sb.toString(), ex);
				}
			}
		}
	}

	public static boolean reloadMapService(String serviceName) {
		boolean result = false;

		try {
			if (mapServices.containsKey(serviceName)) {
				MapService mapService = mapServices.get(serviceName);
				mapService.getMapServicePool().dispose();
				mapServices.remove(serviceName);
			}

			MapService mapService = loadMapService(PathUtil
					.nameToRealPath(serviceName));
			if (mapService != null) {
				result = true;
			}
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleMapServiceLog()
					.getString("ERROR.RELOAD_MAP_SERVICE"));
			sb.append("<");
			sb.append(serviceName);
			sb.append(">");
			logger.error(sb.toString(), ex);
		}

		return result;
	}

	public static HashMap<String, MapService> getMapServices() {
		return mapServices;
	}

}
