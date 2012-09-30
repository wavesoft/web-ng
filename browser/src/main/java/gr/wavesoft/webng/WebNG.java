/*
 * WebNG.java
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
package gr.wavesoft.webng;

import gr.wavesoft.webng.io.WebNGSystem;
import gr.wavesoft.webng.io.http.HTTPRequestListener;
import gr.wavesoft.webng.io.http.HTTPResponse;
import gr.wavesoft.webng.io.http.HTTPStreams;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author icharala
 */
public class WebNG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Set system's look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Initialize WebNG Security Manager
        //System.setSecurityManager(new WebNGSecurityManager());
        
        // Initialize WebIO
        WebNGSystem.Initialize();
        
//        HTTPStreams.download("http://pgl.yoyo.org/http/browser-headers.php", new HTTPRequestListener() {
        HTTPStreams.download("http://web.scott.k12.va.us/martha2/dmbtest.gif", new HTTPRequestListener() {

            public void httpProgress(Integer value, Integer max) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void httpCompleted(HTTPResponse response) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void httpFailed(Exception e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        });
        try {
            Thread.sleep(10000);
            
            /*
            WebNGKeyStore ks = new WebNGKeyStore(new File("test.store"));
            try {
                ks.installRootCertificate(new File("/Users/icharala/Documents/harca.cer"));
            } catch (KeyStoreException ex) {
                Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
            }
             */
            
            /*
            WebStreamContext ctx = new WebStreamContext();
            try {
                ctx.openRStream(new HTTPRequest("http://www.google.gr/"), new RStreamCallback() {

                    @Override
                    public void streamFailed(Exception e) {
                        SystemConsole.error("UNABLE TO FETCH STREAM");
                    }

                    @Override
                    public void streamReady(InputStream is, ResponseInfo info) {
                        try {
                            BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
                            StringBuilder strBuffer = new StringBuilder();

                            // Download
                            char[] charBuf = new char[1024];
                            int len = 0;
                            while ((len = bufReader.read(charBuf))>0) {
                                strBuffer.append(charBuf, 0, len);
                            }
                            SystemConsole.error("GOT RESPONSE: "+strBuffer.toString());
                        } catch (IOException ex) {
                            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
            }
           */
                
            // Create and display the form
            /*
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    new Browser().setVisible(true);
                }

            });
             */
        } catch (InterruptedException ex) {
            Logger.getLogger(WebNG.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
