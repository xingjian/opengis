/**文件: PostGISDBConnection.java @作者： xingjian  @创建日期： Dec 24, 2010 11:26:11 AM*/

package com.dhccgis.cn.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
	 * 功能：关闭数据源连接
	 * 描述：无
	 * 作者：xingjian
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
	
	/**
	 * 功能：执行sql语句
	 * 说明： 只是用来执行单一的sql语句
	 */
	public void executeSQL(String sql){
		try {
			con.createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能：通过表名获取数据对象,默认就直接查询表,也可以添加模式.tableName
	 * 说明：如果结果为一条数据就返回一个object,如果是多个对象就返回object的
	 *      一个集合，空的话,返回null,封装数据采用json对象
	 */
	public List<Object> selectSqlByTableName(String tableName,String whereStr){
		String sqlStr = "select * from "+tableName ;
		if(null!=whereStr&&!whereStr.trim().endsWith("")){
			sqlStr = sqlStr + whereStr;
		}
		try {
			Statement statement = con.createStatement();
			statement.execute(sqlStr);
			ResultSet rs = statement.getResultSet();
			ResultSetMetaData dsmd = rs.getMetaData();
			while(rs.next()){
			}
			if(rs.isFirst()==rs.isLast()){
				//值返回一个结果
				String json = "{}";
				dsmd.getColumnCount();
			}else{
				//返回多个结果
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
