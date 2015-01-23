package com.gi.server;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.server.core.config.ConfigManager;



public class ServerContextListener implements ServletContextListener {

	public ServerContextListener() {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String appRootPath = servletContext.getRealPath("");
		ServerManager.setAppRootPath(appRootPath);

		try {
			// Load configurations
			ConfigManager.loadConfigs(appRootPath);
			ConfigManager.checkServicesForReload();

			// Load custom coordinateReferenceSystem
			// Use 'WEB-INF/crs' as custom coordinate reference system directory
			SpatialReferenceManager.loadCustomCRSs(appRootPath + File.separator
					+ "WEB-INF" + File.separator + "crs" + File.separator);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
	}

}
