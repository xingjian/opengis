package com.gi.engine.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ArchitectureUtilTest {

	@Test
	public void testIsMapServiceDirectory() {
		File dir = new File("D:\\wuyf\\Solution\\GIServer\\svn\\sampledata\\map\\world");
		assertTrue(ArchitectureUtil.isMapServiceDirectory(dir));
	}

}
