/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public abstract class Trigger implements Runnable {

    protected Boolean running = false;
    
    @Override
    public void run() {
        startTrigger();
        running = true;
        while (!Thread.interrupted()) {
            try {
                stepTrigger();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                break;
            }
        }
        running = false;
        stopTrigger();
    }
    
    public Boolean isRunning() { 
        return running;
    }
    
    // Overridable functions
    public void startTrigger() { }
    public void stopTrigger() { };
    public void stepTrigger() { };
    public abstract void place(QueuedEvent event, Object[] args);
    
}
