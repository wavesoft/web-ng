/*
 * DownloadThread.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 *
 * @author icharala
 */
public class DownloadThread {
    
    public void download() throws MalformedURLException, ProtocolException, IOException {
        
      HttpURLConnection connection = null;
      OutputStreamWriter wr = null;
      BufferedReader rd  = null;
      StringBuilder sb = null;
      String line = null;
    
      URL serverAddress = null;
    
      serverAddress = new URL("http://localhost");
      //set up out communications stuff
      connection = null;

      //Set up the initial connection
      connection = (HttpURLConnection)serverAddress.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      connection.setReadTimeout(10000);

      connection.connect();

      //get the output stream writer and write the output to the server
      //not needed in this example
      //wr = new OutputStreamWriter(connection.getOutputStream());
      //wr.write("");
      //wr.flush();

      //read the result from the server
      rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      sb = new StringBuilder();

      while ((line = rd.readLine()) != null)
      {
          sb.append(line + '\n');
      }

      System.out.println(sb.toString());

      //close the connection, set all objects to null
      connection.disconnect();
      rd = null;
      sb = null;
      wr = null;
      connection = null;

    }
    
}
