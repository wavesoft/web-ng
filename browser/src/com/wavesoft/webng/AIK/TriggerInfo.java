/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK;

/**
 *
 * @author icharala
 */
public class TriggerInfo {
    
    public Thread thread;
    public Trigger trigger;

    public TriggerInfo(Thread thread, Trigger trigger) {
        this.thread = thread;
        this.trigger = trigger;
    }
    
}
