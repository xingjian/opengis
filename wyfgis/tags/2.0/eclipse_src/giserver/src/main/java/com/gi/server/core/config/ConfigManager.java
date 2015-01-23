package com.gi.server.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gi.server.core.i18n.ResourceBundleManager;
import com.gi.server.core.service.ServiceManager;

public class ConfigManager {
	static Logger logger = Logger.getLogger(ConfigManager.class);

	private static ServerConfig serverConfig = null;
	private static AdministratorConfig administratorConfig = null;
	private static TokenConfig tokenConfig = null;

	public static boolean loadConfigs(String appRootPath) {
		boolean result = false;

		try {
			// Use "WEB-INF/classes/config" as configuration directory
			String configDir = appRootPath + File.separator + "WEB-INF"
					+ File.separator + "classes" + File.separator + "config"
					+ File.separator;

			// Load log configuration
			String logConfigPath = configDir + "log.properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(logConfigPath));
			String logPath = appRootPath + File.separator + "WEB-INF"
					+ File.separator + "log" + File.separator + "giserver.log";
			properties.setProperty("log4j.appender.daily.File", logPath);
			PropertyConfigurator.configure(properties);

			// Load server configuration
			String serverConfigPath = configDir + "server.properties";
			ConfigManager.loadServerConfig(serverConfigPath);

			// Load administrator configuration
			String administratorConfigPath = configDir
					+ "administrator.properties";
			ConfigManager.loadAdministratorConfig(administratorConfigPath);

			// Load token configuration
			String tokenConfigPath = configDir + "token.properties";
			ConfigManager.loadTokenConfig(tokenConfigPath);
		} catch (Exception ex) {
			logger.error(ResourceBundleManager.getResourceBundleServerLog()
					.getString("ERROR.LOAD_CONFIG"), ex);
		}

		return result;
	}

	/**
	 * When reload configurations, We MUST check if services configuration has
	 * been changed We MUST reload services if necessary
	 */
	public static void checkServicesForReload() {
		// Check if map service directory has been changed
		if (serverConfig.isMapServicesDirChanged()) {
			ServiceManager.setMapServicesDir(new File(serverConfig
					.getMapServicesDir()));
			ServiceManager.loadMapServices();
			serverConfig.setMapServicesDirChanged(false);
		}
	}

	private static void loadServerConfig(String filePath) throws Exception {
		serverConfig = new ServerConfig(filePath);
	}

	private static void loadAdministratorConfig(String filePath)
			throws Exception {
		administratorConfig = new AdministratorConfig(filePath);
	}

	private static void loadTokenConfig(String filePath) throws Exception {
		tokenConfig = new TokenConfig(filePath);
	}

	public static ServerConfig getServerConfig() {
		return serverConfig;
	}

	public static AdministratorConfig getAdministratorConfig() {
		return administratorConfig;
	}

	public static TokenConfig getTokenConfig() {
		return tokenConfig;
	}
}
