package com.pbgis.engine.carto;

import java.awt.Color;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Envelope;

public class MapDesc {

	private String version = null;
	private String name = null;
	private String wkid = null;
	private boolean antiAlias = false;
	private Color backgroundColor = new Color(255, 255, 255);
	private Envelope initialExtent = null;
	private ArrayList<LayerInfo> layerInfos = new ArrayList<LayerInfo>();

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Envelope getInitialExtent() {
		return initialExtent;
	}

	public LayerInfo getLayerInfo(int layerId) {
		LayerInfo result = null;

		if (layerId < this.layerInfos.size()) {
			result = layerInfos.get(layerId);
		}

		return result;
	}

	public ArrayList<LayerInfo> getLayerInfos() {
		return layerInfos;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getWkid() {
		return wkid;
	}

	public boolean isAntiAlias() {
		return antiAlias;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setInitialExtent(Envelope initialExtent) {
		this.initialExtent = initialExtent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setWkid(String wkid) {
		this.wkid = wkid;
	}

}
