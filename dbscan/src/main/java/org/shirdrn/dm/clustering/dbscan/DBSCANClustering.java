package org.shirdrn.dm.clustering.dbscan;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.dm.clustering.common.ClusterPoint;
import org.shirdrn.dm.clustering.common.ClusterPoint2D;
import org.shirdrn.dm.clustering.common.Clustering2D;
import org.shirdrn.dm.clustering.common.ClusteringResult;
import org.shirdrn.dm.clustering.common.NamedThreadFactory;
import org.shirdrn.dm.clustering.common.Point2D;
import org.shirdrn.dm.clustering.common.utils.ClusteringUtils;
import org.shirdrn.dm.clustering.common.utils.FileUtils;
import org.shirdrn.dm.clustering.common.utils.MetricUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * DBSCAN clustering algorithm implementation.
 * 
 * @author yanjun
 */
public class DBSCANClustering extends Clustering2D {

	private static final Log LOG = LogFactory.getLog(DBSCANClustering.class);
	private double eps;
	private int minPts;
	private final ConcurrentMap<Point2D, Set<Point2D>> corePointWithNeighbours = Maps.newConcurrentMap();
	private final Set<Point2D> outliers = Sets.newHashSet();
	private final CountDownLatch latch;
	private final ExecutorService executorService;
	private final BlockingQueue<Point2D> taskQueue;
	private volatile boolean completed = false;
	private int clusterCount;
	private final List<Point2D> allPoints = Lists.newArrayList();
	private File outputFile;
	
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public DBSCANClustering(int minPts, double eps, int parallism) {
		super(parallism);
		Preconditions.checkArgument(minPts > 0, "Required: minPts > 0!");
		this.minPts = minPts;
		this.eps = eps;
		latch = new CountDownLatch(parallism);
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory("CORE"));
		taskQueue = new LinkedBlockingQueue<Point2D>();
		LOG.info("Config: minPts=" + minPts + " ,eps=" + eps + ", parallism=" + parallism);
	}
	
	@Override
	public void clustering() {
		LOG.info("Config: outputFile=" + inputFiles[0] + ", outputFile=" + outputFile);
		// parse sample files
		Preconditions.checkArgument(inputFiles != null, "inputFiles == null");
		FileUtils.read2DPointsFromFiles(allPoints, "[\t,;\\s]+", inputFiles);
		// recognize core points
		try {
			for (int i = 0; i < parallism; i++) {
				CorePointCalculator calculator = new CorePointCalculator();
				executorService.execute(calculator);
				LOG.info("Core point calculator started: " + calculator);
			}
			
			Iterator<Point2D> iter = allPoints.iterator();
			while(iter.hasNext()) {
				Point2D p = iter.next();
				while(!taskQueue.offer(p)) {
					Thread.sleep(10);
				}
				LOG.debug("Added to taskQueue: " + p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				completed = true;
				latch.await();
			} catch (InterruptedException e) { }
			LOG.info("Shutdown executor service: " + executorService);
			executorService.shutdown();
		}
		LOG.info("Point statistics: corePointSize=" + corePointWithNeighbours.keySet().size());
		
		// join connected core points
		LOG.info("Joining connected core points ...");
		final Map<Point2D, Set<Point2D>> clusteringPoints = Maps.newHashMap();
		Set<Point2D> corePoints = Sets.newHashSet(corePointWithNeighbours.keySet());
		while(true) {
			Set<Point2D> set = Sets.newHashSet();
			Iterator<Point2D> iter = corePoints.iterator();
			if(iter.hasNext()) {
				Point2D p = iter.next();
				iter.remove();
				Set<Point2D> connectedPoints = joinConnectedCorePoints(p, corePoints);
				set.addAll(connectedPoints);
				while(!connectedPoints.isEmpty()) {
					connectedPoints = joinConnectedCorePoints(connectedPoints, corePoints);
					set.addAll(connectedPoints);
				}
				clusteringPoints.put(p, set);
			} else {
				break;
			}
		}
		LOG.info("Connected core points computed.");
		
		// process outliers
		Iterator<Point2D> iter = outliers.iterator();
		while(iter.hasNext()) {
			Point2D np = iter.next();
			if(corePointWithNeighbours.containsKey(np)) {
				iter.remove();
			} else {
				for(Set<Point2D> set : corePointWithNeighbours.values()) {
					if(set.contains(np)) {
						iter.remove();
						break;
					}
				}
			}
		}
		
		// generate clustering result
		Iterator<Entry<Point2D, Set<Point2D>>> coreIter = clusteringPoints.entrySet().iterator();
		int clusterId = 0;
		while(coreIter.hasNext()) {
			Entry<Point2D, Set<Point2D>> core = coreIter.next();
			Set<Point2D> set = Sets.newHashSet();
			set.add(core.getKey());
			set.addAll(corePointWithNeighbours.get(core.getKey()));
			for(Point2D p : core.getValue()) {
				set.addAll(core.getValue());
				set.addAll(corePointWithNeighbours.get(p));
			}
			
			Set<ClusterPoint<Point2D>> clusterSet = Sets.newHashSet();
			for(Point2D p : set) {
				clusterSet.add(new ClusterPoint2D(p, clusterId));
			}
			clusteredPoints.put(clusterId, clusterSet);
			++clusterId;
		}
		
		clusterCount = clusteredPoints.size();
		LOG.info("Finished clustering: clusterCount=" + clusterCount + ", outliersCount=" + outliers.size());
	}
	
	private Set<Point2D> joinConnectedCorePoints(Set<Point2D> connectedPoints, Set<Point2D> leftCorePoints) {
		Set<Point2D> set = Sets.newHashSet();
		for(Point2D p1 : connectedPoints) {
			set.addAll(joinConnectedCorePoints(p1, leftCorePoints));
		}
		return set;
	}
	
	private Set<Point2D> joinConnectedCorePoints(Point2D p1, Set<Point2D> leftCorePoints) {
		Set<Point2D> set = Sets.newHashSet();
		for(Point2D p2 : leftCorePoints) {
			double distance = MetricUtils.euclideanDistance(p1, p2);
			if(distance <= eps) {
				// join 2 core points to the same cluster
				set.add(p2);
			}
		}
		// remove connected points
		leftCorePoints.removeAll(set);
		return set;
	}
	
	/**
	 * Compute core point and neighbourhood points.
	 *
	 * @author yanjun
	 */
	private final class CorePointCalculator extends Thread {
		
		private final Log LOG = LogFactory.getLog(CorePointCalculator.class);
		private int processedPoints;
		
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				while(true) {
					while(!taskQueue.isEmpty()) {
						Point2D p1 = taskQueue.poll();
						++processedPoints;
						Set<Point2D> set = Sets.newHashSet();
						Iterator<Point2D> iter = allPoints.iterator();
						while(iter.hasNext()) {
							Point2D p2 = iter.next();
							if(!p2.equals(p1)) {
								double distance = MetricUtils.euclideanDistance(p1, p2);
								// collect a point belonging to the point p1
								if(distance <= eps) {
									set.add(p2);
								}
							}
						}
						// decide whether p1 is core point
						if(set.size() >= minPts) {
							corePointWithNeighbours.put(p1, set);
							LOG.debug("Decide core point: " + p1 + ", set=" + set);
							LOG.info("Core point: " + p1 + ", set.size()=" + set.size());
						} else {
							// here, perhaps a point was wrongly put into outliers set
							// afterwards we should remedy outliers set
							if(!outliers.contains(p1)) {
								outliers.add(p1);
							}
						}
						
					}
					
					Thread.sleep(100);
					
					if(taskQueue.isEmpty() && completed) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				latch.countDown();
				LOG.info("Calculator exit, STAT: [id=" + this + ", processedPoints=" + processedPoints + "]");
			}
		}
	}
	
	public Set<Point2D> getOutliers() {
		return outliers;
	}
	
	// 4 0.004566439044911 F:\gitworkspace\opengis\dbscan\src\main\data\经纬度数据.csv F:\gitworkspace\opengis\dbscan\src\main\data\result1.csv 4
	public static void main(String[] args) {
		int minPts = 4;
		double eps = 0.009566439044911;
		int parallism = 1;
		String inputFile = null;
		String outputFile = null;
		
		StringBuffer sb = new StringBuffer();
		for(String arg : args) {
			sb.append(arg).append(" ");
		}
		
		LOG.info("====================================================");
		LOG.info("Input Args: " + sb.toString());
		LOG.info("====================================================");
		
		if(args.length == 4) {
			try {
				minPts = Integer.parseInt(args[0]);
			} catch (Exception e) {}
			try {
				eps = Integer.parseInt(args[1]);
			} catch (Exception e) {}
			inputFile = args[2];
			outputFile = args[3];
		} else if(args.length == 5) {
			try {
				minPts = Integer.parseInt(args[0]);
			} catch (Exception e) {}
			try {
				eps = Integer.parseInt(args[1]);
			} catch (Exception e) {}
			inputFile = args[2];
			outputFile = args[3];
			try {
				parallism = Integer.parseInt(args[4]);
			} catch (Exception e) {}
		} else {
			System.err.printf("Usage: \n");
			System.err.printf(DBSCANClustering.class.getName() + " <minPts> <eps> <inputFile> <outputFile>[ <parallism>]\n");
			System.exit(1);
		}
		
		DBSCANClustering c = new DBSCANClustering(minPts, eps, parallism);
		c.setInputFiles(new File(inputFile));
//		c.setInputFiles(new File("C:\\Users\\yanjun\\Desktop\\经纬度数据.csv"));
		
		c.setOutputFile(new File(outputFile));
		
		// execute clustering procedure
		c.clustering();
		
//		System.out.println("== Clustered points ==");
		ClusteringResult<Point2D> result = c.getClusteringResult();
//		ClusteringUtils.print2DClusterPoints(result.getClusteredPoints());
		
		LOG.info("Output clustered points: " + c.outputFile);
		ClusteringUtils.output2DClusterPointsToFile(c.outputFile, result.getClusteredPoints());
		LOG.info("Finish outputting, check file: " + c.outputFile);
		
		// print outliers
//		int outliersClusterId = -1;
//		System.out.println("== Outliers ==");
//		for(Point2D p : c.getOutliers()) {
//			System.out.println(p.getX() + "," + p.getY() + "," + outliersClusterId);
//		}
		
		LOG.info("============================================");
		LOG.info("===  [DBSCAN] Clustering Result Summary  ===");
		LOG.info("Input Args    : " + sb.toString());
		LOG.info("Outliers count: " + c.getOutliers().size());
		LOG.info("Cluster count : " + c.getClusteringResult().getClusteredPoints().size());
		LOG.info("Output file: " + c.outputFile);
		LOG.info("============================================");
	}

}
