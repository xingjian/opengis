/**文件: PostGISDBConnectionManagerImpl.java @作者： xingjian  @创建日期： Dec 24, 2010 11:26:11 AM*/

package com.promise.opengis.db.dbconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**   
 * @类名：PostGISDBConnectionManagerImpl.java 
 * @包名： com.promise.opengis.db.dbconnect
 * @描述： PostGIS数据库连接 
 * @作者： promisepb xingjian@yeah.net   
 * @日期： Dec 24, 2010 11:26:11 AM 
 * @版本： V1.0   
 */

public class PostGISDBConnectionManagerImpl implements OpenGISDBConnectionManager{
	//数据库连接
	public Connection con;
	//数据库服务地址
	public String server;
	//数据库用户名
	public String userName;
	//数据库密码
	public String password;
	//数据库端口
	public String port;
	//数据库实例名
	public String instance;
	//数据库url
	public String url;
	//驱动类名称
	public String driverClassName = "org.postgresql.Driver";
	
	/**
	 * 空的构造函数，提供给spring
	 */
	public PostGISDBConnectionManagerImpl(){
		
	}
	
	/**
	 * Postgis数据源对象
	 * @param userName
	 * @param password
	 * @param url jdbc:postgresql://localhost:5432/postgres
	 */
	@SuppressWarnings("all")
	public PostGISDBConnectionManagerImpl(String userName,String password,String url){
		try {
			Class.forName(driverClassName).newInstance();
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
	public PostGISDBConnectionManagerImpl(String server,String userName,String password ,String port,String database){
		try {
			Class.forName(driverClassName).newInstance();
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
	 * 初始化数据源
	 */
	@Override
	public void initConnection(){
		if(null==con){
			if(null!=url&&userName!=null&&password!=null){
				try {
					Class.forName(driverClassName).newInstance();
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
		}
	}
	
	/**
	 * 功能：执行sql语句,没有返回结果
	 * 说明： 只是用来执行单一的sql语句
	 */
	@Override
	public void executeSQL(String sql){
		initConnection();
		try {
			con.createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库连接
	 */
	@Override
	public void closeDBConnection() {
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
	 * 返回连接的状态
	 * true连接 false未连接
	 */
	@Override
	public boolean conectStatus() {
		boolean conStatus = false;
		if(null!=con){
			try {
				conStatus = !con.isClosed();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conStatus;
	}

	
	public  void setServer(String server) {
		this.server = server;
	}

	public  void setUserName(String userName) {
		this.userName = userName;
	}

	public  void setPassword(String password) {
		this.password = password;
	}

	public  void setPort(String port) {
		this.port = port;
	}

	public  void setInstance(String instance) {
		this.instance = instance;
	}

	public  void setUrl(String url) {
		this.url = url;
	}

}
