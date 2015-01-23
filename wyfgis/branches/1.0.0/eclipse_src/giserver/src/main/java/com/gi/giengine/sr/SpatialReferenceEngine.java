package com.gi.giengine.sr;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.geotools.referencing.CRS;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.GenericName;

public class SpatialReferenceEngine {

	private static Hashtable<String, MathTransform> mathTransformsCache = new Hashtable<String, MathTransform>();
	private static Hashtable<String, CoordinateReferenceSystem> coordinateReferenceSystemsCache = new Hashtable<String, CoordinateReferenceSystem>();
	private static Hashtable<String, CoordinateReferenceSystem> customCRSs = new Hashtable<String, CoordinateReferenceSystem>();

	/**
	 * Load custom CRSs from directory The file name of custom CRS is <wkid>.prj
	 * The content of file is wkt Notice: custom CRSs have higher priority
	 * 
	 * @param crsDirPath
	 * @throws Exception
	 */
	public synchronized static void loadCustomCRSs(String crsDirPath)
			throws Exception {
		File file = new File(crsDirPath);
		if (file.isDirectory()) {
			String[] strs = file.list();
			int strCount = strs.length;
			for (int i = 0; i < strCount; i++) {
				String str = strs[i];
				if (str.toLowerCase().endsWith(".prj")) {
					try {
						String wkid = str.substring(0, str.length() - 4);
						if (!crsDirPath.endsWith(File.separator)) {
							crsDirPath += File.separator;
						}
						File fileCRS = new File(crsDirPath + str);
						if (fileCRS.isFile()) {
							FileReader fileReader = new FileReader(fileCRS);
							int length = (int) fileCRS.length();
							char[] chars = new char[length];
							fileReader.read(chars);
							String wkt = String.valueOf(chars);
							CoordinateReferenceSystem crs = wktToCRS(wkt);
							if (crs != null) {
								customCRSs.put(wkid, crs);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static void clearCache() {
		mathTransformsCache.clear();
		coordinateReferenceSystemsCache.clear();
	}

	public static CoordinateReferenceSystem wkidToCRS(String wkid,
			boolean cacheCRS) throws Exception {
		CoordinateReferenceSystem crs = null;

		if (customCRSs.containsKey(wkid)) {
			crs = customCRSs.get(wkid);
		} else {
			if (coordinateReferenceSystemsCache.containsKey(wkid)) {
				crs = coordinateReferenceSystemsCache.get(wkid);
			} else {
				crs = CRS.decode("EPSG:" + wkid, true);
				if (crs != null && cacheCRS) {
					coordinateReferenceSystemsCache.put(wkid, crs);
				}
			}
		}

		return crs;
	}

	public static CoordinateReferenceSystem wktToCRS(String wkt)
			throws Exception {
		return CRS.parseWKT(wkt);
	}

	public static String crsToWkid(CoordinateReferenceSystem crs)
			throws Exception {
		String wkid = null;

		Integer code = CRS.lookupEpsgCode(crs, true);
		if (code != null) {
			wkid = code.toString();
		}

		return wkid;
	}

	public static String crsToWkt(CoordinateReferenceSystem crs)
			throws Exception {
		return crs.toWKT();
	}

	public static MathTransform getMathTransform(String fromWkid,
			String toWkid, boolean cacheMathTransform) throws Exception {
		MathTransform mt = null;

		String key = fromWkid + "|" + toWkid;
		if (mathTransformsCache.containsKey(key)) {
			mt = mathTransformsCache.get(key);
		} else {
			CoordinateReferenceSystem fromCRS = wkidToCRS(fromWkid, false);
			CoordinateReferenceSystem toCRS = wkidToCRS(toWkid, false);
			mt = CRS.findMathTransform(fromCRS, toCRS, true);
			if (mt != null && cacheMathTransform) {
				mathTransformsCache.put(key, mt);
			}
		}

		return mt;
	}

	public static ArrayList<String> getInternalWkids() {
		ArrayList<String> result = new ArrayList<String>();

		Set<String> codes = CRS.getSupportedCodes("EPSG");
		Iterator<String> itCode = codes.iterator();
		while (itCode.hasNext()) {
			try {
				String code = itCode.next();
				if (Integer.valueOf(code) != null) {
					result.add(code);
				}
			} catch (NumberFormatException ex) {
			}
		}

		return result;
	}

	public static ArrayList<String> getAllAvalibleWkids() {
		ArrayList<String> result = new ArrayList<String>();

		for (Iterator<String> itr = customCRSs.keySet().iterator(); itr
				.hasNext();) {
			String wkid = itr.next();
			result.add(wkid);
		}

		ArrayList<String> internalWkids = getInternalWkids();
		for (Iterator<String> itr = internalWkids.iterator(); itr.hasNext();) {
			String wkid = itr.next();
			if (!result.contains(wkid)) {
				result.add(wkid);
			}
		}

		return result;
	}

	public static String getCRSDescription(CoordinateReferenceSystem crs) {
		if (crs != null) {
			ReferenceIdentifier name =  crs.getName();
			return name.getCode();
		} else {
			return "Unknown";
		}
	}

}
