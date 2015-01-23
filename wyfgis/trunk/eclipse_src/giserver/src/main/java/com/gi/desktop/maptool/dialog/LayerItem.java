package com.gi.desktop.maptool.dialog;

import com.gi.engine.carto.LayerInfo;

public class LayerItem {

	private LayerInfo layerInfo = null;

	public LayerInfo getLayerInfo() {
		return layerInfo;
	}

	public void setLayerInfo(LayerInfo layerInfo) {
		this.layerInfo = layerInfo;
	}

	@Override
	public String toString() {
		if (layerInfo != null) {
			return layerInfo.getName();
		} else {
			return "NULL";
		}
	}

}
