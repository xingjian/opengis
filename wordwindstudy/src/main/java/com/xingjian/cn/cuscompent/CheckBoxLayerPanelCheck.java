/**@Title: CusLayerPanel.java @author promisePB xingjian@yeah.net @date 2010-12-1 下午04:51:15 */

package com.xingjian.cn.cuscompent;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.Layer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

/**   
 * @Title: CusLayerPanel.java 
 * @Package com.xingjian.cn.cuscompent 
 * @Description: 图层管理面板 
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-1 下午04:51:15 
 * @version V1.0   
 */

public class CheckBoxLayerPanelCheck extends JPanel
{
	private static final long serialVersionUID = 1L;
	public JPanel layersPanel;
    public JPanel westPanel;
    public JScrollPane scrollPane;

    public CheckBoxLayerPanelCheck(WorldWindow wwd)
    {
        super(new BorderLayout());
        this.makePanel(wwd, new Dimension(200, 400));
    }

    public CheckBoxLayerPanelCheck(WorldWindow wwd, Dimension size)
    {
        super(new BorderLayout());
        this.makePanel(wwd, size);
    }

    private void makePanel(WorldWindow wwd, Dimension size)
    {
        this.layersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.layersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(wwd);

        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.layersPanel, BorderLayout.NORTH);

        this.scrollPane = new JScrollPane(dummyPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        if (size != null)
            this.scrollPane.setPreferredSize(size);

        westPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        westPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder(" 图层管理  ")));
        westPanel.setToolTipText("显示图层");
        westPanel.add(scrollPane);
        this.add(westPanel, BorderLayout.CENTER);
    }

    private Font defaultFont;
    private Font atMaxFont;

    @SuppressWarnings("unused")
	private void updateStatus()
    {
        for (Component layerItem : this.layersPanel.getComponents())
        {
            if (!(layerItem instanceof JCheckBox))
                continue;

            LayerAction action = (LayerAction) ((JCheckBox) layerItem).getAction();
            if (!(action.layer.isMultiResolution()))
                continue;

            if ((action.layer).isAtMaxResolution())
                layerItem.setFont(this.atMaxFont);
            else
                layerItem.setFont(this.defaultFont);
        }
    }

    private void fill(WorldWindow wwd)
    {
        for (Layer layer : wwd.getModel().getLayers())
        {
            LayerAction action = new LayerAction(layer, wwd, layer.isEnabled());
            JCheckBox jcb = new JCheckBox(action);
            jcb.setSelected(action.selected);
            this.layersPanel.add(jcb);

            if (defaultFont == null)
            {
                this.defaultFont = jcb.getFont();
                this.atMaxFont = this.defaultFont.deriveFont(Font.ITALIC);
            }
        }
    }

    public void update(WorldWindow wwd)
    {
        this.layersPanel.removeAll();
        this.fill(wwd);
        this.westPanel.revalidate();
        this.westPanel.repaint();
    }

    @Override
    public void setToolTipText(String string)
    {
        this.scrollPane.setToolTipText(string);
    }

    @SuppressWarnings("serial")
	private static class LayerAction extends AbstractAction
    {
        WorldWindow wwd;
        private Layer layer;
        private boolean selected;

        public LayerAction(Layer layer, WorldWindow wwd, boolean selected)
        {
            super(layer.getName());
            this.wwd = wwd;
            this.layer = layer;
            this.selected = selected;
            this.layer.setEnabled(this.selected);
        }

        public void actionPerformed(ActionEvent actionEvent)
        {
            if (((JCheckBox) actionEvent.getSource()).isSelected())
                this.layer.setEnabled(true);
            else
                this.layer.setEnabled(false);
            wwd.redraw();
        }
    }
}
