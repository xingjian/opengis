/**@Title: MainFrame.java @author promisePB xingjian@yeah.net @date 2010-12-1 上午10:54:25 */

package com.xingjian.cn.main;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.view.firstperson.FlyToFlyViewAnimator;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.xingjian.cn.cuscompent.CheckBoxLayerPanelCheck;

/**   
 * @Title: MainFrame.java 
 * @Package com.xingjian.cn.main 
 * @Description: 主应用程序 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-1 上午10:54:25 
 * @version V1.0   
 */

public class MainFrame extends JFrame {

	Logger log = Logger.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private WorldWindowGLCanvas wwg;
	private CheckBoxLayerPanelCheck layerPanel;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame thisClass = new MainFrame();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * 构造函数
	 */
	public MainFrame() {
		super();
		initialize();
	}

	/**
	 * 初始化方法
	 */
	private void initialize() {
		log.debug("initialize方法调用");
		this.setSize(800, 400);
		this.setContentPane(getJContentPane());
		this.setTitle("WorldWindTest主应用程序");
		this.getContentPane().add(getCenterCanvas(),BorderLayout.CENTER);
		this.getContentPane().add(getLayerPanel(),BorderLayout.WEST);
		this.setLocation(400, 300);
	}
	
	/**
	 * 返回WorldWindowGLCanvas
	 */
	public WorldWindowGLCanvas getCenterCanvas(){
		log.debug("getCenterCanvas方法调用");
		if(null == wwg){
			wwg = new WorldWindowGLCanvas();
			wwg.setSize(300, 300);
			wwg.setModel(new BasicModel());
		}
		return wwg;
	}
	
	/**
	 * 飞行特效
	 */
	public void flyTo(LatLon latlon)
	{
		View view = wwg.getView();
		Globe globe = wwg.getModel().getGlobe();
		view.goTo(new Position(LatLon.ZERO, 0), 0);
		
	}

	
	
	/**
	 * 返回 left面板
	 */
	public CheckBoxLayerPanelCheck getLayerPanel(){
		log.debug("getLayerPanel方法调用");
		if(null == layerPanel){
			layerPanel = new CheckBoxLayerPanelCheck(getCenterCanvas());
		}
		return layerPanel;
	}
	
	/**
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		log.debug("getJContentPane方法调用");
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

}
