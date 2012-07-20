/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Tabs.java
 *
 * Created on Jul 18, 2012, 2:15:38 PM
 */
package com.wavesoft.webng.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author icharala
 */
public class Tabs extends javax.swing.JPanel implements MouseListener, MouseMotionListener, ComponentListener {

    private Integer draggingItem = -1;
    private Integer dragOffset = 0;
    private Integer pressedButton = -1;
    
    protected static int leftPadding = 0;
    protected static int rightPadding = 0;
    protected static int topPadding = 15;
    
    protected static int TAB_SIZE = 150;
    protected static int TAB_OVERLAP = 35;
    
    // =====================================
    //          CLASS DEFINITIONS
    // =====================================

    private static class SmallButton {
        public Integer x;
        public Integer y;
        public Integer diameter;
        public int state = 0;
        public String title = "+";
        public Boolean visible = true;
        
        private Font font;

        public SmallButton(Integer x, Integer y, Integer diameter) {
            this.x = x;
            this.y = y;
            this.diameter = diameter;
            this.font = new java.awt.Font("Arial", Font.BOLD, diameter);
        }
        
        public Boolean hitsCursor(int x, int y) {
            int r = (int) Math.sqrt(Math.pow(x-this.x, 2) + Math.pow(y-this.y, 2));
            return (r < diameter/2);
        }
        
        public void render(Graphics2D g2) {
            g2.setFont(font);
        
            if (state == 0) {
                g2.setColor(Colorset.COLOR_SHADOW);
            } else if (state == 1) {
                g2.setColor(Colorset.COLOR_SHADOW);
                g2.fillOval(x-diameter/2, y-diameter/2, diameter, diameter);
                g2.setColor(Colorset.COLOR_LIGHT_TEXT);
            } else if (state == 2) {
                g2.setColor(Colorset.COLOR_DARK_SHADOW);
                g2.fillOval(x-diameter/2, y-diameter/2, diameter, diameter);
                g2.setColor(Colorset.COLOR_LIGHT_TEXT);
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int fW = fm.stringWidth(title);
            
            g2.drawString(title, 
                    x - fW/2 -1,
                    y + fm.getHeight()/4 +1
                    );
        }
        
    }
    
    /**
     * Internal class to represent and render the tabs
     */
    private static class Tab {
        public String title;
        public Boolean selected;
        public Image img;
        public Integer x;
        public Integer anchorX;
        public Integer width;
        public SmallButton tabClose;
        
        private static int   CURVE_WIDTH = 20;
        private static float CURVED_EDGE[] = new float[] {
            // Relative coordinates of a curved edge
            // Scale them and draw them as lines :D
            0f, 1f, 
            0.1f, 0.99f, 0.18f, 0.96f, 0.26f, 0.90f, 0.32f, 0.84f, 0.37f, 0.75f, 0.54f, 0.29f,
            0.58f, 0.21f, 0.62f, 0.15f, 0.66f, 0.10f, 0.73f, 0.05f, 0.80f, 0.02f, 0.87f, 0.0f, 
            1f, 0f,
        };

        public Tab(String title, Image img) {
            this.title = title;
            this.selected = false;
            this.img = img;
            this.x = 0;
            this.anchorX = 0;
            this.width = 0;
            this.tabClose = new SmallButton(x, 0, 14);
            this.tabClose.title = "x";
        }
        
        public void render(Graphics2D g2, Integer height) {
            int lX=-1, lY=-1;
            int lX2=-1, lY2=-1;

            // ============================
            // Prepare the outline polygon
            // ============================
            Polygon p = new Polygon();
            int oi = 0;
            int outlineX[] = new int[CURVED_EDGE.length*2];
            int outlineY[] = new int[CURVED_EDGE.length*2];
            
            // Start from outside
            p.addPoint(x, height+5);
            
            // Draw left curve
            lX=-1; lY=-1;
            for (int i=0; i<CURVED_EDGE.length; i+=2 ) {
                int xP = x + (int) (CURVE_WIDTH * CURVED_EDGE[i]);
                int yP = (int) ((height-1-topPadding) * CURVED_EDGE[i+1]) + topPadding;
                p.addPoint(xP, yP);
                outlineX[oi++] = xP;
                outlineY[oi] = yP;
            }
            
            // Draw right curve
            lX2=-1; lY2=-1;
            for (int i=CURVED_EDGE.length-2; i>=0; i-=2 ) {
                int xP = x + width + (int) (-CURVE_WIDTH * CURVED_EDGE[i]);
                int yP = (int) ((height-1-topPadding) * CURVED_EDGE[i+1]) + topPadding;
                p.addPoint(xP, yP);
                outlineX[oi++] = xP;
                outlineY[oi] = yP;
            }

            // Finish outside
            p.addPoint(x+width, height+5);

            // ============================
            // Draw the background
            // ============================
            GradientPaint gp;
            if (selected) {
                gp = new GradientPaint(
                    0f, topPadding, Colorset.G_SEL_TOP,
                    0f, height-topPadding, Colorset.G_SEL_BOTTOM );
            } else {
                gp = new GradientPaint(
                    0f, topPadding, Colorset.G_UNSEL_TOP,
                    0f, height-topPadding, Colorset.G_UNSEL_BOTTOM );
            }
            g2.setPaint(gp);
            g2.fillPolygon(p);            
            
            // ============================
            // Draw the Highlight
            // ============================
            g2.setColor(Colorset.COLOR_HIGHLIGHT);
            
            // Draw left curve
            lX=-1; lY=-1;
            for (int i=0; i<CURVED_EDGE.length; i+=2 ) {
                int xP = x + (int) (CURVE_WIDTH * CURVED_EDGE[i]) + 1;
                int yP = (int) ((height-1-topPadding) * CURVED_EDGE[i+1]) + 1 + topPadding;
                if (lX != -1) {
                    g2.drawLine(lX, lY, xP, yP);
                }
                lX = xP;
                lY = yP;
            }
            
            // Draw right curve
            lX2=-1; lY2=-1;
            for (int i=0; i<CURVED_EDGE.length; i+=2 ) {
                int xP = x + width + (int) (-CURVE_WIDTH * CURVED_EDGE[i]) - 1 ;
                int yP = (int) ((height-1-topPadding) * CURVED_EDGE[i+1]) + 1 + topPadding;
                if (lX2 != -1) {
                    g2.drawLine(lX2, lY2, xP, yP);
                }
                lX2 = xP;
                lY2 = yP;
            }
            
            // Close the highlight curve
            g2.drawLine(lX, lY, lX2, lY2);

            // ============================
            // Draw the outline
            // ============================
            g2.setColor(Colorset.COLOR_SHADOW);
            int xpoints[] = Arrays.copyOfRange(p.xpoints, 1, p.npoints-1);
            int ypoints[] = Arrays.copyOfRange(p.ypoints, 1, p.npoints-1);
            g2.drawPolyline(xpoints, ypoints, p.npoints-2);
            
            // ============================
            // Draw text
            // ============================
            g2.setColor(Colorset.COLOR_TEXT);
            g2.setFont(Colorset.FONT_GENERIC);
            FontMetrics fm = g2.getFontMetrics();
            int xP = x + CURVE_WIDTH;
            int yP = (height-topPadding+fm.getHeight())/2 - 3 + topPadding;
            g2.drawString(title, xP, yP);
            
            // ============================
            // Draw close button
            // ============================
            //tabClose.x = x + width - 20;
            //tabClose.y = (height-topPadding)/2 + topPadding;
            //tabClose.render(g2);
        }
        
    }

    // =====================================
    //          EVENT LISTENERS
    // =====================================

    @Override
    public void componentResized(ComponentEvent ce) {
        resizeTabs();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (pressedButton != -1) {
            buttons[pressedButton].state = 2;
            repaint();
            return;
        }
        draggingItem = getTabFromXY(me.getX(), me.getY());
        if (draggingItem != -1) {
            dragOffset = me.getX() - tabs[draggingItem].x;
            selectTab(draggingItem);
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (pressedButton != -1) {
            buttons[pressedButton].state = 1;
            repaint();
        }
        if (draggingItem != -1) {
            tabs[draggingItem].x = tabs[draggingItem].anchorX;
            repaint();
            draggingItem = -1;
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (pressedButton != -1) {
            buttons[pressedButton].state = 0;
            repaint();
            pressedButton = -1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (this.draggingItem != -1) {
            Tab t = this.tabs[this.draggingItem];
            
            // Check for reordering
            int placement = getPlacementFromXY(me.getX(), me.getY());
            if (placement != -1) {
                
                // Swap the item on the specified position
                if (placement != draggingItem) {
                    
                    // Swap element
                    Tab tmp = tabs[draggingItem];
                    tabs[draggingItem] = tabs[placement];
                    tabs[placement] = tmp;
                    
                    // Update the dragging item ID
                    draggingItem = placement;
                    
                    // Resize items
                    resizeTabs();
                    
                }
                
            }

            // Update the actual position of this tab
            t.x = me.getX() - this.dragOffset;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        int foundButton = -1;
        for (int i=0; i<buttons.length; i++) {
            if (buttons[i].hitsCursor(x,y)) {
                buttons[i].state = 1;
                foundButton = i;
            } else {
                buttons[i].state = 0;
            }
        }
        if (pressedButton != foundButton) {
            pressedButton = foundButton;
            repaint();
        }
    }
    
    // =====================================
    //          ACTUAL CODE
    // =====================================
    
    private Tab[] tabs;
    private SmallButton[] buttons;
    private SmallButton newTabButton;
    
    /**
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     * @param oldArray  the old array, to be reallocated.
     * @param newSize   the new array size.
     * @return          A new array with the same contents.
     */
    private static Object resizeArray (Object oldArray, int newSize) {
       int oldSize = java.lang.reflect.Array.getLength(oldArray);
       Class elementType = oldArray.getClass().getComponentType();
       Object newArray = java.lang.reflect.Array.newInstance(
             elementType, newSize);
       int preserveLength = Math.min(oldSize, newSize);
       if (preserveLength > 0)
          System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
       return newArray; 
    }
    
    public Tabs() {
        initComponents();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
        
        tabs = new Tab[0];
        buttons = new SmallButton[0];
        
        addTab("New Tab");
        addTab("Another tab");
        addTab("Yet another");
        selectTab(0);
        
        newTabButton = new SmallButton(0, 0, 16);
        newTabButton.y = topPadding + 12;
        addButton(newTabButton);
        
    }
    
    private Integer getTabFromXY(int x, int y) {
        for (int i=0; i<tabs.length; i++) {
            Tab t = tabs[i];
            if ((x >= t.x) && (x <= t.x+t.width) && (y >= topPadding)) {
                return i;
            }
        }
        return -1;
    }
    
    private Integer getPlacementFromXY(int x, int y) {
        int x1 = tabs[0].anchorX;
        for (int i=0; i<tabs.length; i++) {
            int x2 = x1 + tabs[i].width;
            if ((x >= x1) && (x <= x2)) return i;
            x1 = x2;
        }
        return -1;
    }
    
    public void addTab(String title) {
        int i = tabs.length;
        tabs = (Tab[]) resizeArray(tabs, i+1);
        tabs[i] = new Tab(title, null);
        //addButton(tabs[i].tabClose);
        resizeTabs();
    }
    
    private void addButton(SmallButton btn) {
        int i = buttons.length;
        buttons = (SmallButton[]) resizeArray(buttons, i+1);
        buttons[i] = btn;
        resizeTabs();
    }
    
    public void resizeTabs() {
        if (tabs.length == 0) return;
        int width = getWidth() - leftPadding - rightPadding;
        int slice = width / tabs.length;
        if (slice > TAB_SIZE) slice=TAB_SIZE;
        int x = leftPadding;
        for (int i=0; i<tabs.length; i++) {
            tabs[i].x = x;
            tabs[i].anchorX = x;
            tabs[i].width = slice;
            //tabs[i].tabClose.x = x + slice - 10;
            //tabs[i].tabClose.y = topPadding;
            x += slice - TAB_OVERLAP/2;
        }
        
        if (newTabButton != null)
            newTabButton.x = x + TAB_OVERLAP - 10;
    }
    
    public void selectTab(int tab) {
        if (tab < 0) return;
        if (tab >= tabs.length) return;
        for (int i=0; i<tabs.length; i++) {
            tabs[i].selected = (i == tab);
        }
    }
    

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        Integer height = getHeight();
        
        // Enable antialias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render background
        GradientPaint gp = new GradientPaint(
            0f, 0, Colorset.G_BACK_TOP,
            0f, height, Colorset.G_BACK_BOTTOM );
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), height);
        
        // Render the new tab
        //newTabButton.render(g2);
        
        // Render inactive tabs
        Tab activeTab = null;
        for (int i=tabs.length-1; i>=0; i--) {
            if (!tabs[i].selected) {
                tabs[i].render(g2, height);
            } else {
                activeTab = tabs[i];
            }
        }
        
        // Draw the separator lines
        g2.setColor(Colorset.COLOR_HIGHLIGHT);
        g2.drawLine(0, height-1, getWidth(), height-1);
        g2.setColor(Colorset.COLOR_SHADOW);
        g2.drawLine(0, height-2, getWidth(), height-2);
        
        // Render the active tab
        if (activeTab != null)
            activeTab.render(g2, height-1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 514, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
