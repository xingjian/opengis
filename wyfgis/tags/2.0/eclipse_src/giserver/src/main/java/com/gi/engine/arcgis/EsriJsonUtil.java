package com.gi.engine.arcgis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.graph.util.geom.GeometryUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author wuyf
 * 
 *         Utility for ESRI JSON object
 * 
 */
public class EsriJsonUtil {
	/**
	 * @param json
	 *            ESRI JSON geometry string
	 * @return Geometry
	 * @throws JSONException
	 */
	static public Geometry json2Geometry(String json) throws JSONException {
		Geometry geo = null;

		if (json != null && !"".equals(json.trim())) {

			JSONObject obj = new JSONObject(json);

			if (!obj.isNull("geometry")) {
				obj = obj.getJSONObject("geometry");
			}

			// Point
			if (!obj.isNull("x") && !obj.isNull("y")) {
				double x = obj.getDouble("x");
				double y = obj.getDouble("y");
				geo = GeometryUtil.gf().createPoint(new Coordinate(x, y));
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
					lns[i] = GeometryUtil.gf().createLineString(pts);
				}

				if (lns.length == 1) {
					geo = GeometryUtil.gf().createLineString(
							lns[0].getCoordinates());
				} else {
					geo = GeometryUtil.gf().createMultiLineString(lns);
				}
			}

			// Polygon
			else if (!obj.isNull("rings")) {
				JSONArray rings = obj.getJSONArray("rings");

				int lrCount = rings.length();
				LinearRing shell = null;
				LinearRing[] holes = new LinearRing[lrCount - 1];
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
					LinearRing lr = GeometryUtil.gf().createLinearRing(pts);
					if (i == 0) {
						shell = lr;
					} else {
						holes[i - 1] = lr;
					}
				}
				geo = GeometryUtil.gf().createPolygon(shell, holes);
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
				geo = GeometryUtil.gf().createMultiPoint(pts);
			}

			// Envelope
			else if (!obj.isNull("xmin") && !obj.isNull("ymin")
					&& !obj.isNull("xmax") && !obj.isNull("ymax")) {
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
				LinearRing shell = GeometryUtil.gf().createLinearRing(
						coordinates);
				geo = GeometryUtil.gf().createPolygon(shell, null);
			}

			// SpatialReference
			int wkid = EsriJsonUtil.getSpatialReference(obj);
			if (wkid > 0) {
				geo.setSRID(wkid);
			}
		}

		return geo;
	}

	/**
	 * @param geometries
	 *            ESRI JSON geometries string
	 * @return Geometry List
	 */
	static public List<Geometry> json2Geometries(String geometries)
			throws JSONException {
		if (geometries == null) {
			return null;
		}

		List<Geometry> geometriesList = new ArrayList<Geometry>();
		GeometryFactory geoFactory = new GeometryFactory();
		Geometry geo = null;

		if (!geometries.startsWith("{")) {

			if (geometries.startsWith("[")) {
				// Simple array like "[......]"
				JSONArray geos = new JSONArray(geometries);
				for (int i = 0; i < geos.length(); i++) {
					geo = json2Geometry(geos.getString(i));
					geometriesList.add(geo);
				}
			} else {
				// Simple points like "-104.53, 34.74, -63.53, 10.23"
				String[] strs = geometries.split(",");
				for (int i = 0; i < strs.length / 2; i++) {
					double x = Double.parseDouble(strs[2 * i]);
					double y = Double.parseDouble(strs[2 * i + 1]);
					geo = geoFactory.createPoint(new Coordinate(x, y));
					geometriesList.add(geo);
				}
			}
		} else {
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
					geo = json2Geometry(geos.getString(i));
					geometriesList.add(geo);
				}
			}
		}

		return geometriesList;
	}

	static public JSONObject geometry2JSON(Geometry geometry)
			throws JSONException {
		if (geometry == null) {
			return null;
		}

		JSONObject obj = new JSONObject();

		JSONArray arrayTemp = null;
		JSONArray arrayTemp2 = null;

		// Point
		if (geometry.getClass().equals(Point.class)) {
			Point pt = (Point) geometry;
			obj.put("x", pt.getX());
			obj.put("y", pt.getY());
		}

		// LineString
		else if (geometry.getClass().equals(LineString.class)) {
			LineString ls = (LineString) geometry;
			Coordinate[] coords = ls.getCoordinates();
			arrayTemp = new JSONArray();
			for (int i = 0, count=coords.length; i < count; i++) {
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
			for (int i = 0, count=coords.length; i < count; i++) {
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

		// MultiPoint
		else if (geometry.getClass().equals(MultiPoint.class)) {
			MultiPoint mpt = (MultiPoint) geometry;
			arrayTemp = new JSONArray();
			for (int i = 0, count=mpt.getNumGeometries(); i < count; i++) {
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

		// MultiLineString
		else if (geometry.getClass().equals(MultiLineString.class)) {
			MultiLineString mls = (MultiLineString)geometry;
			arrayTemp2 = new JSONArray();
			for(int i=0,count=mls.getNumGeometries();i<count;i++){
				LineString ls = (LineString)mls.getGeometryN(i);
				Coordinate[] coords = ls.getCoordinates();
				arrayTemp = new JSONArray();
				for (int j = 0, count2=coords.length; j < count2; j++) {
					Coordinate coord = coords[j];
					JSONArray arrayPt = new JSONArray();
					arrayPt.put(coord.x);
					arrayPt.put(coord.y);
					arrayTemp.put(arrayPt);
				}
				arrayTemp2.put(arrayTemp);
			}
			obj.put("paths", arrayTemp2);
		}

		// MultiPolygon
		else if (geometry.getClass().equals(MultiPolygon.class)) {
			MultiPolygon mpg = (MultiPolygon) geometry;
			for (int i = 0, count=mpg.getNumGeometries(); i < count; i++) {
				Geometry geo = mpg.getGeometryN(i);
				if (geo.getClass().equals(Polygon.class)) {
					Polygon pg = (Polygon) geo;
					// TODO
					// If ESRI support multipolygon with hole?
					Coordinate[] coords = pg.getExteriorRing().getCoordinates();
					arrayTemp = new JSONArray();
					for (int j = 0; j < coords.length; j++) {
						Coordinate coord = coords[j];
						JSONArray arrayPt = new JSONArray();
						arrayPt.put(coord.x);
						arrayPt.put(coord.y);
						arrayTemp.put(arrayPt);
					}
					obj.append("rings", arrayTemp);
				}
			}
		}

		// SpatialReference
		if (geometry.getSRID() > 0) {
			EsriJsonUtil.appendSpatialReference(obj, geometry.getSRID());
		}

		return obj;
	}

	static public String geometryType2String(Class<?> type)
			throws JSONException {
		String result = null;

		if (type.equals(Point.class)) {
			result = "esriGeometryPoint";
		} else if (type.equals(MultiPoint.class)) {
			result = "esriGeometryMultipoint";
		} else if (type.equals(LineString.class)) {
			result = "esriGeometryPolyline";
		} else if (type.equals(Polygon.class)
				|| type.equals(MultiPolygon.class)) {
			result = "esriGeometryPolygon";
		} else if (type.equals(Envelope.class)) {
			result = "esriGeometryEnvelope";
		}

		return result;
	}

	public static int getSpatialReference(JSONObject obj) throws JSONException {
		int wkid = 0;

		if (!obj.isNull("spatialReference")) {
			wkid = obj.getJSONObject("spatialReference").getInt("wkid");
		}

		return wkid;
	}

	public static void appendSpatialReference(JSONObject obj, int wkid)
			throws JSONException {
		JSONObject objSR = new JSONObject();
		objSR.append("wkid", wkid);
		obj.append("spatialReference", objSR);
	}

	public static JSONObject feature2JSON(Feature feature,
			boolean isReturnGeometry) throws JSONException {
		JSONObject result = new JSONObject();

		if (isReturnGeometry && feature instanceof SimpleFeature) {
			Geometry geo = (Geometry) feature.getDefaultGeometryProperty()
					.getValue();
			result.put("geometry", geometry2JSON(geo));
			result.put("geometryType", geometryType2String(geo.getClass()));
		}

		JSONObject objAttributes = new JSONObject();
		String geometryFieldName = feature.getDefaultGeometryProperty()
				.getName().getLocalPart();
		for (Iterator<Property> itr = feature.getProperties().iterator(); itr
				.hasNext();) {
			Property property = itr.next();
			String name = property.getDescriptor().getName().getLocalPart();
			if (!geometryFieldName.equals(name)) {
				Object value = property.getValue();
				objAttributes.put(name, value);
			}
		}
		result.put("attributes", objAttributes);

		return result;
	}

	private static SimpleFeature jsonObject2Feature(JSONObject objFeature,
			SimpleFeatureBuilder featureBuilder) throws JSONException {
		SimpleFeature featureResult = featureBuilder.buildFeature(null);		

		// Geometry
		String strGeometry = objFeature.getString("geometry");
		Geometry geometry = json2Geometry(strGeometry);
		featureResult.setDefaultGeometry(geometry);

		// Attributes
		if (objFeature.has("attributes")) {
			JSONObject objAttributes = objFeature.getJSONObject("attributes");
			Iterator<?> itKeys = objAttributes.keys();
			while (itKeys.hasNext()) {
				String key = itKeys.next().toString();
				Object attribute = objAttributes.get(key);
				featureResult.setAttribute(key, attribute);
			}
		}

		return featureResult;
	}

	public static SimpleFeature json2Feature(String feature,
			SimpleFeatureType featureType) throws JSONException {
		SimpleFeature result = null;

		JSONObject objFeature = new JSONObject(feature);
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(
				featureType);

		result = jsonObject2Feature(objFeature, featureBuilder);

		return result;
	}

	public static FeatureCollection<SimpleFeatureType, SimpleFeature> json2FeatureCollection(
			String features, SimpleFeatureType featureType)
			throws JSONException {

		FeatureCollection<SimpleFeatureType, SimpleFeature> result = FeatureCollections
				.newCollection();

		if (features != null && !"".equals(features)) {
			JSONArray arrayFeatures = new JSONArray(features);

			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(
					featureType);

			for (int i = 0,count=arrayFeatures.length(); i < count; i++) {
				JSONObject objFeature = arrayFeatures.getJSONObject(i);
				SimpleFeature featureResult = jsonObject2Feature(objFeature,
						featureBuilder);

				result.add(featureResult);
			}
		}

		return result;
	}

}
