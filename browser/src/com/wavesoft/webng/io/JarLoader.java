/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.render.WebViewError;
import java.net.URL;
import java.io.IOException;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarLoader {

    public static class JarFileLoader extends URLClassLoader {
        public JarFileLoader (URL[] urls) {
            super (urls);
        }
        public void addFile (String path) throws MalformedURLException {
            String urlPath = "jar:file://" + path + "!/";
            addURL(new URL (urlPath));
        }
    }

    private JarFileLoader jarLoader = new JarFileLoader (new URL[] {});
    
    public WebViewNG getViewByName(String name) {
        try {
            
            jarLoader.addFile("/Users/icharala/NetBeansProjects/WebNG-Wavesoft/dist/WebNG-Wavesoft.jar");
            
            // Get the view by name
            Class c = jarLoader.loadClass(name);
            if (!WebViewNG.class.isAssignableFrom(c)) {
                return new WebViewError("Unable to load this view", "The view is not a subclass of WebViewNG!");
            }
            
            // Get instance
            WebViewNG view = (WebViewNG)c.newInstance();
            
            // Return view
            return view;
            
        } catch (MalformedURLException ex) {
            return new WebViewError("Unable to load the view file", ex);
        } catch (InstantiationException ex) {
            return new WebViewError("Unable to load this view", ex);
        } catch (IllegalAccessException ex) {
            return new WebViewError("Unable to load this view", ex);
        } catch (ClassNotFoundException ex) {
            return new WebViewError("Unable to load this view", ex);
        }
    }
    
}
