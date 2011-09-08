/*@文件名: Menu.java  @创建人: 邢健   @创建日期: 2011-6-17 下午10:59:28*/
package com.promise.cn.common.domain;

import java.util.List;

/**   
 * @类名: Menu.java 
 * @包名: com.promise.cn.common.domain 
 * @描述: 菜单表 
 * @作者: 邢健 xingjian@dhcc.com.cn   
 * @日期: 2011-6-17 下午10:59:28 
 * @版本 V1.0   
 */
public class Menu {

	private String id;
	
	private String text;
	
	private boolean leaf;
	
	private String icon;
	
	private String qtip;
	
	private List<Menu> children;

	public String getQtip() {
		return qtip;
	}

	public void setQtip(String qtip) {
		this.qtip = qtip;
	}

	public String getId() {
		return id;
	}


	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setId(String id) {
		this.id = id;
	}


	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}
	
	
}
