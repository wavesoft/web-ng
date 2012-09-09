/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

/**
 *
 * @author icharala
 */
public interface AsyncEventListener {
    
    public void completed(Object result);
    public void failed(Exception e, Object result);
    
}
