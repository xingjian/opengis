package com.gi.engine.util.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class PasswordUtil {

	static public String base64md5password(String password) {
		String result = null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5password = md.digest(password.getBytes());
			BASE64Encoder encoder = new BASE64Encoder();
			result = encoder.encode(md5password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result;
	}

}
