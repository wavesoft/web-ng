/*
 * WebNGSecurityManager.java
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
package gr.wavesoft.webng.security;

import java.io.FileDescriptor;
import java.security.Permission;

/**
 *
 * @author icharala
 */
public class WebNGSecurityManager extends SecurityManager {

    // Tricky method to find the calling class
    private Class<?> getCallingClass() {
        return sun.reflect.Reflection.getCallerClass(3);        
    }
    
    @Override
    public void checkRead(FileDescriptor fd) {
        System.out.println(getCallingClass().toString()+ " wants to read FD "+fd.toString());
        //super.checkRead(fd);
    }

    @Override
    public void checkRead(String string) {
        System.out.println(getCallingClass().toString()+ " wants to read STR: "+string);
        //super.checkRead(string);
    }

    @Override
    public void checkRead(String string, Object o) {
        System.out.println(getCallingClass().toString()+ " wants to read STR: "+string + " and O:"+o.toString());
        //super.checkRead(string, o);
    }

    @Override
    public void checkPermission(Permission prmsn) {
        if (prmsn.getName().equals("accessClassInPackage.sun.reflect")) return;
        System.out.println(getCallingClass().toString()+ " wants permission: "+ prmsn.getName());
        //super.checkPermission(prmsn);
    }
    
}
