package com.gi.server.core.service.mapservice;

import java.util.Vector;

public class MapTilesCreatingMonitor {

	// The tile's key is "<level>|<row>|<col>"
	private Vector<String> mapTilesCreating = new Vector<String>();

	public boolean isMapTileCreating(int level, int row, int col) {
		String key = String.format("%d|%d|%d", level, row, col);
		return mapTilesCreating.contains(key);
	}

	public void setMapTileCreating(int level, int row, int col) {
		String key = String.format("%d|%d|%d", level, row, col);
		if (!mapTilesCreating.contains(key)) {
			mapTilesCreating.addElement(key);
		}
	}

	public void removeMapTileCreating(int level, int row, int col) {
		String key = String.format("%d|%d|%d", level, row, col);
		if (mapTilesCreating.contains(key)) {
			mapTilesCreating.removeElement(key);
		}
	}
}
