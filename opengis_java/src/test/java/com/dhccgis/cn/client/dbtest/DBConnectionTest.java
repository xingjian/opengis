/**@Title: DBConnectionTest.java @author promisePB xingjian@yeah.net @date 2010-12-27 下午03:49:49 */

package com.dhccgis.cn.client.dbtest;

import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.junit.Test;
import com.dhccgis.cn.client.db.GISDBConnectionFactory;
import com.dhccgis.cn.client.db.PostGIDDBUtil;
import com.dhccgis.cn.client.db.PostGISDBConnection;

/**   
 * @Title: DBConnectionTest.java 
 * @Package com.dhccgis.cn.client.dbtest 
 * @Description: 数据库连接测试类 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-27 下午03:49:49 
 * @version V1.0   
 */

public class DBConnectionTest{

	//PostGISDBConnection
	public static PostGISDBConnection gisConnection;
	
	/**
	 * 测试postgresql数据库连接
	 */
	@Test
	public void testPostGISConnectionByURL(){
		gisConnection =GISDBConnectionFactory.getPostGISDBConnection("dhccgis", "dhccgis", "jdbc:postgresql://localhost:5432/dhccgis");
		assertNotNull(gisConnection);
		gisConnection.closeConnection();
	}
	
	/**
	 * 测试Postgresql数据库的连接
	 */
	@Test
	public void testPostGISConnection(){
		gisConnection = GISDBConnectionFactory.getPostGISDBConnection("10.10.149.17", "dhccgis", "dhccgis", "5432", "dhccgis");
		assertNotNull(gisConnection);
		gisConnection.closeConnection();
	}
	
	/**
	 * 测试pg数据源执行sql语句
	 */
	@Test
	public void testPostGISConExecuteSQL(){
		gisConnection = GISDBConnectionFactory.getPostGISDBConnection("10.10.149.17", "dhccgis", "dhccgis", "5432", "dhccgis");
		String sql = "insert into dhcctest (gid,shape,name)values(2,GeomFromText('POINT(116.39 39.9)',4326),'北京市')";
		gisConnection.executeSQL(sql);
		gisConnection.closeConnection();
	}
	/**
	 * 测试pg数据源执行集合数据的插入
	 */
	@Test
	public void testInsertGeometryBySQL(){
		gisConnection = GISDBConnectionFactory.getPostGISDBConnection("10.10.149.17", "dhccgis", "dhccgis", "5432", "dhccgis");
		String sql = "insert into dhcctest1 (id,shape,name)values(1,GeomFromText('POINT(116.39 39.9)',4326),'北京')";
		gisConnection.executeSQL(sql);
	}
	
	/**
	 * 测试获取数据库表结构信息
	 */
	@Test
	@SuppressWarnings("all")
	public void testGetTableMessageByName(){
		gisConnection = GISDBConnectionFactory.getPostGISDBConnection("10.10.149.17", "dhccgis", "dhccgis", "5432", "dhccgis");
		PostGIDDBUtil.con = gisConnection.con;
		List<String> tableMessageList = PostGIDDBUtil.getTableMessage(null,"%", "hospital","%");
		for(int i=0;i<tableMessageList.size();i++){
			String tableMessageStr = tableMessageList.get(i);
			String[] columnStr = tableMessageStr.split(":");
			System.out.println(columnStr[0]+"******"+columnStr[1]);
		}
	}
	
}
