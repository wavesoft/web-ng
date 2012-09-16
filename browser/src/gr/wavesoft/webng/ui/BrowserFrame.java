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
package gr.wavesoft.webng.ui;

import gr.wavesoft.webng.api.BrowserWindow;
import gr.wavesoft.webng.api.HeadButton;
import gr.wavesoft.webng.api.WebViewDataListener;
import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.api.WebViewEventListener;
import gr.wavesoft.webng.io.JarLoader;
import gr.wavesoft.webng.io.PublicKeyEventListener;
import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.render.WebViewError;
import gr.wavesoft.webng.render.WebViewHome;
import gr.wavesoft.webng.render.WebViewLoading;
import gr.wavesoft.webng.ui.Tabs.Tab;
import gr.wavesoft.webng.wblang.WLData;
import gr.wavesoft.webng.webstreams.RStreamCallback;
import gr.wavesoft.webng.webstreams.RWStreamCallback;
import gr.wavesoft.webng.webstreams.WStreamCallback;
import gr.wavesoft.webng.webstreams.WebStreamContext;
import gr.wavesoft.webng.webstreams.http.HTTPRequest;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class BrowserFrame extends javax.swing.JPanel implements BrowserWindow, PresenterEventListener, PublicKeyEventListener {
    
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(BrowserFrame.class, "BrowserWindow");
    
    private WebViewNG webView;
    private ArrayList<SmoothButton> headButtons = new ArrayList<SmoothButton>();
    private JarLoader loader = new JarLoader();
    private Tab linkedTab = null;
    private Thread presenterThread = null;
    private String pageURL;
    
    // Some frequently used views
    private WebViewLoading loadingView;
    private WebViewHome homeView;
    
    // The streams object
    private WebStreamContext streams;
    
    /** Creates new form PageFrame */
    public BrowserFrame() {
        initComponents();
        jViewPanel.getVerticalScrollBar().setUnitIncrement(16);
        jViewPanel.getHorizontalScrollBar().setUnitIncrement(16);
        
        loadingView = new WebViewLoading();
        homeView = new WebViewHome();
        streams = new WebStreamContext();
        
        setView(homeView);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                System.out.println(ke.toString());
            }
            
        });
    }
    
    public void focusOnLocationBar() {
        jLocationBar.requestFocus();
    }
    
    public String getTitle() {
        return "New Tab";
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
        if (webView != null) {
            webView.webngSetBrowserWindow(null);
            jViewPanel.remove(webView);
            webView = null;
            System.gc();
        }
        
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
        sbHome = new gr.wavesoft.webng.ui.SmoothButton();
        jToolButtons = new javax.swing.JPanel();
        sbTools = new gr.wavesoft.webng.ui.SmoothButton();
        jViewPanel = new javax.swing.JScrollPane();

        jHeadBarPanel.setBackground(new java.awt.Color(204, 204, 204));
        jHeadBarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jHeadBarPanel.setOpaque(false);

        jLocationBar.setText("webng:home");
        jLocationBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLocationBarActionPerformed(evt);
            }
        });
        jLocationBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLocationBarFocusGained(evt);
            }
        });
        jLocationBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLocationBarKeyPressed(evt);
            }
        });

        jHeadButtons.setOpaque(false);
        jHeadButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        sbHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gr/wavesoft/webng/resources/refresh.png"))); // NOI18N
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

        sbTools.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gr/wavesoft/webng/resources/tools.png"))); // NOI18N

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
                .add(jLocationBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jHeadBarPanelLayout.setVerticalGroup(
            jHeadBarPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jHeadButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
            .add(jToolButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
            .add(jLocationBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
        );

        jViewPanel.setBorder(null);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jHeadBarPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jViewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jHeadBarPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jViewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sbHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sbHomeMouseClicked

        refresh();
        
        //navigateTo(homeView);
        
        //navigateTo(loader.getViewByName("com.wavesoft.templates.basic.BlogHome"));
        /*
        WebNGSystem.downloadManager.download("http://localhost/", new DownloadListener() {

            @Override
            public void downloadCompleted(DownloadJob job) {
                navigateTo(new WebViewError("No error", job.buffer.replace("<", "{").replace(">", "}")));
            }

            @Override
            public void downloadFailed(DownloadJob job) {
            }

        });
         */
        
    }//GEN-LAST:event_sbHomeMouseClicked

    private void jLocationBarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationBarFocusGained
        jLocationBar.selectAll();
    }//GEN-LAST:event_jLocationBarFocusGained

    private void jLocationBarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLocationBarKeyPressed

        // ENTER PRESSED
        if ((evt.getKeyCode() == 13) || (evt.getKeyCode() == 10)) {
            navigateTo(jLocationBar.getText());
        }
        
    }//GEN-LAST:event_jLocationBarKeyPressed

    private void jLocationBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLocationBarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLocationBarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jHeadBarPanel;
    private javax.swing.JPanel jHeadButtons;
    private javax.swing.JTextField jLocationBar;
    private javax.swing.JPanel jToolButtons;
    private javax.swing.JScrollPane jViewPanel;
    private gr.wavesoft.webng.ui.SmoothButton sbHome;
    private gr.wavesoft.webng.ui.SmoothButton sbTools;
    // End of variables declaration//GEN-END:variables

    @Override
    public void navigateTo(String url) {
        
        // Check what to render
        if (url.toLowerCase().startsWith("webng:")) {
            // WEBNG: System views

            url = url.toLowerCase();
            jLocationBar.setText(url);
            jLocationBar.selectAll();
            navigateTo(SystemViews.getSystemView(url));

        } else {
            // All the rest: URLS

            // Simplify some URL USAGES
            if (!url.matches("(?im)^\\w+:.+")) {
                url = "http://" + url;
            }
            if (url.endsWith("/")) {
                url += "index.yml";
            }

            // Update possible changes in the URL
            jLocationBar.setText(url);
            jLocationBar.selectAll();

            // Navigate
            navigateTo(loadingView);
            pageURL = url;
            jLocationBar.setText(url);
            presenterThread = new Thread(new PresenterThread(url, this));
            presenterThread.start();

        } 
            
    }

    @Override
    public void navigateTo(WebViewNG view) {
        updateHeadButtons(null);
        setView(view);
        revalidate();
    }

    @Override
    public void setTitle(String title) {
        if (linkedTab == null) return;
        linkedTab.title = title;
        getParent().getParent().repaint();
    }

    @Override
    public void setIcon(Image icon) {
        if (linkedTab == null) return;
        linkedTab.img = icon;
        getParent().getParent().repaint();
    }

    @Override
    public void setHeadButtons(HeadButton[] buttons) {
        updateHeadButtons(buttons);
    }

    @Override
    public void addWindowEventListener(WebViewEventListener l) {
    }

    public void setTab(Tab tab) {
        linkedTab = tab;
    }

    @Override
    public void statusChanged(String status) {
    }

    @Override
    public void progressChanged(int value, int max) {
    }

    @Override
    public void viewChanged(WebViewNG view) {
        navigateTo(view);
    }

    @Override
    public void dataChanged(WLData data) {
        // Notify listening entities that data are ready
        WebViewDataListener[] listeners = webView.getListeners(WebViewDataListener.class);
        for (WebViewDataListener l: listeners) {
            l.dataReady(data);
        }
    }

    @Override
    public void refresh() {
        if (pageURL != null) {
            presenterThread = new Thread(new PresenterThread(pageURL, this));
            presenterThread.start();
        }
    }

    @Override
    public void publicKeyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_R) {
            ke.consume();
            refresh();
        }
    }

    @Override
    public void publicKeyReleased(KeyEvent ke) {
    }

    @Override
    public void openRStream(String url, RStreamCallback callback) {
        try {
            streams.openRStream(new HTTPRequest(url), callback);
        } catch (MalformedURLException ex) {
            systemLogger.except(ex);
        }
    }

    @Override
    public void openWStream(String url, WStreamCallback callback) {
        try {
            streams.openWStream(new HTTPRequest(url), callback);
        } catch (MalformedURLException ex) {
            systemLogger.except(ex);
        }
    }

    @Override
    public void openRWStream(String url, RWStreamCallback callback) {
        try {
            streams.openRWStream(new HTTPRequest(url), callback);
        } catch (MalformedURLException ex) {
            systemLogger.except(ex);
        }
    }

    
}
