/*
 * DownloadTrigger.java
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
package com.wavesoft.webng.AIK.Triggers;

import com.wavesoft.webng.AIK.Kernel;
import com.wavesoft.webng.AIK.QueuedEvent;
import com.wavesoft.webng.AIK.Trigger;
import com.wavesoft.webng.io.DownloadManager;
import com.wavesoft.webng.io.DownloadManager.DownloadJob;
import com.wavesoft.webng.io.DownloadManager.DownloadListener;
import com.wavesoft.webng.io.SystemConsole;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class DownloadTrigger extends Trigger implements DownloadListener {
    
    // Register to kernel
    static {
        Kernel.registerTrigger("download", new DownloadTrigger());
    }
    
    // Expose download functions
    public static QueuedEvent download(URL url,  String event, Object ... args) {
        return Kernel.trigger("download", new Object[]{new DownloadJob(url, null)}, Kernel.currentSession, event, args);
    }
    public static QueuedEvent download(String url,  String event, Object ... args) {
        try {
            return Kernel.trigger("download", new Object[]{new DownloadJob(new URL(url), null)}, Kernel.currentSession, event, args);
        } catch (MalformedURLException ex) {
            return null;
        }
    }
    public static QueuedEvent downloadFile(URL fileUrl, String localPath, String event, Object ... args) {
        return Kernel.trigger("download", new Object[]{new DownloadJob(fileUrl, localPath, null)}, Kernel.currentSession, event, args);
    }
    public static QueuedEvent downloadFile(String fileUrl, String localPath, String event, Object ... args) {
        try {
            return Kernel.trigger("download", new Object[]{new DownloadJob(new URL(fileUrl),localPath,null)}, Kernel.currentSession, event, args);
        } catch (MalformedURLException ex) {
            return null;
        }
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    private DownloadManager manager;
    private ConcurrentHashMap<DownloadJob, QueuedEvent> events;
    private Boolean pendingCheck = false;

    public DownloadTrigger() {
        events = new ConcurrentHashMap<DownloadJob, QueuedEvent>();
        manager = new DownloadManager();
    }

    @Override
    public void stepTrigger() {
        if (pendingCheck) {
            manager.checkThreads();
            pendingCheck = false;
        }
    }
    
    @Override
    public void place(QueuedEvent event, Object[] args) {
        DownloadJob job = (DownloadJob) args[0];
        events.put(job, event);
        manager.download(job, this);
        pendingCheck = true;
    }

    @Override
    public void downloadCompleted(DownloadJob job) {
        QueuedEvent ev = events.get(job);
        ev.expandArguments(new Object[]{job});
        Kernel.addEvent(ev);
    }

    @Override
    public void downloadFailed(DownloadJob job) {
        QueuedEvent ev = events.get(job);
        ev.expandArguments(new Object[]{null});
        Kernel.addEvent(ev);
    }
    
    
}
