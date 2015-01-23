package com.gi.desktop.configtool;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

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
public class ConfigToolApp extends javax.swing.JFrame {
	{
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7502272075702901241L;
	private JButton jButtonSave;
	private JButton jButtonClose;
	private JFileChooser jFileChooserMapDir;
	private JButton jButtonBrowseMapDir;
	private JTextField jTextFieldMapServicesDir;
	private JLabel jLabel2;
	private JTextField jTextFieldWebRoot;
	private JLabel jLabel1;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		ConfigToolApp f = new ConfigToolApp();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public ConfigToolApp() {
		super();
		initGUI();

		loadConfig();
	}

	private void initGUI() {
		try {
			{
				this.setTitle("Config Tool");
				this.setIconImage(new ImageIcon(getClass().getClassLoader()
						.getResource("image/giserver.png")).getImage());
				this.setResizable(false);
				getContentPane().setLayout(null);
				{
					jLabel1 = new JLabel();
					getContentPane().add(jLabel1);
					jLabel1.setText("Web Server Root: ");
					jLabel1.setBounds(12, 12, 392, 37);
				}
				{
					jButtonClose = new JButton();
					getContentPane().add(jButtonClose);
					jButtonClose.setText("Close");
					jButtonClose.setBounds(512, 248, 120, 42);
					jButtonClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							System.exit(0);
						}
					});
				}
				{
					jButtonSave = new JButton();
					getContentPane().add(jButtonSave);
					jButtonSave.setText("Save");
					jButtonSave.setBounds(387, 248, 120, 42);
					this.jButtonSave.setEnabled(false);
					jButtonSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							saveConfig();
						}
					});
				}
				{
					jTextFieldWebRoot = new JTextField();
					getContentPane().add(jTextFieldWebRoot);
					jTextFieldWebRoot.setBounds(12, 47, 619, 29);
					jTextFieldWebRoot
							.setToolTipText("Web server root url, default is \"http://localhost:8777\", change it to real IP address and port.");
				}
				{
					jLabel2 = new JLabel();
					getContentPane().add(jLabel2);
					jLabel2.setText("Map Services Directory: ");
					jLabel2.setBounds(12, 124, 269, 22);
				}
				{
					jTextFieldMapServicesDir = new JTextField();
					getContentPane().add(jTextFieldMapServicesDir);
					jTextFieldMapServicesDir.setBounds(12, 158, 540, 29);
					jTextFieldMapServicesDir
							.setToolTipText("Map services directory path.");
				}
				{
					jButtonBrowseMapDir = new JButton();
					getContentPane().add(jButtonBrowseMapDir);
					jButtonBrowseMapDir.setText("Browse");
					jButtonBrowseMapDir.setBounds(558, 158, 74, 30);
					jButtonBrowseMapDir.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							openMapDirChooser();
						}
					});
				}
			}

			this.setPreferredSize(new Dimension(652, 334));
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JFileChooser getJFileChooserMapDir() {
		if (jFileChooserMapDir == null) {
			jFileChooserMapDir = new JFileChooser();
			jFileChooserMapDir
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jFileChooserMapDir.setMultiSelectionEnabled(false);
		}
		return jFileChooserMapDir;
	}

	private String getServerConfigFilePath() {
		String result = null;

		try {
			URL url = this.getClass().getProtectionDomain().getCodeSource()
					.getLocation();
			String path = URLDecoder.decode(url.getPath(), "UTF-8");
			File file = new File(path);
			String configDirPath = file.getAbsolutePath() + File.separator
					+ "config";
			System.out.println("Config Directory:" + configDirPath);

			result = configDirPath + File.separator + "server.properties";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void loadConfig() {
		try {
			String path = getServerConfigFilePath();
			if (path != null) {
				FileInputStream input = new FileInputStream(path);
				Properties p = new Properties();
				p.load(input);
				input.close();
				this.jTextFieldWebRoot.setText(p.getProperty("WEB_ROOT"));
				this.jTextFieldMapServicesDir.setText(p
						.getProperty("MAP_SERVICES_DIR"));

				this.jButtonSave.setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveConfig() {
		int value = JOptionPane.showConfirmDialog(this,
				"Save current configuration?", "Confirm Save",
				JOptionPane.YES_NO_OPTION);
		if (value == JOptionPane.YES_OPTION) {
			try {
				String path = getServerConfigFilePath();
				if (path != null) {
					FileInputStream input = new FileInputStream(path);
					Properties p = new Properties();
					p.load(input);
					input.close();
					p.put("WEB_ROOT", this.jTextFieldWebRoot.getText());
					p.put("MAP_SERVICES_DIR", this.jTextFieldMapServicesDir
							.getText());

					FileOutputStream output = new FileOutputStream(path);
					p.store(output, null);
					output.close();

					JOptionPane.showMessageDialog(null, "Save Success!");
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Save Error!");
			}
		}
	}

	private void openMapDirChooser() {
		JFileChooser dialog = getJFileChooserMapDir();
		int value = dialog.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			String dirPath = dialog.getSelectedFile().getAbsolutePath();
			this.jTextFieldMapServicesDir.setText(dirPath);
		}
	}

}
