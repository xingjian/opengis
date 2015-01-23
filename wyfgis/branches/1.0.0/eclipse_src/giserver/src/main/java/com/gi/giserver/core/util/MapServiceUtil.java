package com.gi.giserver.core.util;

import java.io.File;

import com.gi.giengine.util.PathUtil;
import com.gi.giserver.core.config.ConfigManager;

public class MapServiceUtil {

	public static String getMapDir(String mapName) {
		String dir = ConfigManager.getServerConfig().getMapServiceDir();
		if (!dir.endsWith("/") && !dir.endsWith(File.separator)) {
			dir += "/";
		}
		String fakeMapDir = (dir + mapName + "/");
		return PathUtil.fakePath2Real(fakeMapDir);
	}

	public static String getTileFilePath(String mapName, int level, int row,
			int col, String suffix) {
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

			String fakeTileFilePath = getMapDir(mapName) + "/tiles/" + l + "/"
					+ r + "/" + c + "." + suffix;
			tileFilePath = PathUtil.fakePath2Real(fakeTileFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return tileFilePath;
	}

}
