package com.gi.desktop.maptool.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.gi.desktop.maptool.MapToolApp;
import com.gi.engine.carto.Map;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.server.service.MapServiceDescFile;
import com.gi.engine.server.service.TileInfo;
import com.gi.engine.server.service.TileLodInfo;
import com.gi.engine.util.common.PasswordUtil;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class MapServiceConfDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3361714018686183548L;
	private JButton jButtonSave;
	private JButton jButtonClose;
	private JPanel jPanel1;
	private JTextField jTextFieldPassword;
	private JCheckBox jCheckBoxNeedToken;
	private JFileChooser jFileChooserAgsSchema;
	private JButton jButtonLoadAgsSchema;
	private JButton jButtonGetOriginFromMap;
	private JButton jButtonLevelRemove;
	private JButton jButtonLevelSuggest;
	private JButton jButtonLevelAdd;
	private JButton jButtonLevelRemoveAll;
	private JLabel jLabel13;
	private JList jListLevels;
	private JTextField jTextFieldHeight;
	private JTextField jTextFieldWidth;
	private JLabel jLabel12;
	private JLabel jLabel11;
	private JTextField jTextFieldOriginY;
	private JLabel jLabel10;
	private JTextField jTextFieldOriginX;
	private JLabel jLabel9;
	private JComboBox jComboBoxFormat;
	private JCheckBox jCheckBoxCreateOnDemand;
	private JCheckBox jCheckBoxReadCompact;
	private JButton jButtonBrowseTile;
	private JTextField jTextFieldTileDir;
	private JLabel jLabel8;
	private JPanel jPanelTile;
	private JCheckBox jCheckBoxUseTile;
	private JButton jButtonBrowseOutput;
	private JFileChooser jFileChooserOutput;
	private JTextField jTextFieldOutputDir;
	private JLabel jLabel7;
	private JLabel jLabel5;
	private JTextField jTextFieldMaxResults;
	private JLabel jLabel6;
	private JTextField jTextFieldDPI;
	private JTextField jTextFieldTimeout;
	private JLabel jLabel4;
	private JLabel jLabel3;
	private JTextField jTextFieldMaxInstances;
	private JTextField jTextFieldMinInstances;
	private JLabel jLabel2;
	private JCheckBox jCheckBoxAutoStart;
	private JLabel jLabel1;
	private JPanel jPanel4;
	private JPanel jPanel3;
	private JSplitPane jSplitPane1;
	private JPanel jPanel2;

	private DefaultComboBoxModel jListLevelsModel = new DefaultComboBoxModel();

	private MapToolApp app;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public MapServiceConfDialog(MapToolApp app) {
		super(app);
		this.app = app;
		initGUI();
	}

	private void addLevelFromMap() {
		try {
			Map map = app.getMap();
			ReferencedEnvelope env = map.getExtent();
			int width = app.getMapPane().getWidth();
			int height = app.getMapPane().getHeight();
			int dpi = Integer.valueOf(this.jTextFieldDPI.getText());
			double resolution = map.computeResolution(env, width, height);
			double scale = map.computeScale(env, width, height, dpi);
			TileLodInfo info = new TileLodInfo();
			info.setResolution(resolution);
			info.setScale(scale);
			LodItem item = new LodItem();
			item.setTileLodInfo(info);

			int count = jListLevelsModel.getSize();
			int index = count;
			for (int i = 0; i < count; i++) {
				LodItem lod = (LodItem) jListLevelsModel.getElementAt(i);
				double r = lod.getTileLodInfo().getResolution();
				if (resolution > r) {
					index = i;
					break;
				} else if (resolution == r) {
					return;
				}
			}
			jListLevelsModel.insertElementAt(item, index);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Set Level Error!");
		}
	}

	private void chooseOutputDir() {
		JFileChooser dialog = getJFileChooserOutput();
		int val = dialog.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			this.jTextFieldOutputDir.setText(dialog.getSelectedFile()
					.getAbsolutePath());
		}
	}

	private JButton getJButtonAdd() {
		if (jButtonLevelAdd == null) {
			jButtonLevelAdd = new JButton();
			jButtonLevelAdd.setText("Add From Map");
			jButtonLevelAdd.setBounds(250, 277, 132, 31);
			jButtonLevelAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					addLevelFromMap();
				}
			});
		}
		return jButtonLevelAdd;
	}

	private JButton getJButtonBrowse() {
		if (jButtonBrowseOutput == null) {
			jButtonBrowseOutput = new JButton();
			jButtonBrowseOutput.setText("Browse");
			jButtonBrowseOutput.setBounds(285, 332, 87, 29);
			jButtonBrowseOutput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					chooseOutputDir();
				}
			});
		}
		return jButtonBrowseOutput;
	}

	private JButton getJButtonBrowseTile() {
		if (jButtonBrowseTile == null) {
			jButtonBrowseTile = new JButton();
			jButtonBrowseTile.setText("Browse");
			jButtonBrowseTile.setBounds(295, 46, 87, 29);
		}
		return jButtonBrowseTile;
	}

	private JButton getJButtonGetOriginFromMap() {
		if (jButtonGetOriginFromMap == null) {
			jButtonGetOriginFromMap = new JButton();
			jButtonGetOriginFromMap.setText("{");
			jButtonGetOriginFromMap.setBounds(12, 171, 24, 66);
			jButtonGetOriginFromMap.setToolTipText("Get Origin From Map");
			jButtonGetOriginFromMap.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					getOriginFromMap();
				}
			});
		}
		return jButtonGetOriginFromMap;
	}

	private JButton getJButtonLevelRemove() {
		if (jButtonLevelRemove == null) {
			jButtonLevelRemove = new JButton();
			jButtonLevelRemove.setText("Remove");
			jButtonLevelRemove.setBounds(250, 319, 132, 29);
			jButtonLevelRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					removeSelectedLevel();
				}
			});
		}
		return jButtonLevelRemove;
	}

	private JButton getJButtonLevelRemoveAll() {
		if (jButtonLevelRemoveAll == null) {
			jButtonLevelRemoveAll = new JButton();
			jButtonLevelRemoveAll.setText("Remove All");
			jButtonLevelRemoveAll.setBounds(250, 359, 132, 29);
			jButtonLevelRemoveAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					removeAllLevels();
				}
			});
		}
		return jButtonLevelRemoveAll;
	}

	private JButton getJButtonLevelSuggest() {
		if (jButtonLevelSuggest == null) {
			jButtonLevelSuggest = new JButton();
			jButtonLevelSuggest.setText("Suggest");
			jButtonLevelSuggest.setBounds(250, 399, 132, 29);
			jButtonLevelSuggest.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					suggestLevels();
				}
			});
		}
		return jButtonLevelSuggest;
	}

	private JCheckBox getJCheckBoxCreateOnDemand() {
		if (jCheckBoxCreateOnDemand == null) {
			jCheckBoxCreateOnDemand = new JCheckBox();
			jCheckBoxCreateOnDemand.setText("Create On Demand");
			jCheckBoxCreateOnDemand.setBounds(12, 79, 244, 26);
			jCheckBoxCreateOnDemand.setSelected(true);
		}
		return jCheckBoxCreateOnDemand;
	}


	private JCheckBox getJCheckBoxReadCompact() {
		if (jCheckBoxReadCompact == null) {
			jCheckBoxReadCompact = new JCheckBox();
			jCheckBoxReadCompact.setText("Read ArcGIS Compact Tiles");
			jCheckBoxReadCompact.setBounds(12, 102, 244, 26);
			jCheckBoxReadCompact.setToolTipText("Select for reading ArcGIS compact tiles");
			jCheckBoxReadCompact.setSelected(false);
		}
		return jCheckBoxReadCompact;
	}

	private JCheckBox getJCheckBoxUseTile() {
		if (jCheckBoxUseTile == null) {
			jCheckBoxUseTile = new JCheckBox();
			jCheckBoxUseTile.setText("Use Tile");
			jCheckBoxUseTile.setBounds(10, 10, 160, 23);
			jCheckBoxUseTile.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					jPanelTile.setVisible(jCheckBoxUseTile.isSelected());
				}
			});
		}
		return jCheckBoxUseTile;
	}

	private JComboBox getJComboBoxFormate() {
		if (jComboBoxFormat == null) {
			ComboBoxModel jComboBoxFormateModel = new DefaultComboBoxModel(
					new String[] { "PNG", "JPEG" });
			jComboBoxFormat = new JComboBox();
			jComboBoxFormat.setModel(jComboBoxFormateModel);
			jComboBoxFormat.setBounds(261, 86, 120, 29);
		}
		return jComboBoxFormat;
	}

	private JFileChooser getJFileChooserOutput() {
		if (jFileChooserOutput == null) {
			jFileChooserOutput = new JFileChooser();
			jFileChooserOutput.setDialogType(JFileChooser.OPEN_DIALOG);
			jFileChooserOutput
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return jFileChooserOutput;
	}

	private JLabel getJLabel10() {
		if (jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("OriginY:");
			jLabel10.setBounds(40, 213, 68, 24);
		}
		return jLabel10;
	}

	private JLabel getJLabel11() {
		if (jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("Width:");
			jLabel11.setBounds(217, 171, 57, 22);
		}
		return jLabel11;
	}

	private JLabel getJLabel12() {
		if (jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("Height:");
			jLabel12.setBounds(219, 213, 55, 22);
		}
		return jLabel12;
	}

	private JLabel getJLabel13() {
		if (jLabel13 == null) {
			jLabel13 = new JLabel();
			jLabel13.setText("Levels:");
			jLabel13.setBounds(12, 248, 369, 22);
		}
		return jLabel13;
	}

	private JLabel getJLabel8() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("Tiles Directory:");
			jLabel8.setBounds(12, 12, 369, 22);
		}
		return jLabel8;
	}

	private JLabel getJLabel9() {
		if (jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("OriginX:");
			jLabel9.setBounds(40, 171, 68, 24);
		}
		return jLabel9;
	}

	private JList getJListLevels() {
		if (jListLevels == null) {
			jListLevels = new JList();
			jListLevels.setModel(jListLevelsModel);
			jListLevels.setBounds(12, 276, 232, 225);
		}
		return jListLevels;
	}

	private JPanel getJPanelTile() {
		if (jPanelTile == null) {
			jPanelTile = new JPanel();
			jPanelTile.setBounds(10, 42, 393, 513);
			jPanelTile.setLayout(null);
			// jPanelTile.setVisible(false);
			jPanelTile.add(getJLabel8());
			jPanelTile.add(getJTextFieldTileDir());
			jPanelTile.add(getJButtonBrowseTile());
			jPanelTile.add(getJCheckBoxCreateOnDemand());
			jPanelTile.add(getJCheckBoxReadCompact());
			jPanelTile.add(getJComboBoxFormate());
			jPanelTile.add(getJLabel9());
			jPanelTile.add(getJTextFieldOriginX());
			jPanelTile.add(getJLabel10());
			jPanelTile.add(getJTextFieldOriginY());
			jPanelTile.add(getJLabel11());
			jPanelTile.add(getJLabel12());
			jPanelTile.add(getJTextFieldWidth());
			jPanelTile.add(getJTextFieldHeight());
			jPanelTile.add(getJListLevels());
			jPanelTile.add(getJLabel13());
			jPanelTile.add(getJButtonLevelRemoveAll());
			jPanelTile.add(getJButtonAdd());
			jPanelTile.add(getJButtonLevelSuggest());
			jPanelTile.add(getJButtonLevelRemove());
			jPanelTile.add(getJButtonGetOriginFromMap());
			jPanelTile.add(getJButtonLoadAgsSchema());
		}
		return jPanelTile;
	}

	private JTextField getJTextFieldHeight() {
		if (jTextFieldHeight == null) {
			jTextFieldHeight = new JTextField();
			jTextFieldHeight.setText("256");
			jTextFieldHeight.setBounds(281, 211, 79, 29);
		}
		return jTextFieldHeight;
	}

	private JTextField getJTextFieldOriginX() {
		if (jTextFieldOriginX == null) {
			jTextFieldOriginX = new JTextField();
			jTextFieldOriginX.setText("-180");
			jTextFieldOriginX.setBounds(108, 169, 84, 29);
		}
		return jTextFieldOriginX;
	}

	private JTextField getJTextFieldOriginY() {
		if (jTextFieldOriginY == null) {
			jTextFieldOriginY = new JTextField();
			jTextFieldOriginY.setText("90");
			jTextFieldOriginY.setBounds(108, 211, 84, 29);
		}
		return jTextFieldOriginY;
	}

	private JTextField getJTextFieldTileDir() {
		if (jTextFieldTileDir == null) {
			jTextFieldTileDir = new JTextField();
			jTextFieldTileDir.setBounds(12, 46, 283, 29);
		}
		return jTextFieldTileDir;
	}

	private JTextField getJTextFieldWidth() {
		if (jTextFieldWidth == null) {
			jTextFieldWidth = new JTextField();
			jTextFieldWidth.setText("256");
			jTextFieldWidth.setBounds(281, 169, 79, 29);
		}
		return jTextFieldWidth;
	}

	private void getOriginFromMap() {
		try {
			ReferencedEnvelope env = app.getMap().getFullExtent();
			this.jTextFieldOriginX.setText(String.valueOf(Math.floor(env
					.getMinX())));
			this.jTextFieldOriginY.setText(String.valueOf(Math.ceil(env
					.getMaxY())));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Get Origin From Map Failed!");
		}
	}

	private void initGUI() {
		try {
			{
				setTitle("Configure Map Service");
				BoxLayout thisLayout = new BoxLayout(getContentPane(),
						javax.swing.BoxLayout.Y_AXIS);
				getContentPane().setLayout(thisLayout);
				setResizable(false);
				{
					jPanel2 = new JPanel();
					BorderLayout jPanel2Layout = new BorderLayout();
					jPanel2.setLayout(jPanel2Layout);
					getContentPane().add(jPanel2);
					{
						jSplitPane1 = new JSplitPane();
						jPanel2.add(jSplitPane1, BorderLayout.CENTER);
						jSplitPane1.setPreferredSize(new java.awt.Dimension(
								804, 556));
						jSplitPane1.setSize(800, 385);
						{
							jPanel3 = new JPanel();
							jSplitPane1.add(jPanel3, JSplitPane.RIGHT);
							jPanel3.setPreferredSize(new java.awt.Dimension(
									243, 383));
							jPanel3.setLayout(null);
							jPanel3.add(getJCheckBoxUseTile());
							jPanel3.add(getJPanelTile());
						}
						{
							jPanel4 = new JPanel();
							jPanel4.setLayout(null);
							jSplitPane1.add(jPanel4, JSplitPane.LEFT);
							jPanel4.setPreferredSize(new java.awt.Dimension(
									388, 445));
							{
								jLabel1 = new JLabel();
								jPanel4.add(jLabel1);
								jLabel1.setText("Map Password: ");
								jLabel1.setBounds(12, 12, 133, 22);
							}
							{
								jTextFieldPassword = new JTextField();
								jPanel4.add(jTextFieldPassword);
								jTextFieldPassword.setBounds(145, 9, 231, 29);
							}
							{
								jCheckBoxAutoStart = new JCheckBox();
								jPanel4.add(jCheckBoxAutoStart);
								jCheckBoxAutoStart.setText("Auto Start");
								jCheckBoxAutoStart.setBounds(12, 56, 168, 26);
								jCheckBoxAutoStart.setSelected(true);
							}
							{
								jCheckBoxNeedToken = new JCheckBox();
								jPanel4.add(jCheckBoxNeedToken);
								jCheckBoxNeedToken.setText("Need Token");
								jCheckBoxNeedToken.setBounds(191, 56, 180, 25);
							}
							{
								jLabel2 = new JLabel();
								jPanel4.add(jLabel2);
								jLabel2.setText("Instance Number: ");
								jLabel2.setBounds(12, 105, 154, 22);
							}
							{
								jTextFieldMinInstances = new JTextField();
								jPanel4.add(jTextFieldMinInstances);
								jTextFieldMinInstances.setText("1");
								jTextFieldMinInstances.setBounds(166, 102, 66,
										28);
								jTextFieldMinInstances.setSize(64, 28);
							}
							{
								jTextFieldMaxInstances = new JTextField();
								jPanel4.add(jTextFieldMaxInstances);
								jTextFieldMaxInstances.setText("20");
								jTextFieldMaxInstances.setBounds(254, 102, 64,
										29);
							}
							{
								jLabel3 = new JLabel();
								jPanel4.add(jLabel3);
								jLabel3.setText("~");
								jLabel3.setBounds(236, 105, 12, 22);
							}
							{
								jLabel4 = new JLabel();
								jPanel4.add(jLabel4);
								jLabel4.setText("Timeout Seconds: ");
								jLabel4.setBounds(12, 158, 158, 22);
							}
							{
								jTextFieldTimeout = new JTextField();
								jPanel4.add(jTextFieldTimeout);
								jTextFieldTimeout.setText("60");
								jTextFieldTimeout.setBounds(170, 155, 126, 29);
							}
							{
								jLabel5 = new JLabel();
								jPanel4.add(jLabel5);
								jLabel5.setText("DPI: ");
								jLabel5.setBounds(12, 255, 48, 22);
							}
							{
								jTextFieldDPI = new JTextField();
								jPanel4.add(jTextFieldDPI);
								jTextFieldDPI.setText("96");
								jTextFieldDPI.setBounds(60, 252, 48, 29);
							}
							{
								jLabel6 = new JLabel();
								jPanel4.add(jLabel6);
								jLabel6.setText("Max Return Results: ");
								jLabel6.setBounds(12, 206, 174, 22);
							}
							{
								jTextFieldMaxResults = new JTextField();
								jPanel4.add(jTextFieldMaxResults);
								jTextFieldMaxResults.setText("-1");
								jTextFieldMaxResults.setBounds(186, 203, 123,
										29);
							}
							{
								jLabel7 = new JLabel();
								jPanel4.add(jLabel7);
								jLabel7.setText("Output Directory: ");
								jLabel7.setBounds(12, 304, 359, 22);
							}
							{
								jTextFieldOutputDir = new JTextField();
								jPanel4.add(jTextFieldOutputDir);
								jPanel4.add(getJButtonBrowse());
								jTextFieldOutputDir.setBounds(12, 332, 273, 29);
							}
						}
					}

					jPanel1 = new JPanel();
					getContentPane().add(jPanel1);
					jPanel1.setPreferredSize(new java.awt.Dimension(792, 42));
					{
						jButtonSave = new JButton();
						jPanel1.add(jButtonSave);
						jButtonSave.setText("Save");
						jButtonSave.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								saveMapServiceDesc();
							}
						});
					}
					{
						jButtonClose = new JButton();
						jPanel1.add(jButtonClose);
						jButtonClose.setText("Close");
						jButtonClose.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								setVisible(false);
							}
						});
					}
				}
			}
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeAllLevels() {
		jListLevelsModel.removeAllElements();
	}

	private void removeSelectedLevel() {
		Object[] items = jListLevels.getSelectedValues();
		for (int i = 0, count = items.length; i < count; i++) {
			jListLevelsModel.removeElement(items[i]);
		}
	}

	public void loadMapServiceDesc(MapServiceDescFile mapServiceDescFile) {
		MapServiceDesc desc = (MapServiceDesc) mapServiceDescFile.open();

		if (desc != null) {
			this.jCheckBoxAutoStart.setSelected(desc.isAutoStart());
			this.jCheckBoxNeedToken.setSelected(desc.isNeedToken());
			this.jTextFieldMinInstances.setText(String.valueOf(desc
					.getMinInstances()));
			this.jTextFieldMaxInstances.setText(String.valueOf(desc
					.getMaxInstances()));
			this.jTextFieldTimeout.setText(String.valueOf(desc.getTimeout()));
			this.jTextFieldMaxResults.setText(String.valueOf(desc
					.getMaxResults()));
			this.jTextFieldDPI.setText(String.valueOf(desc.getDpi()));
			this.jTextFieldOutputDir.setText(desc.getOutputDir());
			this.jCheckBoxUseTile.setSelected(desc.isUseTile());
			TileInfo tileInfo = desc.getTileInfo();
			if (tileInfo != null) {
				this.jTextFieldTileDir.setText(tileInfo.getTilesDir());
				this.jCheckBoxCreateOnDemand.setSelected(tileInfo
						.isCreateOnDemand());
				this.jCheckBoxReadCompact.setSelected(tileInfo
						.isReadCompact());
				this.jComboBoxFormat.setSelectedItem(tileInfo.getFormat()
						.toUpperCase());
				this.jTextFieldOriginX.setText(String.valueOf(tileInfo
						.getOriginX()));
				this.jTextFieldOriginY.setText(String.valueOf(tileInfo
						.getOriginY()));
				this.jTextFieldWidth.setText(String
						.valueOf(tileInfo.getWidth()));
				this.jTextFieldHeight.setText(String.valueOf(tileInfo
						.getHeight()));
				jListLevelsModel.removeAllElements();
				for (int i = 0, count = tileInfo.getTileLodInfos().size(); i < count; i++) {
					TileLodInfo tileLodInfo = tileInfo.getTileLodInfos().get(i);
					LodItem item = new LodItem();
					item.setTileLodInfo(tileLodInfo);
					jListLevelsModel.addElement(item);
				}
			}
		}
	}

	private void saveMapServiceDesc() {
		try {
			MapServiceDesc desc = new MapServiceDesc();
			String password = this.jTextFieldPassword.getText();
			if (password != null && !"".equals(password)) {
				desc.setPassword(PasswordUtil.base64md5password(password));
			} else {
				desc.setPassword("");
			}
			desc.setAutoStart(this.jCheckBoxAutoStart.isSelected());
			desc.setNeedToken(this.jCheckBoxNeedToken.isSelected());
			desc.setMinInstances(Integer.valueOf(this.jTextFieldMinInstances
					.getText()));
			desc.setMaxInstances(Integer.valueOf(this.jTextFieldMaxInstances
					.getText()));
			desc.setTimeout(Integer.valueOf(this.jTextFieldTimeout.getText()));
			desc.setMaxResults(Integer.valueOf(this.jTextFieldMaxResults
					.getText()));
			desc.setDpi(Integer.valueOf(this.jTextFieldDPI.getText()));
			desc.setOutputDir(this.jTextFieldOutputDir.getText());
			desc.setUseTile(this.jCheckBoxUseTile.isSelected());
			if (this.jCheckBoxUseTile.isSelected()) {
				TileInfo tileInfo = new TileInfo();
				tileInfo.setTilesDir(this.jTextFieldTileDir.getText());
				tileInfo.setCreateOnDemand(this.jCheckBoxCreateOnDemand
						.isSelected());
				tileInfo.setReadCompact(this.jCheckBoxReadCompact
						.isSelected());
				tileInfo.setFormat(this.jComboBoxFormat.getSelectedItem()
						.toString());
				tileInfo.setOriginX(Double.valueOf(this.jTextFieldOriginX
						.getText()));
				tileInfo.setOriginY(Double.valueOf(this.jTextFieldOriginY
						.getText()));
				tileInfo.setWidth(Integer.valueOf(this.jTextFieldWidth
						.getText()));
				tileInfo.setHeight(Integer.valueOf(this.jTextFieldHeight
						.getText()));
				for (int i = 0, count = jListLevelsModel.getSize(); i < count; i++) {
					LodItem item = (LodItem) jListLevelsModel.getElementAt(i);
					TileLodInfo tileLodInfo = item.getTileLodInfo();
					tileLodInfo.setLevel(i);
					tileInfo.addTileLodInfo(tileLodInfo);
				}

				desc.setTileInfo(tileInfo);
			}

			MapServiceDescFile mapServiceDescFile = new MapServiceDescFile(app
					.getMapServiceDescFilePath());
			if (mapServiceDescFile.save(desc)) {
				JOptionPane.showMessageDialog(this, "Save Success!");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error!");
		}
	}

	private void suggestLevels() {
		String levelNum = JOptionPane.showInputDialog("Level Numbers:");
		try {
			jListLevelsModel.removeAllElements();
			int num = Integer.valueOf(levelNum);
			Map map = app.getMap();
			ReferencedEnvelope fullEnv = map.getFullExtent();
			int width = app.getMapPane().getWidth();
			int height = app.getMapPane().getHeight();
			int dpi = Integer.valueOf(this.jTextFieldDPI.getText());
			double maxResolution = map
					.computeResolution(fullEnv, width, height);
			double maxScale = map.computeScale(fullEnv, width, height, dpi);
			for (int i = 0; i < num; i++) {
				TileLodInfo info = new TileLodInfo();
				double factor = Math.pow(2, i);
				info.setResolution(maxResolution / factor);
				info.setScale(maxScale / factor);

				LodItem item = new LodItem();
				item.setTileLodInfo(info);
				jListLevelsModel.addElement(item);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Set Level Numbers Error!");
		}
	}

	private JButton getJButtonLoadAgsSchema() {
		if (jButtonLoadAgsSchema == null) {
			jButtonLoadAgsSchema = new JButton();
			jButtonLoadAgsSchema.setText("Load ArcGIS Server Tile Schema");
			jButtonLoadAgsSchema.setBounds(12, 131, 370, 29);
			jButtonLoadAgsSchema.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					loadAgsSchema();
				}
			});
		}
		return jButtonLoadAgsSchema;
	}

	private JFileChooser getJFileChooserAgsSchema() {
		if (jFileChooserAgsSchema == null) {
			jFileChooserAgsSchema = new JFileChooser();
			jFileChooserAgsSchema.setMultiSelectionEnabled(false);
			jFileChooserAgsSchema.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().equalsIgnoreCase("conf.xml");
				}

				@Override
				public String getDescription() {
					return "ArcGIS Server Tile Schema (conf.xml)";
				}
			});
		}
		return jFileChooserAgsSchema;
	}

	@SuppressWarnings("unchecked")
	private void loadAgsSchema() {
		JFileChooser dialog = getJFileChooserAgsSchema();
		int val = dialog.showOpenDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			File agsSchema = dialog.getSelectedFile();
			try {
				FileInputStream in = new FileInputStream(agsSchema);
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(in);
				Element root = document.getRootElement();
				Element eTileCacheInfo = root.element("TileCacheInfo");

				Element eTileOrigin = eTileCacheInfo.element("TileOrigin");
				this.jTextFieldOriginX.setText(eTileOrigin.elementText("X"));
				this.jTextFieldOriginY.setText(eTileOrigin.elementText("Y"));

				this.jTextFieldWidth.setText(eTileCacheInfo
						.elementText("TileCols"));
				this.jTextFieldHeight.setText(eTileCacheInfo
						.elementText("TileRows"));

				jListLevelsModel.removeAllElements();
				Element eLODInfos = eTileCacheInfo.element("LODInfos");
				for (Iterator iLODInfos = eLODInfos.elementIterator(); iLODInfos
						.hasNext();) {
					Element eLODInfo = (Element) iLODInfos.next();
					TileLodInfo tileLodInfo = new TileLodInfo();
					tileLodInfo.setLevel(Integer.valueOf(eLODInfo
							.elementText("LevelID")));
					tileLodInfo.setScale(Double.valueOf(eLODInfo
							.elementText("Scale")));
					tileLodInfo.setResolution(Double.valueOf(eLODInfo
							.elementText("Resolution")));

					LodItem item = new LodItem();
					item.setTileLodInfo(tileLodInfo);
					jListLevelsModel.addElement(item);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Load Error!");
			}
		}
	}

}
