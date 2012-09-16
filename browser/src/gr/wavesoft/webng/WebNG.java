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

import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.io.WebNGSystem;
import gr.wavesoft.webng.webstreams.RStreamCallback;
import gr.wavesoft.webng.webstreams.ResponseInfo;
import gr.wavesoft.webng.webstreams.WebStreamContext;
import gr.wavesoft.webng.webstreams.http.HTTPRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
        
        /*
        WebStreamContext ctx = new WebStreamContext();
        try {
            ctx.openRStream(new HTTPRequest("http://localhost/web-ng/big.yml"), new RStreamCallback() {

                @Override
                public void streamFailed() {
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
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Browser().setVisible(true);
            }

        });
        
    }
    
}
