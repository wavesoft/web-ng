/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd.impl;

import gr.wavesoft.webng.wwd.Locator;
import java.net.URL;

/**
 *
 * @author icharala
 */
public class URLLocator implements Locator {

    protected URL url;
    protected String domain;
    protected long[] path;
    
    public URLLocator(URL url) {
        this.url = url;
        this.domain = url.getHost();
        String[] s_path = url.getPath().split("/");
        this.path = new long[s_path.length];
        for (int i=0; i<s_path.length; i++) {
            this.path[i] = s_path[i].hashCode();
        }
    }
    
    public long[] getPathComponents() {
        return path;
    }
    
}
