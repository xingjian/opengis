package com.tongtu.gisservice.vo;

import com.vividsolutions.jts.geom.Geometry;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@yeah.net">邢健</a>  
 * @version: V1.0
 * 日期:2016年8月7日 下午2:17:53  
 */
public class TrafficSmallArea {

	private String id;
	private Geometry geom;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Geometry getGeom() {
		return geom;
	}
	public void setGeom(Geometry geom) {
		this.geom = geom;
	}
	
	
}
