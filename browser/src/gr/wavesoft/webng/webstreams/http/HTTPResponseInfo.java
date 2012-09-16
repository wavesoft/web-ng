/*
 * HTTPResponseInfo.java
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
package gr.wavesoft.webng.webstreams.http;

import gr.wavesoft.webng.webstreams.ResponseCacheInfo;
import gr.wavesoft.webng.webstreams.ResponseInfo;
import java.net.HttpURLConnection;

/**
 *
 * @author icharala
 */
public class HTTPResponseInfo extends ResponseInfo {

    public HTTPResponseInfo(HttpURLConnection c) {
        // Create a cache info
        cacheInfo = new ResponseCacheInfo();

        // Check for Cache-Control
        String cc = c.getHeaderField("Cache-Control");
        if (cc != null) {
            String[] parts = cc.split(",");
            for (String p: parts) {
                p = p.trim();
                if (p.startsWith("max-age=")) {
                    cacheInfo.expiresTTL = Long.parseLong(p.substring(8));
                    cacheInfo.useCache = true;
                } else if (p.equals("no-store")) {
                    cacheInfo.useCache = false;
                }
            }
        } else {
            
            // Check for expiresTTL if Cache-Control was not found
            cc = c.getHeaderField("Expires");
            if (cc != null) {
                
                // Convert to TTL
                Long expiresTime = HTTPTransport.GMTToMillis(cc);
                if (cc != null) {
                    expiresTime = expiresTime - System.currentTimeMillis();
                    cacheInfo.useCache = true;
                    cacheInfo.expiresTTL = expiresTime;
                }
            }
            
        }
        
        // Check for ETag
        cc = c.getHeaderField("ETag");
        if (cc != null) {
            cacheInfo.customDetails = cc;
        }
        
        // Update content-size
        cc = c.getHeaderField("Content-Length");
        if (cc != null) {
            contentSize = Long.parseLong(cc);
        }
        
    }
    
}
