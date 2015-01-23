package com.gi.server.core.service.pool;

public interface Instance {

	/**
	 * Instance of different types must implement different dispose method.
	 */
	public void dispose();

}
