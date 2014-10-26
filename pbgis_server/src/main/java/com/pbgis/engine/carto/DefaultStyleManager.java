package com.pbgis.engine.carto;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

public class DefaultStyleManager {

	private static HashMap<String, Style> defaultFeatureStyles = new HashMap<String, Style>();

	public static Style getDefaultFeatureStyle(String type) {
		Style result = null;

		if (type != null) {
			String key = type.toLowerCase();
			if (defaultFeatureStyles.containsKey(key)) {
				result = defaultFeatureStyles.get(key);
			} else {
				InputStream stream = DefaultStyleManager.class
						.getResourceAsStream(key + ".sld");
				StyleFactory styleFactory = new StyleFactoryImpl();
				SLDParser sldParser = new SLDParser(styleFactory, stream);
				Style[] styles = sldParser.readXML();
				result = styles[0];
				defaultFeatureStyles.put(key, result);
			}
		}

		return result;
	}
	

	public static Style getDefaultRasterStyle(AbstractGridCoverage2DReader reader) {
        GridCoverage2D cov = null;
        try {
            cov = reader.read(null);
        } catch (IOException giveUp) {
            throw new RuntimeException(giveUp);
        }
        
        int numBands = cov.getNumSampleDimensions();
        if (numBands < 3) {
            return createGreyscaleRasterStyle(cov);
        }else{
        	return createRGBRasterStyle(cov);
        }
	}

    private static Style createGreyscaleRasterStyle(GridCoverage2D cov) {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        SelectedChannelType sct = sf.createSelectedChannelType("1", ce);

        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }

    private static Style createRGBRasterStyle(GridCoverage2D cov) {        
        int numBands = cov.getNumSampleDimensions();        
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++) {
            GridSampleDimension dim = cov.getSampleDimension(i);
            sampleDimensionNames[i] = dim.getDescription().toString();
        }
        final int RED = 0, GREEN = 1, BLUE = 2;
        int[] channelNum = { -1, -1, -1 };
        
        for (int i = 0; i < numBands; i++) {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null) {
                if (name.matches("red.*")) {
                    channelNum[RED] = i + 1;
                } else if (name.matches("green.*")) {
                    channelNum[GREEN] = i + 1;
                } else if (name.matches("blue.*")) {
                    channelNum[BLUE] = i + 1;
                }
            }
        }
        
        if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
            channelNum[RED] = 1;
            channelNum[GREEN] = 2;
            channelNum[BLUE] = 3;
        }
        SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
        
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        for (int i = 0; i < 3; i++) {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }

}
