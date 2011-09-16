/**文件: GISDBConnectionFactory.java @作者： xingjian  @创建日期： Dec 24, 2010 11:13:17 AM*/

package com.promise.opengis.db.dbconnect;

/**   
 * @类名： GISDBConnectionFactory.java 
 * @包名： com.promise.opengis.db.dbconnect 
 * @描述： opengis数据库连接 
 * @作者： promisepb xingjian@yeah.net   
 * @date Dec 24, 2010 11:13:17 AM 
 * @version V1.0   
 */

public class GISDBConnectionFactory {
	
	//数据库连接类型
	public static String OPENGISDBTYPE_POSTGIS = "postgis";
	/**
	 * 获取opengis数据库数据源
	 * @param type 包括postgis
	 * @param server
	 * @param userName
	 * @param password
	 * @param port
	 * @param instance
	 * @return
	 */
    public static OpenGISDBConnectionManager getGISDBConnectionByType(String type,String server,String userName,String password,String port,String instance){
    	OpenGISDBConnectionManager openGISDBConnectionManager = null;
    	if(type==OPENGISDBTYPE_POSTGIS){
    		openGISDBConnectionManager = new PostGISDBConnectionManagerImpl(server, userName, password, port, instance);
		}
    	return openGISDBConnectionManager;
    }
   
    
    /**
     * 获取opengis数据库数据源
     * @param type 包括postgis
     * @param userName
     * @param password
     * @param url postgis格式'jdbc:postgresql://localhost:5432/postgres'
     * @return
     */
    public static OpenGISDBConnectionManager getGISDBConnectionByType(String type,String userName,String password,String url){
    	OpenGISDBConnectionManager openGISDBConnectionManager = null;
    	if(type==OPENGISDBTYPE_POSTGIS){
    		openGISDBConnectionManager = new PostGISDBConnectionManagerImpl(userName,password,url);
    	}
    	return openGISDBConnectionManager;
    }
    
    /**
     * 关闭数据库连接
     * @param openGISDBConnection
     */
    public static void closeOpenGISDBConnection(OpenGISDBConnectionManager openGISDBConnectionManager){
    	openGISDBConnectionManager.closeDBConnection();
    }
}
