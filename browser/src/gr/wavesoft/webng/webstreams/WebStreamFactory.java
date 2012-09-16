/*
 * WebStreamFactory.java
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
package gr.wavesoft.webng.webstreams;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author icharala
 */
public class WebStreamFactory {
    
    public static final int DIRECTION_READ = 1;
    public static final int DIRECTION_WRITE = 2;
    
    public static List<Transport> transports;
    
    public static void Initialize() {
        transports = new ArrayList<Transport>();
    }
    
    public static void registerTransport(Transport t) {
        transports.add(t);
    }
    
    public static Transport getTransport(StreamRequest req, int directionFlags) {
        for (Transport t: transports) {
            if (t.isCompatibleFor(req)) {
                return t;
            }
        }
        return null;
    }
    
}
