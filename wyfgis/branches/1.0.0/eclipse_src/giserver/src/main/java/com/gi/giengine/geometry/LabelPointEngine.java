package com.gi.giengine.geometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LabelPointEngine {

	public static Point[] labelPoints(Geometry[] geometries) {
		Point[] result = null;

		if (geometries != null) {
			int count = geometries.length;
			result = new Point[count];
			Geometry geo = null;
			for (int i = 0; i < count; i++) {
				geo = geometries[i];
				result[i] = labelPoint(geo);
			}
		}

		return result;
	}

	public static Point labelPoint(Geometry geometry) {
		return geometry.getInteriorPoint();
	}
}
