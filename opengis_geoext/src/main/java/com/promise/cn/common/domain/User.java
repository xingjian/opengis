/**@文件名: User.java @作者： promisePB xingjian@yeah.net @日期 2011-6-3 下午10:59:45 */

package com.promise.cn.common.domain;

/**   
 * @类名: User.java 
 * @包名: com.promise.cn.common.domain 
 * @描述: 用户
 * @作者： promisePB xingjian@yeah.net   
 * @日期： 2011-6-3 下午10:59:45 
 * @版本： V1.0   
 */

public class User {

	private String id;//id
	
	private String name;//名字
	
	private String pwd;//密码
	
	private String email;//邮箱地址

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
