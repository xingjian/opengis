/**@文件名: LoginAction.java @作者： promisePB xingjian@yeah.net @日期 2011-6-1 下午11:14:44 */

package com.promise.cn.common.action;

import org.omg.PortableInterceptor.SUCCESSFUL;

import com.promise.cn.common.domain.User;
import com.promise.cn.common.service.UserManagerService;

/**   
 * @类名: LoginAction.java 
 * @包名: com.promise.cn.common.action 
 * @描述: TODO(用一句话描述该文件做什么) 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-6-1 下午11:14:44 
 * @版本： V1.0   
 */

public class LoginAction{
	
	private UserManagerService userManagerService;
	
	private User user;
	
	/**
	 * 检验用户登
	 * 功能：
	 * 描述：
	 * @param userName
	 * @param pwd
	 * @return
	 */
	public String checkLogin(){
		System.out.println("LoginAction=="+user.getName());
		//return userManagerService.checkLogin(user.getName(), user.getPwd());
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
	
	
}
