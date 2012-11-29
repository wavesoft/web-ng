/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.streams;

import java.io.InputStream;

/**
 *
 * @author icharala
 */
public interface Stream {
    
    /**
     * If specified the getOutputStream will return a stream capable
     * of reading data.
     */
    public static final int FEATURE_READ = 1;
    
    /**
     * If specified the getInputStream will return a stream capable
     * of writing data.
     */
    public static final int FEATURE_WRITE = 2;
    
    /**
     * If specified, someone can read and write from independent
     * channels. Otherwise someone must first use the input stream
     * to write something and then use the outputStream to read the
     * response.
     */
    public static final int FEATURE_FULL_DUPLEX = 4;
    
    /**
     * Return the features of the stream
     * 
     * @return Returns one or more FEATURE_ constants
     */
    public int getFeatures();
    
    /**
     * Return the input stream from which data can be sent.
     * @return 
     */
    public InputStream getInputStream();
    
    /**
     * Return the output stream from which data can be read.
     * @return 
     */
    public InputStream getOutputStream();
    
}
