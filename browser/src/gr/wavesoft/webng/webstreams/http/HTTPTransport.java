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
import gr.wavesoft.webng.security.WebNGHostnamVerifier;
import gr.wavesoft.webng.security.WebNGSSLSocketFactory;
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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author icharala
 */
public class HTTPTransport implements Transport {

    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(HTTPTransport.class, "HTTPTransport");
    
    @Override
    public Boolean isCompatibleFor(StreamRequest req) {
        return (req instanceof HTTPRequest);
    }

    @Override
    public Boolean isResourceModified(StreamRequest req, CacheItem tok) throws IOException {
        
        // Prepare head-only URL connection
        URLConnection connection = req.url.openConnection();
        ((HttpURLConnection)connection).setRequestMethod("HEAD");
        
        // If we have SSL, add our custom socket factory and hostname verifier
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setHostnameVerifier(WebNGHostnamVerifier.getDefault());
            ((HttpsURLConnection)connection).setSSLSocketFactory(new WebNGSSLSocketFactory());
        }

        // If we have customData, it means that the server responded with an ETag
        if (!tok.customData.isEmpty()) {

            // Check if it's modified since probe time
            connection.setRequestProperty("If-None-Match", tok.customData);

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
            
            // Prepare read-only URL connection
            URLConnection u = req.url.openConnection();
            u.setDoOutput(true);
            u.setReadTimeout(10000);
            
            // Specify request method (Currently it's only GET for R-Streams)
            ((HttpURLConnection)u).setRequestMethod("GET");
        
            // If we have SSL, add our custom socket factory and hostname verifier
            if (u instanceof HttpsURLConnection) {
                ((HttpsURLConnection)u).setHostnameVerifier(WebNGHostnamVerifier.getDefault());
                ((HttpsURLConnection)u).setSSLSocketFactory(new WebNGSSLSocketFactory());
            }
            
            // Establish connection
            u.connect();
            
            // Notify the endpoints that the stream is ready
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
