package com.gi.desktop.maptool.dialog;

import com.gi.engine.server.service.TileLodInfo;

public class LodItem {
	TileLodInfo tileLodInfo = null;

	public TileLodInfo getTileLodInfo() {
		return tileLodInfo;
	}

	public void setTileLodInfo(TileLodInfo tileLodInfo) {
		this.tileLodInfo = tileLodInfo;
	}

	@Override
	public String toString() {
		if (tileLodInfo != null) {
			return "1 : " + String.format("%f", tileLodInfo.getScale());
		} else {
			return "NULL";
		}
	}
}
