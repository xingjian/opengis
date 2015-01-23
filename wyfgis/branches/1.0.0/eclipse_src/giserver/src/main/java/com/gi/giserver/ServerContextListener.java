package com.gi.giserver;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giserver.core.config.ConfigManager;

/**
 * Application Lifecycle Listener implementation class ServerContextListener
 * 
 */
public class ServerContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ServerContextListener() {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		String appRootPath = event.getServletContext().getRealPath("");
		context.setAttribute("APP_ROOT_PATH", appRootPath);

		try {
			// Load configurations
			ConfigManager.loadConfigs(appRootPath);
			ConfigManager.checkServicesForReload();

			// Load custom coordinateReferenceSystem
			SpatialReferenceEngine.loadCustomCRSs(appRootPath + File.separator
					+ "WEB-INF" + File.separator + "crs" + File.separator);

			/*
			 * // Publish WMS JaxWsServerFactoryBean svrFactory = new
			 * JaxWsServerFactoryBean();
			 * svrFactory.setServiceClass(WMS_1_3_0.class);
			 * svrFactory.setAddress("http://localhost:8080/giserver/wms");
			 * WMSImpl_1_3_0 wmsImpl1_3_0 = new WMSImpl_1_3_0();
			 * svrFactory.setServiceBean(wmsImpl1_3_0); svrFactory.create();
			 */
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
