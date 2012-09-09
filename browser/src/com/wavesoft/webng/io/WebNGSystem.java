/*
 * WebNGSystem.java
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
package com.wavesoft.webng.io;

import java.io.File;

/**
 *
 * @author icharala
 */
public class WebNGSystem {
    
    private static String getAppDataDir(String appName) {
        String OS = System.getProperty("os.name").toUpperCase();
        
        // Find the appropriate directory based ont he OS
        if (OS.contains("WIN"))
            return System.getenv("APPDATA") + '/' + appName;
        else if (OS.contains("MAC"))
            return System.getProperty("user.home") + "/Library/Application Support/" + appName;
        else if (OS.contains("NUX"))
            return System.getProperty("user.home") + "/." + appName;
        
        // Return linux-like default
        return System.getProperty("user.dir") + "/." + appName;
    }
    
    public static void mkdirIfMissing(String path) {
        File dir = new File(path);
        if (!dir.isDirectory())
            dir.mkdir();        
    }
    
    public static void Initialize() {
        
        // Get/Make user directory
        String webNGdir = getAppDataDir("WebNGBrowser");
        mkdirIfMissing(webNGdir);
        mkdirIfMissing(webNGdir+"/jarbox");
        
        // Initialize JarIO
        WebNGIO.Initialize(webNGdir);
                
    }
    
}
