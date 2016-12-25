package com.tongtu.gisservice.service;

import java.util.List;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月7日 下午1:55:04  
 */
public interface Tocc2GISService {

	/**
	 * 根据经纬度坐标x y 返回交通小区编号No
	 * @param x 经度
	 * @param y 纬度
	 * @return
	 */
	public String getTrafficSmallAreaCodeByXY(double x,double y);
	/**
	 * 根据x,y坐标判断所属行政区划名字
	 * @param x
	 * @param y
	 * @return
	 */
	public String getBQNameByXY(double x,double y);
	/**
	 * 根据经纬度转换成google投影坐标
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] wgs84TO900913(double x,double y);
	/**
	 * 根据google投影坐标转换成经纬度
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] googleTOWGS84(double x,double y);
	/**
	 * 根据传入的xy坐标转换成标准的wkt
	 * @param x
	 * @param y
	 * @return
	 */
	public String getWKTByXY(double x,double y);
	/**
	 * 返回空间对象的描述信息
	 * @param strType wkt,json
	 * @param tableName 表名
	 * @param geomColumnName 几何字段名称
	 * @return
	 */
	public List<String> getGeometryStr(String strType,String tableName,String geomColumnName);
	/**
	 * 根据条件返回空间对象描述信息
	 * @param strType
	 * @param tableName
	 * @param geomColumnName
	 * @param filterStr
	 * @return
	 */
	public List<String> getGeometryStrByFilter(String strType,String tableName,String geomColumnName,String filterStr);
}
