package org.shirdrn.dm.clustering.common;


public class ClusterPoint2D implements ClusterPoint<Point2D> {

	private int clusterId;
	private final Point2D point;
	
	public ClusterPoint2D(Point2D point, int clusterId) {
		this.point = point;
		this.clusterId = clusterId;
	}
	
	@Override
	public Point2D getPoint() {
		return point;
	}

	@Override
	public int getClusterId() {
		return clusterId;
	}

	@Override
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

}
