/*
 * TimedTrigger.java
 * 
 * BrowserNG - A workbench for the browser of the new generation
 * Copyright (C) 2012 Ioannis Charalampidis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
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
