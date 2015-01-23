package com.gi.server.core.config;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class TokenConfig {
	static Logger logger = Logger.getLogger(TokenConfig.class);

	private HashMap<String, String> users = new HashMap<String, String>();

	public TokenConfig(String filePath) throws Exception {
		FileInputStream stream = new FileInputStream(filePath);
		Properties p = new Properties();
		p.load(stream);

		Enumeration<Object> enumKeys = p.keys();
		String key = null;
		while (enumKeys.hasMoreElements()) {
			key = enumKeys.nextElement().toString();
			if (p.containsKey(key)) {
				users.put(key, p.getProperty(key));
			}
		}
	}

	public HashMap<String, String> getUsers() {
		return users;
	}

}
