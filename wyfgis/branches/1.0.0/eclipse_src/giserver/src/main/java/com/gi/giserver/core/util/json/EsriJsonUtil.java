package com.gi.giserver.core.util.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.error.ServiceErrorUtil;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author wuyf Utility for ESRI JSON object
 * 
 */
public class EsriJsonUtil {
	static Logger logger = Logger.getLogger(EsriJsonUtil.class);

	public static int getSpatialReference(JSONObject obj) {
		int wkid = 0;

		try {
			if (!obj.isNull("spatialReference")) {
				wkid = obj.getJSONObject("spatialReference").getInt("wkid");
			}
		} catch (Exception ex) {
			logger.error("Get spatial reference error.", ex);
		}

		return wkid;
	}

	public static void appendSpatialReference(JSONObject obj, int wkid) {
		try {
			JSONObject objSR = new JSONObject();
			objSR.append("wkid", wkid);
			obj.append("spatialReference", objSR);
		} catch (Exception ex) {
			logger.error("Append spatial reference error.", ex);
		}
	}

	public static JSONObject feature2JSON(Feature feature, boolean isReturnGeometry) {
		JSONObject result = new JSONObject();

		try {
			if (isReturnGeometry && feature instanceof SimpleFeature) {
				Geometry geo = (Geometry) feature.getDefaultGeometryProperty().getValue();
				result.put("geometry", EsriJsonGeometryUtil.geometry2JSON(geo));
				result.put("geometryType", EsriJsonGeometryUtil.geometryType2String(geo.getClass()));
			}

			JSONObject objAttributes = new JSONObject();
			String geometryFieldName = feature.getDefaultGeometryProperty().getName().getLocalPart();
			for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
				Property property = itr.next();
				String name = property.getDescriptor().getName().getLocalPart();
				if (!geometryFieldName.equals(name)) {
					Object value = property.getValue();
					objAttributes.put(name, value);
				}
			}
			result.put("attributes", objAttributes);
		} catch (Exception ex) {
			logger.error("Generate json feature error.", ex);
		}

		return result;
	}

	private static SimpleFeature jsonObject2Feature(JSONObject objFeature, SimpleFeatureBuilder featureBuilder)
			throws Exception {
		SimpleFeature featureResult = featureBuilder.buildFeature(null);

		// Geometry
		String strGeometry = objFeature.getString("geometry");
		Geometry geometry = EsriJsonGeometryUtil.json2Geometry(strGeometry);
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

	public static SimpleFeature json2Feature(String feature, SimpleFeatureType featureType) {
		SimpleFeature result = null;

		try {
			JSONObject objFeature = new JSONObject(feature);
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

			result = jsonObject2Feature(objFeature, featureBuilder);
		} catch (Exception ex) {
			logger.error("Parser json feature '" + feature + "' error.", ex);
		}

		return result;
	}

	public static FeatureCollection<SimpleFeatureType, SimpleFeature> json2FeatureCollection(String features,
			SimpleFeatureType featureType) {

		FeatureCollection<SimpleFeatureType, SimpleFeature> result = FeatureCollections.newCollection();

		try {
			if (features != null && !"".equals(features)) {
				JSONArray arrayFeatures = new JSONArray(features);

				SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

				int featureCount = arrayFeatures.length();
				for (int i = 0; i < featureCount; i++) {
					JSONObject objFeature = arrayFeatures.getJSONObject(i);
					SimpleFeature featureResult = jsonObject2Feature(objFeature, featureBuilder);

					result.add(featureResult);
				}
			}
		} catch (Exception ex) {
			result = null;
			logger.error("Parser json features '" + features + "' error.", ex);
		}

		return result;
	}

	public static JSONObject generateJSONError(ServiceError error, ArrayList<String> details) {
		JSONObject result = null;

		try {
			result = new JSONObject();
			result.put("code", ServiceErrorUtil.getErrorCode(error));
			result.put("message", ServiceErrorUtil.getErrorMessage(error));
			result.put("details", new JSONArray(details));
		} catch (Exception ex) {
			result = null;
		}

		return result;
	}

}
