/**文件: GISDBConnection.java @作者： xingjian  @创建日期： Dec 24, 2010 11:13:17 AM*/

package com.dhccgis.cn.client.db;

/**   
 * @Title: GISDBConnection.java 
 * @Package com.dhcc.gis.service 
 * @Description: gis数据库连接 
 * @author promisepb xingjian@yeah.net   
 * @date Dec 24, 2010 11:13:17 AM 
 * @version V1.0   
 */

public class GISDBConnectionFactory {
	
    public GISDBConnectionFactory(String type,String userName,String password,String instance){
		
    }
    
    /**
     * 获取数据库的连接对象
     * @param type
     * @param userName
     * @param password
     * @param instance
     * @return
     */
    public static GISDBConnection getDBConnection(String type,String userName,String password,String instance){
    	GISDBConnection gisDBConnection = null;
    	if(type.equals("postgis")){
    		gisDBConnection = new PostGISDBConnection();
		}
    	return gisDBConnection;
    }

}
