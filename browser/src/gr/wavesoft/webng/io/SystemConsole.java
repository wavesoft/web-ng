/*
 * SystemConsole.java
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author icharala
 */
public class SystemConsole {
    
    public static byte LOG_ERROR = 4;
    public static byte LOG_WARNING = 3;
    public static byte LOG_INFO = 2;
    public static byte LOG_DEBUG = 1;

    private static ArrayList<SystemConsoleListener> listeners = new ArrayList<SystemConsoleListener>();
    
    public static void addConsoleListener(SystemConsoleListener l) {
        listeners.add(l);
    }
    
    public static void removeConsoleListener(SystemConsoleListener l) {
        listeners.remove(l);
    }
    
    public static class Logger {
        String className = "";
        String category = "";
        
        public Logger(Class c, String category) {
            this.className = c.getCanonicalName();
            this.category = category;
        }
        
        public void error(Object ... args) {
            SystemConsole.log(LOG_ERROR, className, category, stringify(args));
        }
        
        public void warn(Object ... args) {
            SystemConsole.log(LOG_WARNING, className, category, stringify(args));
        }
        
        public void warning(Object ... args) {
            SystemConsole.log(LOG_WARNING, className, category, stringify(args));
        }
        
        public void info(Object ... args) {
            SystemConsole.log(LOG_INFO, className, category, stringify(args));
        }
        
        public void debug(Object ... args) {
            SystemConsole.log(LOG_DEBUG, className, category, stringify(args));
        }
        
        public void except(Exception e) {
            SystemConsole.log(LOG_ERROR, className, category, stringify(new Object[]{e}));
        }
        
        public void exception(Exception e) {
            SystemConsole.log(LOG_ERROR, className, category, stringify(new Object[]{e}));
        }
        
    }
    
    public static void log(byte level, String source, String category, String message) {
        String ans;
        ans = "[" + DateFormat.getDateTimeInstance().format(new Date()) + "][";
        
        if (level == LOG_DEBUG) ans+="DEBUG";
        if (level == LOG_INFO) ans+="INFO";
        if (level == LOG_WARNING) ans+= "WARNING";
        if (level == LOG_ERROR) ans+="ERROR";
        
        ans += "][";
        ans += category;
        ans += "] " + source + ": ";
        ans += message;
        
        if (level < 3) {
            System.out.println(ans);
        } else {
            System.err.println(ans);
        }
        
        for (SystemConsoleListener l: listeners) {
            l.log(level, ans);
        }
    }
    
    private static String stringify(Object[] msg) {
        String ans = "";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        
        for (Object o: msg) {
            if (o == null) {
                ans += "[null]";
            } else if (o instanceof Exception) {
                os.reset();
                ((Exception) o).printStackTrace(ps);
                ans += ((Exception) o).getClass().getCanonicalName() + ": " + ((Exception) o).getMessage() + "\n" + os.toString();
            } else {
                ans += o.toString();
            }
        }
        return ans;
    }
    
    public static void debug(Object ... msg) {
        log(LOG_DEBUG, "", "General", stringify(msg));
    }
    
    public static void info(Object ... msg) {
        log(LOG_INFO, "", "General", stringify(msg));
    }
    
    public static void warn(Object ... msg) {
        log(LOG_WARNING, "", "General", stringify(msg));
    }
    
    public static void error(Object ... msg) {
        log(LOG_ERROR, "", "General", stringify(msg));
    }
    
}
