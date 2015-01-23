package com.gi.gidesk.map.dialog;
import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

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
public class AboutDialog extends JDialog {
	private JLabel jLabel;
	
	public AboutDialog(){
		initGUI();
	}

	private void initGUI() {
		try {
			{
				this.setTitle("About");
				{
					jLabel = new JLabel();
					getContentPane().add(jLabel, BorderLayout.CENTER);
					jLabel.setText("<html>\n<b>Author:Wu Yongfeng</b>\n<br>\n<b><a href=\"\"><u>warrenwyf@gmail.com</u></a></b>\n</html>");
					jLabel.setPreferredSize(new java.awt.Dimension(310, 97));
				}
			}
			pack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
