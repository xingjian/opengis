package com.gi.engine.arcgis;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.engine.spatialreference.SpatialReferenceManager;

public class EsriUnitsUtilTest {

	@Test
	public void testGetUnits() {
		String esriUnits = null;
		try {
			CoordinateReferenceSystem crs = SpatialReferenceManager.wkidToCRS(
					"4326", false);
			esriUnits = EsriUnitsUtil.getUnits(crs);
			System.out.println(esriUnits);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(esriUnits);
	}

}
