package com.gi.engine.util.common;

import java.io.File;
import java.io.FilenameFilter;

public class FileSuffixFilter implements FilenameFilter {

	private String suffix = null;

	public FileSuffixFilter(String suffix) {
		this.suffix = suffix;
	}

	public boolean accept(File dir, String name) {
		boolean result = false;

		if (suffix != null) {
			String lowSuffix = suffix.toLowerCase();
			if (name.toLowerCase().endsWith("." + lowSuffix)) {
				result = true;
			}
		}

		return result;
	}

}
