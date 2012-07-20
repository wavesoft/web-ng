/*
 * HeadButton.java
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
package com.wavesoft.webng.api;

import javax.swing.Icon;

/**
 *
 * @author icharala
 */
public abstract class HeadButton {
    
    /**
     * Return the icon you want the button to have
     * @return Icon The icon object
     */
    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/question.png"));
    } 
    
    /**
     * Return the tool tip for the button
     * @return String The tool tip text for the button
     */
    public String getToolTip() {
        return "";
    }
    
    /**
     * Button click handler
     */
    public abstract void buttonClicked();
    
}
