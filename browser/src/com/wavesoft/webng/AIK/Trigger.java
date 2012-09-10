/*
 * Trigger.java
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
