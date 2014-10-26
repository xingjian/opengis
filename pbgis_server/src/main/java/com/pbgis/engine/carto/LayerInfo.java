package com.pbgis.engine.carto;
/**
 * 
* @类名: LayerInfo.java 
* @包名: com.pbgis.engine.carto 
* @描述: Layer信息
* @作者: xingjian xingjian@yeah.net   
* @日期:2014-1-24 下午2:12:02 
* @版本: V1.0
 */
public class LayerInfo {

	private String name = null;
	private boolean visible = false;
	private boolean editable = false;
	private String dataSourceType = null;
	private String dataSource = null;
	private String charset = null;
	private String style = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
