/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 *
 * @author icharala
 */
public class RoundedCordersBorder implements javax.swing.border.Border {

    protected static Color COLOR_SHADOW = new Color(0x999999);
    protected static Color COLOR_HIGHLIGHT = new Color(0xf9f9f9);
    protected static Color COLOR_TEXT = new Color(0);

    @Override
    public void paintBorder(Component cmpnt, Graphics grphcs, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(COLOR_HIGHLIGHT);
        g2.drawRoundRect(2, 2, w-4, h-4, 5, 5);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(COLOR_SHADOW);
        g2.drawRoundRect(2, 2, w-4, h-4, 5, 5);
    }

    @Override
    public Insets getBorderInsets(Component cmpnt) {
        return new Insets(7, 7, 7, 7);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
    
}
