package com.pbgis.engine.carto;

import com.vividsolutions.jts.geom.Geometry;

public class IdentifyParam {
	private Geometry geometry;
	private IdentifyType identifyType;
	private int tolerance;
	private double resolution;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public IdentifyType getIdentifyType() {
		return identifyType;
	}

	public void setIdentifyType(IdentifyType identifyType) {
		this.identifyType = identifyType;
	}

	public int getTolerance() {
		return tolerance;
	}

	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
	}

	public double getResolution() {
		return resolution;
	}

	public void setResolution(double resolution) {
		this.resolution = resolution;
	}

}
