package com.gi.server.core.service.geometryservice;

public class GeometryIndexCouple {
	private int geometry1Index;
	private int geometry2Index;

	public GeometryIndexCouple(int geometry1Index, int geometry2Index) {
		this.geometry1Index = geometry1Index;
		this.geometry2Index = geometry2Index;
	}

	public int getGeometry1Index() {
		return geometry1Index;
	}

	public void setGeometry1Index(int geometry1Index) {
		this.geometry1Index = geometry1Index;
	}

	public int getGeometry2Index() {
		return geometry2Index;
	}

	public void setGeometry2Index(int geometry2Index) {
		this.geometry2Index = geometry2Index;
	}
}
