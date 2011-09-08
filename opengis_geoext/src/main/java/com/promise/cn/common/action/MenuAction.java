/*@文件名: MenuAction.java  @创建人: 邢健   @创建日期: 2011-6-17 下午11:02:10*/
package com.promise.cn.common.action;

import java.io.IOException;
import java.io.PrintWriter;

import com.promise.cn.common.service.MenuService;

/**   
 * @类名: MenuAction.java 
 * @包名: com.promise.cn.common.action 
 * @描述: 菜单action 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-6-17 下午11:02:10 
 * @版本 V1.0   
 */
public class MenuAction extends BaseAction{

	private String menusString;
	
	private MenuService menuService;

	public String getMenus(){
		this.menusString = menuService.getMenusJSON();
		response.addHeader("Cache-Control", "no-cache");
		response.setContentType("HTML/JavaScript;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(menusString);
		out.flush();
		out.close();
		return "success";
	}
	
	public String getMenusString() {
		return menusString;
	}

	public void setMenusString(String menusString) {
		this.menusString = menusString;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}
}
