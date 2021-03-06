/*
 * WebViewNG.java
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
 * Created on Jul 19, 2012, 9:42:52 AM
 */
package gr.wavesoft.webng.api;
import java.awt.Component;

/**
 *
 * @author icharala
 */
public abstract class WebViewNG extends javax.swing.JPanel {
    protected BrowserWindow browserWindow = null;
    
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
    
    public void webngSetBrowserWindow(BrowserWindow window) {
        browserWindow = window;
    }
    
    public BrowserWindow getWindow() {
        return browserWindow;
    }
    
    public void addViewEventListener(WebViewEventListener l) {
        listenerList.add(WebViewEventListener.class, l);
    }
    
    public void removeViewEventListener(WebViewEventListener l) {
        listenerList.remove(WebViewEventListener.class, l);
    }
    
    public void addDataListener(WebViewDataListener l) {
        listenerList.add(WebViewDataListener.class, l);
    }
    
    public void removeDataListener(WebViewDataListener l) {
        listenerList.remove(WebViewDataListener.class, l);
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
            .add(0, 63, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 56, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
