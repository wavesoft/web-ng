/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io;

import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.io.repos.Aether;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarTracker {
    
    public static WebViewNG trackView(String fqdn, AsyncEventListener callback, ProgressEventListener pevl) {
        
        // Extract group name and artifact for maven-based 
        // class location
        String[] parts = fqdn.split("\\.");
        String group = parts[0]+"."+parts[1];
        String artifact = parts[2];
        
        // Use aether to load the JAR and it's dependencies
        try {
            Aether.resolveDependency(group, artifact, "");
        } catch (IOException ex) {
            Logger.getLogger(JarTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
