/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.render;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
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
