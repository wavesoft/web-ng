/*
 * JarCache.java
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
package com.wavesoft.webng.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class JarCache {
    
    private Connection conn;

    private void setupDatabase() {
        try {
            Statement stat = conn.createStatement();
            
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS jars ("
                    + "id        INTEGER PRIMARY KEY,"
                    + "package   VARCHAR(1024),"
                    + "jar       VARCHAR(1024)"
                    + ")");
            
        } catch (SQLException ex) {
            Logger.getLogger(JarCache.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public JarCache(String directory, String database) {
        try {
            
            // Try to load the SQLite JDBC
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            conn = DriverManager.getConnection("jdbc:sqlite:"+database);
            
            // Setup database
            setupDatabase();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JarCache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(JarCache.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void getJarFor(String className) {
        
    }
    
}
