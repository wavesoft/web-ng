/*
 * WebViewCertificates.java
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
 * Created on Sep 17, 2012, 3:36:27 PM
 */
package gr.wavesoft.webng.ui.system;
import gr.wavesoft.webng.api.BrowserWindow;
import gr.wavesoft.webng.api.HeadButton;
import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.security.WebNGKeyStore;
import gr.wavesoft.webng.ui.SystemIcons;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author icharala
 */
public class WebViewCertificates extends WebViewNG {

    DefaultTreeModel caCerts;
     
    /** Creates new form WebViewCertificates */
    public WebViewCertificates() {
        initComponents();
        updateCertList();
        
    }
    
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
    
    private void updateCertList() {
        try {
            
            // Build map
            HashMap<String, ArrayList<String>> certs = new HashMap<String, ArrayList<String>>();
            for (Certificate c: WebNGKeyStore.getRootCertificates()) {
                if (c instanceof X509CertImpl) {
                    X509CertImpl cert = (X509CertImpl) c;
                    
                    X500Name issuer = (X500Name) cert.get(X509CertImpl.ISSUER_DN);
                    String issName = issuer.getOrganization();
                    if (issName == null) issName = issuer.getOrganizationalUnit();
                    if (issName == null) issName = issuer.getCommonName();
                    
                    if (!certs.containsKey(issName)) certs.put(issName, new ArrayList<String>());
                    ArrayList<String> issuerCerts = certs.get(issName);
                    
                    X500Name subj = (X500Name) cert.get(X509CertImpl.SUBJECT_DN);
                    String subjName = subj.getCommonName();
                    if (subjName == null) subjName = subj.getOrganizationalUnit();
                    if (subjName == null) subjName = subj.getOrganization();
                    issuerCerts.add(subjName);
                }
            }
            
            // Build tree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            for (String k: certs.keySet()) {
                DefaultMutableTreeNode group = new DefaultMutableTreeNode(k);
                for (String c: certs.get(k)) {
                    DefaultMutableTreeNode e = new DefaultMutableTreeNode(c);
                    group.add(e);
                }
                root.add(group);
            }
            
            DefaultTreeModel dtm = new DefaultTreeModel(root);
            jTree1.setModel(dtm);
            jTree1.setRootVisible(false);

            expandAll(jTree1, new TreePath(root), true);
            
        } catch (IOException ex) {
            Logger.getLogger(WebViewCertificates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateParsingException ex) {
            Logger.getLogger(WebViewCertificates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(WebViewCertificates.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void webngSetBrowserWindow(BrowserWindow window) {
        super.webngSetBrowserWindow(window);
        if (window == null) return;
        
        window.setIcon(SystemIcons.tabIconRosette);
        window.setTitle("Certificates");

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        roundedButton3 = new gr.wavesoft.webng.components.RoundedButton();
        roundedButton4 = new gr.wavesoft.webng.components.RoundedButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        roundedButton1 = new gr.wavesoft.webng.components.RoundedButton();
        roundedButton2 = new gr.wavesoft.webng.components.RoundedButton();

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jScrollPane2.setViewportView(jTree2);

        roundedButton3.setText("View selected");
        roundedButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundedButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout roundedButton3Layout = new org.jdesktop.layout.GroupLayout(roundedButton3);
        roundedButton3.setLayout(roundedButton3Layout);
        roundedButton3Layout.setHorizontalGroup(
            roundedButton3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 132, Short.MAX_VALUE)
        );
        roundedButton3Layout.setVerticalGroup(
            roundedButton3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 25, Short.MAX_VALUE)
        );

        roundedButton4.setText("Import");
        roundedButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundedButton4ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout roundedButton4Layout = new org.jdesktop.layout.GroupLayout(roundedButton4);
        roundedButton4.setLayout(roundedButton4Layout);
        roundedButton4Layout.setHorizontalGroup(
            roundedButton4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 106, Short.MAX_VALUE)
        );
        roundedButton4Layout.setVerticalGroup(
            roundedButton4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 25, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(roundedButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(roundedButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(roundedButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(roundedButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Users", jPanel4);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jScrollPane1.setViewportView(jTree1);

        roundedButton1.setText("View selected");
        roundedButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundedButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout roundedButton1Layout = new org.jdesktop.layout.GroupLayout(roundedButton1);
        roundedButton1.setLayout(roundedButton1Layout);
        roundedButton1Layout.setHorizontalGroup(
            roundedButton1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 142, Short.MAX_VALUE)
        );
        roundedButton1Layout.setVerticalGroup(
            roundedButton1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 33, Short.MAX_VALUE)
        );

        roundedButton2.setText("Import");
        roundedButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundedButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout roundedButton2Layout = new org.jdesktop.layout.GroupLayout(roundedButton2);
        roundedButton2.setLayout(roundedButton2Layout);
        roundedButton2Layout.setHorizontalGroup(
            roundedButton2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 106, Short.MAX_VALUE)
        );
        roundedButton2Layout.setVerticalGroup(
            roundedButton2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 33, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(65, Short.MAX_VALUE)
                .add(roundedButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(roundedButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(roundedButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(roundedButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Authorities", jPanel3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void roundedButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundedButton1ActionPerformed

    }//GEN-LAST:event_roundedButton1ActionPerformed

    private void roundedButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundedButton2ActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PEM Certificate", "pem"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("DER Certificate", "der"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("CRT Certificate", "crt"));
        fc.showOpenDialog(getRootPane());
        File f = fc.getSelectedFile();
        if (f != null) {
            try {
                WebNGKeyStore.installRootCertificate(f);
            } catch (KeyStoreException ex) {
                JOptionPane.showMessageDialog(null, "Import error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
        
        updateCertList();
        
    }//GEN-LAST:event_roundedButton2ActionPerformed

    private void roundedButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundedButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roundedButton3ActionPerformed

    private void roundedButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundedButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roundedButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    private gr.wavesoft.webng.components.RoundedButton roundedButton1;
    private gr.wavesoft.webng.components.RoundedButton roundedButton2;
    private gr.wavesoft.webng.components.RoundedButton roundedButton3;
    private gr.wavesoft.webng.components.RoundedButton roundedButton4;
    // End of variables declaration//GEN-END:variables
}
