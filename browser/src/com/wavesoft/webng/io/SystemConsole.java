/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
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
