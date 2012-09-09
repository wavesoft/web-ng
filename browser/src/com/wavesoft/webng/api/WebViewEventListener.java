/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.api;

import java.util.EventListener;

/**
 *
 * @author icharala
 */
public interface WebViewEventListener extends EventListener {
    
    public void viewsLoaded();
    public void contentLoaded();
    
}
