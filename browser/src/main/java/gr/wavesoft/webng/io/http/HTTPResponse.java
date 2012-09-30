/*
 * HTTPResponse.java
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
package gr.wavesoft.webng.io.http;

import java.util.HashMap;

/**
 *
 * @author icharala
 */
public abstract class HTTPResponse {
    
    public HTTPRequest request;
    public HashMap<String, String> headers = new HashMap<String, String>();
    
    public int statusCode = 0;
    public String statusText = "";
    
    public String contentType;
    public long contentSize;
    
    public HTTPResponse(HTTPRequest req) {
        this.request = req;
    }
    
}