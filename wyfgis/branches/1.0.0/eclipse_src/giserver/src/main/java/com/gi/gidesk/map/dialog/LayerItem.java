package com.gi.gidesk.map.dialog;

import com.gi.giengine.map.desc.LayerDesc;

public class LayerItem {

	private LayerDesc layerDesc = null;

	public LayerDesc getLayerDesc() {
		return layerDesc;
	}

	public void setLayerDesc(LayerDesc layerDesc) {
		this.layerDesc = layerDesc;
	}

	@Override
	public String toString() {
		if (layerDesc != null) {
			return layerDesc.getName();
		} else {
			return "NULL";
		}
	}

}
