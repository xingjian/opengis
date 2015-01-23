package com.gi.giengine.geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gi.giserver.core.service.geometryservice.Project;
import com.vividsolutions.jts.geom.Geometry;

public class BufferEngine {

	public static List<Geometry> buffers(List<Geometry> geometries,
			String inSR, String outSR, String bufferSR, double[] distances) {
		List<Geometry> result = new ArrayList<Geometry>();
		
		for(Iterator<Geometry> itr = geometries.iterator();itr.hasNext();){
			Geometry geo = itr.next();
			for (int i = 0; i < distances.length; i++) {
				geo = buffer(geo, inSR, outSR, bufferSR, distances[i]);
				result.add(geo);
			}
		}

		return result;
	}

	public static Geometry buffer(Geometry geometry, String inSR, String outSR,
			String bufferSR, double distance) {

		Geometry geo = geometry;
		if (bufferSR != inSR) {
			geo = Project.project(geo, inSR, bufferSR);
		}
		geo = geo.buffer(distance);
		if (outSR != bufferSR) {
			geo = Project.project(geo, bufferSR, outSR);
		}

		return geo;
	}
}
