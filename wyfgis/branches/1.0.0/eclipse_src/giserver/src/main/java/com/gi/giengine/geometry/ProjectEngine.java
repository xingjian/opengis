package com.gi.giengine.geometry;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.gi.giengine.sr.SpatialReferenceEngine;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ProjectEngine {

	public static List<Geometry> projects(List<Geometry> geometries,
			String inSR, String outSR) throws Exception {
		List<Geometry> result = new ArrayList<Geometry>();

		if (geometries != null) {
			Iterator<Geometry> it = geometries.iterator();
			while (it.hasNext()) {
				Geometry geo = it.next();
				geo = project(geo, inSR, outSR);
				result.add(geo);
			}
		}

		return result;
	}

	public static Geometry project(Geometry geometry, String inSR, String outSR)
			throws Exception {
		Geometry result = null;

		if (!outSR.equals(inSR)) {
			MathTransform transform = SpatialReferenceEngine.getMathTransform(
					inSR, outSR, true);
			if (transform != null) {
				result = JTS.transform(geometry, transform);
			}
		}

		return result;
	}

	public static Envelope projectEnvelope(Envelope env, String inSR,
			String outSR) throws Exception {
		Envelope result = env;

		if (!outSR.equals(inSR)) {
			MathTransform transform = SpatialReferenceEngine.getMathTransform(
					inSR, outSR, true);
			if (transform != null) {
				result = JTS.transform(env, transform);
			}
		}

		return result;
	}

	public static ReferencedEnvelope projectReferencedEnvelope(
			ReferencedEnvelope env, String targetSR) throws Exception {
		ReferencedEnvelope result = env;

		CoordinateReferenceSystem targetCRS = SpatialReferenceEngine.wkidToCRS(
				targetSR, true);
		if (targetCRS != null) {
			result = env.transform(targetCRS, true);
			GeneralEnvelope maxEnv = (GeneralEnvelope) CRS
					.getEnvelope(targetCRS);
			if (maxEnv != null) {
				Rectangle2D maxRec = maxEnv.toRectangle2D();
				ReferencedEnvelope maxTargetEnv = new ReferencedEnvelope(maxRec
						.getMinX(), maxRec.getMaxX(), maxRec.getMinY(), maxRec
						.getMaxY(), targetCRS);
				CoordinateReferenceSystem sourceCRS = env
						.getCoordinateReferenceSystem();
				ReferencedEnvelope maxSourceEnv = maxTargetEnv.transform(
						sourceCRS, true);
				double xmin = env.getMinX() > maxSourceEnv.getMinX() ? env
						.getMinX() : maxSourceEnv.getMinX();
				double xmax = env.getMaxX() < maxSourceEnv.getMaxX() ? env
						.getMaxX() : maxSourceEnv.getMaxX();
				double ymin = env.getMinY() > maxSourceEnv.getMinY() ? env
						.getMinY() : maxSourceEnv.getMinY();
				double ymax = env.getMaxY() < maxSourceEnv.getMaxY() ? env
						.getMaxY() : maxSourceEnv.getMaxY();
				env = new ReferencedEnvelope(xmin, xmax, ymin, ymax, sourceCRS);
				result = env.transform(targetCRS, true);
			}
		}

		return result;
	}
}
