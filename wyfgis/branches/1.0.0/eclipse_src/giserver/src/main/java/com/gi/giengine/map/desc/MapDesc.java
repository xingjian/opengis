package com.gi.giengine.map.desc;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.gi.giengine.map.util.MapUtil;

/**
 * @author wuyf
 * 
 */
public class MapDesc {
	private String mapDescFilePath = null;
	private String mapName = null;
	private String wkid = null;
	private boolean antiAlias = false;
	private Color backgroundColor = new Color(255, 255, 255);
	private ExtentDesc initialExtentDesc = new ExtentDesc(0,0,0,0);
	private ArrayList<LayerDesc> layerDescs = new ArrayList<LayerDesc>();
	
	public String getMapDescFilePath() {
		return mapDescFilePath;
	}

	public String getMapName() {
		return mapName;
	}

	public String getWkid() {
		return wkid;
	}

	public boolean isAntiAlias() {
		return antiAlias;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ExtentDesc getInitialExtentDesc() {
		return initialExtentDesc;
	}

	public ArrayList<LayerDesc> getLayerDescs() {
		return layerDescs;
	}
	
	public void setMapDescFilePath(String mapDescFilePath) {
		this.mapDescFilePath = mapDescFilePath;
	}

	public void setWkid(String wkid) {
		this.wkid = wkid;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setInitialExtentDesc(ExtentDesc initialExtentDesc) {
		this.initialExtentDesc = initialExtentDesc;
	}

	// -----------------------------------------------------------------------------
	

	public MapDesc(){
		
	}
	
	public MapDesc(String mapDescFilePath) throws Exception{
		this.loadFromFile(mapDescFilePath);
	}


	public LayerDesc getLayerDesc(int layerId) {
		LayerDesc layerDesc = null;

		if (layerId < this.layerDescs.size()) {
			layerDesc = layerDescs.get(layerId);
		}

		return layerDesc;
	}

	public boolean loadFromFile(String mapDescFilePath) throws Exception {		
		this.mapDescFilePath = mapDescFilePath;
		File file = new File(mapDescFilePath);
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		Element root = document.getRootElement();

		this.mapName = MapUtil.getMapName(mapDescFilePath);

		this.wkid = root.elementTextTrim("WKID");

		try {
			this.antiAlias = Boolean.parseBoolean(root
					.elementTextTrim("AntiAlias"));
		} catch (Exception e) {
		}

		try {
			Element eBackgroundColor = root.element("BackgroundColor");
			int r = Integer.parseInt(eBackgroundColor.attributeValue("r"));
			int g = Integer.parseInt(eBackgroundColor.attributeValue("g"));
			int b = Integer.parseInt(eBackgroundColor.attributeValue("b"));
			this.backgroundColor = new Color(r, g, b);
		} catch (Exception e) {
		}

		Element eInitialExtent = root.element("InitialExtent");
		double xmin = Double.parseDouble(eInitialExtent.attributeValue("xmin"));
		double xmax = Double.parseDouble(eInitialExtent.attributeValue("xmax"));
		double ymin = Double.parseDouble(eInitialExtent.attributeValue("ymin"));
		double ymax = Double.parseDouble(eInitialExtent.attributeValue("ymax"));
		this.initialExtentDesc = new ExtentDesc(xmin, xmax, ymin, ymax);

		Element eLayers = root.element("Layers");
		this.layerDescs.clear();
		for (Iterator<Element> iLayer = eLayers.elementIterator(); iLayer
				.hasNext();) {
			Element eLayer = iLayer.next();
			LayerDesc layerDesc = new LayerDesc();
			layerDesc.setName(eLayer.elementTextTrim("Name"));
			layerDesc.setVisible(Boolean.parseBoolean(eLayer
					.elementTextTrim("Visible")));
			layerDesc.setEditable(Boolean.parseBoolean(eLayer
					.elementTextTrim("Editable")));
			layerDesc.setDataSourceType(eLayer
					.elementTextTrim("DataSourceType"));
			layerDesc.setDataSource(eLayer.elementTextTrim("DataSource"));
			layerDesc.setStyle(eLayer.elementTextTrim("Style"));
			layerDescs.add(layerDesc);
		}	
		
		return true;
	}


}
