package com.gi.giserver.core.service.concurrent;

public interface Pool {

	public Instance getIdleInstance();
	
	public void setInstanceIdle(Instance instance);

	public void disposeIdleInstaces(int remainIdle);

	public void disposeAllInstances();

	public int getRunningInstanceCount();

	public int getTotleInstanceCount();

	public int getMaxInstanceCount();

}
