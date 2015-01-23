package com.gi.engine.carto;

import java.awt.image.BufferedImage;

import org.geotools.geometry.jts.ReferencedEnvelope;

public class MapImage {

	private BufferedImage image = null;
	private ReferencedEnvelope extent = null;

	public ReferencedEnvelope getExtent() {
		return extent;
	}

	public void setExtent(ReferencedEnvelope extent) {
		this.extent = extent;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
}
