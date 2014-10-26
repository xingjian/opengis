/** @文件名: ConfigManager.java @创建人：邢健  @创建日期： 2014-2-11 上午9:40:20 */

package com.pbgis.server.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
 * @类名: ConfigManager.java 
 * @包名: com.pbgis.server.core.config 
 * @描述: ConfigManager 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-2-11 上午9:40:20 
 * @版本: V1.0   
 */
public class ConfigManager {

	static Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	public static ServerConfig serverConfig = null; 
	
	public ConfigManager() {
	}

	public static void loadServerConfig(String filePath) throws Exception {
		serverConfig = new ServerConfig(filePath);
	}
}
