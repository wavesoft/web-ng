/*
 * PageFrame.java
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
 * Created on Jul 18, 2012, 6:13:25 PM
 */
package com.wavesoft.webng.ui;

import com.wavesoft.webng.api.BrowserWindow;
import com.wavesoft.webng.api.HeadButton;
import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.api.WebViewEventListener;
import com.wavesoft.webng.io.DownloadManager.DownloadJob;
import com.wavesoft.webng.io.DownloadManager.DownloadListener;
import com.wavesoft.webng.io.JarLoader;
import com.wavesoft.webng.io.WebNGSystem;
import com.wavesoft.webng.render.WebViewError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author icharala
 */
public class BrowserFrame extends javax.swing.JPanel implements BrowserWindow {
    
    private WebViewNG webView;
    private ArrayList<SmoothButton> headButtons = new ArrayList<SmoothButton>();
    private JarLoader loader = new JarLoader();
    
    /** Creates new form PageFrame */
    public BrowserFrame() {
        initComponents();
        
        setView(new WebViewError("No view available", "<html><p>No view is currently available</p></html>"));
    }
    
    public void renderBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw the gradient background of the text address bar
        g2.setPaint(new GradientPaint(
                    0f, 0f, Colorset.G_SEL_BOTTOM,
                    0f, jHeadBarPanel.getHeight(), Colorset.G_SEL_FLOOR ));
        g2.fillRect(0, 0, jHeadBarPanel.getWidth(), jHeadBarPanel.getHeight());
        
        // Draw the separation line
        g2.setColor(Colorset.COLOR_SHADOW);
        g2.drawLine(0, jHeadBarPanel.getHeight()-1, jHeadBarPanel.getWidth(), jHeadBarPanel.getHeight()-1);
    }
    
    private void updateHeadButtons(HeadButton[] buttons) {
        
        // Remove previous head buttons
        if (!headButtons.isEmpty()) {
            for (SmoothButton btn: headButtons) {
                jHeadButtons.remove(btn);
            }
            headButtons.clear();
            
            revalidate();
        }
        
        // Create the head buttons
        if (buttons == null) return;
        for (HeadButton btn: buttons) {
            SmoothButton b = new SmoothButton();
            b.setCustomizations(btn);
            headButtons.add(b);
            jHeadButtons.add(b);
        }
    }
    
    public void setView(WebViewNG view) {
        // Remove previous component
        if (webView != null)
            jViewPanel.remove(webView);
        
        // Add the new webView
        webView = view;
        jViewPanel.setViewportView(view);
        
        view.setVisible(true);
        view.webngSetBrowserWindow(this);
        
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jHeadBarPanel = new javax.swing.JPanel() {
            @Override
            public void paint(Graphics g) {
                renderBackground(g);
                super.paint(g);
            }
        };
        jLocationBar = new javax.swing.JTextField();
        jHeadButtons = new javax.swing.JPanel();
        sbHome = new com.wavesoft.webng.ui.SmoothButton();
        jToolButtons = new javax.swing.JPanel();
        sbTools = new com.wavesoft.webng.ui.SmoothButton();
        jViewPanel = new javax.swing.JScrollPane();

        jHeadBarPanel.setBackground(new java.awt.Color(204, 204, 204));
        jHeadBarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jHeadBarPanel.setOpaque(false);

        jLocationBar.setText("jTextField1");

        jHeadButtons.setOpaque(false);
        jHeadButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        sbHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/home.png"))); // NOI18N
        sbHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sbHomeMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout sbHomeLayout = new org.jdesktop.layout.GroupLayout(sbHome);
        sbHome.setLayout(sbHomeLayout);
        sbHomeLayout.setHorizontalGroup(
            sbHomeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 28, Short.MAX_VALUE)
        );
        sbHomeLayout.setVerticalGroup(
            sbHomeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 28, Short.MAX_VALUE)
        );

        jHeadButtons.add(sbHome);

        jToolButtons.setOpaque(false);
        jToolButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        sbTools.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/tools.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout sbToolsLayout = new org.jdesktop.layout.GroupLayout(sbTools);
        sbTools.setLayout(sbToolsLayout);
        sbToolsLayout.setHorizontalGroup(
            sbToolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 28, Short.MAX_VALUE)
        );
        sbToolsLayout.setVerticalGroup(
            sbToolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 28, Short.MAX_VALUE)
        );

        jToolButtons.add(sbTools);

        org.jdesktop.layout.GroupLayout jHeadBarPanelLayout = new org.jdesktop.layout.GroupLayout(jHeadBarPanel);
        jHeadBarPanel.setLayout(jHeadBarPanelLayout);
        jHeadBarPanelLayout.setHorizontalGroup(
            jHeadBarPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jHeadBarPanelLayout.createSequentialGroup()
                .add(jHeadButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLocationBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jHeadBarPanelLayout.setVerticalGroup(
            jHeadBarPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jHeadButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jToolButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
            .add(jLocationBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jViewPanel.setBorder(null);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jHeadBarPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jViewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jHeadBarPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jViewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sbHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sbHomeMouseClicked

        //navigateTo(loader.getViewByName("com.wavesoft.templates.basic.BlogHome"));
        WebNGSystem.downloadManager.download("http://localhost/", new DownloadListener() {

            @Override
            public void downloadCompleted(DownloadJob job) {
                navigateTo(new WebViewError("No error", job.buffer.replace("<", "{").replace(">", "}")));
            }

            @Override
            public void downloadFailed(DownloadJob job) {
            }
            
        });
        
    }//GEN-LAST:event_sbHomeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jHeadBarPanel;
    private javax.swing.JPanel jHeadButtons;
    private javax.swing.JTextField jLocationBar;
    private javax.swing.JPanel jToolButtons;
    private javax.swing.JScrollPane jViewPanel;
    private com.wavesoft.webng.ui.SmoothButton sbHome;
    private com.wavesoft.webng.ui.SmoothButton sbTools;
    // End of variables declaration//GEN-END:variables

    @Override
    public void navigateTo(String url) {
    }

    @Override
    public void navigateTo(WebViewNG view) {
        setView(view);
        revalidate();
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void setHeadButtons(HeadButton[] buttons) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addWindowEventListener(WebViewEventListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
