package com.gi.desktop.maptool.dialog;

public class WkidItem {

	private String wkid = null;
	private String wkidLabel = null;

	public WkidItem(String wkid, String wkidLabel) {
		this.wkid = wkid;
		this.wkidLabel = wkidLabel;
	}

	public String getWkid() {
		return wkid;
	}

	@Override
	public String toString() {
		return wkidLabel;
	}
}
