/*@文件名: DBService.java  @创建人: 邢健   @创建日期: 2011-9-14 下午3:48:50*/
package com.promise.opengis.db.service;

import java.util.List;

/**   
 * @类名: DBService.java 
 * @包名: com.promise.opengis.db.service 
 * @描述: 数据库服务 
 * @作者: 邢健 xingjian@yeah.net   
 * @日期: 2011-9-14 下午3:48:50 
 * @版本 V1.0   
 */
public interface DBService {

	/**
	 * 功能：将数据库中点对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	public String getPointStr(String tableName,String id);
	/**
	 * 功能：将数据库中线对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	public String getLineStr(String tableName,String id);
	/**
	 * 功能：将数据库中的面对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	public String getPolygonStr(String tableName,String id);
	/**
	 * 功能：点缓冲区分析
	 * 描述：通过一个地理feature进行缓冲区分析,返回结果
	 * @return
	 */
	public String pointBufferAnalyse(String tableName,String x,String y,String radius);
	/**
	 * 功能：缓冲区分析
	 * 描述：List集合索引为0的是缓冲区，从1开始是缓冲结果
	 * @param tableName 表名字
	 * @param x 坐标
	 * @param y 坐标
	 * @param radius 缓冲半径
	 * @return
	 */
	public List<String> bufferAnalyse(String tableName,String x,String y,String radius);
}
