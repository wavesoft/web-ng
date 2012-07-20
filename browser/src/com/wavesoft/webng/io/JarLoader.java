/*
 * JarLoader.java
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
package com.wavesoft.webng.io;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.api.Website;
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
    
    public WebViewNG getViewFromJar(String jarFile, String className) 
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        // It automatically de-duplicates classes
        jarLoader.addFile(jarFile);

        // Get the view by name
        Class c = jarLoader.loadClass(className);
        if (!WebViewNG.class.isAssignableFrom(c)) 
            throw new IllegalAccessException("The specified class is not a WebViewNG instance!");

        // Get instance
        WebViewNG instance = (WebViewNG)c.newInstance();

        // Return view
        return instance;
    }
    
    public Website getWebsiteFromJar(String jarFile, String className) 
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        // It automatically de-duplicates classes
        jarLoader.addFile(jarFile);

        // Get the view by name
        Class c = jarLoader.loadClass(className);
        if (!Website.class.isAssignableFrom(c)) 
            throw new IllegalAccessException("The specified class is not a Website instance!");

        // Get instance
        Website instance = (Website)c.newInstance();

        // Return view
        return instance;
    }
    
}
