package com.gi.giengine.map.param;

import com.gi.giengine.map.query.SpatialFilterType;
import com.vividsolutions.jts.geom.Geometry;

public class QueryParam {

	private Geometry geometry = null;
	private String inSR = null;
	private SpatialFilterType spatialFilterType = null;
	private String where = null;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getInSR() {
		return inSR;
	}

	public void setInSR(String inSR) {
		this.inSR = inSR;
	}

	public SpatialFilterType getSpatialFilterType() {
		return spatialFilterType;
	}

	public void setSpatialFilterType(SpatialFilterType spatialFilterType) {
		this.spatialFilterType = spatialFilterType;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

}
