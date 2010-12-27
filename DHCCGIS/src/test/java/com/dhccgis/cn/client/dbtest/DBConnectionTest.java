/**@Title: DBConnectionTest.java @author promisePB xingjian@yeah.net @date 2010-12-27 下午03:49:49 */

package com.dhccgis.cn.client.dbtest;

import junit.framework.TestCase;

import com.dhccgis.cn.client.db.GISDBConnectionFactory;
import com.dhccgis.cn.client.db.PostGISDBConnection;

/**   
 * @Title: DBConnectionTest.java 
 * @Package com.dhccgis.cn.client.dbtest 
 * @Description: 数据库连接测试类 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-27 下午03:49:49 
 * @version V1.0   
 */

public class DBConnectionTest extends TestCase {

	//PostGISDBConnection
	public static PostGISDBConnection gisConnection;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * 测试postgresql数据库连接
	 */
	public void testPostGISConnectionByURL(){
		gisConnection =GISDBConnectionFactory.getPostGISDBConnection("dhccgis", "dhccgis", "jdbc:postgresql://localhost:5432/dhccgis");
		assertNotNull(gisConnection);
		gisConnection.closeConnection();
	}
	
	/**
	 * 测试Postgresql数据库的连接
	 */
	public void testPostGISConnection(){
		gisConnection = GISDBConnectionFactory.getPostGISDBConnection("10.10.149.17", "dhccgis", "dhccgis", "5432", "dhccgis");
		assertNotNull(gisConnection);
		gisConnection.closeConnection();
	}
	
	@Override
	protected void runTest() throws Throwable {
		super.runTest();
	}
}
