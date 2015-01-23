package com.gi.giserver.core.util.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.type.GeometryType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author wuyf Utility for ESRI JSON geometry object
 * 
 */
public class EsriJsonGeometryUtil {
	static Logger logger = Logger.getLogger(EsriJsonGeometryUtil.class);

	/**
	 * @param json
	 *            ESRI JSON geometry string
	 * @return Geometry
	 */
	static public Geometry json2Geometry(String json) {
		Geometry geo = null;

		try {
			if (json != null && !"".equals(json.trim())) {

				JSONObject obj = new JSONObject(json);
				GeometryFactory geoFactory = new GeometryFactory();

				// Point
				if (!obj.isNull("x") && !obj.isNull("y")) {
					double x = obj.getDouble("x");
					double y = obj.getDouble("y");
					geo = geoFactory.createPoint(new Coordinate(x, y));
				}

				// Polyline
				else if (!obj.isNull("paths")) {
					JSONArray paths = obj.getJSONArray("paths");

					int lsCount = paths.length();
					LineString[] lns = new LineString[lsCount];
					for (int i = 0; i < lsCount; i++) {
						JSONArray path = paths.getJSONArray(i);

						int ptCount = path.length();
						Coordinate[] pts = new Coordinate[ptCount];
						for (int j = 0; j < ptCount; j++) {
							JSONArray pt = path.getJSONArray(j);
							double x = pt.getDouble(0);
							double y = pt.getDouble(1);
							pts[j] = new Coordinate(x, y);
						}
						lns[i] = geoFactory.createLineString(pts);
					}
					geo = geoFactory.createMultiLineString(lns);
				}

				// Polygon
				else if (!obj.isNull("rings")) {
					JSONArray rings = obj.getJSONArray("rings");

					int lrCount = rings.length();
					Polygon[] pgs = new Polygon[lrCount];
					for (int i = 0; i < lrCount; i++) {
						JSONArray ring = rings.getJSONArray(i);

						int ptCount = ring.length();
						Coordinate[] pts = new Coordinate[ptCount];
						for (int j = 0; j < ptCount; j++) {
							JSONArray pt = ring.getJSONArray(j);
							double x = pt.getDouble(0);
							double y = pt.getDouble(1);
							pts[j] = new Coordinate(x, y);
						}
						LinearRing lr = geoFactory.createLinearRing(pts);
						pgs[i] = geoFactory.createPolygon(lr, null);
					}
					geo = geoFactory.createMultiPolygon(pgs);
				}

				// MultiPoint
				else if (!obj.isNull("points")) {
					JSONArray points = obj.getJSONArray("points");

					int ptCount = points.length();
					Coordinate[] pts = new Coordinate[ptCount];
					for (int i = 0; i < ptCount; i++) {
						JSONArray pt = points.getJSONArray(i);
						double x = pt.getDouble(0);
						double y = pt.getDouble(1);
						pts[i] = new Coordinate(x, y);
					}
					geo = geoFactory.createMultiPoint(pts);
				}

				// Envelope
				else if (!obj.isNull("xmin") && !obj.isNull("ymin") && !obj.isNull("xmax") && !obj.isNull("ymax")) {
					double xmin = obj.getDouble("xmin");
					double ymin = obj.getDouble("ymin");
					double xmax = obj.getDouble("xmax");
					double ymax = obj.getDouble("ymax");
					Coordinate[] coordinates = new Coordinate[5];
					coordinates[0] = new Coordinate(xmin, ymin);
					coordinates[1] = new Coordinate(xmin, ymax);
					coordinates[2] = new Coordinate(xmax, ymax);
					coordinates[3] = new Coordinate(xmax, ymin);
					coordinates[4] = new Coordinate(xmin, ymin);
					LinearRing shell = geoFactory.createLinearRing(coordinates);
					geo = geoFactory.createPolygon(shell, null);
				}

				// SpatialReference
				int wkid = EsriJsonUtil.getSpatialReference(obj);
				if (wkid > 0) {
					geo.setSRID(wkid);
				}
			}
		} catch (Exception ex) {
			logger.error("Parser json geometry '" + json + "' error.", ex);
		}

		return geo;
	}

	/**
	 * @param geometries
	 *            ESRI JSON geometries string
	 * @return Geometry List
	 */
	static public List<Geometry> json2Geometries(String geometries) {
		if (geometries == null) {
			return null;
		}

		List<Geometry> geometriesList = new ArrayList<Geometry>();
		GeometryFactory geoFactory = new GeometryFactory();
		Geometry geo = null;

		if (!geometries.startsWith("{")) {

			if (geometries.startsWith("[")) {
				// Simple array like "[......]"
				try {
					JSONArray geos = new JSONArray(geometries);
					for (int i = 0; i < geos.length(); i++) {
						geo = EsriJsonGeometryUtil.json2Geometry(geos.getString(i));
						geometriesList.add(geo);
					}
				} catch (Exception ex) {
					logger.error("Parser simple array json geometries '" + geometries + "' error.", ex);
				}
			} else {
				// Simple points like "-104.53, 34.74, -63.53, 10.23"
				try {
					String[] strs = geometries.split(",");
					for (int i = 0; i < strs.length / 2; i++) {
						double x = Double.parseDouble(strs[2 * i]);
						double y = Double.parseDouble(strs[2 * i + 1]);
						geo = geoFactory.createPoint(new Coordinate(x, y));
						geometriesList.add(geo);
					}
				} catch (Exception ex) {
					logger.error("Parser simple json points '" + geometries + "' error.", ex);
				}
			}
		} else {
			try {
				JSONObject obj = new JSONObject(geometries);
				JSONArray geos = null;
				if (!obj.isNull("url")) {
					// TO DO
					// URL based geometries like
					// ""http://myserver/mygeometries/afile.txt""
				}
				if (!obj.isNull("geometries")) {
					// Normal ESRI JSON geometries
					geos = obj.getJSONArray("geometries");
					for (int i = 0; i < geos.length(); i++) {
						geo = EsriJsonGeometryUtil.json2Geometry(geos.getString(i));
						geometriesList.add(geo);
					}
				}
			} catch (Exception ex) {
				logger.error("Parser json geometries '" + geometries + "' error.", ex);
			}
		}

		return geometriesList;
	}

	static public JSONObject geometry2JSON(Geometry geometry) {
		if (geometry == null) {
			return null;
		}

		JSONObject obj = new JSONObject();

		try {
			JSONArray arrayTemp = null;
			JSONArray arrayTemp2 = null;

			// Point
			if (geometry.getClass().equals(Point.class)) {
				Point pt = (Point) geometry;
				obj.put("x", pt.getX());
				obj.put("y", pt.getY());
			}

			// Polyline
			else if (geometry.getClass().equals(LineString.class)) {
				LineString ls = (LineString) geometry;
				Coordinate[] coords = ls.getCoordinates();
				arrayTemp = new JSONArray();
				for (int i = 0; i < coords.length; i++) {
					Coordinate coord = coords[i];
					arrayTemp2 = new JSONArray();
					arrayTemp2.put(coord.x);
					arrayTemp2.put(coord.y);
					arrayTemp.put(arrayTemp2);
				}
				arrayTemp2 = new JSONArray();
				arrayTemp2.put(arrayTemp);
				obj.put("paths", arrayTemp2);
			}

			// Polygon
			else if (geometry.getClass().equals(Polygon.class)) {
				Polygon pg = (Polygon) geometry;
				Coordinate[] coords = pg.getExteriorRing().getCoordinates();
				arrayTemp = new JSONArray();
				for (int i = 0; i < coords.length; i++) {
					Coordinate coord = coords[i];
					arrayTemp2 = new JSONArray();
					arrayTemp2.put(coord.x);
					arrayTemp2.put(coord.y);
					arrayTemp.put(arrayTemp2);
				}
				arrayTemp2 = new JSONArray();
				arrayTemp2.put(arrayTemp);
				obj.put("rings", arrayTemp2);
			}

			// MultiPolygon
			else if (geometry.getClass().equals(MultiPolygon.class)) {
				MultiPolygon mpg = (MultiPolygon) geometry;
				int count = mpg.getNumGeometries();
				for (int i = 0; i < count; i++) {
					Geometry geo = mpg.getGeometryN(i);
					if (geo.getClass().equals(Polygon.class)) {
						Polygon pg = (Polygon) geo;
						Coordinate[] coords = pg.getExteriorRing().getCoordinates();
						arrayTemp = new JSONArray();
						for (int j = 0; j < coords.length; j++) {
							Coordinate coord = coords[j];
							arrayTemp2 = new JSONArray();
							arrayTemp2.put(coord.x);
							arrayTemp2.put(coord.y);
							arrayTemp.put(arrayTemp2);
						}
						obj.append("rings", arrayTemp);
					}
				}
			}

			// MultiPoint
			else if (geometry.getClass().equals(MultiPoint.class)) {
				MultiPoint mpt = (MultiPoint) geometry;
				int count = mpt.getNumGeometries();
				arrayTemp = new JSONArray();
				for (int i = 0; i < count; i++) {
					Geometry geo = mpt.getGeometryN(i);
					if (geo.getClass().equals(Point.class)) {
						Point pt = (Point) geo;
						arrayTemp2 = new JSONArray();
						arrayTemp2.put(pt.getX());
						arrayTemp2.put(pt.getY());
						arrayTemp.put(arrayTemp2);
					}
				}
				obj.put("points", arrayTemp);
			}

			// SpatialReference
			if (geometry.getSRID() > 0) {
				EsriJsonUtil.appendSpatialReference(obj, geometry.getSRID());
			}
		} catch (Exception ex) {
			logger.error("Create json geometry error.", ex);
		}

		return obj;
	}

	static public String geometryType2String(Class<?> type) {
		String result = null;

		try {
			if (type.equals(Point.class)) {
				result = "esriGeometryPoint";
			} else if (type.equals(MultiPoint.class)) {
				result = "esriGeometryMultipoint";
			} else if (type.equals(LineString.class)) {
				result = "esriGeometryPolyline";
			} else if (type.equals(Polygon.class) || type.equals(MultiPolygon.class)) {
				result = "esriGeometryPolygon";
			} else if (type.equals(Envelope.class)) {
				result = "esriGeometryEnvelope";
			}
		} catch (Exception ex) {
			logger.error("Create geometry type string error.", ex);
		}

		return result;
	}

}
