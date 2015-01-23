package com.gi.engine.carto;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.gi.engine.util.common.PasswordUtil;
import com.vividsolutions.jts.geom.Envelope;

public class MapDescFile extends File {

	private static final long serialVersionUID = -5601942549879741581L;

	public MapDescFile(String pathname) {
		super(pathname);
	}

	private Document generateDocument(MapDesc mapDesc) {
		Document result = null;

		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("Map");

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

			root.addElement("Name").setText(mapDesc.getName());
			root.addElement("Wkid").setText(mapDesc.getWkid());
			root.addElement("AntiAlias").setText(
					String.valueOf(mapDesc.isAntiAlias()));

			Element eBgColor = root.addElement("BackgroundColor");
			Color bgColor = mapDesc.getBackgroundColor();
			eBgColor.addAttribute("r", String.valueOf(bgColor.getRed()));
			eBgColor.addAttribute("g", String.valueOf(bgColor.getGreen()));
			eBgColor.addAttribute("b", String.valueOf(bgColor.getBlue()));

			Element eInitialExtent = root.addElement("InitialExtent");
			Envelope initialExtent = mapDesc.getInitialExtent();
			eInitialExtent.addAttribute("xmin", String.valueOf(initialExtent
					.getMinX()));
			eInitialExtent.addAttribute("xmax", String.valueOf(initialExtent
					.getMaxX()));
			eInitialExtent.addAttribute("ymin", String.valueOf(initialExtent
					.getMinY()));
			eInitialExtent.addAttribute("ymax", String.valueOf(initialExtent
					.getMaxY()));

			Element eLayers = root.addElement("Layers");
			ArrayList<LayerInfo> layerInfos = mapDesc.getLayerInfos();
			for (Iterator<LayerInfo> itr = layerInfos.iterator(); itr.hasNext();) {
				LayerInfo layerInfo = itr.next();
				Element eLayer = eLayers.addElement("Layer");
				eLayer.addElement("Name").setText(layerInfo.getName());
				eLayer.addElement("Visible").setText(
						String.valueOf(layerInfo.isVisible()));
				eLayer.addElement("Editable").setText(
						String.valueOf(layerInfo.isEditable()));
				eLayer.addElement("DataSourceType").setText(
						layerInfo.getDataSourceType());
				eLayer.addElement("DataSource").setText(
						layerInfo.getDataSource());
				eLayer.addElement("Style").setText(layerInfo.getStyle());
				eLayer.addElement("Charset").setText(layerInfo.getCharset());
			}

			result = document;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private Cipher getCipher(String base64md5password, int cipherMode)
			throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(base64md5password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(keySpec);
		byte[] salt = new byte[8];
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(cipherMode, key, paramSpec);
		return cipher;
	}

	/**
	 * Open Map Description File and read MapDesc from no-encrypted file
	 * 
	 * @return MapDesc
	 */
	public MapDesc open() {
		MapDesc result = null;

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

	/**
	 * Open Map Description File and read MapDesc from encrypted file
	 * 
	 * @param base64md5password
	 * @return MapDesc
	 */
	public MapDesc open(String base64md5password) {
		MapDesc result = null;

		try {
			if (this.canRead()) {
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(this));
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				byte[] temp = new byte[1024];
				int size = 0;
				while ((size = in.read(temp)) != -1) {
					out.write(temp, 0, size);
				}
				in.close();
				byte[] content = out.toByteArray();

				Cipher cipher = getCipher(base64md5password,
						Cipher.DECRYPT_MODE);
				byte[] decrypt = cipher.doFinal(content);
				ByteArrayInputStream decryptin = new ByteArrayInputStream(
						decrypt);
				result = parseContent(decryptin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private MapDesc parseContent(InputStream in) {
		MapDesc result = null;

		try {
			MapDesc mapDesc = new MapDesc();

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);
			Element root = document.getRootElement();

			// Try-catch all not necessary properties.

			try {
				mapDesc.setVersion(root.attributeValue("version"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			mapDesc.setName(root.elementTextTrim("Name"));
			mapDesc.setWkid(root.elementTextTrim("Wkid"));
			try {
				mapDesc.setAntiAlias(Boolean.parseBoolean(root
						.elementTextTrim("AntiAlias")));
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Element eBackgroundColor = root.element("BackgroundColor");
				int r = Integer.parseInt(eBackgroundColor.attributeValue("r"));
				int g = Integer.parseInt(eBackgroundColor.attributeValue("g"));
				int b = Integer.parseInt(eBackgroundColor.attributeValue("b"));
				mapDesc.setBackgroundColor(new Color(r, g, b));
			} catch (Exception e) {
				e.printStackTrace();
			}

			Element eInitialExtent = root.element("InitialExtent");
			double xmin = Double.parseDouble(eInitialExtent
					.attributeValue("xmin"));
			double xmax = Double.parseDouble(eInitialExtent
					.attributeValue("xmax"));
			double ymin = Double.parseDouble(eInitialExtent
					.attributeValue("ymin"));
			double ymax = Double.parseDouble(eInitialExtent
					.attributeValue("ymax"));
			mapDesc.setInitialExtent(new Envelope(xmin, xmax, ymin, ymax));

			ArrayList<LayerInfo> layerInfos = mapDesc.getLayerInfos();
			Element eLayers = root.element("Layers");
			for (Iterator<Element> itr = eLayers.elementIterator(); itr
					.hasNext();) {
				Element eLayer = itr.next();
				LayerInfo layerInfo = new LayerInfo();
				layerInfo.setName(eLayer.elementTextTrim("Name"));
				layerInfo.setVisible(Boolean.parseBoolean(eLayer
						.elementTextTrim("Visible")));
				layerInfo.setEditable(Boolean.parseBoolean(eLayer
						.elementTextTrim("Editable")));
				layerInfo.setDataSourceType(eLayer
						.elementTextTrim("DataSourceType"));
				layerInfo.setDataSource(eLayer.elementTextTrim("DataSource"));
				layerInfo.setStyle(eLayer.elementTextTrim("Style"));
				try {
					String charset = eLayer.elementTextTrim("Charset");
					if (!charset.equalsIgnoreCase("null") && !charset.equals("")) {
						layerInfo.setCharset(charset);
					}
				} catch (Exception ex) {
				}
				layerInfos.add(layerInfo);
			}

			result = mapDesc;
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Save MapDesc to MapDescFile
	 * 
	 * @param mapDesc
	 * @return
	 */
	public boolean save(MapDesc mapDesc) {
		boolean result = false;

		try {
			Document document = generateDocument(mapDesc);

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

	/**
	 * Save MapDesc to MapDescFile, the file will be encrypted by password
	 * 
	 * @param mapDesc
	 * @param password
	 * @return
	 */
	public boolean save(MapDesc mapDesc, String password) {
		boolean result = false;

		try {
			Document document = generateDocument(mapDesc);

			CharArrayWriter charWriter = new CharArrayWriter();
			XMLWriter xmlWriter = new XMLWriter(charWriter);
			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();
			byte[] input = charWriter.toString().getBytes();

			String base64md5password = PasswordUtil.base64md5password(password);
			Cipher cipher = getCipher(base64md5password, Cipher.ENCRYPT_MODE);

			byte[] encrypt = cipher.doFinal(input);
			FileOutputStream out = new FileOutputStream(this);
			out.write(encrypt);
			out.flush();
			out.close();

			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
