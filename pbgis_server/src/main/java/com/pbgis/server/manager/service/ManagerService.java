/** @文件名: ManagerService.java @创建人：邢健  @创建日期： 2014-2-11 上午8:59:34 */

package com.pbgis.server.manager.service;

import java.util.List;

import com.pbgis.server.manager.vo.ServerService;

/**   
 * @类名: ManagerService.java 
 * @包名: com.pbgis.server.manager.service 
 * @描述: 管理端 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-2-11 上午8:59:34 
 * @版本: V1.0   
 */
public interface ManagerService {

	public boolean checkUser(String userName,String password);
	public void addServerService(ServerService serverService);
	public void editServerService(ServerService serverService);
	public void deleteServerService(ServerService serverService);
	public List<ServerService> queryAllServerService(ServerService serverService);
}
