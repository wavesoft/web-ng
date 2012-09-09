/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

import com.wavesoft.webng.io.DownloadManager.DownloadJob;
import com.wavesoft.webng.io.DownloadManager.DownloadListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarDiscovery {
    
    private static SystemConsole.Logger securityLogger = new SystemConsole.Logger(JarDiscovery.class, "Security");
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(JarDiscovery.class, "JarDiscovery");
    
    // The exports URL is expected at: http(s)://<WEBNG_ROOT>.<DOMAIN>.<TLD>/<WEBNG_ENTRY_POINT>
    private static final String WEBNG_EXPORTS = "exports";
    private static final String WEBNG_ROOT = "webng";
    
    private static HashMap<String, URL> readExportsFromJob(DownloadJob job) {
        HashMap<String,URL> list = new HashMap<String, URL>();

        // Process exports file
        StringReader sr = new StringReader(job.buffer);
        BufferedReader br = new BufferedReader(sr);
        String line;
        int lineNO = 0;

        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                lineNO++;
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    systemLogger.error("Unexpected definition in line #", lineNO, " in ", job.url);
                    continue;
                }

                // Properly format the FQN
                String fqn = parts[0].trim();
                fqn = fqn.replaceAll("[\\?\\%\\/\\\\~!@#\\$\\^&(){}\\[\\]]", "");
                fqn = fqn.replaceAll("\\.\\.", "");
                fqn = fqn.replaceFirst("\\*$", "%");
                fqn = fqn.replaceAll("\\*", "");

                // Parse the URL
                String url  = parts[1];
                if (url.matches("^\\w+://")) {
                    securityLogger.warning("Refused to process direct export mapping to ", url);
                } else {
                    if (url.endsWith(".jar")) {
                        URL c_url = new URL(job.url, url);
                        list.put(fqn, c_url);

                    } else if (url.endsWith("/")) {
                        URL c_url = new URL(job.url, url + WEBNG_EXPORTS);
                        list.put(fqn, c_url);

                    } else {
                        securityLogger.warning("Refused to process directive that does not map to a .jar file or nested map.");
                    }
                }

            }
        } catch (IOException ex) {
            systemLogger.exception(ex);
            list = null;
        }
        
        // Return the generated list
        return list;
    }
    
    /**
     * Blocking wrapper for WebNGIO.download*
     */
    private static class Downloader implements DownloadListener {
        final Object sync = new Object();
        HashMap<String, URL> list;
        URL baseURL;
        
        @Override
        public void downloadCompleted(DownloadJob job) {
            list =  readExportsFromJob(job);
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        @Override
        public void downloadFailed(DownloadJob job) {
            list = null;
            systemLogger.error("Unable to download ", job.url);
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        public HashMap<String, URL> downloadList(URL url) {
            
            // Synchronize
            synchronized(sync) {
                // Schedule download
                
                baseURL = url;
                WebNGIO.downloadData(url, this);
            
                try {
                    sync.wait();
                } catch (InterruptedException ex) {
                    return null;
                }
            }
            
            // Return list
            return list;
        }
    }

    /**
     * Lookup the URL of a previously defined JAR
     * @param fqn
     * @return 
     */
    private static URL lookupJAR(String fqn) {
        
        // Remove expired entries
        WebNGIO.dbUpdate("DELETE FROM jar_lookup WHERE expire < ?", System.currentTimeMillis());
        
        // Fetch jar
        PreparedStatement s = WebNGIO.dbPrepareStatement("SELECT url FROM jar_lookup WHERE ? LIKE match ORDER BY LENGTH(match) DESC LIMIT 0,1");
        try {
            
            // Query
            s.setString(1, fqn);
            ResultSet r = s.executeQuery();
            
            // Get first item
            if (!r.next()) return null;
            try {
                return new URL(r.getString(1));
            } catch (MalformedURLException ex) {
                systemLogger.exception(ex);
                return null;
            }
            
        } catch (SQLException ex) {
            systemLogger.exception(ex);
        }
        return null;
        
    }
    
    /**
     * Lookup the exports file URL for the specified Class
     * @param fqn
     * @return 
     */
    private static URL lookupExports(String fqn) {
        
        // Remove expired entries
        WebNGIO.dbUpdate("DELETE FROM jar_lookup WHERE expire < ?", System.currentTimeMillis());
        
        // Fetch matching export statemet
        PreparedStatement s = WebNGIO.dbPrepareStatement("SELECT url FROM exports_lookup WHERE ? LIKE match ORDER BY LENGTH(match) DESC LIMIT 0,1");
        try {
            
            // Query
            s.setString(1, fqn);
            ResultSet r = s.executeQuery();
            
            // Get first item
            if (!r.next()) return null;
            try {
                return new URL(r.getString(1));
            } catch (MalformedURLException ex) {
                systemLogger.exception(ex);
                return null;
            }
            
        } catch (SQLException ex) {
            systemLogger.exception(ex);
        }

        return null;
    }
    
    /**
     * Return the URL that points to the exports information for the specified
     * class FQN.
     * 
     * @param fqn
     * @return 
     */
    private static URL directLookupExport(String fqn) throws IOException {
        
        // Direct URL lookup
        String[] parts = fqn.split("\\.");
        if (parts.length < 3) {
            systemLogger.error("The specified class name: '",fqn,"' is not in an expected format!");
            throw new IOException("The specified class name: '"+fqn+"' is not in an expected format!");
        }
        
        // Build default entry point: http://webng.<domain>.<tld>/exports
        String reqURL = "http://" + WEBNG_ROOT + "." + parts[1] + "." + parts[0] + "/" + WEBNG_EXPORTS; 
        
        // Check if the pecified 
        /*
        try {
            InetAddress.getByName(parts[2] + "." + parts[1] + "." + parts[0]);
            reqURL = "http://" + parts[2] + "." + parts[1] + "." + parts[0] + "/" + WEBNG_EXPORTS; 
        } catch (UnknownHostException ex) {
            // Nope....
        }
         */
        
        // Return the detected URL
        try {
            URL req = new URL(reqURL);
            return req;
        } catch (MalformedURLException ex) {
            systemLogger.exception(ex);
        }
        
        return null;
    }
    
    // The downloader entry point to WebNGIO.download
    private static Downloader downloader = new Downloader();
    
    /**
     * Download the specified exports file and update the database
     * @param exportsFile
     * @return 
     */
    private static Boolean updateExports(URL exportsFile) {
        
        // Try to download
        HashMap<String, URL> lookupInfo = downloader.downloadList(exportsFile);
        if (lookupInfo == null) return false;

        // Prepare DB transactions
        PreparedStatement stmtJAR = WebNGIO.dbPrepareStatement("INSERT INTO jar_lookup (match,url,expire) VALUES (?,?,?)");
        PreparedStatement stmtEXPORTS = WebNGIO.dbPrepareStatement("INSERT INTO exports_lookup (match,url,expire) VALUES (?,?,?)");
        //WebNGIO.dbTransactionBegin();
        
        // Import data in the DB
        for (String k: lookupInfo.keySet()) {
            URL u = lookupInfo.get(k);
            if (u.getPath().endsWith(".jar")) {
                try {
                    stmtJAR.setString(1, k);
                    stmtJAR.setString(2, u.toString());
                    stmtJAR.setLong(3, System.currentTimeMillis() + 3600000l);
                    stmtJAR.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(JarDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else {
                try {
                    stmtEXPORTS.setString(1, k);
                    stmtEXPORTS.setString(2, u.toString());
                    stmtEXPORTS.setLong(3, System.currentTimeMillis() + 3600000l);
                    stmtEXPORTS.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(JarDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }

        // Commit transaction
        //WebNGIO.dbTransactionCommit();
        
        // Everything went ok!
        return true;
    }
    
    private static void updateExports(URL exportsFile, final AsyncEventListener listener) {
        
        // Try to download
        systemLogger.debug("Will update exports from ",exportsFile);
        WebNGIO.downloadData(exportsFile, new DownloadListener() {

            @Override
            public void downloadCompleted(DownloadJob job) {
                
                // Read exports from the job
                HashMap<String, URL> lookupInfo = readExportsFromJob(job);
                
                // Prepare DB transactions
                PreparedStatement stmtJAR = WebNGIO.dbPrepareStatement("INSERT INTO jar_lookup (match,url,expire) VALUES (?,?,?)");
                PreparedStatement stmtEXPORTS = WebNGIO.dbPrepareStatement("INSERT INTO exports_lookup (match,url,expire) VALUES (?,?,?)");

                // Import data in the DB
                for (String k: lookupInfo.keySet()) {
                    URL u = lookupInfo.get(k);
                    systemLogger.debug(" - Export for ",k," -> ", u);
                    if (u.getPath().endsWith(".jar")) {
                        try {
                            stmtJAR.setString(1, k);
                            stmtJAR.setString(2, u.toString());
                            stmtJAR.setLong(3, System.currentTimeMillis() + 3600000l);
                            stmtJAR.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(JarDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                            // But don't fail!
                        }

                    } else {
                        try {
                            stmtEXPORTS.setString(1, k);
                            stmtEXPORTS.setString(2, u.toString());
                            stmtEXPORTS.setLong(3, System.currentTimeMillis() + 3600000l);
                            stmtEXPORTS.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(JarDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                            // But don't fail!
                        }

                    }

                }
                
                // Completed
                listener.completed(true);
                
            }

            @Override
            public void downloadFailed(DownloadJob job) {
                listener.failed(new IOException("Unable to download exports file "+job.url.toString()), job);
            }
            
        });
        
    }
    
    public static int locateLocalJarID(String className) {
        
        // Remove expired entries
        WebNGIO.dbUpdate("DELETE FROM jar_lookup WHERE expire < ?", System.currentTimeMillis());
        
        // Fetch jar
        PreparedStatement s = WebNGIO.dbPrepareStatement("SELECT jar_id FROM jar_lookup WHERE ? LIKE match ORDER BY LENGTH(match) DESC LIMIT 0,1");
        try {
            
            // Query
            s.setString(1, className);
            ResultSet r = s.executeQuery();
            
            // Get first item
            if (!r.next()) return -1;
            return r.getInt(1);
            
        } catch (SQLException ex) {
            systemLogger.exception(ex);
        }
        return -1;
        
    }
    
    private static void locateJarURLTask(ArrayList<URL> processedURLs, final String className, final AsyncEventListener listener) {
        try {
            URL url = null;
            systemLogger.debug("Looking-up JAR cache for "+className);

            // Firsly, check if the lookup information we already have
            // can provide a URL for this className...
            url = lookupJAR(className);
            if (url != null) {
                systemLogger.debug("Found in the jar cache: ", url);
                listener.completed(url);
                return;
            }

            // If that's not found, if we can fetch it from a known, nested
            // export.
            systemLogger.debug("Looking up expors cache for "+className);
            URL exportURL = lookupExports(className);
            if (exportURL != null) {
                systemLogger.debug("Found Exports URL ", exportURL);

                // If we already tried this, exit
                if (processedURLs.contains(exportURL))
                    throw new IOException("Blocked into an infinite loop while looking up "+className);
                processedURLs.add(exportURL);
                
                // Final accessor to processedURLs (We are not modifying it anyways)
                final ArrayList<URL> finalProcessedURLs = processedURLs;
                
                // We found a nested export... download and update DB
                systemLogger.debug("Importing exports from ", exportURL);
                updateExports(exportURL, new AsyncEventListener() {

                    @Override
                    public void completed(Object result) {
                        // Completed successfully? -> Nested call
                        locateJarURLTask(finalProcessedURLs, className, listener);
                    }

                    @Override
                    public void failed(Exception e, Object result) {
                        listener.failed(e, result);
                    }
                    
                });
            }
                        
            // Not even that is avalable, perform direct request
            systemLogger.debug("Direct lookup for "+className);
            exportURL = directLookupExport(className);
            if (exportURL != null) {
                systemLogger.debug("Found Direct URL ", exportURL);

                // Process URLs only once
                if (processedURLs.contains(exportURL)) 
                    throw new IOException("Blocked into an infinite loop while looking up "+className);
                processedURLs.add(exportURL);
                
                
                // Final accessor to processedURLs (We are not modifying it anyways)
                final ArrayList<URL> finalProcessedURLs = processedURLs;
                
                // We found a nested export... download and update DB
                systemLogger.debug("Importing direct exports from ", exportURL);
                updateExports(exportURL, new AsyncEventListener() {

                    @Override
                    public void completed(Object result) {
                        // Completed successfully? -> Nested call
                        locateJarURLTask(finalProcessedURLs, className, listener);
                    }

                    @Override
                    public void failed(Exception e, Object result) {
                        listener.failed(e, result);
                    }
                    
                });
            }
            
        } catch (IOException e) {
            listener.failed(e, null);
        }
    }
    
    public static void locateJarURL(final String className, final AsyncEventListener listener) {
        ArrayList<URL> processedURLs = new ArrayList<URL>();
        locateJarURLTask(processedURLs, className, listener);
    }
    
    /**
     * Detects where in the web can we find the jar file that contains the
     * specified class name!
     * 
     * @param className
     * @return URL Returns a URL object that points to the appropriate JAR file
     */
    public static URL locateJarURL(String className) {
        URL url = null;
        ArrayList<URL> processedURLs = new ArrayList<URL>();
        
        while (url == null) {
        
            // Firsly, check if the lookup information we already have
            // can provide a URL for this className...
            url = lookupJAR(className);
            if (url != null) return url;

            // If that's not found, if we can fetch it from a known, nested
            // export.
            URL exportURL = lookupExports(className);
            if (exportURL != null) {
                // We found a nested export... download and update DB
                if (!updateExports(exportURL)) 
                    return null; // Unable to update exports DB
                continue; // << Retry jar lookup
            }
            try {
                // Not even that is avalable, perform direct request
                exportURL = directLookupExport(className);
            } catch (IOException ex) {
                // Error
                break;
            }
            if (exportURL != null) {

                // Process URLs only once
                if (processedURLs.contains(exportURL)) break;
                processedURLs.add(exportURL);
                
                // We found the entry point... download and update DB
                if (!updateExports(exportURL)) 
                    return null; // Unable to update exports DB
                continue; // << Retry jar lookup
            }
            
            // Unable to find anything :(
            break;
            
        }
        
        // (Never supposed to reach this point)
        return null;
    }
    
}
