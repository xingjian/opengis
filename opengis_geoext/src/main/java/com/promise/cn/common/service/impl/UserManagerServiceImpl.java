/**@文件名: UserManagerServiceImpl.java @作者： promisePB xingjian@yeah.net @日期 2011-6-3 下午10:27:09 */

package com.promise.cn.common.service.impl;

import com.promise.cn.common.domain.User;
import com.promise.cn.common.service.UserManagerService;

/**   
 * @类名: UserManagerServiceImpl.java 
 * @包名: com.promise.cn.common.service.impl 
 * @描述: 用户管理 
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-6-3 下午10:27:09 
 * @版本： V1.0   
 */

public class UserManagerServiceImpl implements UserManagerService {

	/**
	 * 检验用户登录方法
	 */
	@Override
	public String checkLogin(String userName, String pwd) {
		System.out.println(userName+pwd);
		User user = new User();
		user.setName("admin");
		return "{login:'success',name:'admin',success:true}";
	}

}
