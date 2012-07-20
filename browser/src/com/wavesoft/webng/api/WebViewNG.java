/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WebViewNG.java
 *
 * Created on Jul 19, 2012, 9:42:52 AM
 */
package com.wavesoft.webng.api;
import java.awt.Component;
import java.util.ArrayList;

/**
 *
 * @author icharala
 */
public abstract class WebViewNG extends javax.swing.JPanel {
    private BrowserWindow browserWindow = null;
    
    /** Creates new form WebViewNG */
    public WebViewNG() {
        initComponents();
    }

    @Override
    public Component add(Component cmpnt) {
        
        // If we are adding a WebViewNG, update
        // some special information
        if (cmpnt instanceof WebViewNG) {
            ((WebViewNG) cmpnt).webngSetBrowserWindow(browserWindow);
        }
        
        // Add the component as usual
        return super.add(cmpnt);
    }
    
    /**
     * Return the buttons the website wants to put on the location bar
     * @return Returns null or a list of HeadButton instances
     */
    public HeadButton[] webngGetHeadButtons() {
        return null;
    }
    
    public void webngSetBrowserWindow(BrowserWindow window) {
        browserWindow = window;
    }
    
    public BrowserWindow getWindow() {
        return browserWindow;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 427, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 202, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
