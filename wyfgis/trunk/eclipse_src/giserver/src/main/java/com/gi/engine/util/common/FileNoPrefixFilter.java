package com.gi.engine.util.common;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File filter which has NOT a prefix.
 * 
 * @author wuyf
 *
 */
public class FileNoPrefixFilter implements FilenameFilter {

	private String prefix = null;

	public FileNoPrefixFilter(String prefix) {
		this.prefix = prefix;
	}

	public boolean accept(File dir, String name) {
		boolean result = false;

		if (prefix != null) {
			String lowPrefix = prefix.toLowerCase();
			if (!name.toLowerCase().startsWith(lowPrefix)) {
				result = true;
			}
		}

		return result;
	}

}
