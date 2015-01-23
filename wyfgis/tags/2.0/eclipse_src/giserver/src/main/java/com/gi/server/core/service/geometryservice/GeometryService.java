package com.gi.server.core.service.geometryservice;

import org.apache.log4j.Logger;

import com.gi.engine.geometry.GeometryToStringUtil;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.geometry.RelationType;
import com.gi.server.core.i18n.ResourceBundleManager;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class GeometryService {
	static Logger logger = Logger.getLogger(GeometryService.class);

	public static Geometry project(Geometry geometry, String inSR, String outSR) {
		Geometry result = null;

		try {
			result = GeometryToolkit.project(geometry, inSR, outSR);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.PROJECT"));
			sb.append("<");
			sb.append(inSR);
			sb.append("-");
			sb.append(outSR);
			sb.append(":");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static Geometry simplify(Geometry geometry, double tolerance) {
		Geometry result = null;

		try {
			result = GeometryToolkit.simplify(geometry, tolerance);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.SIMPLIFY"));
			sb.append("<");
			sb.append(tolerance);
			sb.append(":");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static Geometry buffer(Geometry geometry, String inSR, String outSR,
			String bufferSR, double distance) {
		Geometry result = null;

		try {
			result = GeometryToolkit.buffer(geometry, inSR, outSR, bufferSR,
					distance);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.BUFFER"));
			sb.append("<");
			sb.append(distance);
			sb.append(":");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static double area(Geometry geometry) {
		double result = Double.NaN;

		try {
			result = geometry.getArea();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.AREA"));
			sb.append("<");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static double length(Geometry geometry) {
		double result = Double.NaN;

		try {
			result = geometry.getLength();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LENGTH"));
			sb.append("<");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static boolean relation(Geometry geometry1, Geometry geometry2,
			RelationType relation, String relationPattern) {
		boolean result = false;

		try {
			result = GeometryToolkit.relation(geometry1, geometry2, relation,
					relationPattern);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.RELATION"));
			sb.append("<");
			sb.append(relation.toString());
			sb.append("-");
			sb.append(relationPattern.toString());
			sb.append(":");
			sb.append(GeometryToStringUtil.toString(geometry1));
			sb.append("-");
			sb.append(GeometryToStringUtil.toString(geometry2));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static Point labelPoint(Geometry geometry) {
		Point result = null;

		try {
			result = geometry.getInteriorPoint();
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LABELPOINT"));
			sb.append("<");
			sb.append(GeometryToStringUtil.toString(geometry));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static double distance(Geometry geometry1, Geometry geometry2) {
		double result = Double.NaN;

		try {
			result = geometry1.distance(geometry2);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.DISTANCE"));
			sb.append("<");
			sb.append(GeometryToStringUtil.toString(geometry1));
			sb.append("-");
			sb.append(GeometryToStringUtil.toString(geometry2));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

	public static double trimExtend(Geometry geometry1, Geometry geometry2) {
		double result = Double.NaN;

		try {
			result = geometry1.distance(geometry2);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.TRIMEXTEND"));
			sb.append("<");
			sb.append(GeometryToStringUtil.toString(geometry1));
			sb.append("-");
			sb.append(GeometryToStringUtil.toString(geometry2));
			sb.append(">");
			logger.error(sb.toString(), e);
		}

		return result;
	}

}
