package org.shirdrn.dm.clustering.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.shirdrn.dm.clustering.common.ClusterPoint;
import org.shirdrn.dm.clustering.common.Point2D;

public class ClusteringUtils {

	public static void print2DClusterPoints(Map<Integer, Set<ClusterPoint<Point2D>>> clusterPoints) {
		Iterator<Entry<Integer, Set<ClusterPoint<Point2D>>>> iter = clusterPoints.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<Integer, Set<ClusterPoint<Point2D>>> entry = iter.next();
			int clusterId = entry.getKey();
			for(ClusterPoint<Point2D> cp : entry.getValue()) {
				System.out.println(cp.getPoint().getX() + "," + cp.getPoint().getY() + "," + clusterId);
			}
		}
	}
	
	public static void output2DClusterPointsToFile(File outputFile, Map<Integer, Set<ClusterPoint<Point2D>>> clusterPoints) {
		FileOutputStream fos = null; 
        OutputStreamWriter osw = null; 
        try {
			fos = new FileOutputStream(outputFile.getAbsolutePath());
			osw = new OutputStreamWriter(fos, "UTF-8");
			
			// write header
			osw.write("ID,ID,O_LON,O_LAT,CLUSTER_LABEL");
			osw.write("\n");
			
			// write body content
			Iterator<Entry<Integer, Set<ClusterPoint<Point2D>>>> iter = clusterPoints.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Integer, Set<ClusterPoint<Point2D>>> entry = iter.next();
				int clusterId = entry.getKey();
				String clusterName = "cluster" + clusterId;
				for(ClusterPoint<Point2D> cp : entry.getValue()) {
					osw.write(cp.getPoint().getId() + "," + cp.getPoint().getId() + "," + cp.getPoint().getX() + "," + cp.getPoint().getY() + "," + clusterName);
					osw.write("\n");
				}
				osw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(osw != null) {
				try {
					fos.close();
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
