package com.gi.giengine.geometry;

import com.vividsolutions.jts.geom.Geometry;

public class MeasureEngine {

	public static double[] areas(Geometry[] geometries) {
		double[] result = null;

		if (geometries != null) {
			int count = geometries.length;
			result = new double[count];
			for (int i = 0; i < count; i++) {
				Geometry geo = geometries[i];
				result[i] = area(geo);
			}
		}

		return result;
	}

	public static double area(Geometry geometry) {
		return geometry.getArea();
	}

	public static double[] lengths(Geometry[] geometries) {
		double[] result = null;

		if (geometries != null) {
			int count = geometries.length;
			result = new double[count];
			Geometry geo = null;
			for (int i = 0; i < count; i++) {
				geo = geometries[i];
				result[i] = length(geo);
			}
		}

		return result;
	}

	public static double length(Geometry geometry) {
		return geometry.getLength();
	}
}
