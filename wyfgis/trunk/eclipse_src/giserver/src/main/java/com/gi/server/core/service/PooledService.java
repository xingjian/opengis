package com.gi.server.core.service;

import com.gi.engine.server.service.AbstractServiceDesc;

public interface PooledService extends Service {	
	public String getServiceDir();// All resources of pooled service will be organized under a directory.
	public AbstractServiceDesc getServiceDesc();// Pooled service must has a service description file.
}
