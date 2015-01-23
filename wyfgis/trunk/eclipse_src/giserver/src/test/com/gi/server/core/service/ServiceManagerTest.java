package com.gi.server.core.service;

import java.io.File;

import org.junit.Test;

public class ServiceManagerTest {

	@Test
	public void testLoadMapServices() {
		File mapServicesDir = new File("D:\\wuyf\\Solution\\GIServer\\svn\\sampledata");
		ServiceManager.setMapServicesDir(mapServicesDir);
		ServiceManager.loadMapServices();
	}

}
