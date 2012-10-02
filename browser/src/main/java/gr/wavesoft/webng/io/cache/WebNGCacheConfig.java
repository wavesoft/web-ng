/*
 * WebNGCacheConfig.java
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

/**
 *
 * @author icharala
 */
public class WebNGCacheConfig {
    
    public long     MAX_OBJECT_SIZE   = 1*1024*1024; // 1Mb max cached item size
    public int      MAX_ENTRIES_MEM   = 10; // Max 10Mb in memory
    public int      MAX_ENTRIES_DISK  = 100; // Max 100Mb in disk
    
    public String   CACHE_DIR;

    public WebNGCacheConfig(String cacheDir) {
        this.CACHE_DIR = cacheDir;
    }


}
