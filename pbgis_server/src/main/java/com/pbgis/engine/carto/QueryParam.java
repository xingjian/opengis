package com.pbgis.engine.carto;

import com.vividsolutions.jts.geom.Geometry;

public class QueryParam {

	private Geometry geometry = null;
	private SpatialFilterType spatialFilterType = null;
	private String where = null;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
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
