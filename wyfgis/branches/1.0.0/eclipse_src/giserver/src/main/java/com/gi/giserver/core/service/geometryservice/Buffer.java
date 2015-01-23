package com.gi.giserver.core.service.geometryservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gi.giengine.geometry.BufferEngine;
import com.gi.giengine.util.ToTextUtil;
import com.gi.giserver.core.i18n.ResourceManager;
import com.vividsolutions.jts.geom.Geometry;

public class Buffer {
	static Logger logger = Logger.getLogger(Buffer.class);

	public static List<Geometry> buffers(List<Geometry> geometries,
			String inSR, String outSR, String bufferSR, double[] distances) {
		List<Geometry> result = new ArrayList<Geometry>();

		try {
			result = BufferEngine.buffers(geometries, inSR, outSR, bufferSR,
					distances);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.BUFFER")
					+ "<"
					+ ToTextUtil.toText(distances)
					+ ":"
					+ ToTextUtil.toText(geometries) + ">", ex);
		}

		return result;
	}

	public static Geometry buffer(Geometry geometry, String inSR, String outSR,
			String bufferSR, double distance) {
		Geometry result = null;

		try {
			result = BufferEngine.buffer(geometry, inSR, outSR, bufferSR,
					distance);
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleGeometryServiceLog()
					.getString("ERROR.BUFFER")
					+ "<" + distance + ":" + ToTextUtil.toText(geometry) + ">",
					ex);
		}

		return result;
	}

}
