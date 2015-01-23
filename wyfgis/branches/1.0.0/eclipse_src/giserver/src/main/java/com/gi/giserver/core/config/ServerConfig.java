package com.gi.giserver.core.config;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gi.giengine.util.PathUtil;

public class ServerConfig {
	static Logger logger = Logger.getLogger(ServerConfig.class);

	private String webRoot = null;
	private Locale locale = null;
	private boolean internalServiceNeedTokenVerify = false;
	private String mapServiceDir = null;
	private boolean mapServiceDirChanged = false;
	private String tokenMark = null;
	private int tokenMinExpire = 0;
	private int tokenMaxExpire = 0;
	private int tokenClearInterval = 0;

	public ServerConfig(String filePath) {
		try {
			FileInputStream stream = new FileInputStream(filePath);
			Properties p = new Properties();
			p.load(stream);

			// WebRoot is used for some URL output
			// Such as export map image in JSON format, the image URL needs webRoot
			webRoot = p.getProperty("WEB_ROOT");
			
			locale = Locale.getDefault();
			try{
				String langAndCountry = p.getProperty("LOCALE");
				if(langAndCountry.contains("_")){
					String[] strs = langAndCountry.split("_");
					locale = new Locale(strs[0], strs[1], "");
				}else{
					locale = new Locale(langAndCountry, "", "");
				}
			}catch(Exception ex){
				logger.warn("Set Locale Error", ex);
			}
			
			// Internal Service need verify token first
			internalServiceNeedTokenVerify =Boolean.valueOf(p.getProperty("INTERNAL_SERVICE_NEED_TOKEN_VERIFY"));
			
			// MapServiceDir is the map services directory
			String newMapServiceDir = p.getProperty("MAP_SERVICE_DIR");
			if (!newMapServiceDir.endsWith("/")) {
				newMapServiceDir += "/";
			}
			newMapServiceDir = PathUtil.fakePath2Real(newMapServiceDir);
			if (!newMapServiceDir.equals(mapServiceDir)) {
				mapServiceDir = newMapServiceDir;
				mapServiceDirChanged = true;
			}
			
			// TokenMark is used for identify token service
			tokenMark = p.getProperty("TOKEN_MARK");
			
			// Token time settings
			tokenMinExpire = Integer.valueOf(p.getProperty("TOKEN_MIN_EXPIRE"));
			tokenMaxExpire = Integer.valueOf(p.getProperty("TOKEN_MAX_EXPIRE"));
			tokenClearInterval = Integer.valueOf(p.getProperty("TOKEN_CLEAR_INTERVAL"));

			logger.info("Server configuration '" + filePath + "' has been loaded.");
		} catch (Exception ex) {
			logger.error("Server configuration '" + filePath + "' load failed.", ex);
		}
	}

	public String getWebRoot() {
		return webRoot;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public boolean isInternalServiceNeedTokenVerify() {
		return internalServiceNeedTokenVerify;
	}

	public String getMapServiceDir() {
		return mapServiceDir;
	}

	public boolean isMapServiceDirChanged() {
		return mapServiceDirChanged;
	}

	public void setMapServiceDirChanged(boolean mapServiceDirChanged) {
		this.mapServiceDirChanged = mapServiceDirChanged;
	}
	
	public String getTokenMark(){
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
