package com.pbgis.engine.carto;

import java.util.HashMap;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;

public class RenderParam {

	private boolean antiAlias = false;
	private ReferencedEnvelope extent = null;
	private int imageWidth = 256;
	private int imageHeight = 256;
	private HashMap<String, String> layerDefs = null;
	private List<String> visibleLayerIds = null;
	private boolean transparent = true;

	public ReferencedEnvelope getExtent() {
		return extent;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public HashMap<String, String> getLayerDefs() {
		return layerDefs;
	}

	public List<String> getVisibleLayerIds() {
		return visibleLayerIds;
	}

	public boolean isAntiAlias() {
		return antiAlias;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public void setExtent(ReferencedEnvelope extent) {
		this.extent = extent;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setLayerDefs(HashMap<String, String> layerDefs) {
		this.layerDefs = layerDefs;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public void setVisibleLayerIds(List<String> visibleLayerIds) {
		this.visibleLayerIds = visibleLayerIds;
	}
	public RenderParam clone(){
		RenderParam param = new RenderParam();
		
		param.setAntiAlias(antiAlias);
		param.setExtent(extent);
		param.setImageHeight(imageHeight);
		param.setImageWidth(imageWidth);
		param.setLayerDefs(layerDefs);
		param.setTransparent(transparent);
		param.setVisibleLayerIds(visibleLayerIds);
		
		return param;
	}

}
