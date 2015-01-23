package com.gi.engine.geometry;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.graph.util.geom.GeometryUtil;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.vividsolutions.jts.densify.Densifier;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class GeometryToolkit {

	public static Geometry buffer(Geometry geometry, String inSR, String outSR,
			String bufferSR, double distance) throws Exception {

		Geometry geo = geometry;
		geo = project(geo, inSR, bufferSR);
		geo = geo.buffer(distance);
		geo = project(geo, bufferSR, outSR);

		return geo;
	}

	public static Geometry project(Geometry geometry, String inSR, String outSR)
			throws Exception {
		Geometry result = null;

		if (!outSR.equals(inSR)) {
			MathTransform transform = SpatialReferenceManager.getMathTransform(
					inSR, outSR, true);
			if (transform != null) {
				result = JTS.transform(geometry, transform);
			}
		} else {
			result = geometry;
		}

		return result;
	}

	public static Envelope project(Envelope env, String inSR, String outSR)
			throws Exception {
		Envelope result = null;

		if (!outSR.equals(inSR)) {
			MathTransform transform = SpatialReferenceManager.getMathTransform(
					inSR, outSR, true);
			if (transform != null) {
				result = JTS.transform(env, transform);
			}
		} else {
			result = env;
		}

		return result;
	}

	public static ReferencedEnvelope project(ReferencedEnvelope env,
			String targetSR) throws Exception {
		ReferencedEnvelope result = env;

		CoordinateReferenceSystem targetCRS = SpatialReferenceManager
				.wkidToCRS(targetSR, true);
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

	public static Geometry simplify(Geometry geometry, double tolerance) {
		Geometry geo = geometry;
		/*
		 * if (!geo.isValid()) { if (geo.getClass().equals(Polygon.class) ||
		 * geo.getClass().equals(MultiPolygon.class)) { geo =
		 * DouglasPeuckerSimplifier.simplify(geo, tolerance); } }
		 */

		if (!geo.isValid()) {
			geo = DouglasPeuckerSimplifier.simplify(geo, tolerance);
		}

		return geo;
	}

	public static Geometry densify(Geometry geometry, double distanceTolerance)
			throws Exception {
		Geometry geo = Densifier.densify(geometry, distanceTolerance);
		return geo;
	}

	/**
	 * @param geometry1
	 * @param geometry2
	 * @param relationType
	 * @param relationPattern
	 *            like FFFTTT***, take effect when relation parameter is
	 *            Relation.RELATION
	 * @return
	 */
	public static boolean relation(Geometry geometry1, Geometry geometry2,
			RelationType relationType, String relationPattern) {
		boolean result = false;

		// Determine whether geometry1 and geometry2 satisfy the given
		// relation
		if (relationType.equals(RelationType.CROSS)) {
			result = geometry1.crosses(geometry2);
		} else if (relationType.equals(RelationType.DISJOINT)) {
			result = geometry1.disjoint(geometry2);
		} else if (relationType.equals(RelationType.IN)) {
			result = geometry1.within(geometry2)
					&& !(geometry1.coveredBy(geometry2));
		} else if (relationType.equals(RelationType.INTERIOR_INTERSECTION)) {
			result = geometry1.intersects(geometry2)
					&& !(geometry1.touches(geometry2));
		} else if (relationType.equals(RelationType.INTERSECTION)) {
			result = geometry1.intersects(geometry2);
		} else if (relationType.equals(RelationType.LINE_COINCIDENCE)) {
			result = geometry1.intersection(geometry2) instanceof LineString;
		} else if (relationType.equals(RelationType.LINE_TOUCH)) {
			result = geometry1.touches(geometry2)
					&& geometry1.intersection(geometry2) instanceof LineString;
		} else if (relationType.equals(RelationType.OVERLAP)) {
			result = geometry1.overlaps(geometry2);
		} else if (relationType.equals(RelationType.POINT_TOUCH)) {
			result = geometry1.touches(geometry2)
					&& geometry1.intersection(geometry2) instanceof Point;
		} else if (relationType.equals(RelationType.RELATION)) {
			result = geometry1.relate(geometry2, relationPattern);
		} else if (relationType.equals(RelationType.TOUCH)) {
			result = geometry1.touches(geometry2);
		} else if (relationType.equals(RelationType.WITHIN)) {
			result = geometry1.within(geometry2);
		}

		return result;
	}

	public static ArrayList<LineString> trimExtend(LineString source,
			List<LineString> trimExtendToList) {
		ArrayList<LineString> result = new ArrayList<LineString>();

		// If source does not intersects any trimExtendTo line, trim
		boolean intersects = false;
		for (Iterator<LineString> itr = trimExtendToList.iterator(); itr
				.hasNext();) {
			LineString l = itr.next();
			if (source.intersects(l)) {
				intersects = true;
				break;
			}
		}

		if (intersects) {
			// Trim

			Geometry rest = source;
			for (Iterator<LineString> itr = trimExtendToList.iterator(); itr
					.hasNext();) {
				LineString trimExtendTo = itr.next();
				if (trimExtendTo.getStartPoint().equals(
						trimExtendTo.getEndPoint())) {
					// If trimExtendTo has the same start and end vertex, make
					// it as a polygon
					LinearRing shell = GeometryUtil.gf().createLinearRing(
							trimExtendTo.getCoordinates());
					Polygon pg = GeometryUtil.gf().createPolygon(shell, null);
					rest = rest.difference(pg);
				} else {
					Geometry explodedLines = source.difference(trimExtendTo);
					if (explodedLines instanceof MultiLineString) {
						MultiLineString multiLines = (MultiLineString) explodedLines;
						for (int i = 0, count = multiLines.getNumGeometries(); i < count; i++) {
							LineString l = (LineString) multiLines
									.getGeometryN(i);
							if (i % 2 != 0) {
								rest = rest.difference(l);
							}
						}
					}
				}
			}

			if (rest instanceof LineString) {
				result.add((LineString) rest);
			} else if (rest instanceof MultiLineString) {
				MultiLineString ml = (MultiLineString) rest;
				for (int i = 0, count = ml.getNumGeometries(); i < count; i++) {
					LineString l = (LineString) ml.getGeometryN(i);
					result.add(l);
				}
			}
		} else {
			// Extend
			ArrayList<Coordinate> lineCoords = new ArrayList<Coordinate>();
			for (int i = 0, count = source.getCoordinates().length; i < count; i++) {
				lineCoords.add(source.getCoordinateN(i));
			}

			// Calculate a maximum extend distance
			Envelope env = source.getEnvelopeInternal();
			double xmin = env.getMinX();
			double ymin = env.getMinY();
			double xmax = env.getMaxX();
			double ymax = env.getMaxY();
			for (Iterator<LineString> itrTrimExtend = trimExtendToList
					.iterator(); itrTrimExtend.hasNext();) {
				LineString l = itrTrimExtend.next();
				env = l.getEnvelopeInternal();
				if (env.getMinX() < xmin) {
					xmin = env.getMinX();
				}
				if (env.getMinY() < ymin) {
					ymin = env.getMinY();
				}
				if (env.getMaxX() > xmax) {
					xmax = env.getMaxX();
				}
				if (env.getMaxY() > ymax) {
					ymax = env.getMaxY();
				}
			}
			double max = xmax - xmin + ymax - ymin;

			// Extend start vertex
			Point ptStart = source.getStartPoint();
			Point ptStart2 = source.getPointN(1);
			// Calculate an extend point
			double aStart = Math.atan((ptStart2.getY() - ptStart.getY())
					/ (ptStart2.getX() - ptStart.getX()));
			double xStart = ptStart.getX() + max
					* (ptStart2.getX() > ptStart.getX() ? -1 : 1)
					* Math.abs(Math.cos(aStart));
			double yStart = ptStart.getY() + max
					* (ptStart2.getY() > ptStart.getY() ? -1 : 1)
					* Math.abs(Math.sin(aStart));
			Point ptStart3 = GeometryUtil.gf().createPoint(
					new Coordinate(xStart, yStart));
			LineString lStart = GeometryUtil.gf().createLineString(
					new Coordinate[] { ptStart.getCoordinate(),
							ptStart3.getCoordinate() });

			Point nearestCrossStart = null;
			double nearestStart = Double.MAX_VALUE;
			for (Iterator<LineString> itrTrimExtend = trimExtendToList
					.iterator(); itrTrimExtend.hasNext();) {
				LineString l = itrTrimExtend.next();
				Geometry crossPoints = l.intersection(lStart);
				if (crossPoints != null) {
					ArrayList<Point> pts = new ArrayList<Point>();
					if (crossPoints instanceof Point) {
						pts.add((Point) crossPoints);
					} else if (crossPoints instanceof MultiPoint) {
						MultiPoint mp = (MultiPoint) crossPoints;
						for (int i = 0, count = mp.getNumGeometries(); i < count; i++) {
							Point pt = (Point) mp.getGeometryN(i);
							pts.add(pt);
						}
					}
					for (Iterator<Point> itr = pts.iterator(); itr.hasNext();) {
						Point pt = itr.next();
						double d = pt.distance(ptStart);
						if (d < nearestStart) {
							nearestStart = d;
							nearestCrossStart = pt;
						}
					}
				}
			}
			if (nearestCrossStart != null) {
				lineCoords.add(0, nearestCrossStart.getCoordinate());
			}

			// Extend end vertex
			Point ptEnd = source.getEndPoint();
			Point ptEnd2 = source.getPointN(source.getNumPoints() - 2);
			// Calculate an extend point
			double aEnd = Math.atan((ptEnd2.getY() - ptEnd.getY())
					/ (ptEnd2.getX() - ptEnd.getX()));
			double xEnd = ptEnd.getX() + max
					* (ptEnd2.getX() > ptEnd.getX() ? -1 : 1)
					* Math.abs(Math.cos(aEnd));
			double yEnd = ptEnd.getY() + max
					* (ptEnd2.getY() > ptEnd.getY() ? -1 : 1)
					* Math.abs(Math.sin(aEnd));
			Point ptEnd3 = GeometryUtil.gf().createPoint(
					new Coordinate(xEnd, yEnd));
			LineString lEnd = GeometryUtil.gf().createLineString(
					new Coordinate[] { ptEnd.getCoordinate(),
							ptEnd3.getCoordinate() });

			Point nearestCrossEnd = null;
			double nearestEnd = Double.MAX_VALUE;
			for (Iterator<LineString> itrTrimExtend = trimExtendToList
					.iterator(); itrTrimExtend.hasNext();) {
				LineString l = itrTrimExtend.next();
				Geometry crossPoints = l.intersection(lEnd);
				if (crossPoints != null) {
					ArrayList<Point> pts = new ArrayList<Point>();
					if (crossPoints instanceof Point) {
						pts.add((Point) crossPoints);
					} else if (crossPoints instanceof MultiPoint) {
						MultiPoint mp = (MultiPoint) crossPoints;
						for (int i = 0, count = mp.getNumGeometries(); i < count; i++) {
							Point pt = (Point) mp.getGeometryN(i);
							pts.add(pt);
						}
					}
					for (Iterator<Point> itr = pts.iterator(); itr.hasNext();) {
						Point pt = itr.next();
						double d = pt.distance(ptEnd);
						if (d < nearestEnd) {
							nearestEnd = d;
							nearestCrossEnd = pt;
						}
					}
				}
			}
			if (nearestCrossEnd != null) {
				lineCoords.add(nearestCrossEnd.getCoordinate());
			}

			Coordinate[] coords = new Coordinate[lineCoords.size()];
			lineCoords.toArray(coords);
			result.add(GeometryUtil.gf().createLineString(coords));
		}

		return result;
	}

	public static Geometry reshape(Geometry geometry, LineString reshaper) {
		Geometry result = geometry;

		Geometry maxGeometry = null;
		int maxPoints = Integer.MIN_VALUE;
		List<Geometry> cut = cut(geometry,reshaper);
		for (Iterator<Geometry> itr=cut.iterator(); itr.hasNext();) {
			Geometry geo = itr.next();
			if (!geo.isEmpty()) {
				int numPoints = geo.getNumPoints();
				if (numPoints > maxPoints) {
					maxGeometry = geo;
					maxPoints = numPoints;
				}
			}
		}

		if (maxGeometry != null) {
			result = maxGeometry;
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Geometry> cut(Geometry geometry, LineString cutter) {
		List<Geometry> result = new ArrayList<Geometry>();

		Geometry split = null;// Split geometry
		if (geometry.getGeometryType().equals(Polygon.class.getSimpleName())) {
			Polygon pg = (Polygon) geometry;

			LineString extRing = pg.getExteriorRing();

			// Get solid polygon(no hole)
			LinearRing solidRing = GeometryUtil.gf().createLinearRing(
					extRing.getCoordinates());
			Polygon solidPg = GeometryUtil.gf().createPolygon(solidRing, null);
			Geometry solidCutter = cutter.intersection(solidPg);
			Geometry solidBounds = solidRing.difference(cutter);

			Polygonizer pgzer = new Polygonizer();
			for (int i = 0, count = solidBounds.getNumGeometries(); i < count; i++) {
				pgzer.add(solidBounds.getGeometryN(i));
			}
			pgzer.add(solidCutter);
			ArrayList<Geometry> pgs = new ArrayList<Geometry>();
			for (Iterator itr = pgzer.getPolygons().iterator(); itr.hasNext();) {
				Polygon p = (Polygon) itr.next();
				pgs.add(p.intersection(pg));
			}
			split = GeometryUtil.gf().buildGeometry(pgs);
		} else {
			split = geometry.difference(cutter);
		}

		if (split != null) {
			for (int i = 0, count = split.getNumGeometries(); i < count; i++) {
				Geometry part = split.getGeometryN(i);
				if (!part.isEmpty()) {
					result.add(part);
				}
			}
		}

		return result;
	}

	public static Geometry generalize(Geometry geometry, double tolerance) {
		return TopologyPreservingSimplifier.simplify(geometry, tolerance);
	}


}
