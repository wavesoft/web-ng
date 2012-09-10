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

/**
 *
 * @author icharala
 */
public class SystemIcons {
    
    public static final Image tabDefaultIcon;
    public static final Image tabErrorIcon;
    
    static {
        
        Image im1 = null;
        try {
            im1 = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/page-icon.png"));
        } catch (IOException ex) {
        }
        tabDefaultIcon = im1;
        
        Image im2 = null;
        try {
            im2 = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/error-icon.png"));
        } catch (IOException ex) {
        }
        tabErrorIcon = im2;

    }
    
}
