/*
 * AnimatorSync.java
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
package gr.wavesoft.webng.render;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author icharala
 */
public class AnimationSync {
    
    private static class HandlerEntry {
        public Boolean enabled;
        public AnimationSyncHandler handler;
        public int ticksPerFrame;
        public long lastTick;

        public HandlerEntry(AnimationSyncHandler handler, int fpsPreference) {
            this.enabled = true;
            this.handler = handler;
            this.ticksPerFrame = 1000 / fpsPreference;
            this.lastTick = 0;
        }
    }
    
    private static ArrayList<HandlerEntry> animationHandlers = new ArrayList<HandlerEntry>();
    private static int minimumTicksPerFrame = Integer.MAX_VALUE;
    private static Timer timer = null;
    //private static TimerTask timerTask = null;
    
    static {
        
        // Setup timer
        timer = new Timer(0, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Thread.currentThread().setName("Animation-Sync");
                AnimationSync.broadcastTick(System.currentTimeMillis());                
            }
            
        });
        
    }
    
    private static void restartTimingThread() {
        timer.setDelay(minimumTicksPerFrame);
        if (!timer.isRunning()) timer.start();
    }
    
    public static void registerHandler(AnimationSyncHandler h, Integer prefferedFPS) {
        // Create the handler entry
        HandlerEntry e = new HandlerEntry(h, prefferedFPS);
        
        // Recalibrate timing thread
        if (e.ticksPerFrame < minimumTicksPerFrame) {
            minimumTicksPerFrame = e.ticksPerFrame;
            restartTimingThread();
        }
        
        // Register animation handler
        animationHandlers.add(e);
        
    }

    public static void removeHandler(AnimationSyncHandler h) {
        // Remove animation handler
        for (int i=0; i<animationHandlers.size(); i++) {
            if (animationHandlers.get(i).handler.equals(h)) {
                animationHandlers.remove(i);
                break;
            }
        }
        
        // If we ran out of animation handlers, stop the timer thread
        if (animationHandlers.isEmpty()) {
            if (timer != null) {
                timer.stop();
                minimumTicksPerFrame = Integer.MAX_VALUE;
            }
        }
    }
    
    public static void broadcastTick(Long tick) {
        // Boradcast a tick event
        for (HandlerEntry a: animationHandlers) {
            if (a.enabled) {
                if (tick - a.lastTick >= a.ticksPerFrame) {
                    a.lastTick = tick;
                    a.handler.animate(tick);
                }
            }
        }
    }
    
}
