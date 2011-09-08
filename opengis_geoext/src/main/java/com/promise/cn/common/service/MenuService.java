/*@文件名: MenuService.java  @创建人: 邢健   @创建日期: 2011-6-17 下午11:03:29*/
package com.promise.cn.common.service;

/**   
 * @类名: MenuService.java 
 * @包名: com.promise.cn.common.service 
 * @描述: 菜单service接口 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-6-17 下午11:03:29 
 * @版本 V1.0   
 */
public interface MenuService {
	/**
	 * 获取json格式菜单数组
	 * @return
	 */
	public String getMenusJSON();
}
