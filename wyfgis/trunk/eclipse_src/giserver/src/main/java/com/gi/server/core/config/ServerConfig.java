package com.gi.server.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gi.engine.util.common.PathUtil;

public class ServerConfig {
	static Logger logger = Logger.getLogger(ServerConfig.class);

	private String webRoot = null;
	private Locale locale = null;
	private int adminCookieMaxageMinutes = 15;
	private boolean internalServiceNeedTokenVerify = false;
	private String mapServicesDir = null;
	private boolean mapServicesDirChanged = false;
	private String tokenMark = null;
	private int tokenMinExpire = 0;
	private int tokenMaxExpire = 0;
	private int tokenClearInterval = 0;

	public ServerConfig(String filePath) throws Exception {
		FileInputStream stream = new FileInputStream(filePath);
		Properties p = new Properties();
		p.load(stream);

		// WebRoot is used for some URL output.
		// Such as export map image in JSON format, the image URL needs
		// webRoot.
		webRoot = p.getProperty("WEB_ROOT");

		locale = Locale.getDefault();
		try {
			String langAndCountry = p.getProperty("LOCALE");
			if (langAndCountry.contains("_")) {
				String[] strs = langAndCountry.split("_");
				locale = new Locale(strs[0], strs[1], "");
			} else {
				locale = new Locale(langAndCountry, "", "");
			}
		} catch (Exception ex) {
			logger.warn("Get Locale Error", ex);
		}

		adminCookieMaxageMinutes = Integer.valueOf(p
				.getProperty("ADMIN_COOKIE_MAXAGE_MINUTES"));

		// Internal Service need verify token first
		internalServiceNeedTokenVerify = Boolean.valueOf(p
				.getProperty("INTERNAL_SERVICE_NEED_TOKEN_VERIFY"));

		// MapServiceDir is the map services directory
		String newMapServicesDir = p.getProperty("MAP_SERVICES_DIR");
		newMapServicesDir = PathUtil.fakePathToReal(newMapServicesDir);
		if (!newMapServicesDir.endsWith(File.separator)) {
			newMapServicesDir += File.separator;
		}
		if (!newMapServicesDir.equals(mapServicesDir)) {
			mapServicesDir = newMapServicesDir;
			mapServicesDirChanged = true;
		}

		// TokenMark is used for identify token service
		tokenMark = p.getProperty("TOKEN_MARK");

		// Token time settings
		tokenMinExpire = Integer.valueOf(p.getProperty("TOKEN_MIN_EXPIRE"));
		tokenMaxExpire = Integer.valueOf(p.getProperty("TOKEN_MAX_EXPIRE"));
		tokenClearInterval = Integer.valueOf(p
				.getProperty("TOKEN_CLEAR_INTERVAL"));
	}

	public String getWebRoot() {
		return webRoot;
	}

	public Locale getLocale() {
		return locale;
	}	

	public int getAdminCookieMaxageMinutes() {
		return adminCookieMaxageMinutes;
	}

	public boolean isInternalServiceNeedTokenVerify() {
		return internalServiceNeedTokenVerify;
	}

	public String getMapServicesDir() {
		return mapServicesDir;
	}

	public boolean isMapServicesDirChanged() {
		return mapServicesDirChanged;
	}

	public void setMapServicesDirChanged(boolean mapServicesDirChanged) {
		this.mapServicesDirChanged = mapServicesDirChanged;
	}

	public String getTokenMark() {
		return tokenMark;
	}

	public int getTokenMinExpire() {
		return tokenMinExpire;
	}

	public int getTokenMaxExpire() {
		return tokenMaxExpire;
	}

	public int getTokenClearInterval() {
		return tokenClearInterval;
	}

}
