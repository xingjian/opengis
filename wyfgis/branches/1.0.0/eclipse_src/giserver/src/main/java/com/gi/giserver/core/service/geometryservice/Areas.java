package com.gi.giserver.core.service.geometryservice;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.MeasureEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Areas {
	static Logger logger = Logger.getLogger(Areas.class);

	public static double[] areas(Geometry[] geometries) {
		double[] result = null;

		try {
			result = MeasureEngine.areas(geometries);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.AREA")
					+ "<" + ToTextUtil.toText(geometries) + ">", ex);
		}

		return result;
	}

	public static double area(Geometry geometry) {
		double result = 0;

		try {
			result = MeasureEngine.area(geometry);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.AREA")
					+ "<" + ToTextUtil.toText(geometry) + ">", ex);
		}

		return result;
	}

}
