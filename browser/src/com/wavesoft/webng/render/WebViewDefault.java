/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WebViewDefault.java
 *
 * Created on Jul 30, 2012, 10:24:53 PM
 */
package com.wavesoft.webng.render;

import com.wavesoft.webng.api.WebViewNG;

/**
 *
 * @author icharala
 */
public class WebViewDefault extends WebViewNG {

    /** Creates new form WebViewDefault */
    public WebViewDefault() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        image1 = new com.wavesoft.webng.components.Image();

        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(false);

        image1.setImage(new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/image-question.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout image1Layout = new org.jdesktop.layout.GroupLayout(image1);
        image1.setLayout(image1Layout);
        image1Layout.setHorizontalGroup(
            image1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 204, Short.MAX_VALUE)
        );
        image1Layout.setVerticalGroup(
            image1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 270, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(image1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(image1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.wavesoft.webng.components.Image image1;
    // End of variables declaration//GEN-END:variables
}