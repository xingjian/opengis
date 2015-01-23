package com.gi.engine.arcgis;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class EsriUnitsUtil {	
	public final static String UNKNOWN = "esriUnknownUnits";
	public final static String INCHES = "esriInches";
	public final static String FEET = "esriFeet";
	public final static String YARDS = "esriYards";
	public final static String MILES = "esriMiles";
	public final static String NAUTICAL_MILES = "esriNauticalMiles";
	public final static String MILLIMETERS = "esriMillimeters";
	public final static String CENTIMETERS = "esriCentimeters";
	public final static String DECIMETERS = "esriDecimeters";
	public final static String METERS = "esriMeters";
	public final static String KILOMETERS = "esriKilometers";
	public final static String DECIMALDEGREES = "esriDecimalDegrees";
	public final static String POINTS = "esriPoints";
	
	public static String getUnits(CoordinateReferenceSystem crs){
		String result = UNKNOWN;
		
		try{
			String wkt = crs.toWKT().toLowerCase();
			int startIndex = wkt.lastIndexOf("unit[\"")+6;
			int endIndex = wkt.indexOf("\"", startIndex);
			String unit = wkt.substring(startIndex, endIndex);
			if("meter".equals(unit)){
				result = METERS;
			}else if("degree".equals(unit) || "decimal_degree".equals(unit)){
				result = DECIMALDEGREES;
			}else if("us_foot".equals(unit)){
				result = FEET;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
