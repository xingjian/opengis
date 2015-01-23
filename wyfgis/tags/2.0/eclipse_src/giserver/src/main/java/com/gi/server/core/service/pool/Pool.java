package com.gi.server.core.service.pool;

public interface Pool {

	/**
	 * Different pools should implement different createInstance method.
	 * 
	 * @return
	 */
	public AbstractInstance createInstance();
	
}
