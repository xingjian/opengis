package com.gi.engine.geometry;

import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Describe geometries as a human readable string. The String format is like
 * JSON.
 * 
 * @author wuyf
 * 
 */
public class GeometryToStringUtil {

	public static String toString(Geometry geometry) {
		if (geometry == null) {
			return "NULL";
		} else {
			return geometry.toText();
		}
	}

	public static String toString(Geometry[] geometries) {
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

	public static String toString(List<Geometry> geometries) {
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

}
