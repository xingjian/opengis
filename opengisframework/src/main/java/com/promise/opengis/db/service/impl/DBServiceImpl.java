/*@文件名: DBServiceImpl.java  @创建人: 邢健   @创建日期: 2011-9-14 下午3:50:33*/
package com.promise.opengis.db.service.impl;

import java.util.List;

import com.promise.opengis.db.service.DBService;

/**   
 * @类名: DBServiceImpl.java 
 * @包名: com.promise.opengis.db.service.impl 
 * @描述: 数据库服务对象接口实现类 
 * @作者: 邢健 xingjian@yeah.net   
 * @日期: 2011-9-14 下午3:50:33 
 * @版本 V1.0   
 */
public class DBServiceImpl implements DBService {

	/**
	 * 功能：将数据库中点对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	public String getPointStr(String tableName, String id) {
		return null;
	}

	/**
	 * 功能：将数据库中线对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	public String getLineStr(String tableName, String id) {
		return null;
	}

	/**
	 * 功能：将数据库中的面对象转换成字符串
	 * 描述：数据不做任何封装直接返回(wkt)
	 * @return
	 */
	@Override
	public String getPolygonStr(String tableName, String id) {
		return null;
	}

	/**
	 * 功能：点缓冲区分析
	 * 描述：通过一个地理feature进行缓冲区分析,返回结果
	 * @return
	 */
	@Override
	public String pointBufferAnalyse(String tableName, String x, String y,
			String radius) {
		return null;
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
	public List<String> bufferAnalyse(String tableName, String x, String y,
			String radius) {
		return null;
	}

}
