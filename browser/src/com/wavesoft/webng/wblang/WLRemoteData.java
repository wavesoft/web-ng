/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
