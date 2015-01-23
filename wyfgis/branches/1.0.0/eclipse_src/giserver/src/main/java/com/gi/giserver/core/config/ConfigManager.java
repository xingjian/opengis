package com.gi.giserver.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gi.giserver.core.service.ServiceManager;

public class ConfigManager {
	static Logger logger = Logger.getLogger(ConfigManager.class);

	private static ServerConfig serverConfig = null;
	private static AdministratorConfig administratorConfig = null;
	private static EditorConfig editorConfig = null;
	private static TokenConfig tokenConfig = null;

	public static boolean loadConfigs(String appRootPath) {
		boolean result = false;

		try {
			String configDir = appRootPath + File.separator + "WEB-INF" + File.separator + "conf" + File.separator;

			// log configuration
			String logConfigPath = configDir + "log.properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(logConfigPath));
			String logPath = appRootPath + File.separator + "WEB-INF" + File.separator + "log" + File.separator
					+ "giserver.log";
			properties.setProperty("log4j.appender.daily.File", logPath);
			PropertyConfigurator.configure(properties);

			// server configuration
			String serverConfigPath = configDir + "server.properties";
			ConfigManager.loadServerConfig(serverConfigPath);

			// administrator configuration
			String administratorConfigPath = configDir + "administrator.properties";
			ConfigManager.loadAdministratorConfig(administratorConfigPath);

			// editor configuration
			String editorConfigPath = configDir + "editor.properties";
			ConfigManager.loadEditorConfig(editorConfigPath);

			// token configuration
			String tokenConfigPath = configDir + "token.properties";
			ConfigManager.loadTokenConfig(tokenConfigPath);
		} catch (Exception ex) {
			logger.error("Load configurations error.", ex);
		}

		return result;
	}
	
	/**
	 * When reload configurations,
	 * We MUST check if services configuration has been changed
	 * We MUST reload services if necessary
	 */
	public static void checkServicesForReload(){
		// Check if map service directory has been changed
		if (serverConfig.isMapServiceDirChanged()) {
			ServiceManager.loadMapServices(serverConfig.getMapServiceDir());
			serverConfig.setMapServiceDirChanged(false);
		}
	}

	private static void loadServerConfig(String filePath) {
		serverConfig = new ServerConfig(filePath);
	}

	private static void loadAdministratorConfig(String filePath) {
		administratorConfig = new AdministratorConfig(filePath);
	}

	private static void loadEditorConfig(String filePath) {
		editorConfig = new EditorConfig(filePath);
	}

	private static void loadTokenConfig(String filePath) {
		tokenConfig = new TokenConfig(filePath);
	}

	public static ServerConfig getServerConfig() {
		return serverConfig;
	}

	public static AdministratorConfig getAdministratorConfig() {
		return administratorConfig;
	}

	public static EditorConfig getEditorConfig() {
		return editorConfig;
	}

	public static TokenConfig getTokenConfig() {
		return tokenConfig;
	}
}
