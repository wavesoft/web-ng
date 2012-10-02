/*
 * GETThread.java
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

import gr.wavesoft.webng.io.SystemConsole;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author icharala
 */
public class GETThread extends Thread {
    
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(GETThread.class, "HTTP IO");
    private final HttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;
    private final WebRequest request;
    
    public GETThread(HttpClient httpClient, WebRequest request) throws URISyntaxException {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpget = new HttpGet(request.url.toURI());
        this.request = request;
        
        // Add headers
        for (String k: request.headers.keySet()) {
            this.httpget.setHeader(k, request.headers.get(k));
        }
        this.httpget.setHeader("User-Agent", "WebNG/1.0 ("+getClass().getPackage().getImplementationVersion()+")");
        
    }
    
    @Override
    public void run() {
        try {
            systemLogger.debug("Thread started");
            
            // Download the resource
            HttpResponse response = this.httpClient.execute(this.httpget, this.context);
            
            // Use the common handleResponse procedure
            WebStreams.handleResponse(response, request);
            
        } catch (Exception ex) {
            this.httpget.abort();
            
            systemLogger.except(ex);
            for (WebRequestListener l: request.listeners) {
                l.httpFailed(ex);
            }
            
        }
    }

}
