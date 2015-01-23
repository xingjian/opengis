package com.gi.desktop.maptool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapFrame;

import sun.misc.BASE64Encoder;

import com.gi.desktop.maptool.dialog.AboutDialog;
import com.gi.desktop.maptool.dialog.MapConfDialog;
import com.gi.desktop.maptool.dialog.MapServiceConfDialog;
import com.gi.engine.carto.Map;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.carto.MapDescFile;
import com.gi.engine.server.service.MapServiceDescFile;
import com.gi.engine.util.ArchitectureUtil;

public class MapToolApp extends JMapFrame {
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
	private java.awt.Component jToolBar$Separator3;
	private JButton jButtonMapConf;
	private JButton jButtonMapServiceConf;
	private JButton jButtonSaveMapDesc;
	private JToolBar jToolBar;

	private MapDesc mapDesc = null;
	private String mapDirPath = null;

	private Map map = new Map();

	private MapConfDialog mapConfDialog = null;
	private MapServiceConfDialog mapServiceConfDialog = null;
	private AboutDialog aboutDialog = null;

	public static void main(String[] args) {
		MapToolApp f = new MapToolApp();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public MapToolApp() {
		super();
		initGUI();
	}

	private void initGUI() {
		this.setTitle("Map Tool");
		this.setIconImage(new ImageIcon(getClass().getClassLoader()
				.getResource("image/giserver.png")).getImage());
		this.enableToolBar(true);
		this.enableStatusBar(true);
		this.enableLayerTable(false);
		this.getMapPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(
						0, 0, 0)));
		this.getMapPane().setBackground(null);
		this.getMapPane().setDoubleBuffered(true);

		{
			jToolBar = new JToolBar();
			getContentPane().add(jToolBar, BorderLayout.NORTH);
			{
				jButtonNew = new JButton();
				jToolBar.add(jButtonNew);
				jButtonNew.setText("New");
				jButtonNew.setToolTipText("Create A New Map Based A Directory");
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
				jButtonOpen.setToolTipText("Open Map Directory");
				jButtonOpen.setIcon(new ImageIcon(getClass().getClassLoader()
						.getResource("image/open.png")));
				jButtonOpen.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonOpen.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonOpen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMap();
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
				jButtonMapConf
						.setToolTipText("Configure Map Properties and Datasource");
				jButtonMapConf.setIcon(new ImageIcon(getClass()
						.getClassLoader().getResource("image/mapconf.png")));
				jButtonMapConf.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonMapConf.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonMapConf.setEnabled(false);
				jButtonMapConf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMapConfDialog();
					}
				});
			}
			{
				jButtonSaveMapDesc = new JButton();
				jToolBar.add(jButtonSaveMapDesc);
				jButtonSaveMapDesc.setText("Save Map");
				jButtonSaveMapDesc.setToolTipText("Save Map Descripton File");
				jButtonSaveMapDesc
						.setIcon(new ImageIcon(getClass().getClassLoader()
								.getResource("image/savemapdesc.png")));
				jButtonSaveMapDesc
						.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonSaveMapDesc
						.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonSaveMapDesc.setEnabled(false);
				jButtonSaveMapDesc.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						saveMapDescFile();
					}
				});
			}
			{
				jToolBar$Separator2 = new javax.swing.JToolBar.Separator(
						new Dimension(12, 0));
				jToolBar.add(jToolBar$Separator2);
			}
			{
				jButtonMapServiceConf = new JButton();
				jToolBar.add(jButtonMapServiceConf);
				jButtonMapServiceConf.setText("Configure Map Service");
				jButtonMapServiceConf
						.setToolTipText("Configure Map Service Settings");
				jButtonMapServiceConf.setIcon(new ImageIcon(getClass()
						.getClassLoader().getResource(
								"image/mapserviceconf.png")));
				jButtonMapServiceConf
						.setVerticalTextPosition(SwingConstants.BOTTOM);
				jButtonMapServiceConf
						.setHorizontalTextPosition(SwingConstants.CENTER);
				jButtonMapServiceConf.setEnabled(false);
				jButtonMapServiceConf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						openMapServiceConfDialog();
					}
				});
			}
			{
				jToolBar$Separator3 = new javax.swing.JToolBar.Separator(
						new Dimension(12, 0));
				jToolBar.add(jToolBar$Separator3);
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

		this.setPreferredSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		//setExtendedState(MAXIMIZED_BOTH);
	}

	public Map getMap() {
		return map;
	}

	public MapContext getMapContext() {
		MapContext mapContext = this.getMapPane().getMapContext();

		if (mapContext == null) {
			mapContext = new DefaultMapContext();
		}

		return mapContext;
	}

	private JFileChooser getJFileChooserNew() {
		if (jFileChooserNew == null) {
			jFileChooserNew = new JFileChooser();
			jFileChooserNew.setDialogTitle("Select New Map Directory");
			jFileChooserNew.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
			jFileChooserOpen.setDialogTitle("Open Map Directory");
			jFileChooserOpen
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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

	private void newMap() {
		try {
			JFileChooser jFileChooser = getJFileChooserNew();
			int returnVal = jFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.mapDirPath = jFileChooser.getSelectedFile()
						.getAbsolutePath();

				this.getMap().dispose();

				this.mapDesc = new MapDesc();
				this.getMap().initByMapDesc(mapDesc, getMapDescFilePath());
				this.setMapContext(new DefaultMapContext());
				this.getMapPane().setBackground(mapDesc.getBackgroundColor());
				this.repaint();

				this.getMapConfDialog().setVisible(false);
				jButtonMapConf.setEnabled(true);
				jButtonMapServiceConf.setEnabled(true);
				jButtonSaveMapDesc.setEnabled(true);
				openMapConfDialog();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "New Map Error!");
		}
	}

	private void openMap() {
		JFileChooser fileOpen = getJFileChooserOpen();
		int returnVal = fileOpen.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mapDirPath = fileOpen.getSelectedFile().getAbsolutePath();

			try {
				File file = new File(getAppPropertiesFilePath());
				if (!file.exists()) {
					file.createNewFile();
				}
				Properties properties = new Properties();
				properties.load(new FileInputStream(file));
				properties.setProperty("LAST_DIR", mapDirPath);
				OutputStream out = new FileOutputStream(
						getAppPropertiesFilePath());
				properties.store(out, "Last used directory");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				MapDescFile mapDescFile = new MapDescFile(this
						.getMapDescFilePath());
				mapDesc = mapDescFile.open();
				if (mapDesc == null) {
					String password = JOptionPane
							.showInputDialog("Is the file has a password?");
					if (password != null && !"".equals(password)) {
						MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("MD5");
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						byte[] md5password = md.digest(password.getBytes());
						BASE64Encoder encoder = new BASE64Encoder();
						String base64md5password = encoder.encode(md5password);
						mapDesc = mapDescFile.open(base64md5password);
					}
				}

				if (mapDesc != null) {
					map.initByMapDesc(mapDesc, this.getMapDescFilePath());
					MapContext mapContext = map.getMapContext();
					if (mapContext != null) {
						this.getMapPane().setBackground(
								mapDesc.getBackgroundColor());
						this.setMapContext(mapContext);

						pack();
						this.getMapPane().reset();
						//setExtendedState(MAXIMIZED_BOTH);

						this.getMapConfDialog().setVisible(false);
						jButtonMapConf.setEnabled(true);
						jButtonMapServiceConf.setEnabled(true);
						jButtonSaveMapDesc.setEnabled(true);
					}
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Open Map Description File Error!");
			}
		}
	}

	private void saveMapDescFile() {
		try {
			String password = JOptionPane
					.showInputDialog("Input encrypt password, no encrypt if leave it blank.");
			MapDescFile file = new MapDescFile(this.getMapDescFilePath());
			if (password != null && "".equals(password.trim())) {
				if (file.save(mapDesc)) {
					JOptionPane.showMessageDialog(this, "Save success!");
				}
			} else if (password != null && !"".equals(password.trim())) {
				if (file.save(mapDesc, password)) {
					JOptionPane.showMessageDialog(this, "Save success!");
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Save Map Description File Error!");
		}
	}

	private MapConfDialog getMapConfDialog() {
		if (mapConfDialog == null) {
			mapConfDialog = new MapConfDialog(this);
		}

		return mapConfDialog;
	}

	private void openMapConfDialog() {
		MapConfDialog dialog = getMapConfDialog();
		dialog.loadMapDesc();
		dialog.setLocation(this.getX()+(this.getWidth() - dialog.getWidth()) / 2, this.getY()+(this
				.getHeight() - dialog.getHeight()) / 2);
		dialog.setVisible(true);
	}

	private MapServiceConfDialog getMapServiceConfDialog() {
		if (mapServiceConfDialog == null) {
			mapServiceConfDialog = new MapServiceConfDialog(this);
		}

		return mapServiceConfDialog;
	}

	private void openMapServiceConfDialog() {
		MapServiceConfDialog dialog = getMapServiceConfDialog();
		MapServiceDescFile file = new MapServiceDescFile(this
				.getMapServiceDescFilePath());
		if (file.exists()) {
			dialog.loadMapServiceDesc(file);
		}
		dialog.setLocation(this.getX()+(this.getWidth() - dialog.getWidth()) / 2, this.getY()+(this
				.getHeight() - dialog.getHeight()) / 2);
		dialog.setVisible(true);
	}

	private AboutDialog geAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
		}

		return aboutDialog;
	}

	private void openAboutDialog() {
		AboutDialog dialog = geAboutDialog();
		dialog.setModal(true);
		dialog.setLocation(this.getX()+(this.getWidth() - dialog.getWidth()) / 2, this.getY()+(this
				.getHeight() - dialog.getHeight()) / 2);
		dialog.setVisible(true);
	}

	public MapDesc getMapDesc() {
		return mapDesc;
	}

	public String getMapDescFilePath() {
		return mapDirPath + File.separator
				+ ArchitectureUtil.MAP_DESC_FILE_NAME;
	}

	public String getMapServiceDescFilePath() {
		return mapDirPath + File.separator
				+ ArchitectureUtil.MAP_SERVICE_DESC_FILE_NAME;
	}

	public String getMapDirPath() {
		return mapDirPath;
	}

}
