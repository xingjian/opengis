package com.gi.engine.spatialreference;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class SpatialReferenceManagerTest {

	@Test
	public void testWkidToCRS() throws Exception {
		String wkid = "3857";
		CoordinateReferenceSystem crs = SpatialReferenceManager.wkidToCRS(wkid, false);
		assertNotNull(crs);
	}

	@Test
	public void testWktToCRS() {
		fail("Not yet implemented");
	}

	@Test
	public void testCrsToWkid() {
		fail("Not yet implemented");
	}

	@Test
	public void testCrsToWkt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllAvalibleWkids() {
		ArrayList<String> wkids = SpatialReferenceManager.getAllAvalibleWkids();
		for (Iterator<String> itr = wkids.iterator(); itr.hasNext();) {
			String wkid = itr.next();
			System.out.println(wkid);
		}
	}

	@Test
	public void testGetCRSDescription() {
		fail("Not yet implemented");
	}

}
