package com.gi.gidesk.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapFrame;

import com.gi.gidesk.map.dialog.AboutDialog;
import com.gi.gidesk.map.dialog.MapConfDialog;
import com.gi.gidesk.map.dialog.MapServiceConfDialog;
import com.gi.giengine.map.MapEngine;
import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.desc.MapDesc;

public class MapApp extends JMapFrame {
	{
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 6636364048691698040L;

	private JButton jButtonNew;
	private JButton jButtonAbout;
	private JButton jButtonOpen;
	private JFileChooser jFileChooserNew;
	private JFileChooser jFileChooserOpen;
	private java.awt.Component jToolBar$Separator1;
	private java.awt.Component jToolBar$Separator2;
	private JButton jButtonMapConf;
	private JButton jButtonMapServiceConf;
	private JButton jButtonSave;
	private JToolBar jToolBar;

	private String mapDescFilePath = null;
	private MapEngine mapEngine = new MapEngine();
	
	private MapConfDialog mapConfDialog = null;
	private MapServiceConfDialog mapServiceConfDialog = null;
	private AboutDialog aboutDialog = null;

	public static void main(String[] args) {
		MapApp f = new MapApp();
		f.setVisible(true);
	}

	public MapApp() {
		super();		
		initGUI();
	}
	
	private void initGUI(){
		this.setTitle("GIMap");
		this.enableToolBar(true);
		this.enableStatusBar(true);
		this.enableLayerTable(false);
		this.getMapPane().setBorder(BorderFactory.createMatteBorder(
				1, 1, 1, 1, new java.awt.Color(0, 0, 0)));
		this.getMapPane().setBackground(null);
		this.getMapPane().setDoubleBuffered(true);

		{
			jToolBar = new JToolBar();
			getContentPane().add(jToolBar, BorderLayout.NORTH);
			{
				jButtonNew = new JButton();
				jToolBar.add(jButtonNew);
				jButtonNew.setText("New");
				jButtonNew.setToolTipText("Create A New Map Description File");
				jButtonNew.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/new.png")));
				jButtonNew.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonNew.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonNew.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						newMap();
					}
				});
			}
			{
				jButtonOpen = new JButton();
				jToolBar.add(jButtonOpen);
				jButtonOpen.setText("Open");
				jButtonOpen.setToolTipText("Open Map Description File");
				jButtonOpen.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/open.png")));
				jButtonOpen.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonOpen.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonOpen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMapDescFile();
					}
				});
			}
			{
				jButtonSave = new JButton();
				jToolBar.add(jButtonSave);
				jButtonSave.setText("Save");
				jButtonSave.setToolTipText("Save Map Descripton File");
				jButtonSave.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/save.png")));
				jButtonSave.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonSave.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonSave.setEnabled(false);
				jButtonSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						saveMapDescFile();
					}
				});
			}
			{
				jToolBar$Separator1 = new javax.swing.JToolBar.Separator(
						new Dimension(12, 0));
				jToolBar.add(jToolBar$Separator1);
			}
			{
				jButtonMapConf = new JButton();
				jToolBar.add(jButtonMapConf);
				jButtonMapConf.setText("Configure Map");
				jButtonMapConf.setToolTipText("Configure Map Properties and Datasource");
				jButtonMapConf.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/mapconf.png")));
				jButtonMapConf.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonMapConf.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonMapConf.setEnabled(false);
				jButtonMapConf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMapConfDialog();
					}
				});
			}
			/*
			{
				jButtonMapServiceConf = new JButton();
				jToolBar.add(jButtonMapServiceConf);
				jButtonMapServiceConf.setText("Configure Map Service");
				jButtonMapServiceConf.setToolTipText("Configure Map Service Settings");
				jButtonMapServiceConf.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/mapserviceconf.png")));
				jButtonMapServiceConf.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonMapServiceConf.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonMapServiceConf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMapServiceConfDialog();
					}
				});
			}
			*/
			{
				jToolBar$Separator2 = new javax.swing.JToolBar.Separator(
						new Dimension(12, 0));
				jToolBar.add(jToolBar$Separator2);
			}
			{
				jButtonAbout = new JButton();
				jToolBar.add(jButtonAbout);
				jButtonAbout.setText("About");
				jButtonAbout.setToolTipText("Show About Information");
				jButtonAbout.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/about.png")));
				jButtonAbout.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonAbout.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonAbout.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openAboutDialog();
					}
				});
			}
		}

		setPreferredSize(new Dimension(800, 600));
		pack();
		setExtendedState(MAXIMIZED_BOTH);
	}
	
	public MapEngine getMapEngine() {
		return mapEngine;
	}

	public MapContext getMapContext(){
		MapContext mapContext = this.getMapPane().getMapContext();
		
		if(mapContext==null){
			mapContext = new DefaultMapContext();
		}
		
		return mapContext;
	}

	private JFileChooser getJFileChooserNew() {
		if (jFileChooserNew == null) {
			jFileChooserNew = new JFileChooser();
			jFileChooserNew.setDialogTitle("Select New Map Directory");
			FileFilter filter = new FileFilter() {
				public boolean accept(File file) {
					return (file.isDirectory());
				}

				public String getDescription() {
					return "Directory";
				}
			};
			jFileChooserNew.setFileFilter(filter);

			// To last used directory
			try {
				Properties properties = new Properties();
				properties
						.load(new FileInputStream(getAppPropertiesFilePath()));
				String dir = properties.getProperty("LAST_DIR");
				if (dir != null) {
					File file = new File(dir);
					if (file.exists()) {
						jFileChooserNew.setSelectedFile(file);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return jFileChooserNew;
	}

	private JFileChooser getJFileChooserOpen() {
		if (jFileChooserOpen == null) {
			jFileChooserOpen = new JFileChooser();
			jFileChooserOpen.setDialogTitle("Open Map Description File");
			FileFilter filter = new FileFilter() {
				public boolean accept(File file) {
					return (file.isDirectory() || file.getName().toLowerCase()
							.endsWith("map.desc"));
				}

				public String getDescription() {
					return "Map Description File (map.desc)";
				}
			};
			jFileChooserOpen.setFileFilter(filter);

			// To last used directory
			try {
				Properties properties = new Properties();
				properties
						.load(new FileInputStream(getAppPropertiesFilePath()));
				String dir = properties.getProperty("LAST_DIR");
				if (dir != null) {
					File file = new File(dir);
					if (file.exists()) {
						jFileChooserOpen.setSelectedFile(file);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return jFileChooserOpen;
	}

	private String getAppPropertiesFilePath() {
		return this.getClass().getResource("").getPath() + File.separator
				+ "app.properties";
	}
	
	private void newMap(){
		try{
			JFileChooser jFileChooser = getJFileChooserNew();
			int returnVal = jFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String mapDirPath = jFileChooser.getSelectedFile().getAbsolutePath();

				this.getMapEngine().dispose();
				this.getMapEngine().newMapDesc(mapDirPath);
				this.setMapContext(new DefaultMapContext());
				this.getMapPane().setBackground(null);
				this.repaint();

				this.getMapConfDialog().setVisible(false);
				jButtonMapConf.setEnabled(true);
			}			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(this,
					"New Map Error!");
		}
	}

	private void openMapDescFile() {
		JFileChooser jFileChooser = getJFileChooserOpen();
		int returnVal = jFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mapDescFilePath = jFileChooser.getSelectedFile().getAbsolutePath();

			try {
				String dir = jFileChooser.getSelectedFile().getParent();
				File file = new File(getAppPropertiesFilePath());
				if (!file.exists()) {
					file.createNewFile();
				}
				Properties properties = new Properties();
				properties.load(new FileInputStream(file));
				properties.setProperty("LAST_DIR", dir);
				OutputStream out = new FileOutputStream(
						getAppPropertiesFilePath());
				properties.store(out, "Last used directory");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				MapDesc mapDesc = new MapDesc(mapDescFilePath);
				mapEngine.loadMapDesc(mapDesc);
				MapContext mapContext = mapEngine.getMapContext();
				if (mapContext != null) {
					this.getMapPane().setBackground(
							mapDesc.getBackgroundColor());
					this.setMapContext(mapContext);
					
					pack();
					setExtendedState(MAXIMIZED_BOTH);
					
					this.getMapConfDialog().setVisible(false);
					jButtonMapConf.setEnabled(true);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Open Map Description File Error!");
			}
		}
	}

	private void saveMapDescFile() {
		try {
			File file = new File(mapDescFilePath);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(file);
			Element root = document.getRootElement();
			
			MapDesc mapDesc = this.mapEngine.getMapDesc();
			root.element("WKID").setData(mapDesc.getWkid());
			root.element("AntiAlias").setData(mapDesc.isAntiAlias());
			Color bgColor = mapDesc.getBackgroundColor();
			Element eBgColor = root.element("BackgroundColor");
			eBgColor.attribute("r").setData(bgColor.getRed());
			eBgColor.attribute("g").setData(bgColor.getGreen());
			eBgColor.attribute("b").setData(bgColor.getBlue());
			ExtentDesc initialExtentDesc = mapDesc.getInitialExtentDesc();
			Element eInitialExtent = root.element("InitialExtent");
			eInitialExtent.attribute("xmin").setData(initialExtentDesc.getXmin());
			eInitialExtent.attribute("xmax").setData(initialExtentDesc.getXmax());
			eInitialExtent.attribute("ymin").setData(initialExtentDesc.getYmin());
			eInitialExtent.attribute("ymax").setData(initialExtentDesc.getYmax());
			
			XMLWriter xmlWriter=new XMLWriter(new FileWriter(mapDescFilePath));
			xmlWriter.write(document);
			xmlWriter.close();

			setSaveEnable(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Save Map Description File Error!");
		}
	}
	
	public void setSaveEnable(boolean enable){
		jButtonSave.setEnabled(enable);
	}
	
	private MapConfDialog getMapConfDialog(){
		if(mapConfDialog==null){
			mapConfDialog = new MapConfDialog(this);
		}
		
		return mapConfDialog;
	}

	private void openMapConfDialog() {
		MapConfDialog dialog = getMapConfDialog();
		dialog.setVisible(true);
	}
	
	private MapServiceConfDialog getMapServiceConfDialog(){
		if(mapServiceConfDialog==null){
			mapServiceConfDialog = new MapServiceConfDialog(this);
		}
		
		return mapServiceConfDialog;
	}

	private void openMapServiceConfDialog() {
		MapServiceConfDialog dialog = getMapServiceConfDialog();
		dialog.setVisible(true);
	}
	
	private AboutDialog geAboutDialog(){
		if(aboutDialog==null){
			aboutDialog = new AboutDialog();
		}
		
		return aboutDialog;
	}
	
	private void openAboutDialog(){
		AboutDialog dialog = geAboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
	}
	

}
