package com.gi.engine.server.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MapServiceDescFileTest {
	static String pathName = "D:\\wuyf\\Solution\\GIServer\\svn\\sampledata\\map\\world\\mapservice.desc";

	@Test
	public void testOpen() {
		MapServiceDescFile file = new MapServiceDescFile(pathName);
		MapServiceDesc desc = (MapServiceDesc) file.open();
		assertNotNull(desc);
	}

	@Test
	public void testSave() {
		MapServiceDescFile file = new MapServiceDescFile(pathName);
		MapServiceDesc desc = (MapServiceDesc) file.open();
		MapServiceDescFile fileSave = new MapServiceDescFile(pathName+".save");
		assertTrue(fileSave.save(desc));
	}

}
