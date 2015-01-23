package com.gi.giengine.map.param;

import java.util.HashMap;
import java.util.List;

import com.gi.giengine.map.desc.ExtentDesc;

public class RenderParam {

	private boolean antiAlias = false;
	private ExtentDesc bbox = null;
	private String bboxSR = null;
	private int imageWidth = 256;
	private int imageHeight = 256;
	private String imageSR = "4326";
	private HashMap<String, String> layerDefs = null;
	private List<String> visibleLayerIds = null;
	boolean transparent = true;

	public boolean isAntiAlias() {
		return antiAlias;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public ExtentDesc getBbox() {
		return bbox;
	}

	public void setBbox(ExtentDesc bbox) {
		this.bbox = bbox;
	}

	public String getBboxSR() {
		return bboxSR;
	}

	public void setBboxSR(String bboxSR) {
		this.bboxSR = bboxSR;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public String getImageSR() {
		return imageSR;
	}

	public void setImageSR(String imageSR) {
		this.imageSR = imageSR;
	}

	public HashMap<String, String> getLayerDefs() {
		return layerDefs;
	}

	public void setLayerDefs(HashMap<String, String> layerDefs) {
		this.layerDefs = layerDefs;
	}

	public List<String> getVisibleLayerIds() {
		return visibleLayerIds;
	}

	public void setVisibleLayerIds(List<String> visibleLayerIds) {
		this.visibleLayerIds = visibleLayerIds;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	
	//---------------------------------------------------------------
	
	public RenderParam clone(){
		RenderParam param = new RenderParam();
		
		param.setAntiAlias(antiAlias);
		param.setBbox(bbox.clone());
		param.setBboxSR(bboxSR);
		param.setImageHeight(imageHeight);
		param.setImageWidth(imageWidth);
		param.setImageSR(imageSR);
		param.setLayerDefs(layerDefs);
		param.setTransparent(transparent);
		param.setVisibleLayerIds(visibleLayerIds);
		
		return param;
	}

}
