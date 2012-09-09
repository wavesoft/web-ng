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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class DownloadManager {
    
    private static final int BUFFER_SIZE = 1024;

    public static interface DownloadListener {
        
        public void downloadCompleted(DownloadJob job);
        public void downloadFailed(DownloadJob job);
        
    }
    
    /**
     * Description of a download job
     */
    public static class DownloadJob {

        public enum DownloadType {
            BUFFER, FILE
        }
        
        public URL url;
        public String requestMethod;
        public String requestData;
        
        public String buffer;
        public String filename;
        public int    status;
        public String statusMessage;
        public DownloadListener listener;
        public DownloadType type;
        
        public DownloadJob(URL url,DownloadListener listener) {
            this.url = url;
            this.buffer = "";
            this.filename = "";
            this.requestMethod = "GET";
            this.requestData = "";
            this.listener = listener;
            this.type = DownloadType.BUFFER;
        }
        
        public DownloadJob(URL url, String requestMethod, String requestData, DownloadListener listener) {
            this.url = url;
            this.buffer = "";
            this.filename = "";
            this.requestMethod = requestMethod;
            this.requestData = requestData;
            this.listener = listener;
            this.type = DownloadType.BUFFER;
        }
        
        public DownloadJob(URL url, String filename, DownloadListener listener) {
            this.url = url;
            this.buffer = "";
            this.requestMethod = "GET";
            this.requestData = "";
            this.filename = filename;
            this.listener = listener;
            this.type = DownloadType.FILE;
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
                
                // Connect to the server and download the file
                try {
                    connection = (HttpURLConnection)job.url.openConnection();
                    connection.setRequestMethod(job.requestMethod);
                    connection.setDoOutput(true);
                    connection.setReadTimeout(10000);
                    
                    // Send request data
                    if (!job.requestData.isEmpty()) {
                        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                        osw.append(job.requestData);
                        osw.close();
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(DownloadManager.class.getName()).log(Level.SEVERE, null, ex);
                    job.setFailure(601, "IO Exception: "+ex.getMessage());
                    continue; // With the next job
                }
                
                // Read the entire buffer
                try {
                    
                    // Download to BUFFER
                    if (job.type == DownloadJob.DownloadType.BUFFER) {
                        
                        // Prepare StringBuilder
                        bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        strBuffer = new StringBuilder();
                        
                        // Download
                        char[] charBuf = new char[BUFFER_SIZE];
                        int len = 0;
                        while ((len = bufReader.read(charBuf))>0) {
                            strBuffer.append(charBuf, 0, len);
                        }
                        job.buffer = strBuffer.toString();
                        
                    // Download to FILE
                    } else if (job.type == DownloadJob.DownloadType.FILE) {
                        
                        // Download to FileOutputStream
                        FileOutputStream fos = new FileOutputStream(job.filename, false);
                        InputStream inStream = connection.getInputStream();
                        byte[] byteBuf = new byte[BUFFER_SIZE];
                        int len = 0;
                        while ((len = inStream.read(byteBuf))>0) {
                            fos.write(byteBuf, 0, len);
                        }
                        
                    }
                    
                    // Update job information
                    job.status = connection.getResponseCode();
                    
                    // Notify event
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
    
    public synchronized void checkThreads() {
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
            threads[freeSlot].start();
        }
    }
    
    public DownloadJob download(URL url, DownloadListener listener) {
        DownloadJob job = new DownloadJob(url, listener);
        queue.add(job);
        return job;
    }
    
    public void download(DownloadJob job, DownloadListener listener) {
        job.listener = listener;
        queue.add(job);
    }
    
    public void download(URL url, String requestMethod, String requestData, DownloadListener listener) {
        queue.add(new DownloadJob(url, requestMethod, requestData, listener));
    }
    
    public void downloadFile(URL url, String targetFilename, DownloadListener listener) {
        queue.add(new DownloadJob(url, targetFilename, listener));
    }
    
}
