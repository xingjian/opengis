/** @文件名: WebMapService.java @创建人：邢健  @创建日期： 2014-1-18 下午3:00:26 */

package com.pbgis.server.wms;



/**   
 * @类名: WebMapService.java 
 * @包名: com.pbgis.server.wms 
 * @描述: WebMap服务接口 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-18 下午3:00:26 
 * @版本: V1.0   
 */
public interface WebMapService {
	//Basic WMS
	void getCapabilities();
	byte[] getMap(GetMapRequest wmsRequest);
	//Queryable WFS
	void getFeatureInfo();
}
