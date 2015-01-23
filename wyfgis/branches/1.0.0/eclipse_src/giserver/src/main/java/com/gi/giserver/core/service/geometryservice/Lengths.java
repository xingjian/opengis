package com.gi.giserver.core.service.geometryservice;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.MeasureEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Lengths {
	static Logger logger = Logger.getLogger(Lengths.class);

	public static double[] lengths(Geometry[] geometries) {
		double[] result = null;

		try {
			result = MeasureEngine.lengths(geometries);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LENGTH")
					+ "<" + ToTextUtil.toText(geometries) + ">", ex);
		}

		return result;
	}

	public static double length(Geometry geometry) {
		double result = 0;

		try {
			result = MeasureEngine.length(geometry);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LENGTH")
					+ "<" + ToTextUtil.toText(geometry) + ">", ex);
		}

		return result;
	}

}
