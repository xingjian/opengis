/*@文件名: MenuServiceImpl.java  @创建人: 邢健   @创建日期: 2011-6-17 下午11:05:20*/
package com.promise.cn.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.promise.cn.common.domain.Menu;
import com.promise.cn.common.service.MenuService;

/**   
 * @类名: MenuServiceImpl.java 
 * @包名: com.promise.cn.common.service.impl 
 * @描述: 菜单服务对象接口
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-6-17 下午11:05:20 
 * @版本 V1.0   
 */
public class MenuServiceImpl implements MenuService {

	/**
	 * 返回json格式菜单
	 */
	@Override
	public String getMenusJSON() {
		List<Menu> menus = getTestMenus();
		JSONArray jsonObject = JSONArray.fromObject(menus);
		return jsonObject.toString();
	}
	
	/**
	 * 测试方法 手工构造menus集合
	 * @return
	 */
	public List<Menu> getTestMenus(){
		List<Menu> menus = new ArrayList<Menu>();
		Menu root = new Menu();
		root.setId("rootnode");
		root.setLeaf(false);
		root.setText("开源GIS功能菜单");
		root.setIcon("images/icon_home.gif");
		root.setQtip("开源GIS功能菜单");
		menus.add(root);
		List<Menu> nodes = new ArrayList<Menu>();
		root.setChildren(nodes);
		Menu node1 = new Menu();
		node1.setId("node1");
		node1.setLeaf(true);
		node1.setText("显示地图");
		node1.setIcon("images/book.png");
		node1.setQtip("显示地图");
		Menu node2 = new Menu();
		node2.setId("node2");
		node2.setLeaf(true);
		node2.setText("地图定位");
		node2.setIcon("images/book.png");
		node2.setQtip("地图定位");
		nodes.add(node1);
		nodes.add(node2);
		return menus;
	}
}
