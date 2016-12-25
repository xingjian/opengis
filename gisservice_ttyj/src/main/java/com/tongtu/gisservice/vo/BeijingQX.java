package com.tongtu.gisservice.vo;

import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月7日 下午6:36:16  
 */
public class BeijingQX {
	private String adminCode;
	private Geometry geom;
	private String name;
	public String getAdminCode() {
		return adminCode;
	}
	public void setAdminCode(String adminCode) {
		this.adminCode = adminCode;
	}
	public Geometry getGeom() {
		return geom;
	}
	public void setGeom(Geometry geom) {
		this.geom = geom;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
