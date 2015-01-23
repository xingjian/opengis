package com.gi.giengine.geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

public class SimplifyEngine {

	public static List<Geometry> simplifies(List<Geometry> geometries,
			double tolerance) {
		List<Geometry> result = new ArrayList<Geometry>();

		if (geometries != null) {
			Iterator<Geometry> it = geometries.iterator();
			Geometry geo = null;
			while (it.hasNext()) {
				geo = it.next();
				geo = simplify(geo, tolerance);
				result.add(geo);
			}
		}

		return result;
	}

	public static Geometry simplify(Geometry geometry, double tolerance) {
		Geometry geo = geometry;
		if (!geo.isValid()) {
			if (geo.getClass().equals(Polygon.class)
					|| geo.getClass().equals(MultiPolygon.class)) {
				geo = DouglasPeuckerSimplifier.simplify(geo, tolerance);
			}
		}

		return geo;
	}
}
