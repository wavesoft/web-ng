/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.ui;
import com.wavesoft.webng.api.WebViewNG;

/**
 *
 * @author icharala
 */
public interface PresenterEventListener {
    
    public void statusChanged(String status);
    public void progressChanged(int value, int max);
    public void viewChanged(WebViewNG view);
    
}
