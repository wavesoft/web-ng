/*
 * WebRequest.java
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
package gr.wavesoft.webng.io.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author icharala
 */
public class WebRequest {
    
    public URL url;
    public String method = "GET";
    public Object requestData = null;
    public HashMap<String, String> headers;
    
    public File file;
    public int responseType = RESPONSE_BUFFER;
    public ArrayList<WebRequestListener> listeners = new ArrayList<WebRequestListener>();
    
    public static final int RESPONSE_BUFFER = 1;
    public static final int RESPONSE_STREAM = 2;
    public static final int RESPONSE_FILE = 3;

    public WebRequest(URL url) {
        this.headers = new HashMap<String, String>();
        this.url = url;
    }
    
    public WebRequest(URL url, String method, Object data) {
        this.headers = new HashMap<String, String>();
        this.url = url;
        this.requestData = data;
        this.method = method;
    }
    
    public WebRequest(URL url, String method, Object data, File file) {
        this.headers = new HashMap<String, String>();
        this.url = url;
        this.method = method;
        this.requestData = data;
        this.file = file;
        this.responseType = RESPONSE_FILE;
    }
    
    public WebRequest(String url) throws MalformedURLException {
        this.headers = new HashMap<String, String>();
        this.url = new URL(url);
    }
    
    public WebRequest(String url, int type) throws MalformedURLException {
        this.headers = new HashMap<String, String>();
        this.url = new URL(url);
        this.responseType = type;
    }
    
    public WebRequest(String url, String file) throws MalformedURLException {
        this.headers = new HashMap<String, String>();
        this.url = new URL(url);
        this.file = new File(file);
        this.responseType = RESPONSE_FILE;
    }
    
    public WebRequest addHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }
    
    public WebRequest setData(Object data) {
        requestData = data;
        return this;
    }
    
    public WebRequest addListener(WebRequestListener listener) {
        this.listeners.add(listener);
        return this;
    }
    
    public WebRequest setResponseType(int type) {
        this.responseType = type;
        return this;
    }
    
}
