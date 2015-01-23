package com.gi.giserver.core.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.SimplifyEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Simplify {
	static Logger logger = Logger.getLogger(Simplify.class);

	public static List<Geometry> simplifies(List<Geometry> geometries,
			double tolerance) {
		List<Geometry> result = new ArrayList<Geometry>();

		try {
			result = SimplifyEngine.simplifies(geometries, tolerance);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.SIMPLIFY")
					+ "<"
					+ tolerance
					+ ":"
					+ ToTextUtil.toText(geometries)
					+ ">", ex);
		}

		return result;
	}

	public static Geometry simplify(Geometry geometry, double tolerance) {
		Geometry result = null;

		try {
			result = SimplifyEngine.simplify(geometry, tolerance);
		} catch (Exception ex) {
			logger.error(
					ResourceManager.getResourceBundleGeometryServiceLog()
							.getString("ERROR.SIMPLIFY")
							+ "<"
							+ tolerance
							+ ":"
							+ ToTextUtil.toText(geometry) + ">", ex);
		}

		return result;
	}

}
