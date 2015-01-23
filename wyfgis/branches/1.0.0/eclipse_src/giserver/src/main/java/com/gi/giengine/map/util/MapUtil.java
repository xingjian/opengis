package com.gi.giengine.map.util;

import java.io.File;

import org.geotools.geometry.jts.ReferencedEnvelope;

public class MapUtil {

	public static ReferencedEnvelope adjustEnvelopeToSize(
			ReferencedEnvelope env, int width, int height) {
		ReferencedEnvelope result = env;

		double widthEnv = env.getMaxX() - env.getMinX();
		double heightEnv = env.getMaxY() - env.getMinY();
		double whEnv = widthEnv / heightEnv;
		double whSize = 1.0 * width / height;
		if (whEnv > whSize) {
			result.expandBy(0, (widthEnv / whSize - heightEnv) / 2);
		} else if (whEnv < whSize) {
			result.expandBy((heightEnv * whSize - widthEnv) / 2, 0);
		}

		return result;
	}

	public static String getMapDir(String mapDescFilePath) {
		File file = new File(mapDescFilePath).getParentFile();
		return file.getAbsolutePath();
	}

	public static String getMapName(String mapDescFilePath) {
		File file = new File(mapDescFilePath).getParentFile();
		return file.getName();
	}

	public static String getMapDescFilePath(String mapDir) {
		return mapDir + "map.desc";
	}

	public static String getMapServiceDescFilePath(String mapDir) {
		return mapDir + "mapservice.desc";
	}

}
