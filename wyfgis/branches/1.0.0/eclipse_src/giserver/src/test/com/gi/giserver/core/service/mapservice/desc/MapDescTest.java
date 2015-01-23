package com.gi.giserver.core.service.mapservice.desc;

import org.junit.Assert;
import org.junit.Test;

import com.gi.giengine.map.desc.MapDesc;

public class MapDescTest {

	@Test
	public void testLoadFromFile() {
		MapDesc mapDesc = new MapDesc();
		try {
			boolean loaded = mapDesc.loadFromFile("D:\\wuyf\\Solution\\GIServer\\svn\\sampledata\\map\\world\\map.xml");
			Assert.assertTrue(loaded);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
