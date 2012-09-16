/*
 * HTTPTransport.java
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
package gr.wavesoft.webng.webstreams.http;
import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.webstreams.CacheItem;
import gr.wavesoft.webng.webstreams.RStreamCallback;
import gr.wavesoft.webng.webstreams.RWStreamCallback;
import gr.wavesoft.webng.webstreams.StreamRequest;
import gr.wavesoft.webng.webstreams.Transport;
import gr.wavesoft.webng.webstreams.WStreamCallback;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icharala
 */
public class HTTPTransport implements Transport {

    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(HTTPTransport.class, "HTTPTransport");
    
    /*
    private static final SimpleDateFormat gmtReader;
    private static final SimpleDateFormat gmtParser;

    static {
        gmtReader = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        gmtReader.setTimeZone(TimeZone.getTimeZone("GMT"));
        gmtParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        gmtParser.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String millisToGMT(Long millis) {
        // gmtReader has no timezone in order to place a string "GMT"
        return gmtReader.format(millis)+" GMT";
    }
    
    public static Long GMTToMillis(String GMTDate) {
        // Properly format timezone
        if (GMTDate.endsWith(" GMT"))
            GMTDate = GMTDate.substring(0, GMTDate.length()-4)+" -0000";
        
        // Try to parse the string given
        try {
            Date d = gmtReader.parse(GMTDate);
            return d.getTime();
        } catch (ParseException ex) {
            systemLogger.except(ex);
            return 0l;
        }
    }
     */
    
    @Override
    public Boolean isCompatibleFor(StreamRequest req) {
        return (req instanceof HTTPRequest);
    }

    @Override
    public Boolean isResourceModified(StreamRequest req, CacheItem tok) throws IOException {
        URLConnection connection = req.url.openConnection();
        ((HttpURLConnection)connection).setRequestMethod("HEAD");

        // If we have customData, it means that the server responded with an ETag
        if (!tok.customDetails.isEmpty()) {

            // Check if it's modified since probe time
            connection.setRequestProperty("If-None-Match", tok.customDetails);

        // Otherwise use classic Last-Modified cache validation
        } else {

            // Check if it's modified since probe time
            connection.setIfModifiedSince(tok.dateProbed);

        }

        // Return TRUE if response is not HTTP_NOT_MODIFIED
        int code = ((HttpURLConnection)connection).getResponseCode();
        return (code != HttpURLConnection.HTTP_NOT_MODIFIED);
            
    }

    @Override
    public void openRStream(StreamRequest req, RStreamCallback callback) throws UnsupportedOperationException {
        try {
            URLConnection u = req.url.openConnection();
            u.setDoOutput(true);
            u.setReadTimeout(10000);
            ((HttpURLConnection)u).setRequestMethod("GET");
            
            u.connect();
            
            callback.streamReady(u.getInputStream(), new HTTPResponseInfo((HttpURLConnection)u));
            
        } catch (IOException ex) {
            systemLogger.except(ex);
            callback.streamFailed(ex);
        }
    }

    @Override
    public void openWStream(StreamRequest req, WStreamCallback callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void openRWStream(StreamRequest req, RWStreamCallback callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    
}
