/** @文件名: PBMapPanel.java @创建人：邢健  @创建日期： 2013-12-28 下午12:33:43 */
package com.promise.study;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.swing.WindowConstants;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.SingleLayerMapContent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

/**   
 * @类名: PBMapPanel.java 
 * @包名: com.promise.study 
 * @描述: 地图面板 
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-12-28 下午12:33:43 
 * @版本: V1.0   
 */
public class PBMapPanel extends JMapFrame{
	
	private String realMapDescDirectoryPath = "E:\\wyfgisserver\\data\\mapservices\\sample\\china\\";
	private CoordinateReferenceSystem crs = null;
	private SingleLayerMapContent mapContext = null;
	private ShapefileDataStore shapeDS = null;
	private static HashMap<String, Style> defaultFeatureStyles = new HashMap<String, Style>();
	
	public ShapefileDataStore getShapefileDataStore(File file){
		try {
			shapeDS = new ShapefileDataStore(file.toURI().toURL());
			shapeDS.setCharset(Charset.forName("GBK"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return shapeDS;
	}

	private Layer createLayerByFile(File file, String charset,String styleSource) throws Exception{
		String typeNames[] = shapeDS.getTypeNames();
		String typeName = typeNames[0];
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shapeDS.getFeatureSource(typeName);
		Class<?> geometryType = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
		Style style = getFeatureLayerStyle(styleSource, geometryType);
		Layer layer = new FeatureLayer(featureSource,style);
		return layer;
	}
	
	private Style getFeatureLayerStyle(String styleSource, Class<?> geometryType)
			throws Exception {
		Style style = createStyleFromStyleSource(styleSource);

		String low = "";
		if (styleSource != null && !"".equals(styleSource)) {
			low = styleSource.toLowerCase();
		}

		if (style == null) {
			if ("point".equals(low) || geometryType.equals(Point.class)
					|| geometryType.equals(MultiPoint.class)) {
				style = getDefaultFeatureStyle("point");
			} else if ("line".equals(low)
					|| geometryType.equals(LineString.class)
					|| geometryType.equals(MultiLineString.class)) {
				style = getDefaultFeatureStyle("line");
			} else if ("polygon".equals(low)
					|| geometryType.equals(Polygon.class)
					|| geometryType.equals(MultiPolygon.class)) {
				style = getDefaultFeatureStyle("polygon");
			}
		}

		return style;
	}
	
	private Style createStyleFromStyleSource(String styleSource)
			throws Exception {
		Style style = null;

		// Check absolute file path
		String fakeFilePath = styleSource;
		String filePath = fakePathToReal(fakeFilePath);
		if (filePath != null && !filePath.trim().equals("")) {
			File file = new File(filePath);
			if (!file.exists() && realMapDescDirectoryPath != null) {
				// Check relative file path
				if (realMapDescDirectoryPath.endsWith(File.separator)) {
					realMapDescDirectoryPath += File.separator;
				}
				fakeFilePath = realMapDescDirectoryPath + styleSource;
				file = new File(fakePathToReal(fakeFilePath));
			}
			if (file.exists() && file.isFile()) {
				StyleFactory styleFactory = new StyleFactoryImpl();
				SLDParser sldParser = new SLDParser(styleFactory, file);
				Style[] styles = sldParser.readXML();
				style = styles[0];
			}
		}

		return style;
	}
	
	public String fakePathToReal(String fakePath) {
		String result = null;

		if (fakePath != null) {
			boolean endWithSeparator = fakePath.endsWith("/");

			String[] strs = fakePath.split("/");
			StringBuilder sb = new StringBuilder();
			for (int i = 0, count = strs.length; i < count; i++) {
				if (i != 0) {
					sb.append(File.separator);
				}
				sb.append(strs[i]);
			}

			if (endWithSeparator) {
				sb.append(File.separator);
			}

			result = sb.toString();
		}

		return result;
	}
	
	public Style getDefaultFeatureStyle(String type) {
		Style result = null;

		if (type != null) {
			String key = type.toLowerCase();
			if (defaultFeatureStyles.containsKey(key)) {
				result = defaultFeatureStyles.get(key);
			} else {
				InputStream stream = PBMapPanel.class.getResourceAsStream(key + ".sld");
				StyleFactory styleFactory = new StyleFactoryImpl();
				SLDParser sldParser = new SLDParser(styleFactory, stream);
				Style[] styles = sldParser.readXML();
				result = styles[0];
				defaultFeatureStyles.put(key, result);
			}
		}

		return result;
	}
	
	public PBMapPanel() {
		try {
			getShapefileDataStore(new File("E:\\wyfgisserver\\data\\mapservices\\sample\\china\\data\\省级行政中心.shp"));
			Layer layer = createLayerByFile(new File("E:\\wyfgisserver\\data\\mapservices\\sample\\china\\data\\省级行政中心.shp"), "GBK", "sld/省级行政中心.sld");
			crs =  CRS.decode("EPSG:3857",true);	
			mapContext = new SingleLayerMapContent(layer);
//			FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shapeDS.getFeatureSource("point");
//			mapContext.addLayer(layer);
			//xmin="5540084.827423009" xmax="1.767367929577699E7" ymin="704818.0274999999" ymax="7086873.4196000025"
			Envelope env = new Envelope(5540084.827423009, 1.767367929577699E7, 704818.0274999999, 7086873.4196000025);
			ReferencedEnvelope refenv = new ReferencedEnvelope(env,crs);
//			mapContext.setAreaOfInterest(mapContext.getLayerBounds());
//			mapContext.setBgColor(new Color(255, 255, 255));
			this.setMapContent(mapContext);
			this.setBackground(new Color(255, 255, 255));
			Rectangle r = new Rectangle();
			mapContext.getViewport().setBounds(refenv);
			this.setPreferredSize(new Dimension(900, 700));
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//
		//E:\wyfgisserver\data\mapservices\sample\china\\
		//sld/省级行政中心.sld
		PBMapPanel p = new PBMapPanel();
		p.setLocationRelativeTo(null);
		p.setVisible(true);
	}

}
