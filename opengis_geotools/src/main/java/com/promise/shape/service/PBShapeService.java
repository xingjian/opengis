/** @文件名: PBShapeService.java @创建人：邢健  @创建日期： 2013-2-28 下午3:19:14 */
package com.promise.shape.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**   
 * @类名: PBShapeService.java 
 * @包名: com.promise.shape.service 
 * @描述: shape数据的操作 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-2-28 下午3:19:14 
 * @版本: V1.0   
 */
public interface PBShapeService {

	/**
	 * 读取shape数据
	 * @param file
	 * @param cla
	 * @return
	 */
	public <T> List<T> readShapeFile(File file,Class cla);
	
	/**
	 * 读取shape文件
	 * @param file
	 * @param charset
	 * @return
	 */
	public Map<String,String> readShapeFile(String fileURL,String charset);
	
	public void writeShapeFile(String fileURL,String charset);
	
}
