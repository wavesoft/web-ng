/*
 * Database.java
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
package gr.wavesoft.webng.io;

import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class Database {
    
    private Connection conn = null;

    public Database(String database) {
        try {
            
            // Try to load the SQLite JDBC
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            conn = DriverManager.getConnection("jdbc:sqlite:"+database);

        } catch (ClassNotFoundException ex) {
            SystemConsole.error(ex);
            
        } catch (SQLException ex) {
            SystemConsole.error(ex);
            
        }
        
    }
    
    public void setupSchema(String table, String definition) {
        try {
            // Prepare statement for DB Creations
            Statement stat = conn.createStatement();
            
            // Setup table
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+table+" ("+definition+")");
            
            // Close statement
            stat.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        
    public ResultSet query(String query) {
        try {
            Statement stm = conn.createStatement();
            return stm.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public int update(String query) {
        try {
            Statement stm = conn.createStatement();
            return stm.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public static void assignArgStatements(PreparedStatement stm, Object[] args) {
        try {
            for (int i=1; i<=args.length; i++) {
                Object o = args[i-1];
                Class c = o.getClass();
                if (c.isArray() && byte.class.isAssignableFrom(c)) {
                    stm.setBytes(i, (byte[])o);
                } else if (Integer.class.isAssignableFrom(c)) {
                    stm.setInt(i, (Integer) o);
                } else if (Long.class.isAssignableFrom(c)) {
                    stm.setLong(i, (Long) o);
                } else if (Float.class.isAssignableFrom(c)) {
                    stm.setFloat(i, (Float) o);
                } else if (Double.class.isAssignableFrom(c)) {
                    stm.setDouble(i, (Double) o);
                } else if (String.class.isAssignableFrom(c)) {
                    stm.setString(i, (String) o);
                } else if (InputStream.class.isAssignableFrom(c)) {
                    stm.setBinaryStream(i, (InputStream) o);
                } else if (Blob.class.isAssignableFrom(c)) {
                    stm.setBlob(i, (Blob) o);
                } else if (Boolean.class.isAssignableFrom(c)) {
                    stm.setBoolean(i, (Boolean) o);
                } else if (Byte.class.isAssignableFrom(c)) {
                    stm.setByte(i, (Byte) o);
                } else if (Short.class.isAssignableFrom(c)) {
                    stm.setShort(i, (Short) o);
                } else if (Time.class.isAssignableFrom(c)) {
                    stm.setTime(i, (Time) o);
                } else if (Timestamp.class.isAssignableFrom(c)) {
                    stm.setTimestamp(i, (Timestamp) o);
                } else if (URL.class.isAssignableFrom(c)) {
                    stm.setURL(i, (URL) o);
                } else {
                    stm.setObject(i, o);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int update(String query, Object ... args) {
        try {
            PreparedStatement stm = conn.prepareStatement(query);
            assignArgStatements(stm, args);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public ResultSet query(String query, Object ... args) {
        try {
            PreparedStatement stm = conn.prepareStatement(query);
            assignArgStatements(stm, args);
            return stm.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public PreparedStatement prepareStatement(String query) {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public void transactionBegin() {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void transactionCommit() {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(WebNGIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
