/**@文件名: SpaceQueryService.java @作者： promisePB xingjian@yeah.net @日期 2011-1-6 上午11:17:07 */

package com.dhccgis.cn.app.service;

import java.util.List;

import com.dhccgis.cn.app.vo.Hospital;

/**   
 * @类名: SpaceQueryService.java 
 * @包名 com.dhccgis.cn.app.service 
 * @描述: 空间查询服务接口 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-1-6 上午11:17:07 
 * @版本： V1.0   
 */

public interface SpaceQueryService {
	/**
	 * 功能：空间查询
	 * 描述：通过名字进行模糊查询，返回list集合
	 */
	public List<Hospital> getListByName(String name,String tableName);
}
