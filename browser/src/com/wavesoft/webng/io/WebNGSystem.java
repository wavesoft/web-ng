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
    
    public static final boolean isOSX;
    public static final boolean isWindows;
    public static final boolean isLinux;
    
    static {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            isWindows = true;
            isOSX = false;
            isLinux = false;
        } else if (OS.contains("MAC")) {
            isWindows = false;
            isOSX = true;
            isLinux = false;
        } else if (OS.contains("NUX")) {
            isWindows = false;
            isOSX = false;
            isLinux = true;
        } else {
            isWindows = false;
            isOSX = false;
            isLinux = false;
        }
    }
    
    private static String getAppDataDir(String appName) {
        
        // Find the appropriate directory based ont he OS
        if (isWindows)
            return System.getenv("APPDATA") + '/' + appName;
        else if (isOSX)
            return System.getProperty("user.home") + "/Library/Application Support/" + appName;
        else if (isLinux)
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
