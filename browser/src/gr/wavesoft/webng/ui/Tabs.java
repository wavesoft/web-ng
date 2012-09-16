/*
 * Tabs.java
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
 * Created on Jul 18, 2012, 2:15:38 PM
 */
package gr.wavesoft.webng.ui;

import java.awt.Component;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Icon;

/**
 *
 * @author icharala
 */
public class Tabs extends javax.swing.JPanel implements MouseListener, MouseMotionListener, ComponentListener {

    private Integer draggingItem = -1;
    private Integer dragOffset = 0;
    private Integer pressedButton = -1;
    
    protected static int leftPadding = 0;
    protected static int rightPadding = 40;
    protected static int topPadding = 15;
    
    protected static int TAB_SIZE = 200;
    protected static int TAB_OVERLAP = 35;
    
    // =====================================
    //          CLASS DEFINITIONS
    // =====================================
    
    public interface TabChangeListener {
        public void tabChanged(int index, String key);
        public void tabClosed(int index, String key);
        public void newTab();
    }
    
    private static class AnimationThread extends Thread {
        Tabs tabsInstance;
        private boolean active;

        public AnimationThread(Tabs tabsInstance) {
            this.tabsInstance = tabsInstance;
            this.active = true;
        }
        
        public void deactivate() {
            this.active = false;
        }
        
        @Override
        public void run() {
            Thread.currentThread().setName("Animation-Tabs");
            while (active && !Thread.interrupted()) {
                active = tabsInstance.animationFrame();
                try {
                    Thread.sleep(17);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
        
    }

    public static class ImageButton {
        public Integer x;
        public Integer y;
        public Integer width;
        public Integer height;
        public Boolean visible = true;
        public int state = 0;
        private BufferedImage image = null;
        public String key;
        public Tab tab;

        public ImageButton(Component cmp, Integer x, Integer y, Icon image) {
            this.x = x;
            this.y = y;
            this.width = 0;
            this.height = 0;
            if (image == null) return;
            this.width = image.getIconWidth()/3;
            this.height = image.getIconHeight();
            this.image = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            image.paintIcon(cmp, this.image.getGraphics(), 0, 0);
        }
        
        public Boolean hitsCursor(int x, int y) {
            return (x >= this.x) && (x <= this.x+this.width) && (y >= this.y) && (y <= this.y+this.height);
        }
        
        public void render(Graphics2D g2) {
            if (image == null) return;
            g2.drawImage(image, x, y, x+width, y+height, width*state, 0, width*state+width, height, null);
        }
        
    }
    
    /**
     * Internal class to represent and render the tabs
     */
    public static class Tab {
        public String title;
        public Boolean selected;
        public Image img;
        public Integer x;
        public Integer anchorX;
        public Integer anchorW;
        public Integer width;
        public ImageButton tabClose;
        public String key;
        public Boolean dragging = false;
        
        private static int   CURVE_WIDTH = 20;
        private static float CURVED_EDGE[] = new float[] {
            // Relative coordinates of a curved edge
            // Scale them and draw them as lines :D
            0f, 1f, 
            0.1f, 0.99f, 0.18f, 0.96f, 0.26f, 0.90f, 0.32f, 0.84f, 0.37f, 0.75f, 0.54f, 0.29f,
            0.58f, 0.21f, 0.62f, 0.15f, 0.66f, 0.10f, 0.73f, 0.05f, 0.80f, 0.02f, 0.87f, 0.0f, 
            1f, 0f,
        };

        public Tab(String title, String key, Image img, Component cmp) {
            this.title = title;
            this.selected = false;
            this.key = key;
            this.img = img;
            this.x = 0;
            this.anchorW = 10;
            this.anchorX = 0;
            this.width = 0;
            this.tabClose = new ImageButton(cmp, x, 0, new javax.swing.ImageIcon(getClass().getResource("/gr/wavesoft/webng/resources/btn-close.png")));
            this.tabClose.key = key;
            this.tabClose.tab = this;
        }
        
        public void moveXW(Integer x, Integer width) {
            if (width != null) this.width = width;
            this.x = x;
            tabClose.x = x + this.width - tabClose.width - 15;
            tabClose.y = topPadding + 7;
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
            // Draw icon + text
            // ============================
            g2.setColor(Colorset.COLOR_TEXT);
            g2.setFont(Colorset.FONT_GENERIC);
            FontMetrics fm = g2.getFontMetrics();
            int xP = x + CURVE_WIDTH - 2;
            int yP = (height-topPadding+fm.getHeight())/2 - 3 + topPadding;
            
            int availTitleSpace = tabClose.x - 5 - xP;
            
            if ((this.img != null) && (availTitleSpace >= 16)) {
                
                // Calculate image clipping
                g2.drawImage(img, 
                    xP, topPadding+4, xP+16, topPadding+4+16, 
                    0, 0, img.getHeight(null), img.getHeight(null), 
                    null);

                xP += 20;
                availTitleSpace -= 20;
                if (availTitleSpace < 0)
                    availTitleSpace = 0;
            }
            
            // Clip title based on the text size
            String renderTitle = title;
            int txt_w = fm.stringWidth(title);
            if (txt_w > availTitleSpace) {
                int txt_dots = fm.stringWidth("..");
                if (txt_dots > availTitleSpace) {
                    renderTitle = "";
                } else {
                    renderTitle = "";
                    for (int i=0; i<title.length(); i++) {
                        char c = title.charAt(i);
                        if (fm.stringWidth(renderTitle + c) + txt_dots > availTitleSpace) {
                            break;
                        }
                        renderTitle += title.charAt(i);
                    }
                    renderTitle += "..";
                }
            }
            
            g2.drawString(renderTitle, xP, yP);
            
            // ============================
            // Draw close button
            // ============================
            tabClose.render(g2);
        }
        
    }

    // =====================================
    //          EVENT LISTENERS
    // =====================================

    @Override
    public void componentResized(ComponentEvent ce) {
        newTabButton.y = getHeight() - newTabButton.height;
        resizeTabs(false);
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
        if (pressedButton != -1) {
            me.consume();
            if ("#NEWTAB#".equals(buttons[pressedButton].key)) {
                for (TabChangeListener l: tabChangeListeners) {
                    l.newTab();
                }
            } else {
                for (TabChangeListener l: tabChangeListeners) {
                    l.tabClosed(
                        java.util.Arrays.asList(tabs).indexOf(buttons[pressedButton].tab), 
                        buttons[pressedButton].key
                        );
                }
            }
        }
        if (draggingItem != -1) {
            me.consume();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (pressedButton != -1) {
            me.consume();
            buttons[pressedButton].state = 2;
            repaint();
            return;
        }
        draggingItem = getTabFromXY(me.getX(), me.getY());
        if (draggingItem != -1) {
            if (tabs.length > 1) {
                me.consume();
                dragOffset = me.getX() - tabs[draggingItem].x;
                selectTab(draggingItem);
                tabs[draggingItem].dragging = true;
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (pressedButton != -1) {
            me.consume();
            buttons[pressedButton].state = 1;
            repaint();
        }
        if (draggingItem != -1) {
            if (tabs.length > 1) {
                me.consume();
                //tabs[draggingItem].moveXW(tabs[draggingItem].anchorX, null);
                tabs[draggingItem].dragging = false;
                startAnimation();
            }
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
        if ((this.draggingItem != -1) && (tabs.length > 1)) {
            me.consume();
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
                    resizeTabs(true);
                    
                }
                
            }

            // Update the actual position of this tab
            t.moveXW(me.getX() - this.dragOffset, null);
            
            // Repaint
            updateNewTabPosition();
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
    private ImageButton[] buttons;
    private ImageButton newTabButton;
    private ArrayList<TabChangeListener> tabChangeListeners;
    private AnimationThread animationThread = new AnimationThread(this);
    
    private void startAnimation() {
        if (animationThread.isAlive()) return;
        animationThread = new AnimationThread(this);
        animationThread.start();
    }
    
    private void updateNewTabPosition() {
        int maxX = 0;
        if (newTabButton == null) return;
        for (int i=0; i<tabs.length; i++) {
            // Update maxX for the newTabButton
            if ((tabs[i].x + tabs[i].width) > maxX)
                maxX = tabs[i].x + tabs[i].width;
        }
        newTabButton.x = maxX - 10;
    }
    
    private boolean animationFrame() {
        Boolean moved=false;
        int maxX = 0;
        
        // Update tabs position
        for (int i=0; i<tabs.length; i++) {
            Tab t = tabs[i];
            if (t == null) continue;
            if (!t.dragging) {
                int cX = t.x;
                int cW = t.width;

                // Change position
                if (cX == t.anchorX) {
                } else if (Math.abs(cX - t.anchorX) < 5) {
                    moved=true;
                    cX = t.anchorX;
                } else {
                    moved=true;
                    cX += (t.anchorX-cX)/3;
                }

                // Change size
                if (cW == t.anchorW) {
                } else if (Math.abs(cW - t.anchorW) < 5) {
                    moved=true;
                    cW = t.anchorW;
                } else {
                    moved=true;
                    cW += (t.anchorW-cW)/3;
                }

                // Update UI
                if (moved)
                    t.moveXW(cX, cW);
                
            }
        }
        
        if (moved) {
            updateNewTabPosition();
            repaint();
        }
        
        return moved;
    }
    
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
        buttons = new ImageButton[0];
        tabChangeListeners = new ArrayList<TabChangeListener>();
        
        newTabButton = new ImageButton(this, 0, 0, new javax.swing.ImageIcon(getClass().getResource("/gr/wavesoft/webng/resources/btn-newtab.png")));
        newTabButton.y = this.getHeight() - newTabButton.height;
        newTabButton.key = "#NEWTAB#";
        addButton(newTabButton);
        
    }
    
    public void addTabChangeListener(TabChangeListener l) {
        tabChangeListeners.add(l);
    }
    
    public void removeTabChangeListener(TabChangeListener l) {
        tabChangeListeners.remove(l);
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
    
    public Tab addTab(String title, String key) {
        int i = tabs.length;
        tabs = (Tab[]) resizeArray(tabs, i+1);
        tabs[i] = new Tab(title, key, SystemIcons.tabIconDefault, this);
        if (i>0) tabs[i].x = tabs[i-1].anchorX + tabs[i-1].anchorW;
        addButton(tabs[i].tabClose);
        resizeTabs(true);
        selectTab(i);
        return tabs[i];
    }
    
    public void removeTab(int index) {
        removeButton(tabs[index].tabClose);
        Tab[] newTabs = new Tab[tabs.length-1];
        int j=0;
        for (int i=0; i<tabs.length; i++) {
            if (i != index) {
                newTabs[j] = tabs[i];
                j++;
            }
        }
        tabs = newTabs;
        resizeTabs(true);
        if (index<tabs.length) {
            selectTab(index);
        } else if (index>0) {            
            selectTab(index-1);
        }
    }
    
    private void addButton(ImageButton btn) {
        int i = buttons.length;
        buttons = (ImageButton[]) resizeArray(buttons, i+1);
        buttons[i] = btn;
    }
    
    private void removeButton(ImageButton btn) {
        ImageButton[] newButtons = new ImageButton[buttons.length-1];
        int j=0;
        for (int i=0; i<buttons.length; i++) {
            if (!buttons[i].equals(btn)) {
                newButtons[j] = buttons[i];
                j++;
            }
        }
        buttons = newButtons;
    }
    
    public void resizeTabs(Boolean animated) {
        if (tabs.length == 0) return;
        int width = getWidth() - leftPadding - rightPadding;
        int slice = (width / tabs.length) + TAB_OVERLAP/2;
        if (slice > TAB_SIZE) slice=TAB_SIZE;
        int x = leftPadding;
        for (int i=0; i<tabs.length; i++) {
            if (!animated) {
                tabs[i].moveXW(x, slice);
            }
            tabs[i].anchorX = x;
            tabs[i].anchorW = slice;
            x += slice - TAB_OVERLAP/2;
        }
        
        updateNewTabPosition();
                
        if (animated)
            startAnimation();
    }
    
    public void selectTab(int tab) {
        if (tab < 0) return;
        if (tab >= tabs.length) return;
        for (int i=0; i<tabs.length; i++) {
            tabs[i].selected = (i == tab);
        }
        String k = tabs[tab].key;
        for (TabChangeListener l: tabChangeListeners) {
            l.tabChanged(tab, k);
        }
    }
    
    public int getSelectedTabIndex() {
        for (int i=tabs.length-1; i>=0; i--) {
            if (tabs[i].selected) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void paint(Graphics grphcs) {
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
        newTabButton.render(g2);
        
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
        
        super.paint(grphcs);
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
        setOpaque(false);

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
