/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Browser.java
 *
 * Created on Jul 18, 2012, 2:13:48 PM
 */
package com.wavesoft.webng;

import com.wavesoft.webng.render.AnimationSync;
import com.wavesoft.webng.render.AnimationSyncHandler;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 *
 * @author icharala
 */
public class Browser extends javax.swing.JFrame implements ComponentListener {

    /** Creates new form Browser */
    public Browser() {
        initComponents();
        addComponentListener(this);
    }

    @Override
    public void setVisible(boolean bln) {
        super.setVisible(bln);
        if (bln) {
            tabs1.repaint();
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

        tabs1 = new com.wavesoft.webng.ui.Tabs();
        pageFrame1 = new com.wavesoft.webng.ui.BrowserFrame();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WebNG Experimental Browser");

        tabs1.setPreferredSize(new java.awt.Dimension(520, 42));

        org.jdesktop.layout.GroupLayout tabs1Layout = new org.jdesktop.layout.GroupLayout(tabs1);
        tabs1.setLayout(tabs1Layout);
        tabs1Layout.setHorizontalGroup(
            tabs1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 630, Short.MAX_VALUE)
        );
        tabs1Layout.setVerticalGroup(
            tabs1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 42, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pageFrame1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
            .add(tabs1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(tabs1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pageFrame1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.wavesoft.webng.ui.BrowserFrame pageFrame1;
    private com.wavesoft.webng.ui.Tabs tabs1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentResized(ComponentEvent ce) {
        /*
        tabs1.setSize(getWidth(), getPreferredSize().height);
        pageFrame1.setLocation(0, tabs1.getHeight());
        pageFrame1.setSize(getWidth(), getHeight() - tabs1.getHeight());
         */
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
    
}
