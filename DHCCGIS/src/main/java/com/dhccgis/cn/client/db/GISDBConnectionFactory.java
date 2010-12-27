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
     * 获取PostGIS数据库数据源
     * @param type
     * @param userName
     * @param password
     * @param instance
     * @return
     */
    public static PostGISDBConnection getPostGISDBConnection(String userName,String password,String url){
    	PostGISDBConnection gisDBConnection = null;
    	gisDBConnection = new PostGISDBConnection(userName,password,url);
    	return gisDBConnection;
    }
    
    /**
     * 获取PostGIS数据库数据源
     * @param server
     * @param userName
     * @param password
     * @param port
     * @param database
     * @return
     */
    public static PostGISDBConnection getPostGISDBConnection(String server,String userName,String password ,String port,String database){
    	PostGISDBConnection gisDBConnection = null;
    	gisDBConnection = new PostGISDBConnection(server,userName,password,port,database);
    	return gisDBConnection;
    }

}
