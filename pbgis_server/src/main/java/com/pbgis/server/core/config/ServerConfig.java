/** @文件名: ServerConfig.java @创建人：邢健  @创建日期： 2014-2-11 上午9:31:47 */

package com.pbgis.server.core.config;

import java.io.FileInputStream;
import java.util.Properties;

/**   
 * @类名: ServerConfig.java 
 * @包名: com.pbgis.server.core.config 
 * @描述: ServerConfig 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-2-11 上午9:31:47 
 * @版本: V1.0   
 */
public class ServerConfig {

	private String serverLoginName;
	private String serverLoginPass;
	private String serverServiceFilePath;
	
	public ServerConfig(String filePath) throws Exception {
		FileInputStream stream = new FileInputStream(filePath);
		Properties p = new Properties();
		p.load(stream);
		serverLoginName = p.getProperty("login_name");
		serverLoginPass = p.getProperty("login_pass");
		serverServiceFilePath = p.getProperty("serverServiceFilePath");
	}

	public String getServerLoginName() {
		return serverLoginName;
	}

	public String getServerLoginPass() {
		return serverLoginPass;
	}

	public String getServerServiceFilePath() {
		return serverServiceFilePath;
	}

	
	
}
