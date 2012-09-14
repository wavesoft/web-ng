/*
 * WebViewError.java
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
 * Created on Jul 19, 2012, 5:23:25 PM
 */
package com.wavesoft.webng.render;

import com.wavesoft.webng.api.BrowserWindow;
import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.ui.SystemIcons;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * @author icharala
 */
public class WebViewError extends WebViewNG {

    /** Creates new form WebViewError */
    public WebViewError(String title, String body) {
        initComponents();
        jBodyLabel.setText(body);
        jTitleLabel.setText(title);
    }
    
    public WebViewError(String title, Exception ex) {
        initComponents();
        jTitleLabel.setText(title);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(os));
        jBodyLabel.setText("<html><p>An "+ex.getClass().getName()+" exception occured:</p><pre>"+os.toString()+"</pre>");
        
    }

    @Override
    public void webngSetBrowserWindow(BrowserWindow window) {
        super.webngSetBrowserWindow(window);
        if (window == null) return;
        window.setTitle("Error");
        window.setIcon(SystemIcons.tabIconError);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imgBack = new com.wavesoft.webng.components.Image();
        imgWarning = new com.wavesoft.webng.components.Image();
        jTitleLabel = new javax.swing.JLabel();
        jBodyLabel = new javax.swing.JLabel();

        imgBack.setImage(new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/cloud.jpeg"))); // NOI18N
        imgBack.setRepeat(com.wavesoft.webng.components.Image.RepeatConstants.Stretch);

        imgWarning.setImage(new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/image-warning.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout imgWarningLayout = new org.jdesktop.layout.GroupLayout(imgWarning);
        imgWarning.setLayout(imgWarningLayout);
        imgWarningLayout.setHorizontalGroup(
            imgWarningLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 256, Short.MAX_VALUE)
        );
        imgWarningLayout.setVerticalGroup(
            imgWarningLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 280, Short.MAX_VALUE)
        );

        jTitleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 24));
        jTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitleLabel.setText("Internal error (500)");

        jBodyLabel.setForeground(new java.awt.Color(51, 51, 51));
        jBodyLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jBodyLabel.setText("<html>\n<p>This website has failed to load</p>\n<ol>\n<li>There might be an internal failure</li>\n<li>The connection to the internet is down</li>\n</ol>\n<p>Try to refresh later</p>\n</html>");
        jBodyLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout imgBackLayout = new org.jdesktop.layout.GroupLayout(imgBack);
        imgBack.setLayout(imgBackLayout);
        imgBackLayout.setHorizontalGroup(
            imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBackLayout.createSequentialGroup()
                .add(imgWarning, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jBodyLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .add(jTitleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
        );
        imgBackLayout.setVerticalGroup(
            imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBackLayout.createSequentialGroup()
                .add(imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(imgBackLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jTitleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jBodyLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                    .add(imgWarning, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBack, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBack, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.wavesoft.webng.components.Image imgBack;
    private com.wavesoft.webng.components.Image imgWarning;
    private javax.swing.JLabel jBodyLabel;
    private javax.swing.JLabel jTitleLabel;
    // End of variables declaration//GEN-END:variables

}
