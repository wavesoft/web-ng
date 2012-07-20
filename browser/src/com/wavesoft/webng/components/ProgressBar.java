/*
 * ProgressBar.java
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
 * Created on Jul 19, 2012, 4:05:42 PM
 */
package com.wavesoft.webng.components;

import com.wavesoft.webng.render.AnimationSync;
import com.wavesoft.webng.render.AnimationSyncHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author icharala
 */
public class ProgressBar extends javax.swing.JPanel implements AnimationSyncHandler, ComponentListener {

    private Integer maxValue = 5;
    private Integer minValue = 0;
    private Integer value = 0;
    private Color color = Color.BLUE;
    
    private ArrayList<BarPoint> points = new ArrayList<BarPoint>();
    private Integer aniI = -100;
    
    public enum Kind {
        ProgressBar, WorkerBar
    }
    
    private static class BarPoint {
        public Integer x;
        public Integer y;
        public Integer r;
        public Integer maxR;
        private Color c1;
        private Color c2;

        private BarPoint(Color color) {
            this.x = 0;
            this.y = 0;
            this.r = 0;
            this.maxR = 0;
            setColor(color);
        }
        
        public final void setColor(Color c) {
            // Prepare gradient colors
            c1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
        }
        
        public void render(Graphics2D g2) {
            if (r<=0) return;

            // Setup gradient
            RadialGradientPaint p = new RadialGradientPaint(
                    new Point2D.Float(x,y), r/2,
                    new float[]{ 0f, 1f },
                    new Color[]{ c1, c2 }
                    );

            // Paint bullet
            g2.setPaint(p);
            g2.fillOval(x-r/2, y-r/2, r, r);
        }
        
    }
    
    @Override
    public void componentResized(ComponentEvent ce) {
        realignPoints();
        animate(System.currentTimeMillis());
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
        realignPoints();
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

    @Override
    public void animate(Long tick) {
        for (int i=0; i<points.size(); i++) {
            BarPoint p = points.get(i);
            int dist = Math.abs(p.x - aniI);
            if (dist < 100) {
                double ofs = Math.sin(Math.PI/2+Math.PI*dist/200)*p.maxR/2;
                p.r = p.maxR/2 + (int)ofs;
            } else {
                p.r = p.maxR/2;
            }
        }
        aniI+=5;
        if (aniI >= getWidth()+100) aniI = -100;
        repaint();    
    }
    
    private void regenDots() {
        points.clear();
        for (int i=0; i<maxValue; i++) {
            points.add(new BarPoint(color));
        }
        realignPoints();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        regenDots();
    }
    
    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
        regenDots();
    }
    
    /** Creates new form ProgressBar */
    public ProgressBar() {
        initComponents();
        setMaxValue(10);
        addComponentListener(this);
        AnimationSync.registerHandler(this, 30);
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (BarPoint p: points) {
            p.render(g2);
        }
    }
    
    public void realignPoints() {
        int size = getHeight();
        int space = 0;
        if (points.size()>1) space=(getWidth() - size) / (points.size()-1);
        
        int x=size/2;
        for (int i=0; i<points.size(); i++) {
            BarPoint p = points.get(i);
            p.x = x;
            p.y = size/2;
            p.r = size;
            p.maxR = size;
            x += space;
        }
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
        setSize(new java.awt.Dimension(283, 31));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 283, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 31, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
