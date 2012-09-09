/*
 * RoundedCornersBorder.java
 * 
 * BrowserNG - A workbench for the browser of the new generation
 * Copyright (C) 2012 Ioannis Charalampidis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
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
public class RoundedCornersBorder implements javax.swing.border.Border {

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
