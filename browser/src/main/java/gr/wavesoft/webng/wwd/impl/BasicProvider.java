/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd.impl;

import gr.wavesoft.webng.wwd.Locator;
import gr.wavesoft.webng.wwd.Provider;
import gr.wavesoft.webng.wwd.Q;
import gr.wavesoft.webng.wwd.WWDI;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author icharala
 */
public class BasicProvider implements Provider {
    
    HashMap<Long, WWDI> dataStore;
    
    
    public WWDI get(Locator location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<WWDI> query(Q filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
