package com.gi.server.core.service.mapservice;

import com.gi.server.core.service.PooledService;
import com.gi.server.core.service.pool.AbstractInstance;
import com.gi.server.core.service.pool.AbstractPool;

public class MapServicePool extends AbstractPool {
	
		
	public MapServicePool(PooledService pooledService) {
		super(pooledService);
	}

	public AbstractInstance createInstance() {
		MapServiceInstance instance = new MapServiceInstance(this);
		return instance;
	}

}
