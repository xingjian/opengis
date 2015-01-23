package com.gi.server.core.service.pool;

import java.util.EventListener;

public class PoolEventListener implements EventListener {
	
	public final void anInstanceCheckedOut(PoolEvent event) {
		AbstractPool pool = event.getPool();

		if (pool != null) {
			//We need not do anything as far.
		}
	}
	
	public final void anInstanceCheckedIn(PoolEvent event) {
		AbstractPool pool = event.getPool();

		if (pool != null) {
			//We need not do anything as far.
		}
	}
	
	public final void failedCheckOutAnInstance(PoolEvent event) {
		AbstractPool pool = event.getPool();

		if (pool != null) {
			//We need not do anything as far.
		}
	}
	
}
