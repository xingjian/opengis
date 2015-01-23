package com.gi.giserver.core.i18n;

import java.util.ResourceBundle;

import com.gi.giserver.core.config.ConfigManager;

public class ResourceManager {

	private static ResourceBundle resourceBundleMapServiceLog = ResourceBundle
			.getBundle("com/gi/giserver/core/log/mapservice", ConfigManager
					.getServerConfig().getLocale());

	private static ResourceBundle resourceBundleGeometryServiceLog = ResourceBundle
			.getBundle("com/gi/giserver/core/log/geometryservice",
					ConfigManager.getServerConfig().getLocale());

	private static ResourceBundle resourceBundleTokenServiceLog = ResourceBundle
			.getBundle("com/gi/giserver/core/log/tokenservice", ConfigManager
					.getServerConfig().getLocale());

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
