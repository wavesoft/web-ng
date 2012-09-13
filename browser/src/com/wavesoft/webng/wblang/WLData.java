/*
 * WLData.java
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
package com.wavesoft.webng.wblang;

import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.io.AsyncEventListener;
import com.wavesoft.webng.io.WebNGIO;
import com.wavesoft.webng.render.WebViewDefault;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class WLData {
    
    protected enum DataType {
        SCALAR, ARRAY, HASH, UNKNOWN
    }
    
    protected WLData parent = null;
    protected Object data;
    protected DataType dataType;
    
    protected WLData(Object data, WLData parent) {
        this.parent = parent;
        set(data);
    }

    protected WLData() {
        data = null;
        dataType = DataType.SCALAR;
    }
    
    public WLData getRoot() {
        if (parent == null) {
            return this;
        } else {
            return parent.getRoot();
        }
    }
    
    public URL getNodeURL() {
        if (parent != null) {
            return parent.getNodeURL();
        }
        return null;
    }

    public String getAttribute(String name) {
        Object value;
        
        // If we are not HASH, return default
        if (dataType != DataType.HASH) {
            return WL.getDefaultAttribute(name);
        } 
        
        // If we contain it, return it
        else if ( (value = ((LinkedHashMap<String,Object>) value()).get("~"+name)) != null ) {
            return value.toString();
            
        } 
        
        // Not found, return default
        else {
            return WL.getDefaultAttribute(name);
        }
    }
    
    public WLData encapsulateObject(Object o) {
        if (o instanceof LinkedHashMap) {
            LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) o;
            if (map.containsKey("~src")) {
                URL url = getNodeURL();
                if (url == null) try {
                    url = new URL(map.get("~src").toString());
                } catch (MalformedURLException ex) {
                    return null;
                }
                try {
                    return new WLRemoteData(new URL(url, map.get("~src").toString()), o, this);
                } catch (MalformedURLException ex) {
                    return null;
                }
            } else {
                return new WLData(o, this);
            }
        } else if (o instanceof WLData) {
            return (WLData) o;
        } else {
            return new WLData(o, this);
        }
    }
    
    public void set(Object data) {
        if (data == null) {
            this.data = null;
            
        } else if (data instanceof ArrayList) {
            // Convert array lists to proxy objects
            ArrayList<Object> list = new ArrayList<Object>();
            for (Object o: (ArrayList<Object>) data) {
                list.add(encapsulateObject(o));
            }
            this.data = list;
            dataType = DataType.ARRAY;
        } else if (data instanceof LinkedHashMap) {
            // Convert linked hash maps to proxy objects
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            for (String s: ((LinkedHashMap<String,Object>) data).keySet()) {
                map.put(s, encapsulateObject(((LinkedHashMap<String,Object>) data).get(s)));
            }
            this.data = map;
            dataType = DataType.HASH;
        } else {
            // Otherwise leave it as-is
            this.data = data;
            dataType = DataType.SCALAR;
        }
    }
    
    public Object value() {
        return data;
    }
    
    public ArrayList<WLData> getArray() {
        ArrayList<WLData> value;
        if (dataType == DataType.ARRAY) {
            value = (ArrayList<WLData>) data;
        } else if (dataType == DataType.UNKNOWN) {
            value = (ArrayList<WLData>) value();
            if (!(value instanceof ArrayList)) return null;
        } else {
            return null;
        }
        return value;
    }
    
    public LinkedHashMap<String, WLData> getHashMap() {
        LinkedHashMap<String, WLData> value;
        if (dataType == DataType.HASH) {
            value = (LinkedHashMap<String, WLData>) data;
        } else if (dataType == DataType.UNKNOWN) {
            value = (LinkedHashMap<String, WLData>) value();
            if (!(value instanceof LinkedHashMap)) return null;
        } else {
            return null;
        }
        return value;
    }
    
    public String getString() {
        return value().toString();
    }
    
    public Object get(String key, Object defaultValue) {
        Object value = get(key);
        if (value == null) return defaultValue;
        return value;
    }
    
    public Object get(String key) {
        LinkedHashMap<String, WLData> value;
        if (dataType == DataType.HASH) {
            value = (LinkedHashMap<String, WLData>) data;
        } else if (dataType == DataType.UNKNOWN) {
            value = (LinkedHashMap<String, WLData>) value();
            if (!(value instanceof LinkedHashMap)) return null;
        } else {
            return null;
        }
        return value.get(key).value();
    }
    
    public ArrayList<WLData> getArray(String key) {
        LinkedHashMap<String, WLData> value;
        if (dataType == DataType.HASH) {
            value = (LinkedHashMap<String, WLData>) data;
        } else if (dataType == DataType.UNKNOWN) {
            value = (LinkedHashMap<String, WLData>) value();
            if (!(value instanceof LinkedHashMap)) return null;
        } else {
            return null;
        }
        WLData p = value.get(key);
        if (p == null) return null;
        return p.getArray();
    }
    
    public LinkedHashMap<String, WLData> getHashMap(String key) {
        LinkedHashMap<String, WLData> value;
        if (dataType == DataType.HASH) {
            value = (LinkedHashMap<String, WLData>) data;
        } else if (dataType == DataType.UNKNOWN) {
            value = (LinkedHashMap<String, WLData>) value();
            if (!(value instanceof LinkedHashMap)) return null;
        } else {
            return null;
        }
        WLData p = value.get(key);
        if (p == null) return null;
        return p.getHashMap();
    }
    

    @Override
    public String toString() {
        return value().toString();
    }
    
    public WebViewNG getView() {
        String view = getAttribute("view");
        return WebNGIO.viewFromClassName(view);
    }
    
    public void getView(AsyncEventListener callback) {
        String view = getAttribute("view");
        WebNGIO.viewFromClassName(view, callback);
    }
    
    public void invalidate() {
        // Invalidate child nodes
        if (dataType == DataType.ARRAY) {
            for (WLData d: (ArrayList<WLData>) data) {
                d.invalidate();
            }
        } else if (dataType == DataType.HASH) {
            for (String s: ((LinkedHashMap<String, WLData>) data).keySet() )  {
                ((LinkedHashMap<String, WLData>) data).get(s).invalidate();
            }
        }
    }
    
}
