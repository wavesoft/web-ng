/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SampleView.java
 *
 * Created on Nov 2, 2012, 11:34:56 PM
 */
package gr.wavesoft.webng.render;

import gr.wavesoft.webng.api.WebViewDataListener;
import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.wblang.WLData;

/**
 *
 * @author icharala
 */
public class SampleView extends WebViewNG implements WebViewDataListener  {

    /** Creates new form SampleView */
    public SampleView() {
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void dataReady(WLData data) {
        
    }

    public void dataInvalidated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
