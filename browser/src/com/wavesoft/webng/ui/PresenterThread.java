/*
 * PresenterThread.java
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
package com.wavesoft.webng.ui;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.io.AsyncEventListener;
import com.wavesoft.webng.io.WebNGIO;
import com.wavesoft.webng.render.WebViewError;
import com.wavesoft.webng.wblang.WL;
import com.wavesoft.webng.wblang.WLData;
import com.wavesoft.webng.wblang.WLRemoteData;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class PresenterThread implements Runnable {
    
    private static Integer threadID = 0;
    PresenterEventListener listener;
    String url;
    
    public PresenterThread(String URL, PresenterEventListener listener) {
        this.listener = listener;
        this.url = URL;
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName("Presenter-" + (threadID++).toString());
        
        // Validate URL
        try {
            URL uURL = new URL(url);
        } catch (MalformedURLException ex) {
            listener.viewChanged(new WebViewError("Invalid URL", ex));
            return;
        }
        
        // Prepare the node
        WLData data = new WLRemoteData(url);
        
        // Get and render the view
        data.getView(new AsyncEventListener() {

            @Override
            public void completed(Object result) {
                listener.viewChanged((WebViewNG) result);
            }

            @Override
            public void failed(Exception e, Object result) {
                listener.viewChanged(new WebViewError("Unable to display view", e));
            }
        });
        
    }
    
}
