/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
