package com.gi.giserver.core.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.ProjectEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Project {
	static Logger logger = Logger.getLogger(Project.class);

	public static List<Geometry> projects(List<Geometry> geometries,
			String inSR, String outSR) {
		List<Geometry> result = new ArrayList<Geometry>();

		try {
			result = ProjectEngine.projects(geometries, inSR, outSR);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.PROJECT")
					+ "<"
					+ inSR
					+ "-"
					+ outSR
					+ ":"
					+ ToTextUtil.toText(geometries) + ">", ex);
		}

		return result;
	}

	public static Geometry project(Geometry geometry, String inSR, String outSR) {
		Geometry result = null;

		try {
			result = ProjectEngine.project(geometry, inSR, outSR);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.PROJECT")
					+ "<"
					+ inSR
					+ "-"
					+ outSR
					+ ":"
					+ ToTextUtil.toText(geometry) + ">", ex);
		}

		return result;
	}
}
