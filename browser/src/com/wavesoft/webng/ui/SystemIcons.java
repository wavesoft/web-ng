/*
 * SystemIcons.java
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
package com.wavesoft.webng.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;

/**
 *
 * @author icharala
 */
public class SystemIcons {
    
    public static final Image tabIconDefault;
    public static final Image tabIconError;
    public static final Image tabIconConsole;

    public static final Icon iconError;
    public static final Icon iconWarn;
    public static final Icon iconInfo;
    public static final Icon iconDebug;

    static {
        
        Image im = null;
        try {
            im = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/page-icon.png"));
        } catch (IOException ex) {
        }
        tabIconDefault = im;
        
        im = null;
        try {
            im = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/error-icon.png"));
        } catch (IOException ex) {
        }
        tabIconError = im;
        
        im = null;
        try {
            im = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/console-icon.png"));
        } catch (IOException ex) {
        }
        tabIconConsole = im;

        iconError =     new javax.swing.ImageIcon(SystemIcons.class.getResource("/com/wavesoft/webng/resources/icons/error.png"));
        iconWarn =      new javax.swing.ImageIcon(SystemIcons.class.getResource("/com/wavesoft/webng/resources/icons/warning.png"));
        iconInfo =      new javax.swing.ImageIcon(SystemIcons.class.getResource("/com/wavesoft/webng/resources/icons/info.png"));
        iconDebug =     new javax.swing.ImageIcon(SystemIcons.class.getResource("/com/wavesoft/webng/resources/icons/debug.png"));
        
    }
    
}
