/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd;

import java.util.List;

/**
 *
 * @author icharala
 */
public interface Provider {
    
    /**
     * Return the WWDI object that resides on the location described
     * by the given locator object.
     * 
     * @param location
     * @return 
     */
    public WWDI get(Locator location);
    
    /**
     * Query this provider for items that match the given rules
     * @return 
     */
    public List<WWDI> query(Q filter);
    
}
