/*
 * WLREmoteData.java
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
package com.wavesoft.webng.wblang;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author icharala
 */
public class WLRemoteData extends WLData {
    
    protected URL url;
    private Boolean dataReady;
    private Object requestData;

    public WLRemoteData(String url) {
        super(null, null);
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            // Return the null
            dataReady = true;
            return;
        }
        this.dataReady = false;
        this.dataType = DataType.UNKNOWN;
    }
    
    public WLRemoteData(URL url, Object data, WLData parent) {
        super(null, parent);
        this.requestData = data;
        this.url = url;
        this.dataReady = false;
        this.dataType = DataType.UNKNOWN;
    }
    
    private void download() {
        Object dwlData = WL.download(url);
        if (dwlData == null) {
            this.dataReady = false;
            set(null);
        } else {
            this.dataReady = true;
            set(dwlData);
        }
    }

    @Override
    public URL getNodeURL() {
        return url;
    }

    @Override
    public Object value() {
        if (!dataReady) download();
        return super.value();
    }

    @Override
    public void invalidate() {
        
        // Reset downloaded data
        this.dataReady = false;
        this.data = null;
        this.dataType = DataType.UNKNOWN;
        
        // Continue invalidations
        super.invalidate();
    }
    
}
