/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.streams;

import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author icharala
 */
public class StreamRequest {
    
    public URL url;
    public HashMap<String, Object> params;

    public StreamRequest(URL url) {
        this.url = url;
        this.params = new HashMap<String, Object>();
    }

    public StreamRequest(URL url, HashMap<String, Object> params) {
        this.url = url;
        this.params = params;
    }
    
    public StreamRequest setParameters(String key, Object value) {
        this.params.put(key, value);
        return this;
    }
    
}
