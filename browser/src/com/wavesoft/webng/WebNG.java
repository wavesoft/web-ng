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
package com.wavesoft.webng;

import com.wavesoft.webng.io.WebNGSecurityManager;
import com.wavesoft.webng.io.WebNGSystem;
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
        
        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Browser().setVisible(true);
            }

        });
        
    }
    
}
