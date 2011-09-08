/**@文件名: LoginAction.java @作者： promisePB xingjian@yeah.net @日期 2011-6-1 下午11:14:44 */

package com.promise.cn.common.action;

import com.promise.cn.common.domain.User;
import com.promise.cn.common.service.UserManagerService;

/**   
 * @类名: LoginAction.java 
 * @包名: com.promise.cn.common.action 
 * @描述: 用户登录
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-6-1 下午11:14:44 
 * @版本： V1.0   
 */

public class LoginAction{
	
	private UserManagerService userManagerService;
	
	private User user;
	
	private boolean success;
	
	private String message;

	/**
	 * 检验用户登录
	 * 功能：
	 * 描述：
	 * @param userName
	 * @param pwd
	 * @return
	 */
	public String checkLogin(){
		this.success = true;
		return "success";
	}

	public void setUserManagerService(UserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
