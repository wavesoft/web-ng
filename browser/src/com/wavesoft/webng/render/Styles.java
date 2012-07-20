/*
 * Styles.java
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
package com.wavesoft.webng.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 *
 * @author icharala
 */
public class Styles {
   
    public static final int REPEAT_X = 1;
    public static final int REPEAT_Y = 2;
    public static final int REPEAT = 3;
    public static final int NO_REPEAT = 0;

    public static final int BORDER_NONE = 0;
    public static final int BORDER_SOLID = 1;

    /**
     * Multi-functional positioning information
     */
    public static class CSSPosition {
        
        public static final int MODE_DESCRIPTIVE = 0;
        public static final int MODE_PIXELS = 1;
        public static final int MODE_PERCENT = 2;
        
        public static final int VALUE_LEFT = 1;
        public static final int VALUE_TOP = 2;
        public static final int VALUE_RIGHT = 3;
        public static final int VALUE_BOTTOM = 4;
        public static final int VALUE_CENTER = 5;
        
        public float value = 0f;
        public int mode  = MODE_PIXELS;
        
        public int getValue(int containerSize, int objectSize) {
            if (mode == MODE_PIXELS) {
                return (int) value;
            } else if (mode == MODE_PERCENT) {
                return (int) (containerSize * value);
            } else if (mode == MODE_DESCRIPTIVE) {
                if ((value == VALUE_LEFT) || (value == VALUE_TOP)) {
                    return 0;
                } else if ((value == VALUE_RIGHT) || (value == VALUE_BOTTOM)) {
                    return containerSize;
                } else if (value == VALUE_CENTER) {
                    return (containerSize+objectSize)/2;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        public CSSPosition(int mode, int value) {
            this.mode = mode;
            this.value = value;
        }
        
    }
    
    /**
     * CSS-Like background definition
     */
    public static class CSSBackground {
        public Color         color     = Color.WHITE;
        public BufferedImage image     = null;
        public int           repeat    = NO_REPEAT;
        public CSSPosition   positionX = new CSSPosition(CSSPosition.MODE_DESCRIPTIVE, CSSPosition.VALUE_CENTER);
        public CSSPosition   positionY = new CSSPosition(CSSPosition.MODE_DESCRIPTIVE, CSSPosition.VALUE_CENTER);
    }
    
    public void renderBackground(Graphics2D g2, Dimension size, CSSBackground css) {
        // Render color
        g2.setColor(css.color);
        g2.fillRect(0, 0, size.width, size.height);
        
        // Render background
        if (css.image != null) {
            int imW = css.image.getWidth();
            int imH = css.image.getHeight();
            int xP = css.positionX.getValue(size.width,  imW);
            int yP = css.positionY.getValue(size.height, imH);
            
            if (css.repeat == REPEAT) {
                TexturePaint tp = new TexturePaint(css.image, new Rectangle( xP, yP ));
                g2.setPaint(tp);
                g2.fillRect(0, 0, size.width, size.height);
                
            } else if (css.repeat == REPEAT_X) {
                TexturePaint tp = new TexturePaint(css.image, new Rectangle( 0, 0 ));
                g2.setPaint(tp);
                g2.fillRect(0, yP, size.width, yP+imH);
                
            } else if (css.repeat == REPEAT_Y) {
                TexturePaint tp = new TexturePaint(css.image, new Rectangle( 0, 0 ));
                g2.setPaint(tp);
                g2.fillRect(xP, 0, xP+imH, size.width);
                
            } else {
                g2.drawImage(css.image, xP, yP, null);
                
            }
        }
    }
    
    public static class CSSBorderSide {
        public Color         color      = Color.WHITE;
        public int           type       = BORDER_NONE;
    }
    
    public static class CSSBorder {
        public CSSBorderSide    left;
        public CSSBorderSide    right;
        public CSSBorderSide    top;
        public CSSBorderSide    bottom;
    }
    
    public void renderBorder(Graphics2D g2, Dimension2D size) {
        
    }
    
}
