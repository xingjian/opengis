/** @文件名: PBDeskTopUI.java @创建人：邢健  @创建日期： 2013-3-1 下午2:59:26 */
package com.promise.ui;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.promise.util.PBUtil;

/**   
 * @类名: PBDeskTopUI.java 
 * @包名: com.promise.ui 
 * @描述: 桌面程序导入
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-1 下午2:59:26 
 * @版本: V1.0   
 */
public class PBDeskTopUI {

	private JFrame mainFrame = null;
	private JMenuBar jMenuBar = null;
	private JMenu menuData = null;
	private JMenuItem menuItemLoadShape = null;
	private JPanel mainFrameCenter = null;
	private CardLayout cardManager;
	
	private JFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new JFrame();
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setJMenuBar(getJMenuBar());
			mainFrame.setSize(800, 600);
			mainFrame.setTitle("PBGeoToolsV1.0(xingjian@yeah.net)");
			mainFrame.setFont(PBUtil.getFont("微软雅黑", Font.BOLD, 16));
			mainFrame.setIconImage(PBUtil.getImage("/com/promise/image/home.gif"));
			PBUtil.setCenter(mainFrame);
		}
		return mainFrame;
	}
	
	private JMenuBar getJMenuBar() {
		if (jMenuBar == null) {
			jMenuBar = new JMenuBar();
			jMenuBar.add(getJMenuData());
		}
		return jMenuBar;
	}
	
	private JMenu getJMenuData() {
		if (menuData == null) {
			menuData = new JMenu();
			menuData.setText("加载数据");
			menuData.setFont(PBUtil.getFont("微软雅黑", Font.BOLD, 12));
			menuData.add(getLoadShapeMenuItem());
		}
		return menuData;
	}
	
	private JMenuItem getLoadShapeMenuItem() {
		if (menuItemLoadShape == null) {
			menuItemLoadShape = new JMenuItem();
			menuItemLoadShape.setText("加载Shape");
			menuItemLoadShape.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
				}
			});
		}
		return menuItemLoadShape;
	}
	
	public JPanel getMainFrameCenter(){
		if(null == mainFrameCenter){
			cardManager = new CardLayout();
			mainFrameCenter = new JPanel(cardManager);
		}
		return mainFrameCenter;
	}
	
	/**
	 * 入口函数
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PBDeskTopUI application = new PBDeskTopUI();
				application.getMainFrame().setVisible(true);
				application.getMainFrame().setResizable(false);
			}
		});
	}

}
