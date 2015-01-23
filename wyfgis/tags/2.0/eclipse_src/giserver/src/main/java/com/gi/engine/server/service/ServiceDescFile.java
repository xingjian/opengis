package com.gi.engine.server.service;


public interface ServiceDescFile {

	public AbstractServiceDesc open();

	public boolean save(AbstractServiceDesc serviceDesc);
	
	
}
