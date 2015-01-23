package com.gi.desktop.maptilestool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.server.service.TileInfo;
import com.gi.engine.server.service.TileLodInfo;
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
public class MapTilesToolApp extends javax.swing.JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1404693422743417167L;

	{
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private JButton jButtonRefreshMapServices;
	private JComboBox jComboBoxMapServices;
	private JTextField jTextFieldMapServicesURL;
	private JButton jButtonStart;
	private JProgressBar jProgressBar;
	private JButton jButtonToggleAll;
	private JPanel jPanelSetting;
	private JButton jButtonStop;
	private JLabel jLabelTime;
	private JScrollPane jScrollPane1;
	private CheckBoxList checkListLods;

	private TileInfo tileInfo = null;
	private Envelope fullExtent = null;
	private int maxInstances = 1;

	private int totleTiles = 0;
	private int succeedTiles = 0;
	private int failedTiles = 0;
	private long startTime = System.currentTimeMillis();

	private ArrayList<MapTileCreatingThread> threads = new ArrayList<MapTileCreatingThread>();

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MapTilesToolApp inst = new MapTilesToolApp();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MapTilesToolApp() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			{
				this.setIconImage(new ImageIcon(getClass().getClassLoader()
						.getResource("image/giserver.png")).getImage());
				this.setTitle("Map Tiles Tool");
				getContentPane().setLayout(null);
				this.setResizable(false);
				{
					jButtonStop = new JButton();
					getContentPane().add(jButtonStop);
					jButtonStop.setText("Stop");
					jButtonStop.setBounds(471, 463, 111, 43);
					jButtonStop.setEnabled(false);
					jButtonStop.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							stopProcessing();
						}
					});
				}
				{
					jPanelSetting = new JPanel();
					getContentPane().add(jPanelSetting);
					jPanelSetting.setBounds(0, 5, 592, 413);
					jPanelSetting.setLayout(null);
					{
						jButtonToggleAll = new JButton();
						jPanelSetting.add(jButtonToggleAll);
						jButtonToggleAll.setText("Select/Unselect All");
						jButtonToggleAll.setBounds(10, 374, 265, 33);
						jButtonToggleAll
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										toggleSelectLods();
									}
								});
					}
					{
						ComboBoxModel jComboBoxMapServicesModel = new DefaultComboBoxModel();
						jComboBoxMapServices = new JComboBox();
						jPanelSetting.add(jComboBoxMapServices);
						jComboBoxMapServices
								.setModel(jComboBoxMapServicesModel);
						jComboBoxMapServices.setBounds(10, 46, 433, 35);
						jComboBoxMapServices
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										refreshService();
									}
								});
					}
					{
						jButtonRefreshMapServices = new JButton();
						jPanelSetting.add(jButtonRefreshMapServices);
						jButtonRefreshMapServices.setText("Refresh");
						jButtonRefreshMapServices.setBounds(458, 46, 124, 35);
						jButtonRefreshMapServices
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										refreshServices();
									}
								});
					}
					{
						jTextFieldMapServicesURL = new JTextField();
						jPanelSetting.add(jTextFieldMapServicesURL);
						jTextFieldMapServicesURL
								.setText("http://localhost:8777/giserver/rest/service/MapService");
						jTextFieldMapServicesURL.setBounds(10, 7, 572, 33);
					}
					{
						jScrollPane1 = new JScrollPane();
						jPanelSetting.add(jScrollPane1);
						jScrollPane1.setBounds(10, 87, 572, 283);
						{
							checkListLods = new CheckBoxList();
							jScrollPane1.setViewportView(checkListLods);
						}
					}
				}
				{
					jButtonStart = new JButton();
					getContentPane().add(jButtonStart);
					jButtonStart.setText("Start");
					jButtonStart.setBounds(10, 462, 111, 44);
					jButtonStart.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							startProcessing();
						}
					});
				}
				{
					jProgressBar = new JProgressBar();
					getContentPane().add(jProgressBar);
					getContentPane().add(getJLabelTime());
					jProgressBar.setBounds(10, 424, 572, 36);
				}
			}

			this.setPreferredSize(new Dimension(600, 545));
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshServices() {
		try {
			jComboBoxMapServices.removeAllItems();
			jComboBoxMapServices.setModel(new DefaultComboBoxModel());

			String url = this.jTextFieldMapServicesURL.getText() + "?f=json";
			JSONObject json = this.getJSONResult(url);
			JSONArray array = json.getJSONArray("mapServices");
			int count = array.length();
			Object[] serviceNames = new Object[count + 1];
			serviceNames[0] = "";
			for (int i = 0; i < count; i++) {
				serviceNames[i + 1] = array.getJSONObject(i).get("name");
			}
			final DefaultComboBoxModel model = new DefaultComboBoxModel(
					serviceNames);
			jComboBoxMapServices.setModel(model);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}

	private void refreshService() {
		try {
			checkListLods.setModel(new DefaultComboBoxModel());
			Object item = jComboBoxMapServices.getSelectedItem();
			if (item != null) {
				String serviceName = item.toString();
				if (!"".equals(serviceName)) {
					String url = this.jTextFieldMapServicesURL.getText() + "/"
							+ serviceName + "?f=json";
					JSONObject json = this.getJSONResult(url);

					maxInstances = json.getInt("maxInstances");
					JSONObject objFullExtent = json.getJSONObject("fullExtent");
					fullExtent = new Envelope();
					fullExtent.init(objFullExtent.getDouble("xmin"),
							objFullExtent.getDouble("xmax"), objFullExtent
									.getDouble("ymin"), objFullExtent
									.getDouble("ymax"));
					JSONObject objTileInfo = json.getJSONObject("tileInfo");
					tileInfo = new TileInfo();
					tileInfo
							.setCreateSpread(objTileInfo.getInt("createSpread"));
					tileInfo.setWidth(objTileInfo.getInt("cols"));
					tileInfo.setHeight(objTileInfo.getInt("rows"));
					JSONObject objOrigin = objTileInfo.getJSONObject("origin");
					tileInfo.setOriginX(objOrigin.getDouble("x"));
					tileInfo.setOriginY(objOrigin.getDouble("y"));
					JSONArray array = objTileInfo.getJSONArray("lods");
					int count = array.length();
					for (int i = 0; i < count; i++) {
						JSONObject obj = array.getJSONObject(i);
						TileLodInfo tileLodInfo = new TileLodInfo();
						tileLodInfo.setLevel(obj.getInt("level"));
						tileLodInfo.setScale(obj.getDouble("scale"));
						tileLodInfo.setResolution(obj.getDouble("resolution"));
						tileInfo.addTileLodInfo(tileLodInfo);
					}

					Object[] levels = new Object[count];
					for (int i = 0, size = tileInfo.getTileLodInfos().size(); i < size; i++) {
						TileLodInfo info = tileInfo.getTileLodInfo(i);
						JCheckBox checkBox = new JCheckBox();
						checkBox.setText(info.getLevel() + " 1:"
								+ info.getScale());
						levels[i] = checkBox;
					}
					final DefaultComboBoxModel model = new DefaultComboBoxModel(
							levels);
					checkListLods.setModel(model);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}

	private JSONObject getJSONResult(String url) throws Exception {
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection
				.getOutputStream(), "UTF-8");
		out.flush();
		out.close();

		StringBuilder sb = new StringBuilder();
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String result = sb.toString();
		return new JSONObject(result);
	}

	private void toggleSelectLods() {
		int count = checkListLods.getModel().getSize();
		if (count > 0) {
			boolean select = ((JCheckBox) checkListLods.getModel()
					.getElementAt(0)).isSelected();
			for (int i = 0; i < count; i++) {
				((JCheckBox) checkListLods.getModel().getElementAt(i))
						.setSelected(!select);
			}
			checkListLods.repaint();
		}
	}

	private void startProcessing() {
		this.jButtonStart.setEnabled(false);
		this.jButtonStop.setEnabled(true);
		Component[] comps = this.jPanelSetting.getComponents();
		for (int i = 0, count = comps.length; i < count; i++) {
			comps[i].setEnabled(false);
		}
		this.checkListLods.setEnabled(false);

		caculateTotleTiles();

		// Start threads to create tiles
		if (JOptionPane.showConfirmDialog(null, "Start Processing?", "Confirm",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for (int i = 0, count = threads.size(); i < count; i++) {
				MapTileCreatingThread t = threads.get(i);
				t.shutdown();
			}
			threads.clear();
			for (int i = 0; i < maxInstances; i++) {
				MapTileCreatingThread t = new MapTileCreatingThread(this, i);
				threads.add(t);
			}
			startTime = System.currentTimeMillis();
			for (int i = 0, count = threads.size(); i < count; i++) {
				MapTileCreatingThread t = threads.get(i);
				t.start();
			}
		}
	}

	private void stopProcessing() {
		// Stop threads
		if (JOptionPane.showConfirmDialog(null, "Stop Now?", "Confirm",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for (Iterator<MapTileCreatingThread> itr = threads.iterator(); itr
					.hasNext();) {
				MapTileCreatingThread t = itr.next();
				t.shutdown();
			}
		}
	}

	private void backToIdle() {
		if (!this.jButtonStart.isEnabled()) {
			this.jButtonStart.setEnabled(true);
			this.jButtonStop.setEnabled(false);
			Component[] comps = this.jPanelSetting.getComponents();
			for (int i = 0, count = comps.length; i < count; i++) {
				comps[i].setEnabled(true);
			}
			this.checkListLods.setEnabled(true);

			JOptionPane.showMessageDialog(null, "Succeed:" + this.succeedTiles
					+ "/Failed:" + this.failedTiles);
		}
	}

	public TileInfo getTileInfo() {
		return tileInfo;
	}

	public int getMaxInstances() {
		return maxInstances;
	}

	public CheckBoxList getCheckListLods() {
		return checkListLods;
	}

	public Envelope getFullExtent() {
		return fullExtent;
	}

	public int getTotleTiles() {
		return totleTiles;
	}

	public String getMapServiceURL() {
		String url = jTextFieldMapServicesURL.getText();
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += jComboBoxMapServices.getSelectedItem().toString();
		return url;
	}

	public void recordSucceedTiles() {
		int spread = tileInfo.getCreateSpread();
		succeedTiles += spread * spread;

		caculateProgress();
	}

	public void recordFailedTiles() {
		int spread = tileInfo.getCreateSpread();
		failedTiles += spread * spread;

		caculateProgress();
	}

	private void caculateProgress() {
		jProgressBar.setValue(succeedTiles + failedTiles);
		long s = (System.currentTimeMillis() - startTime) / 1000;
		long m = 0;
		long h = 0;
		long d = 0;
		if (s >= 60) {
			m = s / 60;
			s = s % 60;
		}
		if (m >= 60) {
			h = m / 60;
			m = m % 60;
		}
		if (h >= 24) {
			d = h / 24;
			h = h % 24;
		}
		StringBuilder sb = new StringBuilder();
		if (d > 0) {
			sb.append(" " + d + " day");
		}
		if (h > 0) {
			sb.append(" " + h + " hrs");
		}
		if (m > 0) {
			sb.append(" " + m + " min");
		}
		if (s > 0) {
			sb.append(" " + s + " sec");
		}

		this.jLabelTime.setText(sb.toString());
	}

	public synchronized void checkThreads() {
		boolean noThreadRunning = true;

		for (int i = 0, count = threads.size(); i < count; i++) {
			MapTileCreatingThread t = threads.get(i);
			if (t.isRunning()) {
				noThreadRunning = false;
			}
		}

		if (noThreadRunning) {
			backToIdle();
		}
	}

	private void caculateTotleTiles() {
		totleTiles = 0;
		succeedTiles = 0;
		failedTiles = 0;

		int width = tileInfo.getWidth();
		int height = tileInfo.getHeight();
		double originX = tileInfo.getOriginX();
		double originY = tileInfo.getOriginY();
		if (originX < fullExtent.getMinX()) {
			originX = fullExtent.getMinX();
		}
		if (originY > fullExtent.getMaxY()) {
			originY = fullExtent.getMaxY();
		}

		for (int i = 0, count = checkListLods.getModel().getSize(); i < count; i++) {
			JCheckBox chk = (JCheckBox) checkListLods.getModel()
					.getElementAt(i);
			if (chk.isSelected()) {
				TileLodInfo tileLodInfo = tileInfo.getTileLodInfo(i);
				double resolution = tileLodInfo.getResolution();
				double deltaX = width * resolution;
				double deltaY = height * resolution;
				int colMin = (int) ((fullExtent.getMinX() - originX) / deltaX);
				colMin = colMin < 0 ? 0 : colMin;
				int colMax = (int) ((fullExtent.getMaxX() - originX) / deltaX) + 1;
				int rowMin = (int) ((originY - fullExtent.getMaxY()) / deltaY);
				rowMin = rowMin < 0 ? 0 : rowMin;
				int rowMax = (int) ((originY - fullExtent.getMinY()) / deltaY) + 1;
				totleTiles += (colMax - colMin) * (rowMax - rowMin);
			}
		}

		jProgressBar.setMaximum(totleTiles);
		jProgressBar.setValue(0);
	}

	private JLabel getJLabelTime() {
		if (jLabelTime == null) {
			jLabelTime = new JLabel();
			jLabelTime.setBounds(131, 466, 330, 37);
			jLabelTime.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return jLabelTime;
	}

}
