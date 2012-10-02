/*
 * WebNGKeyStore.java
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
 */
package gr.wavesoft.webng.security;

import gr.wavesoft.webng.io.SystemConsole;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author icharala
 */
public class WebNGKeyStore {

    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(WebNGKeyStore.class, "Security");
    //private static final String password = "AEkm3l1i3!(FP#a031i04_!#MDa3j9apd";
    private static final String password = "changeit";
    private static KeyStore ks;
    private static File store;
    
    public static void Initialize(String store) {
        WebNGKeyStore.store = new File(store);
        load();
    }
    
    public static KeyStore getKeyStore() {
        return ks;
    }
    
    private static void load() {
        try {
            
            // Create a new keystore
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            
            // If keystore file exists, load it, otherwise create empty
            if (store.exists()) {
                ks.load(new FileInputStream(store), password.toCharArray());
            } else {
                ks.load(null, password.toCharArray());
            }
            
        } catch (FileNotFoundException ex) {
            systemLogger.except(ex);
        } catch (IOException ex) {
            systemLogger.except(ex);
        } catch (NoSuchAlgorithmException ex) {
            systemLogger.except(ex);
        } catch (CertificateException ex) {
            systemLogger.except(ex);
        } catch (KeyStoreException ex) {
            systemLogger.except(ex);
        }
    }
    
    private static void save() {
        try {
            
            // Dump keystore to file
            ks.store(new FileOutputStream(store), password.toCharArray());
            
        } catch (FileNotFoundException ex) {
            systemLogger.except(ex);
        } catch (IOException ex) {
            systemLogger.except(ex);
        } catch (NoSuchAlgorithmException ex) {
            systemLogger.except(ex);
        } catch (CertificateException ex) {
            systemLogger.except(ex);
        } catch (KeyStoreException ex) {
            systemLogger.except(ex);
        }
    }
    
    public static Boolean containsAlias(String alias) {
        try {
            return ks.containsAlias(alias);
        } catch (KeyStoreException ex) {
            systemLogger.except(ex);
            return false;
        }
    }
    
    public static Certificate[] getRootCertificates() throws KeyStoreException {
        ArrayList<Certificate> l = new ArrayList<Certificate>();
        Enumeration<String> keys = ks.aliases();
        while (keys.hasMoreElements()) {
            l.add(ks.getCertificate(keys.nextElement()));
        }
        return l.toArray(new Certificate[0]);
    }
    
    public static void installRootCertificate(Certificate c, String alias) throws KeyStoreException {
        ks.setCertificateEntry(alias, c);
    }
    
    public static void installRootCertificate(File file) throws KeyStoreException {
        try {
            FileInputStream fis = new FileInputStream(file);
            X509CertImpl cert = new X509CertImpl(fis);
            
            X500Name cm = (X500Name) cert.get(X509CertImpl.SUBJECT_DN);
            String cn = cm.getCommonName();
            if (cn == null) cn = cm.getGivenName();
            if (cn == null) cn = cm.getOrganizationalUnit();
            ks.setCertificateEntry(cn, cert);
            save();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WebNGKeyStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebNGKeyStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(WebNGKeyStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void uninstallRootCertificate(String alias) {
        
    }
    
}
