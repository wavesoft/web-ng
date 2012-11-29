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
package gr.wavesoft.webng.io;

import gr.wavesoft.webng.io.repos.Aether;
import gr.wavesoft.webng.io.cache.WebNGCache;
import gr.wavesoft.webng.io.repos.AetherCore;
import gr.wavesoft.webng.io.web.WebStreams;
import gr.wavesoft.webng.security.WebNGKeyStore;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class WebNGSystem {
    
    public static final boolean isOSX;
    public static final boolean isWindows;
    public static final boolean isLinux;
    
    public static MessageDigest SHA1Digest = null;

    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(WebNGSystem.class, "System");
    
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
        
        // Initialize Aether repository
        mkdirIfMissing(webNGdir+"/repos");
        Aether.Initialize(webNGdir+"/repos");
        //AetherCore.Initialize(webNGdir+"/repos");
        
        // Initialize JarIO
        WebNGIO.Initialize(webNGdir);
        
        // Setup System cache
        mkdirIfMissing(webNGdir+"/cache");
        WebNGCache.Initialize(webNGdir+"/cache");
        
        // Setup system security
        WebNGKeyStore.Initialize(webNGdir+"/keystore.db");
        
        // Setup streams
        WebStreams.Initialize();
        
        // Register default transports
        //WebStreamFactory.registerTransport(new HTTPTransport());
                
    }
    
    public static String SHA1Sum(String buffer) {
        if (SHA1Digest == null) {
            try {
                SHA1Digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException ex) {
                systemLogger.error("Unable to load SHA1 digest!");
                return null;
            }
        }
        try {
            return byteArray2Hex(SHA1Digest.digest(buffer.getBytes("UTF8")));
        } catch (UnsupportedEncodingException ex) {
            systemLogger.except(ex);
            return null;
        }
    }
    
    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
    
    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
      List<T> list = new ArrayList<T>(c);
      java.util.Collections.sort(list);
      return list;
    }
}
