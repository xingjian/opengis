package com.gi.engine.datasource;

import java.io.File;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;

public class GridCoverage2DReaderFactory {
	public static AbstractGridCoverage2DReader getGridCoverage2DReaderFromFile(File file) throws Exception{
		// For raster data files 
		AbstractGridFormat format = GridFormatFinder.findFormat(file);
		AbstractGridCoverage2DReader reader = format.getReader(file);
		return reader;
	}	
}
