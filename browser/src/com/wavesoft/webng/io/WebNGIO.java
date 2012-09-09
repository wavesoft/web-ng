/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.render.WebViewDefault;
import com.wavesoft.webng.render.WebViewError;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class WebNGIO {
    
    private static DownloadManager downloadManager;

    private static Connection conn;
    
    private static void setupDB(String database) {
        try {
            
            // Try to load the SQLite JDBC
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            conn = DriverManager.getConnection("jdbc:sqlite:"+database);

            // Prepare statement for DB Creations
            Statement stat = conn.createStatement();
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_cache ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "class     VARCHAR(256),"
                    + "jar_id    INTEGER,"
                    + "expire    INTEGER"
                    + ")");
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_files ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "file      VARCHAR(1024),"
                    + "expire    INTEGER"
                    + ")");
            
            /*
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_manifest ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "jar       INTEGER,"
                    + "key       VARCHAR(1024),"
                    + "value     TEXT"
                    + ")");
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_classes ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "jar       INTEGER,"
                    + "name      VARCHAR(1024),"
                    + "class     VARCHAR(1024)"
                    + ")");
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_resources ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "jar       INTEGER,"
                    + "name      VARCHAR(1024),"
                    + "class     VARCHAR(1024)"
                    + ")");
             */
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jar_lookup ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "match     VARCHAR(1024),"
                    + "url       VARCHAR(1024),"
                    + "expire    INTEGER,"
                    + "jar_id    INTEGER"
                    + ")");
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS exports_lookup ("
                    + "id        INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "match     VARCHAR(1024),"
                    + "url       VARCHAR(1024),"
                    + "expire    INTEGER"
                    + ")");

            stat.close();
            
        } catch (SQLException ex) {
            SystemConsole.error(ex);
            
        } catch (ClassNotFoundException ex) {
            SystemConsole.error(ex);
        }
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
    
    public static ResultSet dbQuery(String query) {
        try {
            Statement stm = conn.createStatement();
            return stm.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static int dbUpdate(String query) {
        try {
            Statement stm = conn.createStatement();
            return stm.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    private static void assignArgStatements(PreparedStatement stm, Object[] args) {
        try {
            for (int i=1; i<=args.length; i++) {
                Object o = args[i-1];
                Class c = o.getClass();
                if (Integer.class.isAssignableFrom(c)) {
                    stm.setInt(i, (Integer) o);
                } else if (Long.class.isAssignableFrom(c)) {
                    stm.setLong(i, (Long) o);
                } else if (Float.class.isAssignableFrom(c)) {
                    stm.setFloat(i, (Float) o);
                } else if (Double.class.isAssignableFrom(c)) {
                    stm.setDouble(i, (Double) o);
                } else if (String.class.isAssignableFrom(c)) {
                    stm.setString(i, (String) o);
                } else if (InputStream.class.isAssignableFrom(c)) {
                    stm.setBlob(i, (InputStream) o);
                } else if (Boolean.class.isAssignableFrom(c)) {
                    stm.setBoolean(i, (Boolean) o);
                } else if (Byte.class.isAssignableFrom(c)) {
                    stm.setByte(i, (Byte) o);
                } else if (Short.class.isAssignableFrom(c)) {
                    stm.setShort(i, (Short) o);
                } else if (Time.class.isAssignableFrom(c)) {
                    stm.setTime(i, (Time) o);
                } else if (Timestamp.class.isAssignableFrom(c)) {
                    stm.setTimestamp(i, (Timestamp) o);
                } else if (URL.class.isAssignableFrom(c)) {
                    stm.setURL(i, (URL) o);
                } else {
                    stm.setObject(i, o);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int dbUpdate(String query, Object ... args) {
        try {
            PreparedStatement stm = conn.prepareStatement(query);
            assignArgStatements(stm, args);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public static ResultSet dbQuery(String query, Object ... args) {
        try {
            PreparedStatement stm = conn.prepareStatement(query);
            assignArgStatements(stm, args);
            return stm.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static PreparedStatement dbPrepareStatement(String query) {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException ex) {
            return null;
        }
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
    
    public static void dbTransactionBegin() {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void dbTransactionCommit() {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
