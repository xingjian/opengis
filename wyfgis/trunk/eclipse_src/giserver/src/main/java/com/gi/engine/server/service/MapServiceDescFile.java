package com.gi.engine.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class MapServiceDescFile extends File implements ServiceDescFile {

	private static final long serialVersionUID = -9164404382767733970L;

	public MapServiceDescFile(String pathname) {
		super(pathname);
	}

	private Document generateDocument(MapServiceDesc mapServiceDesc) {
		Document result = null;

		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("MapService");

			ResourceBundle rb = null;
			try {
				rb = ResourceBundle.getBundle("com.gi.engine.parameters");
			} catch (Exception e) {
			}
			try {
				document.addComment(rb.getString("CONTACT"));
			} catch (Exception e) {
			}
			try {
				root.addAttribute("version", rb.getString("VERSION"));
			} catch (Exception e) {
			}

			root.addElement("Passsword").setText(
					String.valueOf(mapServiceDesc.getPassword()));
			root.addElement("AutoStart").setText(
					String.valueOf(mapServiceDesc.isAutoStart()));
			root.addElement("MinInstances").setText(
					String.valueOf(mapServiceDesc.getMinInstances()));
			root.addElement("MaxInstances").setText(
					String.valueOf(mapServiceDesc.getMaxInstances()));
			root.addElement("Timeout").setText(
					String.valueOf(mapServiceDesc.getTimeout()));
			root.addElement("NeedToken").setText(
					String.valueOf(mapServiceDesc.isNeedToken()));
			root.addElement("DPI").setText(
					String.valueOf(mapServiceDesc.getDpi()));
			root.addElement("MaxResults").setText(
					String.valueOf(mapServiceDesc.getMaxResults()));
			root.addElement("OutputDir").setText(
					String.valueOf(mapServiceDesc.getOutputDir()));
			root.addElement("UseTile").setText(
					String.valueOf(mapServiceDesc.isUseTile()));

			TileInfo tileInfo = mapServiceDesc.getTileInfo();
			if (tileInfo != null) {
				Element eTile = root.addElement("Tile");
				eTile.addElement("TilesDir").setText(
						String.valueOf(tileInfo.getTilesDir()));
				eTile.addElement("CreateOnDemand").setText(
						String.valueOf(tileInfo.isCreateOnDemand()));
				eTile.addElement("ReadCompact").setText(
						String.valueOf(tileInfo.isReadCompact()));
				eTile.addElement("CreateSpread").setText(
						String.valueOf(tileInfo.getCreateSpread()));
				eTile.addElement("Format").setText(
						String.valueOf(tileInfo.getFormat()));
				eTile.addElement("Width").setText(
						String.valueOf(tileInfo.getWidth()));
				eTile.addElement("Height").setText(
						String.valueOf(tileInfo.getHeight()));
				eTile.addElement("OriginX").setText(
						String.valueOf(tileInfo.getOriginX()));
				eTile.addElement("OriginY").setText(
						String.valueOf(tileInfo.getOriginY()));
				Element eLods = eTile.addElement("Lods");
				ArrayList<TileLodInfo> tileLodInfos = tileInfo
						.getTileLodInfos();
				for (Iterator<TileLodInfo> itr = tileLodInfos.iterator(); itr
						.hasNext();) {
					TileLodInfo tileLodInfo = itr.next();
					Element eLod = eLods.addElement("Lod");
					eLod.addAttribute("level", String.format("%d", tileLodInfo
							.getLevel()));
					eLod.addAttribute("resolution", String.format("%f",
							tileLodInfo.getResolution()));
					eLod.addAttribute("scale", String.format("%f", tileLodInfo
							.getScale()));
				}
			}

			result = document;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public AbstractServiceDesc open() {
		MapServiceDesc result = null;

		try {
			if (this.canRead()) {
				FileInputStream in = new FileInputStream(this);
				result = parseContent(in);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private MapServiceDesc parseContent(InputStream in) {
		MapServiceDesc result = null;

		try {
			MapServiceDesc mapServiceDesc = new MapServiceDesc();

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);
			Element root = document.getRootElement();

			// Try-catch all not necessary properties.

			try {
				mapServiceDesc.setVersion(root.attributeValue("version"));
			} catch (Exception ex) {
			}

			try {
				mapServiceDesc.setPassword(root.elementTextTrim("Password"));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setAutoStart(Boolean.parseBoolean(root
						.elementTextTrim("AutoStart")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setMinInstances(Integer.parseInt(root
						.elementTextTrim("MinInstances")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setMaxInstances(Integer.parseInt(root
						.elementTextTrim("MaxInstances")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setTimeout(Integer.parseInt(root
						.elementTextTrim("Timeout")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setNeedToken(Boolean.parseBoolean(root
						.elementTextTrim("NeedToken")));
			} catch (Exception ex) {
			}

			try {
				mapServiceDesc.setDpi(Integer.parseInt(root
						.elementTextTrim("DPI")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setMaxResults(Integer.parseInt(root
						.elementTextTrim("MaxResults")));
			} catch (Exception ex) {
			}
			try {
				mapServiceDesc.setOutputDir(root.elementTextTrim("OutputDir"));
			} catch (Exception ex) {
			}

			try {
				mapServiceDesc.setUseTile(Boolean.parseBoolean(root
						.elementTextTrim("UseTile")));
			} catch (Exception ex) {
			}

			if (mapServiceDesc.isUseTile()) {
				Element eTile = root.element("Tile");
				TileInfo tileInfo = new TileInfo();
				mapServiceDesc.setTileInfo(tileInfo);
				try {
					tileInfo.setTilesDir(eTile.elementTextTrim("TilesDir"));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setCreateOnDemand(Boolean.parseBoolean(eTile
							.elementTextTrim("CreateOnDemand")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setReadCompact(Boolean.parseBoolean(eTile
							.elementTextTrim("ReadCompact")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setCreateSpread(Integer.parseInt(eTile
							.elementTextTrim("CreateSpread")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setFormat(eTile.elementTextTrim("Format"));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setWidth(Integer.parseInt(eTile
							.elementTextTrim("Width")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setHeight(Integer.parseInt(eTile
							.elementTextTrim("Height")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setOriginX(Double.parseDouble(eTile
							.elementTextTrim("OriginX")));
				} catch (Exception ex) {
				}
				try {
					tileInfo.setOriginY(Double.parseDouble(eTile
							.elementTextTrim("OriginY")));
				} catch (Exception ex) {
				}
				Element eTileLods = eTile.element("Lods");
				tileInfo.clearTileLodInfos();
				for (Iterator iTileLod = eTileLods.elementIterator(); iTileLod
						.hasNext();) {
					Element eTileLod = (Element) iTileLod.next();
					TileLodInfo tileLodInfo = new TileLodInfo();
					tileLodInfo.setLevel(Integer.parseInt(eTileLod
							.attributeValue("level")));
					tileLodInfo.setResolution(Double.parseDouble(eTileLod
							.attributeValue("resolution")));
					tileLodInfo.setScale(Double.parseDouble(eTileLod
							.attributeValue("scale")));
					tileInfo.addTileLodInfo(tileLodInfo);
				}
			}

			result = mapServiceDesc;
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean save(AbstractServiceDesc serviceDesc) {
		boolean result = false;

		try {
			MapServiceDesc mapServiceDesc = (MapServiceDesc) serviceDesc;
			Document document = generateDocument(mapServiceDesc);

			XMLWriter xmlWriter = new XMLWriter(new FileWriter(this));
			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();

			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
