/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK;

import com.wavesoft.webng.io.SystemConsole;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 *
 * @author icharala
 */
public class Session {
    
    /**
     * Information of the registered message-to-java callbacks
     */
    public static class CallbackInfo {
        Method method;
        Boolean exposed;

        public CallbackInfo(Method method, Boolean exposed) {
            this.method = method;
            this.exposed = exposed;
        }
    }
    
    public LinkedHashMap<String, CallbackInfo>  callbacks;
    public Boolean                              disposable;
    public Object                               context;

    public Object invoke(String name, Object[] args) {
        if (!callbacks.containsKey(name)) return null;
        CallbackInfo cb = callbacks.get(name);
        try {
            return cb.method.invoke(context, args);
        } catch (IllegalAccessException ex) {
            SystemConsole.error(getClass().getName(), ex);
            return null;
        } catch (IllegalArgumentException ex) {
            SystemConsole.error(getClass().getName(), ex);
            return null;
        } catch (InvocationTargetException ex) {
            SystemConsole.error(getClass().getName(), ex.getCause());
            return null;
        }
    }
    
    public Boolean isExposed(String method) {
        CallbackInfo info;
        if ((info = callbacks.get(method)) == null)
            return false;
        return info.exposed;
    }
    
    public Boolean isSupported(String method) {
        return callbacks.containsKey(method);
    }

    /**
     * Initialize session messages based on the class annotations
     * @param context 
     */
    public Session(Object context) {
        
        // Intiialize
        this.context = context;
        this.disposable = true;
        this.callbacks = new LinkedHashMap<String, CallbackInfo>();
        
        // Check for non-volatile sessions
        if (context.getClass().getAnnotation(NonVolatile.class) != null)
            disposable = false;
        
        for (Method m: context.getClass().getDeclaredMethods()) {
            
            // Register methods
            Asynchronous a = m.getAnnotation(Asynchronous.class);
            if (a != null) {
                // Lookup the alias
                String alias = m.getName();
                if (!a.alias().isEmpty())
                    alias = a.alias();
                
                // Register the callbacks
                callbacks.put(alias, new CallbackInfo(m, a.exposed()));
            }
            
            // Register constructor/destructor
            AsyncronousConstructor a2 = m.getAnnotation(AsyncronousConstructor.class);
            if (a2 != null)
                callbacks.put("__start__", new CallbackInfo(m, false));
            AsyncronousDestructor a3 = m.getAnnotation(AsyncronousDestructor.class);
            if (a3 != null)
                callbacks.put("__stop__", new CallbackInfo(m, false));
            
            
        }
    }

    /**
     * Create a blank session
     */
    public Session() {
        this.context = this;
        this.callbacks = new LinkedHashMap<String, Session.CallbackInfo>();
        this.disposable = true;
    }

}
