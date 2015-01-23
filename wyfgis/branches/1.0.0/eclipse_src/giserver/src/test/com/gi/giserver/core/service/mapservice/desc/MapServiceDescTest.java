package com.gi.giserver.core.service.mapservice.desc;

import org.junit.Assert;
import org.junit.Test;

public class MapServiceDescTest {

	@Test
	public void testLoadFromFile() {
		MapServiceDesc mapServiceDesc = new MapServiceDesc();
		try {
			boolean loaded = mapServiceDesc
					.loadFromFile("D:\\wuyf\\Solution\\GIServer\\svn\\sampledata\\map\\world\\service.xml");
			Assert.assertTrue(loaded);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
