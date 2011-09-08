/*@文件名: BaseAction.java  @创建人: 邢健   @创建日期: 2011-6-21 下午09:11:08*/
package com.promise.cn.common.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

/**   
 * @类名: BaseAction.java 
 * @包名: com.promise.cn.common.action 
 * @描述: BaseAction所有的action都继承 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-6-21 下午09:11:08 
 * @版本 V1.0   
 */
public class BaseAction extends ActionSupport implements ServletRequestAware,ServletResponseAware ,SessionAware,ApplicationAware{

	protected HttpServletRequest request;
	
	protected Map session;
	
	protected Map application;
	
	protected HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setApplication(Map<String, Object> arg0) {
		this.application = arg0;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

}
