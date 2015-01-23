package com.gi.engine.server.service;

public class AbstractServiceDesc {

	private boolean autoStart = false;
	private int minInstances = 1;
	private int maxInstances = 10;
	private int timeout = 120;
	private boolean needToken = false;

	public int getMaxInstances() {
		return maxInstances;
	}

	public int getMinInstances() {
		return minInstances;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public boolean isNeedToken() {
		return needToken;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}

	public void setMinInstances(int minInstances) {
		this.minInstances = minInstances;
	}

	public void setNeedToken(boolean needToken) {
		this.needToken = needToken;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
