/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.api;

import com.wavesoft.webng.wblang.WLData;
import java.util.EventListener;

/**
 *
 * @author icharala
 */
public interface WebViewDataListener extends EventListener  {
    
    public void dataReady(WLData data);
    public void dataInvalidated();
    
}
