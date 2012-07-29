/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

import com.wavesoft.webng.api.WebViewNG;

/**
 *
 * @author icharala
 */
public class JarIO {
    public static JarCache jarCache;
    public static JarLoader jarLoader;

    public static void Initialize(String webNGdir) {
        
        // Create cache and jar loader
        jarCache = new JarCache(webNGdir+"/jarbox", webNGdir+"/data.sqlite3");
        jarLoader = new JarLoader();

    }
    
    public Object getInstance(String name, Class requireType) {
        
        return null;
    }
    
    
}
