package com.gi.gidesk.map.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
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

import com.gi.gidesk.map.MapApp;
import com.gi.giengine.geometry.ProjectEngine;
import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.desc.LayerDesc;
import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.sr.SpatialReferenceEngine;
import com.vividsolutions.jts.geom.Envelope;

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
	private JButton jButtonApply;
	private JButton jButtonClose;
	private JPanel jPanel1;
	private JFileChooser jFileChooserOpenCustomCRSDir;
	private DefaultComboBoxModel jListLayersModel;
	private LayerConfDialog layerConfDialog;

	private MapDesc oldMapDesc = null;

	/**
	 * Auto-generated main method to display this JDialog
	 */

	public MapServiceConfDialog(MapApp app) {
		super(app);
		initGUI();
	}

	private void initGUI() {
		try {
			{
				setTitle("Configure Map Service");
				setResizable(false);
				{
					jPanel1 = new JPanel();
					getContentPane().add(jPanel1, BorderLayout.SOUTH);
					jPanel1.setPreferredSize(new java.awt.Dimension(507, 36));
					{
						jButtonApply = new JButton();
						jPanel1.add(jButtonApply);
						jButtonApply.setText("Apply");
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
	

}
