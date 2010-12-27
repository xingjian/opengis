/**文件: GISDBConnectionImpl.java @作者： xingjian  @创建日期： Dec 24, 2010 9:59:46 AM*/

package com.dhccgis.cn.client.db;

import java.util.Vector;



/**   
 * @Title: GISDBConnectionImpl.java 
 * @Package com.dhcc.gis.service.impl 
 * @Description: GISDBConnection实现类 
 * @author promisepb xingjian@yeah.net   
 * @date Dec 24, 2010 9:59:46 AM 
 * @version V1.0   
 */

public class GISDBConnection{
	//默认的数据库类型
	public String dbType = "PostGIS";

	public GISDBConnection getGISDBConnection(Vector<String> args) {
		return null;
	}

	public GISDBConnection getGISDBConnection(String type, String userName,
			String password, String instance) {
		return null;
	}
}
