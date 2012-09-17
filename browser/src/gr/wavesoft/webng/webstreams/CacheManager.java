/*
 * CacheManager.java
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
package gr.wavesoft.webng.webstreams;

import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.io.WebNGIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author icharala
 */
public class CacheManager {
    
    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(CacheManager.class, "CacheManager");
    private final String baseDir;
    
    private static Long CACHE_ALLOC_SPACE = 10485760l; // 10Mb
    private static Long cacheSize;

    public CacheManager(String baseDir) {
        this.baseDir = baseDir;
        updateCacheSize();
    }
    
    private void updateCacheSize() {
        try {
            // Query for the ID
            ResultSet res = WebNGIO.dbQuery("SELECT id, key, size FROM cache_store");
            if (res == null) return;
            
            // Calculate cache size
            cacheSize = 0l;
            while (res.next()) {
                
                // Ensure the file exists (in case someone removed the files by hand)
                File f = new File(baseDir + "/" + res.getString("key") + ".cache");
                if (!f.exists()) {
                    
                    // If not, cleanup
                    WebNGIO.dbUpdate("DELETE FROM cache_store WHERE id = ?", res.getLong("id"));
                    
                } else {
                    
                    // Get disk size if the size in the DB is zero for some reason
                    Long size = res.getLong("size");
                    if (size == 0) {
                        size = f.length();
                        WebNGIO.dbUpdate("UPDATE cache_store SET size = ? WHERE id = ?", size, res.getLong("id"));
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
        if (cacheSize + size <= CACHE_ALLOC_SPACE) return true;
        
        // If there is no way to obtain this space, return false
        if (size > CACHE_ALLOC_SPACE) {
            systemLogger.error("Excessive cache space requested! Will not cache!");
            return false;
        }
        
        // Select items to purge, start from oldest
        try {
            // Query for the ID
            ResultSet res = WebNGIO.dbQuery("SELECT id, key, size FROM cache_store WHERE state = 1 ORDER BY updated ASC");
            if (res == null) return false;
            
            // Start deleting until we reached free space
            while (res.next()) {
                
                // Remove file
                File f = new File(baseDir + "/" + res.getString("key") + ".cache");
                if (f.exists()) {
                    if (!f.delete()) {
                        systemLogger.error("Unable to delete ", f);
                    } else {
                        WebNGIO.dbUpdate("DELETE FROM cache_store WHERE id = ?", res.getLong("id"));
                    }
                }
                
                // Release size
                cacheSize -= res.getLong("size");
                
                // Check free space
                if (cacheSize + size <= CACHE_ALLOC_SPACE) return true;
            }
            
            // Something went wrong :/
            return false;
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return false;
        }   
    }
    
    public void openCachedRStream(CacheItem tok, RStreamCallback callback) {
        File f = new File(baseDir + "/" + tok.key + ".cache");
        if (f.canRead()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                callback.streamReady(fis, new CachedResponseInfo(tok));
                fis.close();
            } catch (FileNotFoundException ex) {
                systemLogger.except(ex);
                callback.streamFailed(ex);
            } catch (IOException ex) {
                systemLogger.except(ex);
                callback.streamFailed(ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    systemLogger.except(ex);
                    callback.streamFailed(ex);
                }
            }
            
        } else {
            systemLogger.warn("Cannot find cache file ", f);
            callback.streamFailed(new IOException("Cannot find cache file "+f.toString()));
        }
    }
    
    public CacheItem getCacheToken(StreamRequest req) {
        return new CacheItem(req.getCacheIndex());
    }
    
    public void reheatCacheToken(CacheItem tok) {
        tok.dateProbed = System.currentTimeMillis();
        tok.save();
    }
    
    public void deleteCache(CacheItem tok) {
        File f = new File(baseDir + "/" + tok.key + ".cache");
        if (f.exists()) {
            f.delete();
        }
        tok.delete();
    }
    
    public void updateCacheToken(CacheItem tok, Long softTTL, String customDetails) {
        tok.dateUpdated = System.currentTimeMillis();
        if (softTTL != null)
            tok.softTTL = softTTL;
        if (customDetails != null)
            tok.customData = customDetails;
        tok.save();
    }

    private OutputStream getCacheOutputStream(CacheItem tok) {
        File f = new File(baseDir + "/" + tok.key + ".cache");
        if (!f.exists() || f.canWrite()) {
            try {
                FileOutputStream fos = new FileOutputStream(f);
                return fos;
            } catch (FileNotFoundException ex) {
                systemLogger.except(ex);
                return null;
            }
        } else {
            systemLogger.warn("Cannot find cache file ", f);
            return null;
        }
    }
    
    public RStreamCallback cacheResponse(final CacheItem tok, final RStreamCallback callback) {
        
        // The following callback will be called when the transport
        // stream is ready:
        return new RStreamCallback() {

            @Override
            public void streamFailed(Exception e) {
                callback.streamFailed(e);
            }

            @Override
            public void streamReady(final InputStream is, final ResponseInfo info) {
                final OutputStream os = getCacheOutputStream(tok);
                
                if (os == null) {
                    // If we did not manage to open cache, passthrough
                    systemLogger.warn("Unable to open cache file for token ",tok);
                    callback.streamReady(is, info);
                    
                } else if ((info.cacheInfo != null) && (!info.cacheInfo.useCache)) {
                    // If the response forbids caching, passthrough
                    systemLogger.debug("Not using cache for token ",tok);
                    callback.streamReady(is, info);
                    
                } else {
                    
                    // Update cache dates
                    tok.dateProbed = System.currentTimeMillis();
                    tok.dateUpdated = System.currentTimeMillis();
                    
                    // Update cache timeouts
                    if (info.cacheInfo != null) {
                        if (info.cacheInfo.expiresTTL != null)
                            tok.softTTL = info.cacheInfo.expiresTTL;
                        if (info.cacheInfo.customDetails != null)
                            tok.customData = info.cacheInfo.customDetails;
                    }
                    
                    // Update cache item size
                    if (info.contentSize != null)
                        tok.size = info.contentSize;
                    
                    // Fast-cleanup cache
                    if (!cleanupToFit(tok.size)) {
                        // Unable to free space!
                        
                        // Delete foken
                        tok.delete();
                        
                        // Passthru
                        systemLogger.warn("Unable to allocate cache space for token ",tok);
                        callback.streamReady(is, info);
                        
                        // Nothing else...
                        return;
                        
                    }
                    
                    // Occupy space
                    cacheSize += tok.size;
                    
                    // Save cached item information
                    tok.state = CacheItem.STATE_CACHED;
                    tok.save();
                    
                    // Create a split stream that reads from input and writes
                    // to cache in the same time.
                    callback.streamReady(new InputStream() {

                        @Override
                        public int read() throws IOException {
                            try {
                                int i = is.read();
                                os.write(i);
                                return i;
                            } catch (IOException e) {
                                os.close();
                                deleteCache(tok);
                                callback.streamFailed(e);
                                throw e;
                            }
                        }

                        @Override
                        public void close() throws IOException {
                            is.close();
                            os.close();
                            super.close();
                        }

                        @Override
                        public int available() throws IOException {
                            return is.available();
                        }

                        @Override
                        public synchronized void mark(int i) {
                            is.mark(i);
                        }

                        @Override
                        public boolean markSupported() {
                            return is.markSupported();
                        }

                        @Override
                        public int read(byte[] bytes) throws IOException {
                            try {
                                int i = is.read(bytes);
                                os.write(bytes,0,i);
                                return i;
                            } catch (IOException e) {
                                os.close();
                                deleteCache(tok);
                                callback.streamFailed(e);
                                throw e;
                            }
                        }

                        @Override
                        public long skip(long l) throws IOException {
                            for (long i=0; i<l; i++) {
                                os.write(0);
                            }
                            return is.skip(l);
                        }
                        
                    }, info);
                    
                }
            }
            
        };
    }
    
}
