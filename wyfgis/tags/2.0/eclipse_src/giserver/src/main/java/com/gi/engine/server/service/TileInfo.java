package com.gi.engine.server.service;

import java.util.ArrayList;
import java.util.Iterator;

public class TileInfo {
	private String tilesDir = null;
	private boolean createOnDemand = false;
	private int createSpread = 2;
	private String format = "png";
	private int width = 256;
	private int height = 256;
	private double originX = 0.0;
	private double originY = 0.0;
	private ArrayList<TileLodInfo> tileLodInfos = new ArrayList<TileLodInfo>();

	public String getTilesDir() {
		return tilesDir;
	}

	public void setTilesDir(String tilesDir) {
		this.tilesDir = tilesDir;
	}

	public boolean isCreateOnDemand() {
		return createOnDemand;
	}

	public void setCreateOnDemand(boolean createOnDemand) {
		this.createOnDemand = createOnDemand;
	}

	public int getCreateSpread() {
		return createSpread;
	}

	public void setCreateSpread(int createSpread) {
		this.createSpread = createSpread;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public double getOriginX() {
		return originX;
	}

	public void setOriginX(double originX) {
		this.originX = originX;
	}

	public double getOriginY() {
		return originY;
	}

	public void setOriginY(double originY) {
		this.originY = originY;
	}

	public ArrayList<TileLodInfo> getTileLodInfos() {
		return tileLodInfos;
	}

	public void clearTileLodInfos() {
		tileLodInfos.clear();
	}

	public void addTileLodInfo(TileLodInfo tileLodInfo) {
		tileLodInfos.add(tileLodInfo);
	}

	public TileLodInfo getTileLodInfo(int level) {
		TileLodInfo result = null;

		try {
			TileLodInfo lod = tileLodInfos.get(level);
			if (lod.getLevel() == level) {
				result = lod;
				return result;
			}
			for (Iterator<TileLodInfo> itr = tileLodInfos.iterator(); itr
					.hasNext();) {
				lod = itr.next();
				if (lod.getLevel() == level) {
					result = lod;
					return result;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}