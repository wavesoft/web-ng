/*
 * WebNGCache.java
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
package gr.wavesoft.webng.io.cache;

import gr.wavesoft.webng.io.Database;

/**
 *
 * @author icharala
 */
public class WebNGCache {
    
    public static WebNGCacheConfig config;
    private static String cacheDir;
    private static Database cacheDB;
    
    public static void Initialize(String cacheDir) {
        
        // Setup variables
        WebNGCache.cacheDir = cacheDir;
        WebNGCache.config = new WebNGCacheConfig(cacheDir);
        
        // Open cache database
        cacheDB = new Database(cacheDir+"/cache.db");
        
        // Used by Disk cache
        cacheDB.setupSchema("cache_store", 
                
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

        // Used by cookie cache
        cacheDB.setupSchema("cookie_store",
                  "id               INTEGER PRIMARY KEY,"
                + "created          INTEGER,"
                + "cookie           TEXT"
                );

    }
    
    public static CookieCache instanceCookieCache() {
        return null;
    }
    
    public static DiskCacheStorage instanceDiskCacheStorage() {
        return new DiskCacheStorage(cacheDir, "e0", cacheDB);
    }
    
}
