package com.gi.giserver.core.auth;

import java.util.HashMap;

import javax.servlet.ServletContext;

import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.MapServicePool;

public class Administrator {

	private boolean authorized = false;
	private String username = null;
	private String password = null;

	public Administrator(String username, String password) {
		this.username = username;
		this.password = password;

		authorized = authorize(username, password);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	private boolean authorize(String username, String password) {
		boolean result = false;

		try {
			HashMap<String, String> administrators = ConfigManager.getAdministratorConfig().getUsers();
			if (administrators.containsKey(username)) {
				if (administrators.get(username).equals(password)) {
					result = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public synchronized void clearServer() {
		if (authorized) {
			// CacheManager.clearCaches();

			// Clear map instances
			MapService[] mapServices = ServiceManager.getMapServices();
			int count = mapServices.length;
			for (int i = 0; i < count; i++) {
				MapService mapService = mapServices[i];
				MapServicePool mapServicePool = mapService.getMapServicePool();
				int minInstances = mapService.getMapServiceDesc().getMinInstances();
				int remainIdleInstanceCount = minInstances - mapServicePool.getRunningInstanceCount();
				if (remainIdleInstanceCount < 0) {
					mapServicePool.disposeIdleInstaces(0);
				} else {
					mapServicePool.disposeIdleInstaces(remainIdleInstanceCount);
				}
			}

		}
	}

	public synchronized boolean startMapService(String mapName) {
		boolean result = false;
		
		if (authorized) {
			try {
				MapService mapService = ServiceManager.getMapService(mapName);
				result = mapService.start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return result;
	}

	public synchronized boolean stopMapService(String mapName) {
		boolean result = false;
		
		if (authorized) {
			try {
				MapService mapService = ServiceManager.getMapService(mapName);
				result = mapService.stop();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return result;
	}

	public synchronized boolean reloadMapService(String mapName) {
		boolean result = false;

		if (authorized) {
			try {
				result = ServiceManager.reloadMapService(mapName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public synchronized boolean reloadConfigs(ServletContext context) {
		boolean result = false;

		if (authorized) {
			try {
				String appRootPath = context.getAttribute("APP_ROOT_PATH").toString();
				ConfigManager.loadConfigs(appRootPath);
				ConfigManager.checkServicesForReload();
				result = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

}
