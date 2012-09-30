/*
 * WebNGIO.java
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
package gr.wavesoft.webng.io;

import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.render.WebViewDefault;
import gr.wavesoft.webng.render.WebViewError;
import java.net.URL;

/**
 *
 * @author icharala
 */
public class WebNGIO {
    
    private static DownloadManager downloadManager;
    public static Database systemDB;
    
    private static void setupDB(String database) {
        
        systemDB = new Database(database);
        
        systemDB.setupSchema("jar_cache",
                  "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "class     VARCHAR(256),"
                + "jar_id    INTEGER,"
                + "expire    INTEGER"
                );
        
        systemDB.setupSchema("jar_files",
                  "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "file      VARCHAR(1024),"
                + "expire    INTEGER"
                );
        
        systemDB.setupSchema("jar_lookup",
                  "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "match     VARCHAR(1024),"
                + "url       VARCHAR(1024),"
                + "expire    INTEGER,"
                + "jar_id    INTEGER"
                );
        
        systemDB.setupSchema("exports_lookup",
                  "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "match     VARCHAR(1024),"
                + "url       VARCHAR(1024),"
                + "expire    INTEGER"
                );
    }    
    
    public static void Initialize(String webNGdir) {
        
        // Initialize library
        setupDB(webNGdir+"/data.sqlite3");
        
        // Create cache and jar loader
        JarCache.setupJarCache(webNGdir+"/jarbox");
        
        //jarCache.importJAR("/Users/icharala/NetBeansProjects/WebNG-Wavesoft/dist/WebNG-Wavesoft.jar", "gr.wavesoft.");
        
        // Setup download manager
        downloadManager = new DownloadManager();

    }
    
    public static void downloadData(URL url, DownloadManager.DownloadListener callback) {
        // Forward to download manager
        downloadManager.download(url, callback);
        downloadManager.checkThreads();
    }
    
    public static void downloadData(URL url, String requestMethod, String requestData, DownloadManager.DownloadListener callback) {
        // Forward to download manager
        downloadManager.download(url,requestMethod,requestData,callback);
        downloadManager.checkThreads();
    }
    
    public static void downloadFile(URL url, String filename, DownloadManager.DownloadListener callback) {
        // Forward to download manager
        downloadManager.downloadFile(url, filename, callback);
        downloadManager.checkThreads();
    }

    
    public static void viewFromClassName(String fqn, AsyncEventListener callback) {
        SystemConsole.debug("Downloading class for FQN ",fqn);
        JarLoader.getInstance(fqn, WebViewNG.class, callback);
    }
    
    public static WebViewNG viewFromClassName(String fqn) {
        WebViewNG view = new WebViewDefault();
        try {
            view = (WebViewNG) JarLoader.getInstance(fqn, WebViewNG.class);
        } catch (ClassNotFoundException ex) {
            view = new WebViewError("Unable to load view "+fqn, ex);
        } catch (InstantiationException ex) {
            view = new WebViewError("Unable to load view "+fqn, ex);
        } catch (IllegalAccessException ex) {
            view = new WebViewError("Unable to load view "+fqn, ex);
        }
        return view;
    }

    
    
}
