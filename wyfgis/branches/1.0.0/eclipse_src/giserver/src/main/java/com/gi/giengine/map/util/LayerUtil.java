package com.gi.giengine.map.util;

import org.geotools.map.MapLayer;
import org.opengis.feature.type.FeatureType;

public class LayerUtil {
	/*
	public static SimpleFeatureType getFeatureType(MapLayer layer) {
		SimpleFeatureType result = null;

		FeatureType featureType = layer.getFeatureSource().getSchema();
		if (featureType instanceof SimpleFeatureType) {
			result = (SimpleFeatureType) featureType;
		}

		return result;
	}
	*/


	public static FeatureType getFeatureType(MapLayer layer) {
		return layer.getFeatureSource().getSchema();
	}
}
