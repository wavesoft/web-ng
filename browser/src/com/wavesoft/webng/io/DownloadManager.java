/*
 * DownloadThread.java
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
package com.wavesoft.webng.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class DownloadManager {

    public static interface DownloadListener {
        
        public void downloadCompleted(DownloadJob job);
        public void downloadFailed(DownloadJob job);
        
    }
    
    /**
     * Description of a download job
     */
    public static class DownloadJob {

        public String url;
        public String buffer;
        public int    status;
        public String statusMessage;
        public DownloadListener listener;
        
        public DownloadJob(String url,DownloadListener listener) {
            this.url = url;
            this.buffer = "";
            this.listener = listener;
        }
        
        public void setFailure(int status, String statusMessage) {
            this.status = status;
            this.statusMessage = statusMessage;
            if (listener != null)
                listener.downloadFailed(this);
        }
        
    }
    
    /**
     * A download thread
     */
    public class DownloadThread implements Runnable {

        @Override
        public void run() {
            HttpURLConnection connection;
            URL serverAddress;
            BufferedReader bufReader;
            StringBuilder strBuffer;
            
            // Keep downloading until we run out of objects
            while (!queue.isEmpty()) {
                
                // Fetch the next job
                DownloadJob job;
                try {
                    job = queue.take();
                } catch (InterruptedException ex) {
                    return;
                }
                
                // Setup URL
                try {
                    serverAddress = new URL(job.url);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(DownloadManager.class.getName()).log(Level.SEVERE, null, ex);
                    job.setFailure(600, "Mailformed URL Exception: "+ex.getMessage());
                    continue; // With the next job
                }
                
                // Connect to the server and download the file
                try {
                    connection = (HttpURLConnection)serverAddress.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    connection.setReadTimeout(10000);
                    
                } catch (IOException ex) {
                    Logger.getLogger(DownloadManager.class.getName()).log(Level.SEVERE, null, ex);
                    job.setFailure(601, "IO Exception: "+ex.getMessage());
                    continue; // With the next job
                }
                
                // Read the entire buffer
                try {
                    bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    strBuffer = new StringBuilder();
                    
                    char[] charBuf = new char[1024];
                    while (bufReader.read(charBuf)>0) {
                        strBuffer.append(charBuf);
                    }
                    
                    job.buffer = strBuffer.toString();
                    job.status = connection.getResponseCode();
                    if (job.listener != null)
                        job.listener.downloadCompleted(job);
                    
                } catch (IOException ex) {
                    Logger.getLogger(DownloadManager.class.getName()).log(Level.SEVERE, null, ex);
                    job.setFailure(602, "IO Exception: "+ex.getMessage());
                    continue; // With the next job
                }

                //close the connection, set all objects to null
                connection.disconnect();
                
            }
        }
        
    }
    
    private static final int MAX_THREADS = 2;
    protected ArrayBlockingQueue<DownloadJob> queue;
    private Thread[] threads;

    public DownloadManager() {
        threads = new Thread[MAX_THREADS];
        queue = new ArrayBlockingQueue<DownloadJob>(MAX_THREADS);
    }
    
    private void checkThreads() {
        // Dispose completed threads
        // and detect free slot
        int freeSlot = -1;
        for (int i=0; i<MAX_THREADS; i++) {
            if (threads[i] != null) {
                if (!threads[i].isAlive()) {
                    threads[i] = null;
                    freeSlot = i;
                }
            } else {
                freeSlot = i;
            }
        }
        
        // No free slot? No new thread...
        if (freeSlot != -1) {
            threads[freeSlot] = new Thread(new DownloadThread());
            threads[freeSlot].run();
        }
    }
    
    public void download(String URL) {
        queue.add(new DownloadJob(URL, null));
        checkThreads();
    }
    
    public void download(String URL, DownloadListener listener) {
        queue.add(new DownloadJob(URL, listener));
        checkThreads();
    }
    
}
