/** @文件名: GetMapRequest.java @创建人：邢健  @创建日期： 2014-1-20 上午9:30:33 */

package com.pbgis.server.wms;

import java.util.Map;

/**   
 * @类名: GetMapRequest.java 
 * @包名: com.pbgis.server.wms 
 * @描述: GetMapRequest 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2014-1-20 上午9:30:33 
 * @版本: V1.0   
 */
public class GetMapRequest extends WMSRequest {

	public String layers;
	public String styles;
	public String bbox;
	public int width;
	public int height;
	public String format;
	public String crs;
	//非必须
	public String transparent; 
	public String bgcolor;
	public String exceptions; 
	public String time;
	public String elevation;
	
	public GetMapRequest(String request) {
		super(request);
	}

	public void initGetMapRequest(Map<String,Object> map){
		this.layers = map.get("LAYERS").toString();
		this.styles = map.get("STYLES").toString();
		this.bbox = map.get("BBOX").toString();
		this.width = new Integer(map.get("WIDTH").toString());
		this.height = new Integer(map.get("HEIGHT").toString());
		this.format = map.get("FORMAT").toString();
		this.crs = map.get("CRS").toString();
	}
	
	public String getBbox() {
		return bbox;
	}

	public void setBbox(String bbox) {
		this.bbox = bbox;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getLayers() {
		return layers;
	}

	public void setLayers(String layers) {
		this.layers = layers;
	}

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public String getTransparent() {
		return transparent;
	}

	public void setTransparent(String transparent) {
		this.transparent = transparent;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public String getExceptions() {
		return exceptions;
	}

	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getElevation() {
		return elevation;
	}

	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	
}
