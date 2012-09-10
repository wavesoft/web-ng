/*
 * WL.java
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
package com.wavesoft.webng.wblang;

import com.wavesoft.webng.io.DownloadManager.DownloadJob;
import com.wavesoft.webng.io.DownloadManager.DownloadListener;
import com.wavesoft.webng.io.WebNGIO;
import java.net.URL;
import java.util.LinkedHashMap;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author icharala
 */
public class WL {

    private static class DownloadWait implements DownloadListener {
        private final Object sync = new Object();
        private Boolean pending = false;
        public LinkedHashMap<String, Object> list;
        
        @Override
        public void downloadCompleted(DownloadJob job) {
            Yaml yaml = new Yaml();
            list = (LinkedHashMap<String, Object>) yaml.load(job.buffer);
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        @Override
        public void downloadFailed(DownloadJob job) {
            list = null;
            synchronized (sync) {
                sync.notifyAll();
            }
        }
        
        public LinkedHashMap<String, Object> getList(URL url) {
            
            // Synchronize
            synchronized(sync) {
                // Schedule download
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
        
        private static String encodeData(LinkedHashMap<String, Object> parameters) {
            return "";
        }
        
        public LinkedHashMap<String, Object> getList(URL url, String data) {
            
            // Synchronize
            synchronized(sync) {
                
                // Schedule download
                WebNGIO.downloadData(url, "POST", data, this);
            
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
    
    private static DownloadWait downloadIO = new DownloadWait();
    
    public static LinkedHashMap<String, Object> download(URL url) {
        return downloadIO.getList(url);
    }
    
    public static LinkedHashMap<String, Object> download(URL url, String data) {
        return downloadIO.getList(url, data);
    }
    
    public static String getDefaultAttribute(String name) {
        if ("view".equals(name)) {
            return "com.wavesoft.webng.render.WebViewDefault";
        }
        return null;
    }
    
}
