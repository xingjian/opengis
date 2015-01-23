package com.gi.server.core.auth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import com.gi.server.ServerManager;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServicePool;

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
			HashMap<String, String> administrators = ConfigManager
					.getAdministratorConfig().getUsers();
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
			HashMap<String, MapService> mapServices = ServiceManager.getMapServices();
			for(Iterator<Entry<String, MapService>> itr=mapServices.entrySet().iterator();itr.hasNext();){
				Entry<String, MapService> entry = itr.next();
				MapService mapService = entry.getValue();
				MapServicePool mapServicePool = mapService.getMapServicePool();
				mapServicePool.shrinkPool();
			}

		}
	}

	public synchronized boolean startMapService(String serviceName) {
		boolean result = false;

		if (authorized) {
			try {
				MapService mapService = ServiceManager
						.loadMapService(serviceName);
				result = mapService.start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public synchronized boolean stopMapService(String serviceName) {
		boolean result = false;

		if (authorized) {
			try {
				MapService mapService = ServiceManager
						.getMapService(serviceName);
				if (mapService != null) {
					result = mapService.stop();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return result;
	}

	public synchronized boolean reloadMapService(String serviceName) {
		boolean result = false;

		if (authorized) {
			try {
				result = ServiceManager.reloadMapService(serviceName);
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
				String appRootPath = ServerManager.getAppRootPath();
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
