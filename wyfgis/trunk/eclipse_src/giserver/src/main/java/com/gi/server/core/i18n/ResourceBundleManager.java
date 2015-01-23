package com.gi.server.core.i18n;

import java.util.ResourceBundle;

import com.gi.server.core.config.ConfigManager;

public class ResourceBundleManager {

	private static ResourceBundle resourceBundleServerLog = ResourceBundle
			.getBundle("com/gi/server/core/log/server", ConfigManager
					.getServerConfig().getLocale());

	private static ResourceBundle resourceBundleMapServiceLog = ResourceBundle
			.getBundle("com/gi/server/core/log/mapservice", ConfigManager
					.getServerConfig().getLocale());

	private static ResourceBundle resourceBundleGeometryServiceLog = ResourceBundle
			.getBundle("com/gi/server/core/log/geometryservice",
					ConfigManager.getServerConfig().getLocale());

	private static ResourceBundle resourceBundleTokenServiceLog = ResourceBundle
			.getBundle("com/gi/server/core/log/tokenservice", ConfigManager
					.getServerConfig().getLocale());
	
	public static ResourceBundle getResourceBundleServerLog() {
		return resourceBundleServerLog;
	}

	public static ResourceBundle getResourceBundleMapServiceLog() {
		return resourceBundleMapServiceLog;
	}

	public static ResourceBundle getResourceBundleGeometryServiceLog() {
		return resourceBundleGeometryServiceLog;
	}

	public static ResourceBundle getResourceBundleTokenServiceLog() {
		return resourceBundleTokenServiceLog;
	}

}
