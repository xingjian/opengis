/** @文件名: CardLayoutTest.java @创建人：邢健  @创建日期： 2013-3-1 下午5:33:20 */
package com.promise.java;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @类名: CardLayoutTest.java
 * @包名: com.promise.java
 * @描述: TODO
 * @作者: xingjian xingjian@yeah.net
 * @日期:2013-3-1 下午5:33:20
 * @版本: V1.0
 */
public class CardLayoutTest {

	private JFrame jframe;
	private CardLayout cardManager;
	private JPanel bottomJPanel, topJPanel;
	private JPanel lowerJPanel1, lowerJPanel2, lowerJPanel3, lowerJPanel4,
			lowerJPanel5, lowerJPanel6;
	private JButton btn1, btn2, btn3, btn4, btn5, btn6;
	private JTextArea jta1, jta2, jta3;
	private JTextField jtf1, jtf2, jtf3;

	public CardLayoutTest() {
		jframe = new JFrame("CardLayout测试");

		cardManager = new CardLayout();

		bottomJPanel = new JPanel();
		topJPanel = new JPanel();
		lowerJPanel1 = new JPanel();
		lowerJPanel2 = new JPanel();
		lowerJPanel3 = new JPanel();
		lowerJPanel4 = new JPanel();
		lowerJPanel5 = new JPanel();
		lowerJPanel6 = new JPanel();

		btn1 = new JButton("按钮一");
		btn2 = new JButton("按钮二");
		btn3 = new JButton("按钮三");
		btn4 = new JButton("按钮四");
		btn5 = new JButton("按钮五");
		btn6 = new JButton("按钮六");

		jta1 = new JTextArea();
		jta2 = new JTextArea();
		jta3 = new JTextArea();
		jtf1 = new JTextField();
		jtf2 = new JTextField();
		jtf3 = new JTextField();

		init();
		addHandlers();

	}

	private void init() {
		Container c = jframe.getContentPane();
		c.add(topJPanel, BorderLayout.NORTH);
		c.add(bottomJPanel, BorderLayout.CENTER);

		bottomJPanelInit();
		topJPanelInit();
		lowerJPanel1Init();
		lowerJPanel2Init();
		lowerJPanel3Init();
		lowerJPanel4Init();
		lowerJPanel5Init();
		lowerJPanel6Init();

	}

	private void bottomJPanelInit() {
		bottomJPanel.setLayout(cardManager);
		bottomJPanel.add(lowerJPanel1, "按钮一");
		bottomJPanel.add(lowerJPanel2, "按钮二");
		bottomJPanel.add(lowerJPanel3, "按钮三");
		bottomJPanel.add(lowerJPanel4, "按钮四");
		bottomJPanel.add(lowerJPanel5, "按钮五");
		bottomJPanel.add(lowerJPanel6, "按钮六");
	}

	private void topJPanelInit() {
		topJPanel.add(btn1);
		topJPanel.add(btn2);
		topJPanel.add(btn3);
		topJPanel.add(btn4);
		topJPanel.add(btn5);
		topJPanel.add(btn6);
	}

	private void lowerJPanel1Init() {
		lowerJPanel1.setBackground(Color.BLUE);
		jta1.setText("btn1----lowerJPanel1");
		lowerJPanel1.add(jta1);
	}

	private void lowerJPanel2Init() {
		lowerJPanel2.setBackground(Color.CYAN);
		jta2.setText("btn2----lowerJPanel2");
		lowerJPanel2.add(jta2);
	}

	private void lowerJPanel3Init() {
		lowerJPanel3.setBackground(Color.GRAY);
		jta3.setText("btn3----lowerJPanel3");
		lowerJPanel3.add(jta3);
	}

	private void lowerJPanel4Init() {
		lowerJPanel4.setBackground(Color.GREEN);
		jtf1.setText("btn4----lowerJPanel4");
		lowerJPanel4.add(jtf1);
	}

	private void lowerJPanel5Init() {
		lowerJPanel5.setBackground(Color.MAGENTA);
		jtf2.setText("btn5----lowerJPanel5");
		lowerJPanel5.add(jtf2);
	}

	private void lowerJPanel6Init() {
		lowerJPanel6.setBackground(Color.BLACK);
		jtf3.setText("btn6----lowerJPanel6");
		lowerJPanel6.add(jtf3);
	}

	public void addHandlers() {
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮一");
			}
		});
		btn2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮二");
			}
		});
		btn3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮三");
			}
		});
		btn4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮四");
			}
		});
		btn5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮五");
			}
		});
		btn6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardManager.show(bottomJPanel, "按钮六");
			}
		});
	}

	public void showMe() {
		jframe.setSize(500, 300);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}

	public static void main(String[] args) {
		new CardLayoutTest().showMe();
	}
}
