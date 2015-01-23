package com.gi.server.core.service.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gi.server.core.i18n.ResourceBundleManager;

public abstract class AbstractInstance implements Instance {
	static Logger logger = Logger.getLogger(AbstractInstance.class);

	private AbstractPool parentPool = null;
	private long workTimeout = -1;
	private ScheduledExecutorService scheduExec = null;
	@SuppressWarnings("unchecked")
	private ScheduledFuture scheduFutr = null;

	private Runnable timeoutRunnable = new Runnable() {
		public void run() {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleServerLog().getString(
					"WARN.INSTANCE_EXECUTE_TIMEOUT"));
			logger.warn(sb.toString());
			
			interrupt();
		}
	};

	public AbstractInstance(AbstractPool pool) {
		this.parentPool = pool;
		this.workTimeout = pool.getTimeout();
	}

	private ScheduledExecutorService getScheduExec() {
		if (scheduExec == null) {
			scheduExec = Executors.newSingleThreadScheduledExecutor();
		}

		return scheduExec;
	}

	protected void stopTimeoutMonitor() {
		if (scheduFutr != null && !scheduFutr.isCancelled()) {
			scheduFutr.cancel(true);
		}
	}

	protected void startTimeoutMonitor() throws RejectedExecutionException {
		stopTimeoutMonitor();

		if (workTimeout > 0) {
			scheduFutr = getScheduExec().schedule(timeoutRunnable, workTimeout,
					TimeUnit.SECONDS);
		}
	}

	public AbstractPool getParentPool() {
		return parentPool;
	}

	/**
	 * Interrupt current invoke.
	 */
	public final void interrupt() {
		Thread.currentThread().interrupt();
	}
}
