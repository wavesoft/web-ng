/*
 * WebViewHome.java
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
 * Created on Jul 19, 2012, 4:06:51 PM
 */
package gr.wavesoft.webng.render;

import gr.wavesoft.webng.api.BrowserWindow;
import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.webstreams.RStreamCallback;
import gr.wavesoft.webng.webstreams.ResponseInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author icharala
 */
public class WebViewHome extends WebViewNG  {

    /** Creates new form WebViewHome */
    public WebViewHome() {
        initComponents();
    }
    
    private void setIcon(InputStream is) {
        if (is == null) {
            jLabel4.setIcon(null);
            return;
        }
        try {
            jLabel4.setIcon(new javax.swing.ImageIcon(ImageIO.read(is)));
        } catch (IOException ex) {
            Logger.getLogger(WebViewHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void webngSetBrowserWindow(BrowserWindow window) {
        super.webngSetBrowserWindow(window);
        if (window == null) return;
        
        window.openRStream("http://webng.wavesoft.gr/itworks.png", new RStreamCallback() {

            @Override
            public void streamFailed(Exception e) {
                setIcon(null);
            }

            @Override
            public void streamReady(InputStream is, ResponseInfo info) {
                setIcon(is);
            }
            
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imgBack = new gr.wavesoft.webng.components.Image();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        imgBack.setImage(new javax.swing.ImageIcon(getClass().getResource("/gr/wavesoft/webng/resources/cloud.jpeg"))); // NOI18N
        imgBack.setRepeat(gr.wavesoft.webng.components.Image.RepeatConstants.Stretch);

        jLabel1.setText("Hello there! Wanna try some demos? Click the buttons!");

        jButton1.setText("http://webng.wavesoft.gr/index.yml");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("http://webng.wavesoft.gr/dynamic.php");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Wanna see what's happening?");

        jButton3.setText("webng:console");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setText("Testing web streams:");

        org.jdesktop.layout.GroupLayout imgBackLayout = new org.jdesktop.layout.GroupLayout(imgBack);
        imgBack.setLayout(imgBackLayout);
        imgBackLayout.setHorizontalGroup(
            imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBackLayout.createSequentialGroup()
                .addContainerGap()
                .add(imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2)
                    .add(jLabel1)
                    .add(jButton1)
                    .add(jLabel2)
                    .add(jButton3)
                    .add(jLabel3)
                    .add(jLabel4))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        imgBackLayout.setVerticalGroup(
            imgBackLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imgBackLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addContainerGap(124, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        browserWindow.navigateTo("http://webng.wavesoft.gr/index.yml");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        browserWindow.navigateTo("http://webng.wavesoft.gr/dynamic.php");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        browserWindow.navigateTo("webng:console");
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gr.wavesoft.webng.components.Image imgBack;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
