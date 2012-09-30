/*
 * NGDiskCache.java
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
package gr.wavesoft.webng.io.http.cache;

import gr.wavesoft.webng.io.Database;
import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.io.WebNGSystem;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.impl.client.cache.FileResource;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;

/**
 *
 * @author icharala
 */
public class NGDiskCache implements HttpCacheStorage {
    
    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(NGDiskCache.class, "DiskCache");
    private String baseDir;
    private Database cacheDB;
    private long cacheSize;

    private static int  IO_BUFFER_SIZE = 4*1024;
    private static Long CACHE_MAX_SIZE = 104857600l; // 100Mb
    private static int  CACHE_MAX_ITEMS = 2000; // 2000 max items
    private static Long CACHE_MAX_ITEM_SIZE = 5242880l; // 5Mb  
    
    private PreparedStatement stmInsert;
    private PreparedStatement stmSelect;
    private PreparedStatement stmUpdate;
    
    public NGDiskCache(NGCacheConfig cfg) {
        this.baseDir = cfg.cacheDir;
        this.cacheDB = new Database(baseDir+"/cache.db");
        
        // Setup cache table schema
        this.cacheDB.setupSchema("cache_store", 
                
                // keying entity
                  "key            VARCHAR(32) PRIMARY KEY,"
                
                // Data required by the HttpCacheEntry
                + "updated          INTEGER,"
                + "data             BLOB,"
                
                // Data required by the cache manager
                + "expires          INTEGER,"
                + "size             INTEGER,"
                + "etag             VARCHAR(256),"
                + "state            INTEGER"
                );
        
        // Prepare stements
        stmInsert = this.cacheDB.prepareStatement("INSERT OR REPLACE INTO cache_store "
                + "(key,updated,data,expires,size,etag,state) "
                + "VALUES (?,?,?,?,?,?,?)");
        stmSelect = this.cacheDB.prepareStatement("SELECT * FROM cache_store "
                + "WHERE key = ?");
        stmUpdate = this.cacheDB.prepareStatement("UPDATE cache_store "
                + "SET data = ? "
                + "WHERE key = ?");
        
        // Update cache size
        updateCacheSize();
        
    }

    private void updateCacheSize() {
        try {
            // Query for the ID
            ResultSet res = cacheDB.query("SELECT key, size FROM cache_store");
            if (res == null) return;
            
            // Calculate cache size
            cacheSize = 0l;
            while (res.next()) {
                
                // Ensure the file exists (in case someone removed the files by hand)
                File f = new File(baseDir + "/" + res.getString("key") + ".cache");
                if (!f.exists()) {
                    
                    // If not, cleanup
                    cacheDB.update("DELETE FROM cache_store WHERE key = ?", res.getString("key"));
                    
                } else {
                    
                    // Get disk size if the size in the DB is zero for some reason
                    Long size = res.getLong("size");
                    if (size == 0) {
                        size = f.length();
                        cacheDB.update("UPDATE cache_store SET size = ? WHERE key = ?", size, res.getString("key"));
                    }
                    
                    // Update cache size
                    cacheSize += res.getLong("size");                    
                }
            }
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
        }   
    }
    
    private Boolean cleanupToFit(Long size) {
        // If there is no need to free space, return
        if (cacheSize + size <= CACHE_MAX_SIZE) return true;
        
        // If there is no way to obtain this space, return false
        if (size > CACHE_MAX_SIZE) {
            systemLogger.error("Excessive cache space requested! Will not cache!");
            return false;
        }
        
        // Select items to purge, start from oldest
        try {
            
            // Query for the IDs of big size
            ResultSet res = cacheDB.query("SELECT key, size FROM cache_store WHERE state = 1 ORDER BY request_date ASC");
            if (res == null) return false;
            
            // Start deleting until we reached free space
            while (res.next()) {
                
                // Remove file
                File f = new File(baseDir + "/" + res.getString("key") + ".cache");
                if (f.exists()) {
                    if (!f.delete()) {
                        systemLogger.error("Unable to delete ", f);
                    } else {
                        cacheDB.update("DELETE FROM cache_store WHERE key = ?", res.getString("id"));
                    }
                }
                
                // Release size
                cacheSize -= res.getLong("size");
                
                // Check free space
                if (cacheSize + size <= CACHE_MAX_SIZE) return true;
            }
            
            // Get the cache size
            res = cacheDB.query("SELECT COUNT(*) FROM cache_store");
            if (res == null) return false;
            
            // Check how many objects we have to delete
            if (!res.next()) return false;
            int cacheSize = res.getInt(0);
            if (cacheSize+1>CACHE_MAX_ITEMS) {
                
                // Calculate required space
                Integer reqSpace = cacheSize+1-CACHE_MAX_ITEMS;
                
                // We need to delete items
                cacheDB.query("DELETE FROM cache_store ORDER BY request_date ASC LIMI 0,"+reqSpace);
                
            }
            
            // Something went wrong :/
            return false;
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return false;
        }   
    }
    
    private byte[] serialize(Serializable o) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            // Dump object
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.close();
            
            // Get byte array
            return bos.toByteArray();
            
        } catch (IOException ex) {
            systemLogger.except(ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                systemLogger.error("Error closing serialization buffer ",ex);
            }
        }
        return null;
        
    }
    
    private Object unserialize(byte[] bytes) {
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(is);
            
            return ois.readObject();
            
        } catch (ClassNotFoundException ex) {
            systemLogger.except(ex);
            return null;
        } catch (UnsupportedEncodingException ex) {
            systemLogger.except(ex);
            return null;
        } catch (IOException ex) {
            systemLogger.except(ex);
            return null;
        }
    }
    
    public HttpCacheEntry getEntry(String string) throws IOException {
        String key = WebNGSystem.SHA1Sum(string);
        try {
            
            // Fetch item
            Database.assignArgStatements(stmSelect, new Object[]{ key });
            ResultSet r = stmSelect.executeQuery();
            if (!r.next()) return null;
            
            // Build entry
            HttpCacheEntry e = (HttpCacheEntry) unserialize(r.getBytes("data"));
            
            // Return the entry
            return e;
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return null;
        }
    }

    public void removeEntry(String string) throws IOException {
        String key = WebNGSystem.SHA1Sum(string);
        cacheDB.update("DELETE FROM cache_store WHERE key = ?", key);
    }

    public void updateEntry(String string, HttpCacheUpdateCallback hcuc) throws IOException, HttpCacheUpdateException {
        String key = WebNGSystem.SHA1Sum(string);
        HttpCacheEntry hce = hcuc.update(getEntry(key));
        Database.assignArgStatements(stmUpdate, new Object[]{ serialize(hce) });
    }

    public void putEntry(String string, HttpCacheEntry hce) throws IOException {
        String key = WebNGSystem.SHA1Sum(string);
        Long size = hce.getResource().length();
        
        // Firstly, can we fit the item?
        if (size > CACHE_MAX_ITEM_SIZE) {
            systemLogger.error("Oversized item requested for key "+key);
            return;
        }
        
        // If yes, cleanup enough space to fit this item (if needed)
        if (!cleanupToFit(size)) {
            systemLogger.error("Unable to fit item "+key+" in cache");
            return;
        }
        
        // Dump file to disk
        InputStream is = hce.getResource().getInputStream();
        FileOutputStream fos = new FileOutputStream(baseDir+"/"+key+".cache");
        byte[] b = new byte[IO_BUFFER_SIZE];  
        int read;  
        while ((read = is.read(b)) != -1) {  
            fos.write(b, 0, read);  
        }
        
        /*
        // Build headers
        String headers = "";
        for (Header h: hce.getAllHeaders()) {
            headers += h.getName()+": "+h.getValue()+"\n";
        }
        
        // Prepare some vars
        ProtocolVersion pv = hce.getStatusLine().getProtocolVersion();
         */
        
        // Update entry
        Database.assignArgStatements(stmInsert, new Object[]{
            key,
            System.currentTimeMillis(),
            "",
            0,
            size,
            "",
            1
        });
        
        try {
            stmInsert.setBytes(3, serialize(hce));
            stmInsert.execute();
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return;
        }
        
    }
    
}
