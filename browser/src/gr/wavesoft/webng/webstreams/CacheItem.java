/*
 * CacheItem.java
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
import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.io.WebNGIO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class CacheItem {
    
    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(CacheItem.class, "CacheToken");
    public String key;
    public Long id;
    
    private static final long DEFAULT_SOFT_TTL = 3600l; // 1 hour
    private static final long DEFAULT_HARD_TTL = 7 * 24 * DEFAULT_SOFT_TTL; // 1 week
    
    public static final int STATE_PENDING = 0;
    public static final int STATE_CACHED = 1;
    
    public Long dateProbed = 0l;
    public Long dateUpdated = 0l;
    public Long softTTL; 
    public Long hardTTL;
    public int state;
    public Long size;
    
    public String customDetails;

    public CacheItem(String key) {
        this.key = key;
        this.id = getID(key);
        if (this.id == null)
            this.id = allocate();
        if (this.id == null) {
            systemLogger.error("Unable to allocate new cache entry for key "+key);
            return;
        }
        load();
    }

    public CacheItem(Long id) {
        this.id = id;
        load();
    }
    
    private Long allocate() {
        try {
            // Create new
            WebNGIO.dbUpdate("INSERT INTO cache_store (key, state, probed, updated, hardttl, softttl) VALUES(?,?,?,?,?,?)", 
                    key, STATE_PENDING, 0, 0, DEFAULT_HARD_TTL, DEFAULT_SOFT_TTL);

            // Query for the ID
            ResultSet res = WebNGIO.dbQuery("SELECT id FROM cache_store ORDER BY id DESC LIMIT 0,1");
            if (res == null) return null;
            if (!res.next()) {
                systemLogger.error("Unable to detect the new cache_store entry's ID!");
                return null;
            }

            // Fetch the id
            return res.getLong("id");
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return null;
        }   
    }
    
    private Long getID(String key) {
        try {
            // Perform query
            ResultSet res = WebNGIO.dbQuery("SELECT id FROM cache_store WHERE key = ?", key);
            if (res == null) return null;
            if (!res.next()) return null;
            
            // Return the ID
            return res.getLong("id");
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
            return null;
        }     
    }
    
    private void load() {
        try {
            // Perform query
            ResultSet res = WebNGIO.dbQuery("SELECT * FROM cache_store WHERE id = ?", id);
            if (res == null) return;
            if (!res.next()) return;
            
            // Fetch info from the DB
            dateProbed = res.getLong("probed");
            dateUpdated = res.getLong("updated");
            state = res.getInt("state");
            softTTL = res.getLong("softttl");
            hardTTL = res.getLong("hardttl");
            key = res.getString("key");
            customDetails = res.getString("custom");
            size = res.getLong("size");
            
        } catch (SQLException ex) {
            systemLogger.except(ex);
        }
    }
    
    public void save() {
        if (this.id == null) return;
        WebNGIO.dbUpdate("UPDATE cache_store SET probed = ?, updated = ?, "
                + "state = ?, softttl = ?, hardttl = ?, key = ?, custom = ?, size = ? WHERE id = ?", 
                dateProbed, dateUpdated, state, softTTL, hardTTL, key, customDetails, size, id);
    }
    
    public void delete() {
        if (this.id == null) return;
        WebNGIO.dbUpdate("DELETE FROM cache_store WHERE id = ?", id);
    }
    
    public Boolean isExpired() {
        if (dateUpdated == 0l) return true;
        Long time = System.currentTimeMillis();
        return (dateUpdated+hardTTL*1000) < time;
    }
    
    public Boolean isWarm() {
        if (dateProbed == 0l) return false;
        Long time = System.currentTimeMillis();
        return (dateProbed+softTTL*1000) >= time;
    }
    
}
