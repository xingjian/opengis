package com.gi.engine.util.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PathUtilTest {

	static String pathName = "D:/wuyf\\Solution\\GIServer\\svn/sampledata\\map\\world/";
	
	@Test
	public void testFakePathToReal() {
		String path = PathUtil.fakePathToReal(pathName);
		assertNotNull(path);
	}

}
