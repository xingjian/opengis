/**@文件名: DBServiceImpl.java @作者： promisePB xingjian@yeah.net @日期 2011-1-6 上午10:51:15 */

package com.dhccgis.cn.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingInclude;
import org.springframework.stereotype.Service;

import com.dhccgis.cn.client.db.GISDBConnectionFactory;
import com.dhccgis.cn.client.db.PostGISDBConnection;
import com.dhccgis.cn.service.DBService;

/**   
 * @类名: DBServiceImpl.java 
 * @包名 com.dhccgis.cn.service.impl 
 * @描述: 数据库服务对象接口实现类 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-1-6 上午10:51:15 
 * @版本： V1.0   
 */
@Service("dbService")
@RemotingDestination(channels={"my-amf","my-secure-amf"})
public class DBServiceImpl implements DBService {

	public PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
	public Connection con = gisConnection.con;
	/**
	 * 功能：将数据库中点对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	@RemotingInclude
	public String getPointStr(String tableName,String id) {
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		String pointStr = "";
		String sql = "select *,st_astext(the_geom) from "+tableName+" where gid="+id;
		System.out.println(sql);
		try {
			ResultSet rs = con.createStatement().executeQuery(sql);
			if(rs.next()){
				pointStr = rs.getString("st_astext");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pointStr;
	}
	/**
	 * 功能：将数据库中线对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	@RemotingInclude
	public String getLineStr(String tableName,String id) {
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		String lineStr = "";
		String sql = "select *,st_astext(the_geom) from "+tableName+" where gid="+id;
		System.out.println(sql);
		try {
			ResultSet rs = con.createStatement().executeQuery(sql);
			if(rs.next()){
				lineStr = rs.getString("st_astext");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lineStr;
	}
	/**
	 * 功能：将数据库中的面对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	@RemotingInclude
	public String getPolygonStr(String tableName,String id) {
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		String polygonStr = "";
		String sql = "select *,st_astext(the_geom) from "+tableName+" where gid="+id;
		System.out.println(sql);
		try {
			ResultSet rs = con.createStatement().executeQuery(sql);
			if(rs.next()){
				polygonStr = rs.getString("st_astext");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return polygonStr;
	}
	
	/**
	 * 功能：点缓冲区分析
	 * 描述：通过一个地理feature进行缓冲区分析,返回结果
	 * @return
	 */
	@Override
	@RemotingInclude
	public String pointBufferAnalyse(String tableName, String x, String y,String radius) {
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		String sql = "select st_astext(ST_Buffer(ST_GeomFromText('POINT("+x+" "+y+")'),"+radius+",'quad_segs=8'))";
		System.out.println(sql);
		String polygonStr = "";
		try {
			ResultSet rs = con.createStatement().executeQuery(sql);
			if(rs.next()){
				polygonStr = rs.getString("st_astext");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return polygonStr;
	}
	
	/**
	 * 功能：缓冲区分析
	 * 描述：List集合索引为0的是缓冲区，从1开始是缓冲结果
	 * @param tableName 表名字
	 * @param x 坐标
	 * @param y 坐标
	 * @param radius 缓冲半径
	 * @return
	 */
	@Override
	@RemotingInclude
	public List<String> bufferAnalyse(String tableName,String x,String y,String radius){
		List<String> retList = new ArrayList<String>();
		PostGISDBConnection gisConnection = GISDBConnectionFactory.getPostGISDBConnection("localhost", "dhccgis", "dhccgis", "5432", "dhccgis");
		Connection con = gisConnection.con;
		String sqlBuffer = "select st_astext(ST_Buffer(ST_GeomFromText('POINT("+x+" "+y+")',4326),"+radius+",'quad_segs=8'))";
		String polygonStr = "";
		try {
			ResultSet rs = con.createStatement().executeQuery(sqlBuffer);
			if(rs.next()){
				polygonStr = rs.getString("st_astext");
				retList.add(0,polygonStr);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sqlBuffeQuery = "select *,st_astext(the_geom) from "+tableName+" where ST_Within(the_geom,ST_Buffer(ST_GeomFromText('POINT("+x+" "+y+")',4326),"+radius+",'quad_segs=8'))='t'";
		try {
			System.out.println(polygonStr);
			ResultSet rs1 = con.createStatement().executeQuery(sqlBuffeQuery);
			while(rs1.next()){
				String name = rs1.getString("st_astext");
				retList.add(name);
			}
			rs1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retList;
	}

}
