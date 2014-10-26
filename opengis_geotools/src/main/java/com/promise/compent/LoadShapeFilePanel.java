/** @文件名: LoadShapeFilePanel.java @创建人：邢健  @创建日期： 2013-3-1 下午3:54:38 */
package com.promise.compent;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**   
 * @类名: LoadShapeFilePanel.java 
 * @包名: com.promise.compent 
 * @描述: 加载shape面板
 * @作者: xingjian xingjian@yeah.net   
 * @日期:2013-3-1 下午3:54:38 
 * @版本: V1.0   
 */
public class LoadShapeFilePanel extends JPanel {

	private JPanel leftPanel,rightPanel;
	
	public LoadShapeFilePanel() {
		this.setLayout(new FlowLayout());
	}
	
	public void initCompent(){
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		this.add(leftPanel);
		this.add(rightPanel);
	}

}
