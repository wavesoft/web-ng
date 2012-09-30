/*
 * BrowserWindow.java
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
package gr.wavesoft.webng.api;
import java.awt.Image;

/**
 *
 * @author icharala
 */
public interface BrowserWindow {
    
    public void navigateTo(String url);
    public void navigateTo(WebViewNG view);
    public void refresh();
    
    public void setIcon(Image icon);
    public void setTitle(String title);
    public void setHeadButtons(HeadButton[] buttons);
    public void addWindowEventListener(WebViewEventListener l);
    
    
}
