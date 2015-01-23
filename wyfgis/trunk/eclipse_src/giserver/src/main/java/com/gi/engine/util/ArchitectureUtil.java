package com.gi.engine.util;

import java.io.File;

import com.gi.engine.util.common.FileSuffixFilter;
import com.gi.engine.util.common.PathUtil;

public class ArchitectureUtil {

	public static String MAP_DESC_FILE_NAME = "map.desc";
	public static String MAP_SERVICE_DESC_FILE_NAME = "mapservice.desc";

	/**
	 * Is this a map service directory. Map service directory must contains the
	 * mapDescFile and mapServiceDescFile.
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean isMapServiceDirectory(File dir) {
		boolean result = false;

		if (dir.isDirectory() && dir.canRead()) {
			boolean containMapDesc = false;
			boolean containMapServiceDesc = false;
			FileSuffixFilter filter = new FileSuffixFilter("desc");
			File[] files = dir.listFiles(filter);
			for (int i = 0, count = files.length; i < count; i++) {
				try {
					File file = files[i];
					if (file.isFile()
							&& MAP_DESC_FILE_NAME.equals(file.getName())) {
						containMapDesc = true;
					}
					if (file.isFile()
							&& MAP_SERVICE_DESC_FILE_NAME
									.equals(file.getName())) {
						containMapServiceDesc = true;
					}

					if (containMapDesc && containMapServiceDesc) {
						result = true;
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		return result;
	}

	public static String getExplodedTileFilePath(String mapDir, int level,
			int row, int col, String suffix) {
		String tileFilePath = null;

		try {
			String l = "0" + level;
			int lLength = l.length();
			if (lLength > 2) {
				l = l.substring(lLength - 2);
			}
			l = "L" + l;

			String r = "0000000" + Integer.toHexString(row);
			int rLength = r.length();
			if (rLength > 8) {
				r = r.substring(rLength - 8);
			}
			r = "R" + r;

			String c = "0000000" + Integer.toHexString(col);
			int cLength = c.length();
			if (cLength > 8) {
				c = c.substring(cLength - 8);
			}
			c = "C" + c;
			
			String fakeTileFilePath = String.format("%s/%s/%s/%s.%s", mapDir
					+ "/tiles", l, r, c, suffix);
			tileFilePath = PathUtil.fakePathToReal(fakeTileFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return tileFilePath;
	}

	public static String getCompactTileFilePath(String mapDir, int level,
			int row, int col) {
		String tileFilePath = null;
		try {
			String l = "0" + level;
			int lLength = l.length();
			if (lLength > 2) {
				l = l.substring(lLength - 2);
			}
			l = "L" + l;

			int rGroup = 128 * (row / 128);
			String r = "000" + Integer.toHexString(rGroup);
			int rLength = r.length();
			if (rLength > 4) {
				r = r.substring(rLength - 4);
			}
			r = "R" + r;

			int cGroup = 128 * (col / 128);
			String c = "000" + Integer.toHexString(cGroup);
			int cLength = c.length();
			if (cLength > 4) {
				c = c.substring(cLength - 4);
			}
			c = "C" + c;

			String fakeTileFilePath = String.format("%s/%s/%s%s", mapDir
					+ "/tiles", l, r, c);
			tileFilePath = PathUtil.fakePathToReal(fakeTileFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return tileFilePath;
	}

}
