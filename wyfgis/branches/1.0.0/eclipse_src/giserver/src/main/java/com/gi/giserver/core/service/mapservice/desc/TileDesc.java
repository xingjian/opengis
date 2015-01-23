package com.gi.giserver.core.service.mapservice.desc;

import java.util.ArrayList;
import java.util.Iterator;

public class TileDesc {
	private String tilesDir = null;
	private boolean createOnDemand = false;
	private int createSpread = 2;
	private String format = "png";
	private int width = 256;
	private int height = 256;
	private double originX = 0.0D;
	private double originY = 0.0D;
	private ArrayList<TileLodDesc> tileLodDescs = new ArrayList();

	public String getTilesDir() {
		return this.tilesDir;
	}

	public void setTilesDir(String tilesDir) {
		this.tilesDir = tilesDir;
	}

	public boolean isCreateOnDemand() {
		return this.createOnDemand;
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
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getOriginX() {
		return this.originX;
	}

	public void setOriginX(double originX) {
		this.originX = originX;
	}

	public double getOriginY() {
		return this.originY;
	}

	public void setOriginY(double originY) {
		this.originY = originY;
	}

	public ArrayList<TileLodDesc> getTileLodDescs() {
		return this.tileLodDescs;
	}

	public void clearTileLodDescs() {
		this.tileLodDescs.clear();
	}

	public void addTileLodDesc(TileLodDesc tileLodDesc) {
		this.tileLodDescs.add(tileLodDesc);
	}

	public TileLodDesc getTileLodDesc(int level) {
		TileLodDesc result = null;
		try {
			TileLodDesc lod = null;
			lod = (TileLodDesc) this.tileLodDescs.get(level);
			if (lod.getLevel() == level) {
				result = lod;
				return result;
			}
			for (Iterator itr = this.tileLodDescs.iterator(); itr.hasNext();) {
				lod = (TileLodDesc) itr.next();
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