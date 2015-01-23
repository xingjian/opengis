package com.gi.giengine.util;

import java.io.File;

import org.apache.log4j.Logger;

public class PathUtil {
	/**
	 * @param fakePath
	 *            path which contains "/"
	 * @return
	 */
	public static String fakePath2Real(String fakePath) {
		String result = null;

		if (fakePath != null) {
			boolean isDirectory = fakePath.endsWith("/");

			String[] strs = fakePath.split("/");
			for (int i = 0; i < strs.length; i++) {
				if (i == 0) {
					result = strs[i];
				} else {
					result += File.separator + strs[i];
				}
			}

			if (isDirectory) {
				result += File.separator;
			}
		}
		
		return result;
	}
}
