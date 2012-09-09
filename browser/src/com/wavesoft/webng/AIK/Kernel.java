/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class Kernel {
    
    /*
     * How many events can we run per millisecond
     * (Used for performance calculations)
     */
    /*
    private static final long EVENTS_PER_SECOND;
    static {
        ConcurrentLinkedQueue<QueuedEvent> testbed = new ConcurrentLinkedQueue<QueuedEvent>();
        QueuedEvent testEvent = null;
        long time = System.currentTimeMillis();
        for (int i=0; i<10000000; i++) {
            if (testbed.isEmpty()) {
                testbed.remove(testEvent);
            } else {
                testEvent = new QueuedEvent();
                testbed.add(testEvent);
            }
        }
        time = System.currentTimeMillis() - time;
        EVENTS_PER_SECOND = 10000000 / time;
        System.out.println("Detected " + EVENTS_PER_SECOND + " events per second");
    }
     */
    
    /**
     * Cross-thread, thread-safe event queue
     */
    private static ConcurrentLinkedQueue<QueuedEvent> eventQueue = new ConcurrentLinkedQueue<QueuedEvent>();
    
    /**
     * Synchronization object
     */
    private static final Object syncObject = new Object();
    
    /**
     * Triggers stack
     */
    private static ConcurrentHashMap<String, TriggerInfo> triggerList = new ConcurrentHashMap<String, TriggerInfo>();
    
    /**
     * Is kernel running
     */
    private static Boolean kernelRunning = false;
    
    /**
     * The current session in the kernel
     */
    public static Session currentSession = null;
    
    /**
     * The current event being processed
     */
    public static QueuedEvent currentEvent = null;
    
    /**
     * Sessions stack
     */
    private static ArrayList<Session> sessions = new ArrayList<Session>();
    
    public static void registerTrigger(String name, Trigger instance) {
        Thread t = new Thread(instance);
        triggerList.put(name, new TriggerInfo(t, instance));
        t.start();
    }
    
    public static QueuedEvent trigger(String triggerName, Object[] triggerArgs, Session session, String message, Object ... args) {
        if (!triggerList.containsKey(triggerName)) return null;
        TriggerInfo inst = triggerList.get(triggerName);
        if (inst != null) {
            QueuedEvent event = new QueuedEvent(session, message, args);
            inst.trigger.place(event, triggerArgs);
            return event;
        }
        return null;
    }
    
    public static Session register(Object host) {
        Session session = new Session(host);
        sessions.add(session);
        if (kernelRunning)
            addEvent(session, "__start__", new Object[]{});
        return session;
    }
    
    public static void unregister(Object host) {
        Session found = null;
        for (Session s: sessions) {
            if (s.context.equals(host)) {
                found = s;
                break;
            }
        }
        if (found != null)
            sessions.remove(found);

    }
    
    public static void unregister(Session host) {
        sessions.remove(host);
    }
    
    /**
     * Perform a time slice
     */
    public static QueuedEvent timeSlice() throws KernelStoppedException {
        
        // Make sure we are running
        if (!kernelRunning)
            throw new KernelStoppedException();
        
        // Process queue
        QueuedEvent e = null;
        while ((e = eventQueue.poll()) == null) {
            try {
                synchronized (syncObject) {
                    syncObject.wait();
                }
            } catch (InterruptedException ex) {
                // Got it!
            }
        }
        
        // Invoke the item on queue
        currentSession = e.session;
        currentEvent = e;
        e.invoke();
        return e;
        
    }
    
    public static QueuedEvent addEvent(Session session, String message, Object[] args) {
        QueuedEvent e = new QueuedEvent(session, message, args);
        eventQueue.add(e);
        if (eventQueue.size() == 1) {
            synchronized (syncObject) {
                syncObject.notifyAll();
            }
        }
        return e;
    }
    
    public static QueuedEvent addEvent(QueuedEvent event) {
        eventQueue.add(event);
        if (eventQueue.size() == 1) {
            synchronized (syncObject) {
                syncObject.notifyAll();
            }
        }
        return event;
    }
    
    public static Boolean isRunning() {
        return kernelRunning;
    }
    
    public static void start() {
        kernelRunning = true;
        
        // Invoke start
        for (Session s: sessions) {
            currentSession = s;
            s.invoke("__start__", new Object[]{});
        }
        
        // Start event loop
        while(true) {
            try {
                Kernel.timeSlice();
            } catch (KernelStoppedException ex) {
                break;
            }
        }        
    }
    
    public static void stop() {
        // We are not running any more
        kernelRunning = false;
        
        // Invoke stop
        for (Session s: sessions) {
            currentSession = s;
            s.invoke("__stop__", new Object[]{});
        }

        // Shut down triggers
        for (TriggerInfo ti: triggerList.values()) {
            ti.thread.interrupt();
        }
    }
    
    /* ============================================================
     *                  SAME-SESSION EVENT FORWARDING
     * ============================================================
     */
    
    public static QueuedEvent yield(String event, Object ... args) {
        if (currentSession == null) return null;
        return addEvent(currentSession, event, args);
    }
    
    public static void broadcast(String event, Object ... args) {
        for (Session s: sessions) {
            if (s.isExposed(event))
                addEvent(s, event, args);
        }
    }
    
    public static Object waitFor(QueuedEvent event) {
        try {
            while (!timeSlice().equals(event)) {
            }
            return currentEvent.response;
        } catch (KernelStoppedException ex) {
            return null;
        }
    }
    
    
}
