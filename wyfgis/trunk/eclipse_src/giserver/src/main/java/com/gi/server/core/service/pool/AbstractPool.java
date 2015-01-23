package com.gi.server.core.service.pool;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.gi.engine.server.service.AbstractServiceDesc;
import com.gi.server.core.i18n.ResourceBundleManager;
import com.gi.server.core.service.PooledService;

public abstract class AbstractPool implements Pool {
	static Logger logger = Logger.getLogger(AbstractPool.class);

	private long createTime = System.currentTimeMillis();

	PooledService parentService = null;
	private ConcurrentLinkedQueue<AbstractInstance> idleInstances = new ConcurrentLinkedQueue<AbstractInstance>();
	private Vector<AbstractInstance> workingInstances = new Vector<AbstractInstance>();
	private int minInstances = 1;
	private int maxInstances = 10;
	private int timeout = 60;
	private PoolEventListener selfListener = new PoolEventListener();
	private Vector<PoolEventListener> poolEventListeners = new Vector<PoolEventListener>();

	public AbstractPool(PooledService pooledService) {
		parentService = pooledService;

		AbstractServiceDesc serviceDesc = pooledService.getServiceDesc();
		int minInstances = serviceDesc.getMinInstances();
		int maxInstances = serviceDesc.getMaxInstances();
		this.minInstances = minInstances < maxInstances ? minInstances
				: maxInstances;
		this.maxInstances = maxInstances > minInstances ? maxInstances
				: minInstances;
		this.timeout = serviceDesc.getTimeout();

		// Add self listener to the listeners list.
		this.addEventListener(selfListener);
	}

	public void addEventListener(PoolEventListener listener) {
		if (!poolEventListeners.contains(listener)) {
			poolEventListeners.addElement(listener);
		}
	}

	/**
	 * Check in an idle instance.
	 * 
	 * @param instance
	 */
	public final void checkinIdelInstance(AbstractInstance instance) {
		if (instance != null) {
			while (workingInstances.contains(instance)) {
				workingInstances.removeElement(instance);
			}

			if (!idleInstances.contains(instance)) {
				idleInstances.add(instance);
			}

			fireAnInstanceCheckedIn();
		}
	}

	/**
	 * Check out an idle instance to work.
	 * 
	 * @return
	 */
	public final AbstractInstance checkoutIdleInstance() {
		AbstractInstance instance = null;

		if (idleInstances.size() > 0) {
			if (workingInstances.size() < maxInstances) {
				// There is idle instance could be used.
				instance = idleInstances.poll();
			} else {
				shrinkPool();
			}
		} else {
			if (workingInstances.size() < maxInstances) {
				// There is no idle instance, but we can create one
				instance = createInstance();
			}
		}

		if (instance != null) {
			// An checked out idle instance must be marked as working.
			markInstanceWorking(instance);
		} else {
			// Notify pool and wait()
			fireFailedCheckOutAnInstance();

			// Try get idle instance loop
			instance = checkoutIdleInstance();
		}

		return instance;
	}

	public final void dispose() {
		while (idleInstances.size() > 0) {
			AbstractInstance instance = idleInstances.poll();
			instance.dispose();
		}

		for (Iterator<AbstractInstance> itr = workingInstances.iterator(); itr
				.hasNext();) {
			AbstractInstance instance = itr.next();
			instance.dispose();
		}

		workingInstances.removeAllElements();
	}

	protected void fireAnInstanceCheckedIn() {
		synchronized (this) {
			notifyAll();
		}

		PoolEvent event = new PoolEvent(this);
		for (Iterator<PoolEventListener> itr = poolEventListeners.iterator(); itr
				.hasNext();) {
			PoolEventListener listener = itr.next();
			listener.anInstanceCheckedIn(event);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ResourceBundleManager.getResourceBundleServerLog().getString(
				"INFO.IDLE_INSTANCE_CHECKED_IN"));
		logger.info(sb.toString());
	}

	protected void fireFailedCheckOutAnInstance() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		PoolEvent event = new PoolEvent(this);
		for (Iterator<PoolEventListener> itr = poolEventListeners.iterator(); itr
				.hasNext();) {
			PoolEventListener listener = itr.next();
			listener.failedCheckOutAnInstance(event);
		}

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleServerLog()
					.getString("WARN.NO_IDLE_INSTANCE"));
			logger.warn(sb.toString());
		} catch (Exception ex) {
		}
	}

	public int getIdleInstanceCount() {
		return idleInstances.size();
	}

	public final int getMaxInstances() {
		return maxInstances;
	}

	public final int getMinInstances() {
		return minInstances;
	}

	public PooledService getParentService() {
		return parentService;
	}

	/**
	 * Get pool startup time in milliseconds
	 * 
	 * @return
	 */
	public final long getStartupTime() {
		return System.currentTimeMillis() - createTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getWorkingInstanceCount() {
		return workingInstances.size();
	}

	public final void markInstanceWorking(AbstractInstance instance) {
		if (instance != null) {
			while (idleInstances.contains(instance)) {
				idleInstances.remove(instance);
			}

			if (!workingInstances.contains(instance)) {
				workingInstances.add(instance);
			}
		}
	}

	public void removeAllEventListener() {
		poolEventListeners.clear();
	}

	public void removeEventListener(PoolEventListener listener) {
		while (poolEventListeners.contains(listener)) {
			poolEventListeners.removeElement(listener);
		}
	}

	public final void setMaxInstances(int maxInstances) {
		if (maxInstances >= minInstances) {
			this.maxInstances = maxInstances;
		}
	}

	public final void setMinInstances(int minInstances) {
		if (minInstances <= maxInstances) {
			this.minInstances = minInstances;
		}
	}

	/**
	 * Shrink pool, only remain minimum instances
	 * 
	 */
	public final void shrinkPool() {
		int remainIdleInstanceCount = minInstances - workingInstances.size();
		remainIdleInstanceCount = remainIdleInstanceCount < 0 ? 0
				: remainIdleInstanceCount;
		while (idleInstances.size() > remainIdleInstanceCount) {
			AbstractInstance instance = idleInstances.poll();
			instance.dispose();
		}

		Runtime.getRuntime().gc();
	}

}
