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
package gr.wavesoft.webng.wblang;

import gr.wavesoft.webng.io.SystemConsole;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author icharala
 */
public class WLRemoteData extends WLData {
    
    protected URL url;
    private Boolean dataReady;
    private Object requestData;

    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(WLRemoteData.class, "WLRemoteData");

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
    
    public void download() throws Exception {
        systemLogger.debug("Downloading URL ",url);
        Object dwlData = WL.download(url);
        if (dwlData == null) {
            systemLogger.error("Invalid YAML Data arrived");
            this.dataReady = false;
            set(null);
        } else {
            systemLogger.debug("Data parsed");
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
        if (!dataReady) try {
            download();
        } catch (Exception ex) {
            return null;
        }
        return super.value();
    }

    @Override
    public Object get(String key) {
        if (!dataReady) try {
            download();
        } catch (Exception ex) {
            return null;
        }
        return super.get(key);
    }

    @Override
    public ArrayList<WLData> getArray(String key) {
        if (!dataReady) try {
            download();
        } catch (Exception ex) {
            return null;
        }
        return super.getArray(key);
    }

    @Override
    public String getAttribute(String name) {
        if (!dataReady) try {
            download();
        } catch (Exception ex) {
            return null;
        }
        return super.getAttribute(name);
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
