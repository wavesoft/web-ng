/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK.Triggers;

import com.wavesoft.webng.AIK.Kernel;
import com.wavesoft.webng.AIK.QueuedEvent;
import com.wavesoft.webng.AIK.Trigger;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A timer-based event trigger.
 * 
 * @author icharala
 */
public class TimedTrigger extends Trigger {
    
    // Register to kernel
    static {
        Kernel.registerTrigger("delay", new TimedTrigger());
    }
    
    // Expose delay function
    public static QueuedEvent delay(Long time,  String event, Object ... args) {
        return Kernel.trigger("delay", new Object[]{time}, Kernel.currentSession, event, args);
    }
    public static QueuedEvent delay(Integer time,  String event, Object ... args) {
        return Kernel.trigger("delay", new Object[]{time.longValue()}, Kernel.currentSession, event, args);
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    private Boolean running = false;
    private Timer timer;

    public TimedTrigger() {
        running = true;
        timer = new Timer();
    }

    @Override
    public void stopTrigger() {
        timer.cancel();
        timer.purge();
        running = false;
    }

    @Override
    public void place(final QueuedEvent event, Object[] args) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Kernel.addEvent(event);
            }
            
        }, (Long)args[0]);
    }

}
