package com.gi.engine.util.common;

import java.io.File;

public class PathUtil {

	/**
	 * GIServer user "/" as a fake file separator, this function turn it to
	 * real.
	 * 
	 * @param fakePath
	 *            path which may contains "/"
	 * @return
	 */
	public static String fakePathToReal(String fakePath) {
		String result = null;

		if (fakePath != null) {
			boolean endWithSeparator = fakePath.endsWith("/");

			String[] strs = fakePath.split("/");
			StringBuilder sb = new StringBuilder();
			for (int i = 0, count = strs.length; i < count; i++) {
				if (i != 0) {
					sb.append(File.separator);
				}
				sb.append(strs[i]);
			}

			if (endWithSeparator) {
				sb.append(File.separator);
			}

			result = sb.toString();
		}

		return result;
	}

	/**
	 * GIServer user "/" as a fake file separator, this function turn real path
	 * to it.
	 * 
	 * @param realPath
	 *            path which may contains File.separator
	 * @return
	 */
	public static String realPathToFake(String realPath) {
		String result = null;

		if (realPath != null) {
			boolean endWithSeparator = realPath.endsWith(File.separator);

			String[] strs = realPath.split("\\" + File.separator);
			StringBuilder sb = new StringBuilder();
			for (int i = 0, count = strs.length; i < count; i++) {
				if (i != 0) {
					sb.append("/");
				}
				sb.append(strs[i]);
			}

			if (endWithSeparator) {
				sb.append("/");
			}

			result = sb.toString();
		}

		return result;
	}

	/**
	 * GIServer user ":" as a name separator, this function turn any path to it.
	 * Name separator used when a file is under a relative path and the path
	 * will be the unique name of it.
	 * 
	 * @param realPath
	 *            path which may contains File.separator
	 * @return
	 */
	public static String realPathToName(String realPath) {
		String result = null;

		if (realPath != null) {
			boolean endWithSeparator = realPath.endsWith(File.separator);

			String[] strs = realPath.split("\\" + File.separator);
			StringBuilder sb = new StringBuilder();
			for (int i = 0, count = strs.length; i < count; i++) {
				if (i != 0) {
					sb.append(":");
				}
				sb.append(strs[i]);
			}

			if (endWithSeparator) {
				sb.append(":");
			}

			result = sb.toString();
		}

		return result;
	}

	/**
	 * GIServer user ":" as a name separator, this function turn it to File.separator.
	 * 
	 * @param name
	 *            name which may contains ":"
	 * @return
	 */
	public static String nameToRealPath(String name) {
		String result = null;

		if (name != null) {
			boolean endWithSeparator = name.endsWith(":");

			String[] strs = name.split("\\:");
			StringBuilder sb = new StringBuilder();
			for (int i = 0, count = strs.length; i < count; i++) {
				if (i != 0) {
					sb.append(File.separator);
				}
				sb.append(strs[i]);
			}

			if (endWithSeparator) {
				sb.append(File.separator);
			}

			result = sb.toString();
		}

		return result;
	}

}
