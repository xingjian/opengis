package com.gi.server.core.service.mapservice;

import java.io.File;

import org.apache.log4j.Logger;

import com.gi.engine.carto.MapDesc;
import com.gi.engine.carto.MapDescFile;
import com.gi.engine.server.service.AbstractServiceDesc;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.server.service.MapServiceDescFile;
import com.gi.engine.util.ArchitectureUtil;
import com.gi.engine.util.common.PathUtil;
import com.gi.server.core.i18n.ResourceBundleManager;
import com.gi.server.core.service.PooledService;
import com.gi.server.core.service.ServiceManager;

public class MapService implements PooledService {
	static Logger logger = Logger.getLogger(MapService.class);

	private String serviceName = null;

	private MapDesc mapDesc = null;
	private MapServiceDesc mapServiceDesc = null;

	private boolean started = false;
	private MapServicePool mapServicePool = null;

	private MapTilesCreatingMonitor mapTilesCreatingMonitor = null;

	/**
	 * Map service started from a directory. The directory contains all
	 * resources(etc. configuration, data) of the service.
	 * 
	 * @param serviceRelativePath
	 */
	public MapService(String serviceRelativePath) {
		String realDirRelPath = PathUtil.fakePathToReal(serviceRelativePath);
		serviceName = PathUtil.realPathToName(realDirRelPath);

		String serviceDirAbsolutePath = ServiceManager.getMapServicesDir()
				.getAbsolutePath()
				+ File.separator + realDirRelPath + File.separator;

		MapServiceDescFile mapServiceDescFile = new MapServiceDescFile(
				serviceDirAbsolutePath
						+ ArchitectureUtil.MAP_SERVICE_DESC_FILE_NAME);
		mapServiceDesc = (MapServiceDesc) mapServiceDescFile.open();

		MapDescFile mapDescFile = new MapDescFile(serviceDirAbsolutePath
				+ ArchitectureUtil.MAP_DESC_FILE_NAME);
		String password = mapServiceDesc.getPassword();
		if (password == null || "".equals(password.trim())) {
			mapDesc = mapDescFile.open();
		} else {
			mapDesc = mapDescFile.open(password);
		}

		if (mapServiceDesc.isAutoStart()) {
			start();
		}
	}

	public String getServiceName() {
		return serviceName;
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

	public AbstractServiceDesc getServiceDesc() {
		return mapServiceDesc;
	}

	public boolean isStarted() {
		return started;
	}

	/**
	 * Start service
	 * 
	 * @return
	 */
	public boolean start() {
		if (!started) {
			try {
				mapServicePool = new MapServicePool(this);
				started = true;
			} catch (Exception ex) {
				StringBuilder sb = new StringBuilder();
				sb.append(ResourceBundleManager
						.getResourceBundleMapServiceLog().getString(
								"ERROR.START_MAP_SERVICE"));
				sb.append("<");
				sb.append(mapServiceDesc);
				sb.append(">");
				logger.error(sb.toString(), ex);
			}
		}

		return started;
	}

	/**
	 * Stop service
	 * 
	 * @return
	 */
	public boolean stop() {
		if (started) {
			try {
				if (mapServicePool != null) {
					mapServicePool.dispose();
					mapServicePool = null;
				}
				started = false;
			} catch (Exception ex) {
				StringBuilder sb = new StringBuilder();
				sb.append(ResourceBundleManager
						.getResourceBundleMapServiceLog().getString(
								"ERROR.STOP_MAP_SERVICE"));
				sb.append("<");
				sb.append(mapServiceDesc);
				sb.append(">");
				logger.error(sb.toString(), ex);
			}
		}

		return !started;
	}

	public String getServiceDir() {
		String realDirRelPath = PathUtil.nameToRealPath(serviceName);
		String serviceDirAbsolutePath = ServiceManager.getMapServicesDir()
				.getAbsolutePath()
				+ File.separator + realDirRelPath + File.separator;

		return serviceDirAbsolutePath;
	}

	public MapTilesCreatingMonitor getMapTilesCreatingMonitor() {
		if (mapTilesCreatingMonitor == null) {
			mapTilesCreatingMonitor = new MapTilesCreatingMonitor();
		}

		return mapTilesCreatingMonitor;
	}

}
