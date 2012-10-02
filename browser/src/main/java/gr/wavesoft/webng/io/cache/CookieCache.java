/*
 * CookieCache.java
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
package gr.wavesoft.webng.io.cache;

import gr.wavesoft.webng.io.Database;
import java.util.Date;
import java.util.List;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

/**
 *
 * @author icharala
 */
public class CookieCache implements CookieStore {

    private Database cacheDB;

    public CookieCache(Database cacheDB) {
        this.cacheDB = cacheDB;
    }
    
    public void addCookie(Cookie cookie) {
    }

    public List<Cookie> getCookies() {
        return null;
    }

    public boolean clearExpired(Date date) {
        return true;
    }

    public void clear() {
    }
    
}
