/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
