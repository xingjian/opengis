package com.gi.giserver.core.service.geometryservice;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.LabelPointEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LabelPoints {
	static Logger logger = Logger.getLogger(LabelPoints.class);

	public static Point[] labelPoints(Geometry[] geometries) {
		Point[] result = null;

		try {
			result = LabelPointEngine.labelPoints(geometries);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LABELPOINT")
					+ "<" + ToTextUtil.toText(geometries) + ">", ex);
		}

		return result;
	}

	public static Point labelPoint(Geometry geometry) {
		Point result = null;

		try {
			result = LabelPointEngine.labelPoint(geometry);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.LABELPOINT")
					+ "<" + ToTextUtil.toText(geometry) + ">", ex);
		}

		return result;
	}

}
