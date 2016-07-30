package com.promisepb.gisserver.server.service.impl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.gisserver.server.core.MapService;
import com.promisepb.gisserver.server.service.ServiceManagerService;

/**  
 * 功能描述: 地图服务管理实现类
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月24日 下午3:45:18  
 */
public class ServiceManagerServiceImpl implements ServiceManagerService {
	final Logger logger  =  LoggerFactory.getLogger(ServiceManagerServiceImpl.class);
	//工作空间目录
	private String workspacePath;
	private static HashMap<String, MapService> mapServices = new HashMap<String, MapService>();
	
	/**
	 * 初始化加载地图配置
	 */
	public void init(){
		loadMapService(workspacePath);
	}
	
	public String loadMapService(String serviceRelativePath) {
		MapService result = null;
		try {
//			result = getMapService(serviceRelativePath);
//			if (result == null) {
//				result = new MapService(serviceRelativePath);
//				mapServices.put(serviceName, result);
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "success";
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}
	
	
	
}
