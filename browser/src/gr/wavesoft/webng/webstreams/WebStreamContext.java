/*
 * WebStreamContext.java
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

/**
 *
 * @author icharala
 */
public class WebStreamContext {
    
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(WebStreamContext.class, "WebStreams");
    private static CacheManager cache;
    
    public static void Initialize(String cacheDir) {
        cache = new CacheManager(cacheDir);
    }
    
    /**
     * Cached stream that supports reading from a resource
     * @param req
     * @param callback 
     */
    public void openRStream(StreamRequest req, RStreamCallback callback) {
        Transport t = WebStreamFactory.getTransport(req, WebStreamFactory.DIRECTION_READ);
        if (t == null) {
            systemLogger.warn("Unable to find transport layer for request ", req);
            callback.streamFailed();
            return;
        }
        if (req.useCache) {
            
            // Fetch the cache token
            CacheItem tok = cache.getCacheToken(req);
            
            if (tok.isWarm()) {
                // Cache is still warm, get response from there
                cache.openCachedRStream(tok, callback);
                
            } else if (!tok.isExpired()) {
                // Cache is cold, but not yet expired. Check via transport
                // if the cache is still valid...
                if (t.isResourceModified(req, tok)) {
                    
                    // Nope, it's expired. Update...
                    t.openRStream(req, cache.cacheResponse(tok, callback));
                    
                } else {
                    
                    // Yes, it's still valid
                    cache.reheatCacheToken(tok);
                    cache.openCachedRStream(tok, callback);
                    
                }
                
            } else {
                // Cache is expired. Update
                t.openRStream(req, cache.cacheResponse(tok, callback));
                
            }
            
        } else {
            
            // No caching will be performed, passthrough to transport
            t.openRStream(req, callback);
            
        }
    }
    
    /**
     * Uncached stream that supports only writing to a resource
     * @param req
     * @param callback 
     */
    public void openWStream(StreamRequest req, WStreamCallback callback) {
        Transport t = WebStreamFactory.getTransport(req, WebStreamFactory.DIRECTION_WRITE);
        if (t == null) {
            systemLogger.warn("Unable to find transport layer for request ", req);
            callback.streamFailed();
            return;
        }
        t.openWStream(req, callback);
    }
    
    /**
     * Uncached bidirectional stream that supports reading and writing to a resource
     * @param req
     * @param callback 
     */
    public void openRWStream(StreamRequest req, RWStreamCallback callback) {
        Transport t = WebStreamFactory.getTransport(req, WebStreamFactory.DIRECTION_READ | WebStreamFactory.DIRECTION_WRITE);
        if (t == null) {
            systemLogger.warn("Unable to find transport layer for request ", req);
            callback.streamFailed();
            return;
        }
        t.openRWStream(req, callback);
    }
    
    /**
     * Abort all the queued streams
     */
    public void cancelPending() {
        
    }
    
    /**
     * Abort all the running streams
     */
    public void cancelRunning() {
        
    }
    
    /**
     * Abort all the pending or active streams
     */
    public void cancelAll() {
        cancelPending();
        cancelRunning();
    }
    
}
