package com.gi.giengine.util;

import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public class ToTextUtil {

	public static String toText(Geometry geometry) {
		if (geometry == null) {
			return "NULL";
		} else {
			return geometry.toText();
		}
	}

	public static String toText(Geometry[] geometries) {
		if (geometries == null) {
			return "NULL";
		} else {
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			int count = geometries.length;
			for (int i = 0; i < count; i++) {
				sb.append("{");
				sb.append(geometries[i].toText());
				sb.append("}");
			}
			sb.append("]");

			return sb.toString();
		}
	}

	public static String toText(List<Geometry> geometries) {
		if (geometries == null) {
			return "NULL";
		} else {
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (Iterator<Geometry> itr = geometries.iterator(); itr.hasNext();) {
				Geometry geometry = itr.next();
				sb.append("{");
				sb.append(geometry.toText());
				sb.append("}");
			}
			sb.append("]");

			return sb.toString();
		}
	}

	public static String toText(double[] objs) {
		if (objs == null) {
			return "NULL";
		} else {
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			int count = objs.length;
			for (int i = 0; i < count; i++) {
				sb.append("{");
				sb.append(objs[i]);
				sb.append("}");
			}
			sb.append("]");

			return sb.toString();
		}
	}

}
