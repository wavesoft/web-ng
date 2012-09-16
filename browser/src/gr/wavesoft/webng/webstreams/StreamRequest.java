/*
 * StreamRequest.java
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
package gr.wavesoft.webng.webstreams;

import gr.wavesoft.webng.io.WebNGSystem;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author icharala
 */
public class StreamRequest {
    
    public URL url;
    public HashMap<String, Object> parameters;
    public InputStream inputStream;
    public Boolean useCache;

    public StreamRequest(URL url, HashMap<String, Object> parameters, InputStream inputStream) {
        this.url = url;
        this.parameters = parameters;
        this.inputStream = inputStream;
        this.useCache = true;
    }
    
    public String getCacheIndex() {
        String data = this.url.toString();
        List<String> keys = WebNGSystem.asSortedList(parameters.keySet());
        for (String k: keys) {
            data += "\n" + k + "=" + parameters.get(k);
        }
        
        // Checksum of the request + parameters
        return WebNGSystem.SHA1Sum(data);
    }
    
}
