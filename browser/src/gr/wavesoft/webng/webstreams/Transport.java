/*
 * Transport.java
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

/**
 *
 * @author icharala
 */
public interface Transport {
    
    /**
     * Check if the transport can serve the specified request
     * @param req
     * @return 
     */
    public Boolean isCompatibleFor(StreamRequest req);
    
    public Boolean isResourceModified(StreamRequest req, CacheItem tok);
    
    public void openRStream(StreamRequest req, RStreamCallback callback) throws UnsupportedOperationException;
    
    public void openWStream(StreamRequest req, WStreamCallback callback) throws UnsupportedOperationException;
    
    public void openRWStream(StreamRequest req, RWStreamCallback callback) throws UnsupportedOperationException;
    
}
