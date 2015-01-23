package com.gi.giserver.core.service.mapservice;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.gi.giserver.core.i18n.ResourceManager;
import com.gi.giserver.core.service.concurrent.Instance;
import com.gi.giserver.core.service.concurrent.Pool;

public class MapServicePool implements Pool {
	static Logger logger = Logger.getLogger(MapServicePool.class);

	private MapService mapService = null;
	private ConcurrentLinkedQueue<MapServiceInstance> idleInstances = new ConcurrentLinkedQueue<MapServiceInstance>();
	private Vector<MapServiceInstance> runningInstances = new Vector<MapServiceInstance>();
	private int minInstances = 0;
	private int maxInstances = 1;

	public MapServicePool(MapService mapService) throws Exception {
		this.mapService = mapService;

		if (mapService != null) {
			disposeAllInstances();
			idleInstances.clear();
			runningInstances.clear();

			minInstances = mapService.getMapServiceDesc().getMinInstances();
			maxInstances = mapService.getMapServiceDesc().getMaxInstances();
			minInstances = minInstances <= maxInstances ? minInstances
					: maxInstances;
			for (int i = 0; i < minInstances; i++) {
				idleInstances.add(new MapServiceInstance(mapService));
			}
		}
	}

	public synchronized void disposeAllInstances() {
		for (Iterator<MapServiceInstance> itr = idleInstances.iterator(); itr
				.hasNext();) {
			MapServiceInstance instance = itr.next();
			if (instance != null) {
				instance.dispose();
			}
		}

		for (Iterator<MapServiceInstance> itr = runningInstances.iterator(); itr
				.hasNext();) {
			MapServiceInstance instance = itr.next();
			if (instance != null) {
				instance.dispose();
			}
		}

		Runtime r = Runtime.getRuntime();
		r.gc();
	}

	public synchronized void disposeIdleInstaces(int remainIdle) {
		for (Iterator<MapServiceInstance> itr = idleInstances.iterator(); itr
				.hasNext()
				&& idleInstances.size() > remainIdle;) {
			MapServiceInstance instance = itr.next();
			if (instance != null) {
				instance.dispose();
			}
		}

		Runtime r = Runtime.getRuntime();
		r.gc();
	}

	//This method can not be synchronized!
	public MapServiceInstance getIdleInstance() {
		MapServiceInstance result = null;

		try {
			if (idleInstances.size() > 0) {
				// Has idle instances
				result = idleInstances.poll();
				runningInstances.add(result);
				return result;
			} else {
				if (runningInstances.size() < maxInstances) {
					// No idle instances, but we can create new instance
					result = new MapServiceInstance(mapService);
					runningInstances.add(result);
					return result;
				} else {
					// No instance available, we must wait
					wait(1000 * mapService.getMapServiceDesc()
							.getGetTimeout());
				}
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleMapServiceLog()
					.getString("ERROR.GET_IDLE_INSTANCE"), ex);
		}

		return result;
	}

	public synchronized void setInstanceIdle(Instance instance) {
		if (instance instanceof MapServiceInstance) {
			MapServiceInstance mapServiceInstance = (MapServiceInstance) instance;
			if (runningInstances.removeElement(mapServiceInstance)) {
				idleInstances.add(mapServiceInstance);
			}
		}
	}

	public synchronized int getMaxInstanceCount() {
		return this.maxInstances;
	}

	public synchronized int getRunningInstanceCount() {
		return runningInstances.size();
	}

	public synchronized int getTotleInstanceCount() {
		return runningInstances.size() + idleInstances.size();
	}

	public synchronized void doNotifyAll() {
		this.notifyAll();
	}

}
