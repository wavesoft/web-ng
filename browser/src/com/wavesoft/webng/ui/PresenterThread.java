/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.ui;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.io.AsyncEventListener;
import com.wavesoft.webng.io.WebNGIO;
import com.wavesoft.webng.render.WebViewError;

/**
 *
 * @author icharala
 */
public class PresenterThread implements Runnable {
    
    PresenterEventListener listener;
    String url;
    
    public PresenterThread(String URL, PresenterEventListener listener) {
        this.listener = listener;
        this.url = URL;
    }
    
    @Override
    public void run() {
        WebNGIO.viewFromClassName(url, new AsyncEventListener() {

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
