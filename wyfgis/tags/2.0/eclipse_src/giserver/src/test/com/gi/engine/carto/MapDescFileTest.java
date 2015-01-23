package com.gi.engine.carto;

import static org.junit.Assert.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import sun.misc.BASE64Encoder;

public class MapDescFileTest {
	
	static String pathName = "D:\\wuyf\\Solution\\GIServer\\svn\\sampledata\\map\\world\\map.desc";
	static String password = "giserver";

	@Test
	public void testOpen() {
		MapDescFile file = new MapDescFile(pathName);
		MapDesc mapDesc = file.open();
		assertNotNull(mapDesc);
	}

	@Test
	public void testOpenString() {
		MapDescFile file = new MapDescFile(pathName+".encrypt");

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] md5password =md.digest(password.getBytes());
		BASE64Encoder encoder = new BASE64Encoder();
		String base64md5password = encoder.encode(md5password);
		MapDesc desc = file.open(base64md5password);
		assertNotNull(desc);
	}

	@Test
	public void testSaveMapDesc() {
		MapDescFile file = new MapDescFile(pathName);
		MapDesc desc = file.open();
		MapDescFile fileSave = new MapDescFile(pathName+".save");
		assertTrue(fileSave.save(desc));
	}

	@Test
	public void testSaveMapDescString() {
		MapDescFile file = new MapDescFile(pathName);
		MapDesc desc = file.open();
		MapDescFile fileSave = new MapDescFile(pathName+".encrypt");
		assertTrue(fileSave.save(desc, password));
	}

}
