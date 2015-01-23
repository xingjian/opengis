package com.gi.desktop.maptilestool;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import com.gi.engine.server.service.TileLodInfo;
import com.vividsolutions.jts.geom.Envelope;

public class MapTileCreatingThread extends Thread {

	private MapTilesToolApp app = null;
	private int n = 0;// Thread Id

	private boolean running = true;

	public MapTileCreatingThread(MapTilesToolApp app, int n) {
		this.app = app;
		this.n = n;
	}

	@Override
	public void run() {
		super.run();
		running = true;

		String tileURL = app.getMapServiceURL() + "/tile/";
		int maxInstances = app.getMaxInstances();
		int spread = app.getTileInfo().getCreateSpread();
		int width = app.getTileInfo().getWidth();
		int height = app.getTileInfo().getHeight();
		double originX = app.getTileInfo().getOriginX();
		double originY = app.getTileInfo().getOriginY();
		Envelope fullExtent = app.getFullExtent();
		if (originX < fullExtent.getMinX()) {
			originX = fullExtent.getMinX();
		}
		if (originY > fullExtent.getMaxY()) {
			originY = fullExtent.getMaxY();
		}

		for (int i = 0, count = app.getCheckListLods().getModel().getSize(); i < count; i++) {
			JCheckBox chk = (JCheckBox) app.getCheckListLods().getModel()
					.getElementAt(i);
			if (chk.isSelected()) {
				TileLodInfo tileLodInfo = app.getTileInfo().getTileLodInfo(i);
				double resolution = tileLodInfo.getResolution();
				double deltaX = width * resolution;
				double deltaY = height * resolution;
				int colMin = (int) ((fullExtent.getMinX() - originX) / deltaX);
				colMin = colMin < 0 ? 0 : colMin;
				int colMax = (int) ((fullExtent.getMaxX() - originX) / deltaX) + 1;
				int rowMin = (int) ((originY - fullExtent.getMaxY()) / deltaY);
				rowMin = rowMin < 0 ? 0 : rowMin;
				int rowMax = (int) ((originY - fullExtent.getMinY()) / deltaY) + 1;

				for (int c = colMin; c < colMax + 1; c += spread) {
					for (int r = rowMin; r < rowMax + 1; r += spread) {
						if (running) {
							if (((c - colMin) / spread) % maxInstances == n) {
								String url = tileURL + i + "/" + r + "/" + c;
								try {
									byte[] tile = getTileResult(url);
									if (tile != null && tile.length > 0) {
										SwingUtilities
												.invokeLater(new Runnable() {
													public void run() {
														if (running) {
															app
																	.recordSucceedTiles();
														}
													}
												});
									} else {
										SwingUtilities
												.invokeLater(new Runnable() {
													public void run() {
														if (running) {
															app
																	.recordFailedTiles();
														}
													}
												});
									}
								} catch (Exception e) {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											if (running) {
												app.recordFailedTiles();
											}
										}
									});
								}
							}
						} else {
							return;
						}
					}
				}
			}
		}

		shutdown();
	}

	public boolean isRunning() {
		return running;
	}

	public synchronized void shutdown() {
		running = false;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				app.checkThreads();
			}
		});
	}

	private byte[] getTileResult(String url) throws Exception {
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection
				.getOutputStream());
		out.flush();
		out.close();

		InputStream in = connection.getInputStream();
		byte[] result = new byte[in.available()];
		in.read(result);
		return result;
	}

}
