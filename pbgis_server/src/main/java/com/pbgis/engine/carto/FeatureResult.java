package com.pbgis.engine.carto;

import org.opengis.feature.Feature;

public class FeatureResult {

	private int layerId = -1;// Layer's ID in Map
	private Layer layer = null;// Layer which contains Feature
	private Feature feature = null;

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}
}
