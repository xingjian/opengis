package com.gi.giserver.core.auth;

import java.util.HashMap;

import com.gi.giserver.core.config.ConfigManager;

public class Editor {

	private boolean authorized = false;
	private String username = null;
	private String password = null;

	public Editor(String username, String password) {
		this.username = username;
		this.password = password;

		authorized = authorize(username, password);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	private boolean authorize(String username, String password) {
		boolean result = false;

		try {
			HashMap<String, String> editors = ConfigManager.getEditorConfig().getUsers();
			if (editors.containsKey(username)) {
				if (editors.get(username).equals(password)) {
					result = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public boolean isAuthorized() {
		return authorized;
	}

}
