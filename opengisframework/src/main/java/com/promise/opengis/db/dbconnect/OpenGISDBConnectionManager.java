/*@文件名: OpenGISDBConnectionManager.java  @创建人: 邢健   @创建日期: 2011-9-14 下午4:52:47*/
package com.promise.opengis.db.dbconnect;

/**   
 * @类名: OpenGISDBConnectionManager.java 
 * @包名: com.promise.opengis.db.dbconnect 
 * @描述: 开源数据库连接 
 * @作者: 邢健 xingjian@yeah.net   
 * @日期: 2011-9-14 下午4:52:47 
 * @版本 V1.0   
 */
public interface OpenGISDBConnectionManager {
	
	/**
	 * 关闭数据库连接
	 */
	public void closeDBConnection();
	/**
	 * 执行sql语句
	 * @param sql
	 */
	public void executeSQL(String sql);
	/**
	 * 初始化数据源
	 */
	public void initConnection();
	/**
	 * 判断数据源连接状态
	 */
	public boolean conectStatus();
}
