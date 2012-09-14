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

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarLoader {
    
    private static SystemConsole.Logger securityLogger = new SystemConsole.Logger(JarLoader.class, "Security");
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(JarLoader.class, "JarLoader");

    public static class JarFileLoader extends URLClassLoader {
        public JarFileLoader (URL[] urls) {
            super (urls);
        }
        public void addFile (String path) throws MalformedURLException {
            String urlPath = "jar:file://";
            if (WebNGSystem.isWindows) {
                urlPath += "/" + path.replace('\\', '/') + "!/";
            } else {
                urlPath += path + "!/";
            }
            addURL(new URL (urlPath));
        }
        
    }

    private static JarFileLoader jarLoader = new JarFileLoader (new URL[] {});
    
    /*
    public static WebViewNG getViewFromJar(String jarFile, String className) 
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
    
    public static Website getWebsiteFromJar(String jarFile, String className) 
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
    */
    
    public static Boolean invalidateJar(String jarFile) {
        return false;
    }
    
    public static Object getInstanceFromJar(String jarFile, String className, Class expectedType)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
                
        // It automatically de-duplicates classes
        jarLoader.addFile(jarFile);

        // Get the view by name
        Class c = jarLoader.loadClass(className);
        if (!expectedType.isAssignableFrom(c)) 
            throw new IllegalAccessException("The specified class is not a Website instance!");

        // Return instance
        return c.newInstance();
        
    }
    
    public static void getInstance(final String className, final Class expectedType, final AsyncEventListener listener) {

        systemLogger.debug("Fetching JAR for FQN ",className);
        JarCache.getJarFor(className, new AsyncEventListener() {

            @Override
            public void completed(Object result) {
                try {
                    String jarFile = (String) result;
                    systemLogger.debug("JAR Found at ",jarFile);

                    if (jarFile == null) 
                        throw new ClassNotFoundException("Unable to discover a jar file that contains this class!");
                    
                    // It automatically de-duplicates classes
                    systemLogger.debug("Importing file ",jarFile);
                    jarLoader.addFile(jarFile);

                    // Get the view by name
                    systemLogger.debug("Loading class ",className, " from ",jarFile);
                    Class c = jarLoader.loadClass(className);
                    if (!expectedType.isAssignableFrom(c)) 
                        throw new IllegalAccessException("The specified class is not an expected instance!");

                    // Callback the final instance
                    listener.completed(c.newInstance());
                    
                } catch (MalformedURLException ex) {
                    systemLogger.warn("Error processing JAR ",ex);
                    listener.failed(ex, null);
                } catch (InstantiationException ex) {
                    systemLogger.warn("Error processing JAR ",ex);
                    listener.failed(ex, null);
                } catch (IllegalAccessException ex) {
                    systemLogger.warn("Error processing JAR ",ex);
                    listener.failed(ex, null);
                } catch (ClassNotFoundException ex) {
                    systemLogger.warn("Error processing JAR ",ex);
                    listener.failed(ex, null);
                }

            }

            @Override
            public void failed(Exception e, Object result) {
                systemLogger.warn("Unable to download JAR ",e);
                listener.failed(e, result);
            }
        });

       
    }
    
    public static Object getInstance(String className, Class expectedType) 
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        // Lookup Jar file
        String jarFile = JarCache.getJarFor(className);
        if (jarFile == null) throw new ClassNotFoundException("Unable to discover a jar file that contains this class!");
        try {
            // It automatically de-duplicates classes
            systemLogger.debug("Loading class ",className, " from ",jarFile);
            jarLoader.addFile(jarFile);
        } catch (MalformedURLException ex) {
            systemLogger.exception(ex);
            return null;
        }

        // Get the view by name
        Class c = jarLoader.loadClass(className);
        if (!expectedType.isAssignableFrom(c)) 
            throw new IllegalAccessException("The specified class is not an expected instance!");

        // Return instance
        return c.newInstance();
        
    }
    
}
