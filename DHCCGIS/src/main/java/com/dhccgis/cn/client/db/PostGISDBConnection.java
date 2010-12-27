/**文件: PostGISDBConnection.java @作者： xingjian  @创建日期： Dec 24, 2010 11:26:11 AM*/

package com.dhccgis.cn.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**   
 * @Title: PostGISDBConnection.java 
 * @Package com.dhcc.gis.service 
 * @Description: PostGIS数据库连接 
 * @author promisepb xingjian@yeah.net   
 * @date Dec 24, 2010 11:26:11 AM 
 * @version V1.0   
 */

public class PostGISDBConnection{

	public static Connection con;
	/**
	 * Postgis数据源对象
	 * @param userName
	 * @param password
	 * @param url jdbc:postgresql://localhost:5432/postgres
	 */
	@SuppressWarnings("all")
	public PostGISDBConnection(String userName,String password,String url){
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(url, userName , password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Postgis数据源对象
	 * @param server
	 * @param user
	 * @param password
	 * @param port
	 * @param database
	 */
	@SuppressWarnings("all")
	public PostGISDBConnection(String server,String userName,String password ,String port,String database){
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        String url = "jdbc:postgresql://"+server+":"+port+"/"+database ;
        try {
			con = DriverManager.getConnection(url, userName , password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据源连接
	 */
	public void closeConnection(){
		if(con!=null){
			try {
				if(!con.isClosed()){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
