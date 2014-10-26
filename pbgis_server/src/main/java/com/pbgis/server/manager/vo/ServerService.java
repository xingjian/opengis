/** @文件名: ServerService.java @创建人：邢健  @创建日期： 2014-2-18 下午2:49:55 */

package com.pbgis.server.manager.vo;

/**   
 * @类名: ServerService.java 
 * @包名: com.pbgis.server.manager.vo 
 * @描述: ServerService 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-2-18 下午2:49:55 
 * @版本: V1.0   
 */
public class ServerService {

	private String name;
	private String interfaceNames;
	private String workspacePath;
	private String useCache;
	private String createTime;
	private String updateTime;
	
	public String toXMLString(){
		String xmlStr = "<service>";
		xmlStr = xmlStr+"<name>"+name+"</name>";
		xmlStr = xmlStr+"<interfaceNames>"+interfaceNames+"</interfaceNames>";
		xmlStr = xmlStr+"<workspacePath>"+workspacePath+"</workspacePath>";
		xmlStr = xmlStr+"<useCache>"+useCache+"</useCache>";
		xmlStr = xmlStr+"<createTime>"+createTime+"</createTime>";
		xmlStr = xmlStr+"<updateTime>"+updateTime+"</updateTime>";
		xmlStr = xmlStr+"</service>";
		return xmlStr;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInterfaceNames() {
		return interfaceNames;
	}
	public void setInterfaceNames(String interfaceNames) {
		this.interfaceNames = interfaceNames;
	}
	public String getWorkspacePath() {
		return workspacePath;
	}
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}
	public String getUseCache() {
		return useCache;
	}
	public void setUseCache(String useCache) {
		this.useCache = useCache;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
