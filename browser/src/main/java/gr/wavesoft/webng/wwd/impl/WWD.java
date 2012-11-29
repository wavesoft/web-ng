/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd.impl;

import gr.wavesoft.webng.wwd.DataClass;
import gr.wavesoft.webng.wwd.WWDI;
import gr.wavesoft.webng.wwd.Provider;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author icharala
 */
public abstract class WWD implements WWDI {

    protected HashMap<Class, List<DataClass>> objectClasses;
    protected Provider provider;

    public WWD(Provider provider) {
        this.objectClasses = new HashMap<Class, List<DataClass>>();
        this.provider = provider;
    }
    
    public List<DataClass> extract(Class classType) {
        if (!objectClasses.containsKey(classType)) return null;
        return objectClasses.get(classType);
    }

    public Provider getProvider() {
        return provider;
    }
    
}
