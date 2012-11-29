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
public interface WWDI {
    
    /**
     * Return the provider that created this WWDI instance.
     * @return 
     */
    public Provider getProvider();
    
    /**
     * Extract an object from the multi-class store
     * @param classType The object class to extract
     * @return Returns the object instance
     */
    public List<DataClass> extract(Class classType);

}
