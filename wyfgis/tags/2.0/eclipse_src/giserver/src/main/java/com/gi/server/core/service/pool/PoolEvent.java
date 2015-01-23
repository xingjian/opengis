package com.gi.server.core.service.pool;

import java.util.EventObject;

public class PoolEvent extends EventObject {

	private static final long serialVersionUID = 445805207779672941L;

	private AbstractPool pool = null;

	public PoolEvent(AbstractPool pool) {
		super(pool);

		this.pool = pool;
	}

	public AbstractPool getPool() {
		return pool;
	}

	public void setPool(AbstractPool pool) {
		this.pool = pool;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
