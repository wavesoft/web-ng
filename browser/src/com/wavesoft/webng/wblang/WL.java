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
import com.wavesoft.webng.io.SystemConsole;
import com.wavesoft.webng.io.WebNGIO;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author icharala
 */
public class WL {

    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(WL.class, "WL");

    private static class DownloadWait implements DownloadListener {
        private final Object sync = new Object();
        private Exception failException = null;
        public LinkedHashMap<String, Object> list;
        
        @Override
        public void downloadCompleted(DownloadJob job) {
            Yaml yaml = new Yaml();
            failException = null;
            try {
                list = (LinkedHashMap<String, Object>) yaml.load(job.buffer);
            } catch (Exception e) {
                systemLogger.exception(e);
                list = null;
                failException = e;
            }
            synchronized (sync) {
                sync.notifyAll();
            }
        }

        @Override
        public void downloadFailed(DownloadJob job) {
            list = null;
            if (job.innerException != null) {
                failException = job.innerException;
            } else {
                failException = new IOException("Server responded with error #"+Integer.toString(job.status)+": "+job.statusMessage);
            }
            synchronized (sync) {
                sync.notifyAll();
            }
        }
        
        public LinkedHashMap<String, Object> getList(URL url) throws Exception {
            
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
            
            // If we have an exception, throw it now
            if (failException!=null) throw failException;
            
            // Return list
            return list;
        }
        
        private static String encodeData(LinkedHashMap<String, Object> parameters) {
            return "";
        }
        
        public LinkedHashMap<String, Object> getList(URL url, String data) throws Exception {
            
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
            
            // If we have an exception, throw it now
            if (failException!=null) throw failException;
            
            // Return list
            return list;
        }
        
    }
    
    private static DownloadWait downloadIO = new DownloadWait();
    
    public static LinkedHashMap<String, Object> download(URL url) throws Exception {
        return downloadIO.getList(url);
    }
    
    public static LinkedHashMap<String, Object> download(URL url, String data) throws Exception {
        return downloadIO.getList(url, data);
    }
    
    public static String getDefaultAttribute(String name) {
        if ("view".equals(name)) {
            return "com.wavesoft.webng.render.WebViewDefault";
        }
        return null;
    }
    
}
