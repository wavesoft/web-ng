/*
 * SystemViews.java
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
package gr.wavesoft.webng.ui.system;

import gr.wavesoft.webng.api.WebViewNG;
import gr.wavesoft.webng.render.WebViewError;
import gr.wavesoft.webng.render.WebViewHome;

/**
 *
 * @author icharala
 */
public class SystemViews {
    
    public static WebViewNG getSystemView(String name) {
        name = name.trim().toLowerCase();
        if ("webng:console".equals(name)) {
            return new WebViewConsole();
            
        } else if ("webng:certificates".equals(name)) {
            return new WebViewCertificates();
            
        } else if ("webng:home".equals(name)) {
            return new WebViewHome();
            
        } else if ("webng:test".equals(name)) {
            return new TestPanel();
            
            
        } else {
            return new WebViewError("Unknown system page", "You have enetered an unknown system page. Use webng:help for more information");
        }
    }
    
}
