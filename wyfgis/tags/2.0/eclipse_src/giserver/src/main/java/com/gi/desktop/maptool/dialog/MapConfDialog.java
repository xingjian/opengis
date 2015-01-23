package com.gi.desktop.maptool.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.desktop.maptool.MapToolApp;
import com.gi.engine.carto.LayerInfo;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.vividsolutions.jts.geom.Envelope;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MapConfDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4199687410535105528L;
	private JTabbedPane jTabbedPane;
	private JPanel jPanelMain;
	private JPanel jPanelName;
	private JPanel jPanelBgColor;
	private JColorChooser jColorChooserBgColor;
	private JPopupMenu jPopupMenuLayer;
	private JList jListLayers;
	private JPanel jPanelLayers;
	private JButton jButtonApply;
	private JButton jButtonLoadCurrentExtent;
	private JButton jButtonLoadCustomCRS;
	private JLabel jLabelWkid;
	private JComboBox jComboBoxWkid;
	private JPanel jPanel3;
	private JLabel jLabel1InitialExtent;
	private JLabel jLabelYmin;
	private JTextField jTextFieldYmin;
	private JLabel jLabelYmax;
	private JTextField jTextFieldYmax;
	private JTextField jTextFieldXmax;
	private JLabel jLabelXmin;
	private JTextField jTextFieldXmin;
	private JLabel jLabelXmax;
	private JPanel jPanel2;
	private JCheckBox jCheckBoxAntiAlias;
	private JButton jButtonClose;
	private JPanel jPanel1;
	private JFileChooser jFileChooserOpenCustomCRSDir;
	private DefaultComboBoxModel jListLayersModel;
	private JMenuItem menuItemAdd;
	private JMenuItem menuItemRemove;
	private JMenuItem menuItemEdit;
	private JMenuItem menuItemMoveUp;
	private JMenuItem menuItemMoveDown;
	private LayerConfDialog layerConfDialog;
	private JTextField jTextFieldName;
	private JLabel jLabel1;


	/**
	 * Auto-generated main method to display this JDialog
	 */

	public MapConfDialog(MapToolApp app) {
		super(app);
		initGUI();

		initWkidItems();
	}

	private void initGUI() {
		try {
			{
				setTitle("Configure Map");
				setResizable(false);
				this.addWindowListener(new WindowListener() {
					public void windowOpened(WindowEvent e) {
						System.out.println("window opened");
					}

					public void windowClosing(WindowEvent e) {
						System.out.println("window closing");
					}

					public void windowClosed(WindowEvent e) {
						System.out.println("window closed");
					}

					public void windowIconified(WindowEvent e) {
						System.out.println("window iconified");
					}

					public void windowDeiconified(WindowEvent e) {
						System.out.println("window deiconified");
					}

					public void windowActivated(WindowEvent e) {
						System.out.println("window activated");
					}

					public void windowDeactivated(WindowEvent e) {
						System.out.println("window deactivated");
					}
				});
				{
					jTabbedPane = new JTabbedPane();
					getContentPane().add(jTabbedPane, BorderLayout.CENTER);
					jTabbedPane.setPreferredSize(new java.awt.Dimension(620,
							310));
					{
						jPanelMain = new JPanel();
						jPanelMain.setLayout(null);
						jTabbedPane.addTab("Main", null, jPanelMain, null);
						jPanelMain.setPreferredSize(new java.awt.Dimension(615,
								205));
						{
							jPanelName = new JPanel();
							jPanelMain.add(jPanelName);
							jPanelName.setBorder(BorderFactory.createMatteBorder(
									1, 1, 1, 1, new java.awt.Color(0, 0, 0)));
							jPanelName.setName("");
							jPanelName.setLayout(null);
							jPanelName.setBounds(0, 0, 613, 41);
							{
								jLabel1 = new JLabel();
								jPanelName.add(jLabel1);
								jLabel1.setText("Map Name:");
								jLabel1.setBounds(7, 13, 90, 19);
							}
							{
								jTextFieldName = new JTextField();
								jPanelName.add(getJTextFieldName());
								jTextFieldName.setText("Untitled");
								jTextFieldName.setBounds(115, 7, 485, 28);
							}
						}
						{
							jCheckBoxAntiAlias = new JCheckBox();
							jPanelMain.add(jCheckBoxAntiAlias);
							jCheckBoxAntiAlias.setText("Anti Alias");
							jCheckBoxAntiAlias.setBounds(0, 45, 89, 26);
						}
						{
							jPanel2 = new JPanel();
							jPanelMain.add(jPanel2);
							jPanel2.setBorder(BorderFactory.createMatteBorder(
									1, 1, 1, 1, new java.awt.Color(0, 0, 0)));
							jPanel2.setName("");
							jPanel2.setBounds(0, 74, 609, 140);
							jPanel2.setLayout(null);
							{
								jButtonLoadCurrentExtent = new JButton();
								jPanel2.add(jButtonLoadCurrentExtent);
								jButtonLoadCurrentExtent
										.setText("Load Current Extent");
								jButtonLoadCurrentExtent.setBounds(8, 108, 178,
										29);
								jButtonLoadCurrentExtent
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												loadCurrentExtent();
											}
										});
							}
							{
								jLabel1InitialExtent = new JLabel();
								jPanel2.add(jLabel1InitialExtent);
								jLabel1InitialExtent.setText("Initial Extent");
								jLabel1InitialExtent.setBounds(13, 1, 103, 22);
							}
							{
								jLabelYmin = new JLabel();
								jPanel2.add(jLabelYmin);
								jLabelYmin.setText("Ymin");
								jLabelYmin
										.setHorizontalAlignment(SwingConstants.RIGHT);
								jLabelYmin.setBounds(186, 85, 57, 26);
							}
							{
								jLabelYmax = new JLabel();
								jPanel2.add(jLabelYmax);
								jLabelYmax.setText("Ymax");
								jLabelYmax
										.setHorizontalAlignment(SwingConstants.RIGHT);
							}
							{
								jLabelXmax = new JLabel();
								jPanel2.add(jLabelXmax);
								jLabelXmax.setText("Xmax");
							}
							{
								jTextFieldXmax = new JTextField();
								jLabelXmax.setLabelFor(jTextFieldXmax);
								jLabelXmax
										.setHorizontalTextPosition(SwingConstants.RIGHT);
								jLabelXmax
										.setHorizontalAlignment(SwingConstants.RIGHT);
								jLabelXmax.setBounds(352, 51, 76, 29);
								jPanel2.add(jTextFieldXmax);
								jTextFieldXmax.setBounds(428, 53, 172, 24);
							}
							{
								jTextFieldXmin = new JTextField();
								jPanel2.add(jTextFieldXmin);
								jTextFieldXmin.setBounds(57, 53, 174, 24);
							}
							{
								jLabelXmin = new JLabel();
								jPanel2.add(jLabelXmin);
								jLabelXmin.setText("Xmin");
								jLabelXmin.setLabelFor(jTextFieldXmin);
								jLabelXmin
										.setHorizontalAlignment(SwingConstants.RIGHT);
								jLabelXmin.setBounds(6, 51, 51, 29);
							}
							{
								jTextFieldYmax = new JTextField();
								jLabelYmax.setLabelFor(jTextFieldYmax);
								jLabelYmax.setBounds(192, 21, 53, 23);
								jPanel2.add(jTextFieldYmax);
								jTextFieldYmax.setBounds(245, 20, 172, 25);
							}
							{
								jTextFieldYmin = new JTextField();
								jPanel2.add(jTextFieldYmin);
								jTextFieldYmin.setBounds(243, 86, 175, 25);
							}
						}
						{
							jPanel3 = new JPanel();
							BoxLayout jPanel3Layout = new BoxLayout(jPanel3,
									javax.swing.BoxLayout.X_AXIS);
							jPanel3.setLayout(jPanel3Layout);
							jPanelMain.add(jPanel3);
							jPanel3.setBounds(0, 229, 611, 29);
							{
								jLabelWkid = new JLabel();
								jPanel3.add(jLabelWkid);
								jLabelWkid.setText("WKID");
								jLabelWkid
										.setHorizontalAlignment(SwingConstants.RIGHT);
								jLabelWkid
										.setPreferredSize(new java.awt.Dimension(
												61, 22));
							}
							{
								ComboBoxModel jComboBoxWkidModel = new DefaultComboBoxModel(
										new String[] {});
								jComboBoxWkid = new JComboBox();
								jPanel3.add(jComboBoxWkid);
								jComboBoxWkid.setModel(jComboBoxWkidModel);
								jComboBoxWkid
										.setPreferredSize(new java.awt.Dimension(
												347, 32));
							}
							{
								jButtonLoadCustomCRS = new JButton();
								jPanel3.add(jButtonLoadCustomCRS);
								jButtonLoadCustomCRS.setText("Load Custom CRS");
								jButtonLoadCustomCRS
										.setPreferredSize(new java.awt.Dimension(
												165, 29));
								jButtonLoadCustomCRS
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent evt) {
												openLoadCustomCRSDialog();
											}
										});
							}
						}
					}
					{
						jPanelBgColor = new JPanel();
						BorderLayout jPanelBgColorLayout = new BorderLayout();
						jPanelBgColor.setLayout(jPanelBgColorLayout);
						jTabbedPane.addTab("Background Color", null,
								jPanelBgColor, null);
						jPanelBgColor.setPreferredSize(new java.awt.Dimension(
								615, 272));
						{
							jColorChooserBgColor = new JColorChooser();
							jPanelBgColor.add(jColorChooserBgColor,
									BorderLayout.CENTER);
						}
					}
					{
						jPanelLayers = new JPanel();
						BorderLayout jPanelLayersLayout = new BorderLayout();
						jPanelLayers.setLayout(jPanelLayersLayout);
						jTabbedPane.addTab("Layers", null, jPanelLayers, null);
						{
							jListLayersModel = new DefaultComboBoxModel();
							jListLayers = new JList();
							jPanelLayers.add(jListLayers, BorderLayout.CENTER);
							jListLayers.setModel(jListLayersModel);
							{
								jPopupMenuLayer = new JPopupMenu();
								jPanelLayers
										.setComponentPopupMenu(jPopupMenuLayer);
								jPopupMenuLayer
										.addPopupMenuListener(new PopupMenuListener() {
											public void popupMenuCanceled(
													PopupMenuEvent e) {
											}

											public void popupMenuWillBecomeInvisible(
													PopupMenuEvent e) {
											}

											public void popupMenuWillBecomeVisible(
													PopupMenuEvent e) {
												int index = jListLayers
														.getSelectedIndex();
												if (index < 0) {
													menuItemRemove
															.setEnabled(false);
													menuItemEdit
															.setEnabled(false);

													menuItemMoveUp
															.setEnabled(false);
													menuItemMoveDown
															.setEnabled(false);
												} else {
													menuItemRemove
															.setEnabled(true);
													menuItemEdit
															.setEnabled(true);

													if (index == jListLayers
															.getFirstVisibleIndex()) {
														menuItemMoveUp
																.setEnabled(false);
														menuItemMoveDown
																.setEnabled(true);
													} else if (index == jListLayers
															.getLastVisibleIndex()) {
														menuItemMoveUp
																.setEnabled(true);
														menuItemMoveDown
																.setEnabled(false);
													} else {
														menuItemMoveUp
																.setEnabled(true);
														menuItemMoveDown
																.setEnabled(true);
													}
												}
											}
										});
								menuItemAdd = new JMenuItem("Add");
								jPopupMenuLayer.add(menuItemAdd);
								menuItemAdd
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												openLayerConfDialogAdd();
											}
										});
								menuItemRemove = new JMenuItem("Remove");
								jPopupMenuLayer.add(menuItemRemove);
								menuItemRemove
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												Object item = jListLayers
														.getSelectedValue();
												jListLayersModel
														.removeElement(item);
											}
										});
								menuItemEdit = new JMenuItem("Edit");
								jPopupMenuLayer.add(menuItemEdit);
								menuItemEdit
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												Object item = jListLayers
														.getSelectedValue();
												LayerItem layerItem = (LayerItem) item;
												openLayerConfDialogEdit(layerItem
														.getLayerInfo());
											}
										});

								jPopupMenuLayer.addSeparator();

								menuItemMoveUp = new JMenuItem("Move Up");
								jPopupMenuLayer.add(menuItemMoveUp);
								menuItemMoveUp
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												Object item = jListLayers
														.getSelectedValue();
												int index = jListLayers
														.getSelectedIndex();
												jListLayersModel
														.removeElement(item);
												jListLayersModel
														.insertElementAt(item,
																index - 1);
												jListLayers.setSelectedValue(
														item, true);
											}
										});
								menuItemMoveDown = new JMenuItem("Move Down");
								jPopupMenuLayer.add(menuItemMoveDown);
								menuItemMoveDown
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												Object item = jListLayers
														.getSelectedValue();
												int index = jListLayers
														.getSelectedIndex();
												jListLayersModel
														.removeElement(item);
												jListLayersModel
														.insertElementAt(item,
																index + 1);
												jListLayers.setSelectedValue(
														item, true);
											}
										});

								setComponentPopupMenu(jListLayers,
										jPopupMenuLayer);
							}
						}
					}
				}
				{
					jPanel1 = new JPanel();
					getContentPane().add(jPanel1, BorderLayout.SOUTH);
					jPanel1.setPreferredSize(new java.awt.Dimension(507, 36));
					{
						jButtonApply = new JButton();
						jPanel1.add(jButtonApply);
						jButtonApply.setText("Apply");
						jButtonApply.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								jButtonApplyActionPerformed(evt);
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

	public JList getJListLayers() {
		return jListLayers;
	}	

	public DefaultComboBoxModel getjListLayersModel() {
		return jListLayersModel;
	}

	public JTextField getjTextFieldYmin() {
		return jTextFieldYmin;
	}

	public JTextField getjTextFieldYmax() {
		return jTextFieldYmax;
	}

	public JTextField getjTextFieldXmax() {
		return jTextFieldXmax;
	}

	public JTextField getjTextFieldXmin() {
		return jTextFieldXmin;
	}

	public JComboBox getjComboBoxWkid() {
		return jComboBoxWkid;
	}

	private void openLoadCustomCRSDialog() {
		JFileChooser jFileChooser = getJFileChooserOpenCustomCRSDir();
		int returnVal = jFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String dir = jFileChooser.getSelectedFile().getAbsolutePath();
			try {
				SpatialReferenceManager.loadCustomCRSs(dir + File.separator);
				initWkidItems();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Load Custom CRS Error!");
			}
		}
	}

	private void configureMap() {
		try {
			MapToolApp app = (MapToolApp) getParent();
			MapDesc mapDesc = app.getMapDesc();

			WkidItem wkidItem = (WkidItem) this.jComboBoxWkid.getSelectedItem();
			mapDesc.setWkid(wkidItem.getWkid());
			mapDesc.setName(this.jTextFieldName.getText());
			mapDesc.setAntiAlias(this.jCheckBoxAntiAlias.isSelected());
			Color bgColor = this.jColorChooserBgColor.getColor();
			mapDesc.setBackgroundColor(bgColor);

			double xmin = Double.parseDouble(jTextFieldXmin.getText()
					.equals("") ? "0" : jTextFieldXmin.getText());
			double xmax = Double.parseDouble(jTextFieldXmax.getText()
					.equals("") ? "0" : jTextFieldXmax.getText());
			double ymin = Double.parseDouble(jTextFieldYmin.getText()
					.equals("") ? "0" : jTextFieldYmin.getText());
			double ymax = Double.parseDouble(jTextFieldYmax.getText()
					.equals("") ? "0" : jTextFieldYmax.getText());
			Envelope initialExtent = new Envelope(xmin, xmax, ymin, ymax);
			mapDesc.setInitialExtent(initialExtent);

			mapDesc.getLayerInfos().clear();
			for (int i = 0, count = this.jListLayersModel.getSize(); i < count; i++) {
				LayerItem item = (LayerItem) this.jListLayersModel
						.getElementAt(i);
				LayerInfo layerInfo = item.getLayerInfo();
				mapDesc.getLayerInfos().add(layerInfo);
			}

			app.getMap().initByMapDesc(mapDesc, app.getMapDescFilePath());
			app.getMapPane().setBackground(bgColor);

			MapContext mapContext = app.getMap().getMapContext();
			if (mapContext != null) {
				app.setMapContext(mapContext);
				app.pack();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Configure Map Error!");
		}
	}

	public void loadMapDesc() {
		MapToolApp app = (MapToolApp) this.getParent();
		
		MapDesc mapDesc = app.getMapDesc();
		if (mapDesc != null) {
			this.jTextFieldName.setText(mapDesc.getName());
			this.jCheckBoxAntiAlias.setSelected(mapDesc.isAntiAlias());
			Envelope initialExtent = mapDesc.getInitialExtent();
			if (initialExtent != null) {
				this.jTextFieldXmin.setText(String.valueOf(initialExtent
						.getMinX()));
				this.jTextFieldXmax.setText(String.valueOf(initialExtent
						.getMaxX()));
				this.jTextFieldYmin.setText(String.valueOf(initialExtent
						.getMinY()));
				this.jTextFieldYmax.setText(String.valueOf(initialExtent
						.getMaxY()));
			}
			this.jColorChooserBgColor.setColor(mapDesc.getBackgroundColor());

			String wkid = mapDesc.getWkid();
			if (wkid != null) {
				for (int i = 0; i < this.jComboBoxWkid.getItemCount(); i++) {
					WkidItem item = (WkidItem) this.jComboBoxWkid.getItemAt(i);
					if (item.getWkid().equals(wkid)) {
						this.jComboBoxWkid.setSelectedIndex(i);
						break;
					}
				}
			}

			this.jListLayersModel.removeAllElements();
			for (Iterator<LayerInfo> itr = mapDesc.getLayerInfos().iterator(); itr
					.hasNext();) {
				LayerInfo layerInfo = itr.next();
				LayerItem layerItem = new LayerItem();
				layerItem.setLayerInfo(layerInfo);
				this.jListLayersModel.addElement(layerItem);
			}

		}
	}

	private void initWkidItems() {
		jComboBoxWkid.removeAllItems();

		ArrayList<String> wkids = SpatialReferenceManager.getAllAvalibleWkids();
		for (Iterator<String> itr = wkids.iterator(); itr.hasNext();) {
			String wkid = itr.next();
			try {
				CoordinateReferenceSystem crs = SpatialReferenceManager
						.wkidToCRS(wkid, false);
				WkidItem item = new WkidItem(wkid, "[" + wkid + "] "
						+ SpatialReferenceManager.getCRSDescription(crs));
				jComboBoxWkid.addItem(item);
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}

		for (int i = 0; i < this.jComboBoxWkid.getItemCount(); i++) {
			WkidItem item = (WkidItem) this.jComboBoxWkid.getItemAt(i);
			if (item.getWkid().equals("4326")) {
				this.jComboBoxWkid.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadCurrentExtent() {
		try {
			MapToolApp app = (MapToolApp) this.getParent();
			ReferencedEnvelope env = app.getMapContext().getAreaOfInterest();
			CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
			String inSR = SpatialReferenceManager.crsToWkid(crs);

			WkidItem item = (WkidItem) this.jComboBoxWkid.getSelectedItem();
			Envelope extent = GeometryToolkit
					.project(env, inSR, item.getWkid());
			this.jTextFieldXmin.setText(String.valueOf(extent.getMinX()));
			this.jTextFieldXmax.setText(String.valueOf(extent.getMaxX()));
			this.jTextFieldYmin.setText(String.valueOf(extent.getMinY()));
			this.jTextFieldYmax.setText(String.valueOf(extent.getMaxY()));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Load Current Extent Error!");
		}
	}

	private JFileChooser getJFileChooserOpenCustomCRSDir() {
		if (jFileChooserOpenCustomCRSDir == null) {
			jFileChooserOpenCustomCRSDir = new JFileChooser();
			jFileChooserOpenCustomCRSDir
					.setDialogTitle("Open Custom CRS Directory");
			jFileChooserOpenCustomCRSDir
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			FileFilter filter = new FileFilter() {
				public boolean accept(File file) {
					return (file.isDirectory());
				}

				public String getDescription() {
					return "Custom CRS Directory";
				}
			};
			jFileChooserOpenCustomCRSDir.setFileFilter(filter);
		}
		return jFileChooserOpenCustomCRSDir;
	}

	private void setComponentPopupMenu(final java.awt.Component parent,
			final javax.swing.JPopupMenu menu) {
		parent.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger())
					menu.show(parent, e.getX(), e.getY());
			}

			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger())
					menu.show(parent, e.getX(), e.getY());
			}
		});
	}

	public LayerConfDialog getLayerConfDialog() {
		if (layerConfDialog == null) {
			layerConfDialog = new LayerConfDialog(this);
		}

		return layerConfDialog;
	}

	private void openLayerConfDialogAdd() {
		LayerConfDialog dialog = getLayerConfDialog();
		
		dialog.setLayerInfo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void openLayerConfDialogEdit(LayerInfo layerInfo) {
		LayerConfDialog dialog = getLayerConfDialog();
		dialog.setLayerInfo(layerInfo);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void jButtonApplyActionPerformed(ActionEvent evt) {
		configureMap();
	}
	
	public JTextField getJTextFieldName() {
		return jTextFieldName;
	}

}
