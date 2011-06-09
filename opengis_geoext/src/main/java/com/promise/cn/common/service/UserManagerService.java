/**@文件名: UserManagerService.java @作者： promisePB xingjian@yeah.net @日期 2011-6-3 下午10:26:21 */

package com.promise.cn.common.service;

import com.promise.cn.common.domain.User;

/**   
 * @类名: UserManagerService.java 
 * @包名: com.promise.cn.common.service 
 * @描述: 用户管理
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-6-3 下午10:26:21 
 * @版本： V1.0   
 */

public interface UserManagerService {

	/**
	 * 功能：检验用户登录
	 * 描述：用户提交用户名和密码进行校验，返回用户对象
	 * @param userName 用户名
	 * @param pwd 密码
	 * @return
	 */
	public String checkLogin(String userName,String pwd);
}
