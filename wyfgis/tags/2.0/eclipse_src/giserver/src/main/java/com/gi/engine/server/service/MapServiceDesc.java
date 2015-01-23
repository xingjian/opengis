package com.gi.engine.server.service;


public class MapServiceDesc extends AbstractServiceDesc {

	private String version = null;
	private String password = null;
	private int dpi = 96;
	private int maxResults = -1;
	private String outputDir = null;
	private boolean useTile = false;
	private TileInfo tileInfo = null;

	public int getDpi() {
		return dpi;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public String getPassword() {
		return password;
	}

	public TileInfo getTileInfo() {
		return tileInfo;
	}

	public String getVersion() {
		return version;
	}

	public boolean isUseTile() {
		return useTile;
	}

	public void setDpi(int dpi) {
		this.dpi = dpi;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTileInfo(TileInfo tileInfo) {
		this.tileInfo = tileInfo;
	}

	public void setUseTile(boolean useTile) {
		this.useTile = useTile;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}