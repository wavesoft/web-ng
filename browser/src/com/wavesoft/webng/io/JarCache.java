/*
 * JarCache.java
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

import com.wavesoft.webng.io.DownloadManager.DownloadJob;
import com.wavesoft.webng.io.DownloadManager.DownloadListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarCache {
    
    private static SystemConsole.Logger securityLogger = new SystemConsole.Logger(JarDiscovery.class, "Security");
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(JarDiscovery.class, "JarCache");
    
    /**
     * Blocking wrapper for WebNGIO.download*
     */
    private static class Downloader implements DownloadListener {
        final Object sync = new Object();
        Boolean status = false;
        
        @Override
        public void downloadCompleted(DownloadJob job) {
            status = true;
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        @Override
        public void downloadFailed(DownloadJob job) {
            status = false;
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        public Boolean downloadFile(URL url, String path) {
            
            // Synchronize
            synchronized(sync) {
                // Schedule download
                
                status = false;
                WebNGIO.downloadFile(url, path, this);
            
                try {
                    sync.wait();
                } catch (InterruptedException ex) {
                    return false;
                }
            }
            
            // Return list
            return status;
        }
    }
    private static Downloader downloader = new Downloader();
    
    private static String cacheDirectory;
    
    public static void setupJarCache(String directory) {
        cacheDirectory = directory;
    }
    
    /**
     * Download a jar in the cache folder and return the unique filename where
     * it's stored.
     * 
     * @param url The URL to download the JAR from
     * @return The filename where the jar is saved
     */
    private static String downloadJar(URL url) {
        // TODO: Ensure that what we are abount to donwnload is indeed a jar
        String path = UUID.randomUUID().toString() + ".jar";
        if (!downloader.downloadFile(url, cacheDirectory + "/" + path)) return null;
        return path;
    }
    
    /**
     * The same as above, but asynchronous
     * @param url The URL to download
     * @param listener  The listener to call back
     */
    private static void downloadJar(URL url, final AsyncEventListener listener) {
        // TODO: Ensure that what we are abount to donwnload is indeed a jar
        final String path = UUID.randomUUID().toString() + ".jar";
        WebNGIO.downloadFile(url, cacheDirectory + "/" + path, new DownloadListener() {

            @Override
            public void downloadCompleted(DownloadJob job) {
                listener.completed(path);
            }

            @Override
            public void downloadFailed(DownloadJob job) {
                listener.failed(new IOException("Unable to download file "+job.url.toString()), job);
            }
            
        });
    }
    
    /**
     * Import a jar in the cache database
     * @param filename The filename where the jar resides
     * @param requirePrefix The domain-specific prefix to be expected (ex. "com.wavesoft.")
     */
    private static int importJAR(String filename, String requirePrefix) throws IOException {
        int jarID = -1;
        try {
            // Read specified file
            JarFile jarFile = new JarFile(cacheDirectory + "/" + filename);
            
            // Insert JAR in the db
            systemLogger.debug("Importing jar file ", cacheDirectory + "/" + filename);
            WebNGIO.dbUpdate("INSERT INTO jar_files (file,expire) VALUES (?,?)", filename, System.currentTimeMillis() + 3600000l);
            ResultSet res = WebNGIO.dbQuery("SELECT id FROM jar_files ORDER BY id DESC LIMIT 0,1");
            if (!res.next()) {
                systemLogger.error("Error while importing jar", filename, "in the database!");
                return -1;
            }
            jarID = res.getInt(1);
            systemLogger.debug("Created JAR entry ",jarID);
            
            // Prepare statement
            PreparedStatement stmtCLS = WebNGIO.dbPrepareStatement("INSERT INTO jar_cache (class,jar_id,expire) VALUES (?,?,?)");
            stmtCLS.setInt(2, jarID);
            
            // Lookup some useful parameters in the manifest
            Manifest mf = jarFile.getManifest();
            Attributes attrib = mf.getMainAttributes();
            for (Object k: attrib.keySet()) {
                System.out.println("Manifest argument: "+k.toString());
            }
            
            stmtCLS.setLong(3, System.currentTimeMillis() + 3600000l); // 1 hour
            
            // Process entries
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                if (!entry.isDirectory() && entryName.endsWith(".class")) {
                    
                    // Convert filename to className
                    final String className = entryName.substring(0, entryName.length()-6).replaceAll("[\\$/]", ".");
                    if (!className.startsWith(requirePrefix)) {
                        securityLogger.warning("Ignored class: " + className);
                    } else {
                        systemLogger.debug("Imported class "+className+" to jar #"+jarID);
                        stmtCLS.setString(1, className);
                        stmtCLS.executeUpdate();
                    }
                    
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(JarCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return jarID;
    }
    
    /**
     * Return the JAR filename that contains the specified class
     * @param className The name of the class
     * @return The full path to the jar file
     */
    private static String lookupJAR(String className) {
        try {
            // Fetch the JAR file from the database
            ResultSet res = WebNGIO.dbQuery("SELECT jar_files.file "
                    + "FROM jar_cache INNER JOIN jar_files ON jar_cache.jar_id = jar_files.id "
                    + "WHERE jar_cache.class = ?", className);
            if (!res.next()) return null;
            return cacheDirectory + "/" + res.getString(1);
            
        } catch (SQLException ex) {
            systemLogger.exception(ex);
            return null;
        }
    }
    
    public static boolean hasJarFor(String className) {
        return (lookupJAR(className) != null);
    }
    
    public static void getJarFor(final String className, final AsyncEventListener listener) {
        try {
            // Return the file if it was found in the database
            String file = lookupJAR(className);
            if (file != null) {
                SystemConsole.debug("Class ",className," found on jar_cache in file ",file);
                listener.completed(file);
                return;
            }
            
            // Check if the local discoveries validates the specified
            // parameters. This means that the appripriate jar file is already 
            // downloaded, but does not contain the class requested.
            if (JarDiscovery.locateLocalJarID(className) >= 0 )
                throw new ClassNotFoundException("Class "+className+" validates lookup cache, but does not exist in jar!");
            
            // If it's not found, lookup a URL on the internets
            SystemConsole.debug("Looking-up URL for "+className);
            JarDiscovery.locateJarURL(className, new AsyncEventListener() {

                @Override
                public void completed(Object result) {
                    final URL jarURL = (URL) result;
                                
                    // If we found the JAR on the internets, download and import it
                    SystemConsole.debug("Downloading JAR ", jarURL);
                    downloadJar(jarURL, new AsyncEventListener() {

                        @Override
                        public void completed(Object result) {
                            try {
                                String file = (String) result;

                                // Calculate the expected domain prefix
                                String hostname = jarURL.getHost();
                                String domainParts[] = hostname.split("\\.");
                                if (domainParts.length < 2) {
                                        systemLogger.error("Unexpected domain detected on URL ",jarURL," while fetching JAR for ",className);
                                        throw new MalformedURLException("Unexpected domain detected on URL "+jarURL.toString()+" while fetching JAR for "+className);
                                }

                                // Import JAR
                                SystemConsole.debug("Importing JAR ", file);
                                int jarID = importJAR(file, domainParts[domainParts.length-1]+"."+domainParts[domainParts.length-2]+"." );

                                // Update local lookup database
                                WebNGIO.dbUpdate("UPDATE jar_lookup SET jar_id = ? WHERE url = ?", jarID, jarURL.toString());

                                // Lookup again the classes in the JAR (To validate that the
                                // exports file was correct and the class was indeed in the jar)
                                SystemConsole.debug("Looking up again JAR for "+className);
                                file = lookupJAR(className);
                                if (file != null) {
                                    listener.completed(file);
                                } else {
                                    
                                    // Otherwise something went wrong and the specified class
                                    // was not detected in the jar file. Unfortunately, we don't
                                    // have any other clue where to search for it ... :(
                                    throw new ClassNotFoundException("Unable to find class "+className+" in any jar file!");
                                    
                                }

                            } catch (ClassNotFoundException ex) {
                                SystemConsole.warn("Error processing JAR ",ex);
                                listener.failed(ex, null);
                            } catch (MalformedURLException ex) {
                                SystemConsole.warn("Error processing JAR ",ex);
                                listener.failed(ex, null);
                            } catch (IOException ex) {
                                SystemConsole.warn("Error processing JAR ",ex);
                                listener.failed(ex, null);
                            }
                        }

                        @Override
                        public void failed(Exception e, Object result) {
                            DownloadJob job = (DownloadJob) result;
                            SystemConsole.warn("Unable to download ", job.url, ". Error ", job.statusMessage);
                            listener.failed(
                                    new ClassNotFoundException("Unable to download jar file from "+job.url.toString()+".Error: "+job.statusMessage), 
                                    null);
                                }

                    });

                }

                @Override
                public void failed(Exception e, Object result) {
                    SystemConsole.warn("Unable to found entry point for "+className);
                    listener.failed(
                            new ClassNotFoundException("Unable to locate an entry point for class "+className), 
                            null);
                }
            });
            
        } catch (ClassNotFoundException ex) {
            SystemConsole.warn("Error processing JAR ",ex);
            listener.failed(ex, null);
        }
        
    }
    
    public static String getJarFor(String className) {
        
        // Return the file if it was found in the database
        String file = lookupJAR(className);
        if (file != null) return file;
        
        // Check if the local discoveries validates the specified
        // parameters. This means that the appripriate jar file is already 
        // downloaded, but does not contain the class requested.
        if (JarDiscovery.locateLocalJarID(className) >= 0 ) {
            systemLogger.info("Class ",className, " validates lookup cache, but does not exist in jar");
            return null;
        }
        
        // If it's not found, lookup a URL on the internets
        URL jarURL = JarDiscovery.locateJarURL(className);
        if (jarURL == null) return null;
        
        // If we found the JAR on the internets, download and import it
        file = downloadJar(jarURL);
        
        // Calculate the expected domain prefix
        String hostname = jarURL.getHost();
        String domainParts[] = hostname.split("\\.");
        if (domainParts.length < 2) {
            systemLogger.error("Unexpected domain detected on URL ",jarURL," while fetching JAR for ",className);
            return null;
        }
        
        // Import JAR
        int jarID;
        try {
            jarID = importJAR(file, domainParts[domainParts.length-1]+"."+domainParts[domainParts.length-2]+"." );
        } catch (IOException ex) {
            Logger.getLogger(JarCache.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        // Update local lookup database
        WebNGIO.dbUpdate("UPDATE jar_lookup SET jar_id = ? WHERE url = ?", jarID, jarURL.toString());
        
        // Lookup again the classes in the JAR (To validate that the
        // exports file was correct and the class was indeed in the jar)
        file = lookupJAR(className);
        if (file != null) return file;
        
        // Otherwise something went wrong and the specified class
        // was not detected in the jar file. Unfortunately, we don't
        // have any other clue where to search for it ... :(
        return null;
        
    }
    
}
