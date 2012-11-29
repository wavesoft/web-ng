/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd.impl;

import gr.wavesoft.webng.wwd.DataClass;
import gr.wavesoft.webng.wwd.Q;
import gr.wavesoft.webng.wwd.WWDI;
import java.net.URL;

/**
 *
 * @author icharala
 */
public class Link implements DataClass {
    
    URL url;

    public Link(URL url) {
        this.url = url;
        
        Q v = Q.and(
                Q.ref(Link.class,"url").eq(Q.v("Good")),
                Q.ref(Link.class,"date").lt(Q.v(System.currentTimeMillis()))
                );
    }
    
    public URL getURL() {
        return url;
    }
    
    public WWDI getData() {
        return null;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
    
}
