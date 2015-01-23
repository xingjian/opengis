/**@Title: WWJGlobeTest.java @author promisePB xingjian@yeah.net @date 2010-12-13 上午09:56:33 */

package com.xingjian.cn.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**   
 * @Title: WWJGlobeTest.java 
 * @Package com.xingjian.cn.test 
 * @Description: WWJ Globe 测试 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-13 上午09:56:33 
 * @version V1.0   
 */

public class WWJGlobeTest extends JFrame{
	
	Logger log = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	
	public WWJGlobeTest(){
		super();
		initialize();
	}
	
	private JPanel getJContentPane() {
		log.debug("getJContentPane方法调用");
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	
	
	/**
	 * 初始化方法
	 */
	private void initialize() {
		log.debug("initialize方法调用");
		this.setSize(800, 400);
		this.setContentPane(getJContentPane());
		this.setTitle("WWJGlobeTest主应用程序");
		this.setLocation(400, 300);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WWJGlobeTest thisClass = new WWJGlobeTest();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

}
