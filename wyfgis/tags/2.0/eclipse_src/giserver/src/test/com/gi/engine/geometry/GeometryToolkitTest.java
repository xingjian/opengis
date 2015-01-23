package com.gi.engine.geometry;

import org.geotools.graph.util.geom.GeometryUtil;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryToolkitTest {

	@Test
	public void testReshape() {
		Coordinate[] pts1 = new Coordinate[5];
		pts1[0] = new Coordinate(0, 0);
		pts1[1] = new Coordinate(-2,0);
		pts1[2] = new Coordinate(-2,4);
		pts1[3] = new Coordinate(0, 4);
		pts1[4] = new Coordinate(0, 0);
		Polygon pg = GeometryUtil.gf().createPolygon(GeometryUtil.gf().createLinearRing(pts1), null);
		
		Coordinate[] pts2 = new Coordinate[4];
		pts2[0] = new Coordinate(-1, 1);
		pts2[1] = new Coordinate(2,1);
		pts2[2] = new Coordinate(2,2);
		pts2[3] = new Coordinate(-1,2);
		LineString reshaper = GeometryUtil.gf().createLineString(pts2);
		
		Geometry geo1 = GeometryToolkit.reshape(pg, reshaper);
		System.out.println(geo1.getNumPoints());
		
	}
	
	

}
