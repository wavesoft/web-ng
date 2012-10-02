/*
 * WebStreams.java
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
package gr.wavesoft.webng.io.web;

import gr.wavesoft.webng.io.SystemConsole;
import gr.wavesoft.webng.io.cache.WebNGCache;
import gr.wavesoft.webng.security.WebNGKeyStore;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author icharala
 */
public class WebStreams {

    private static final SystemConsole.Logger systemLogger = new SystemConsole.Logger(WebStreams.class, "WebStreams");
    
    private static int  IO_BUFFER_SIZE = 4*1024;
    private static int  IO_PROGRESS_DELAY = 256; // Update every downloaded Mb
    
    private static SchemeRegistry schemeRegistry;
    private static PoolingClientConnectionManager connectionManager;
    private static HttpClient cachingClient;
    private static HttpClient httpClient;
    
    public static void Initialize() {
        
        schemeRegistry = new SchemeRegistry();
        
        // Register HTTP
        schemeRegistry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        
        // Register HTTPS with WebNG Extensions
        try {
            schemeRegistry.register(
                     new Scheme("https", 443, new SSLSocketFactory(WebNGKeyStore.getKeyStore())));
            
        } catch (NoSuchAlgorithmException ex) {
            systemLogger.except(ex);
        } catch (KeyManagementException ex) {
            systemLogger.except(ex);
        } catch (KeyStoreException ex) {
            systemLogger.except(ex);
        } catch (UnrecoverableKeyException ex) {
            systemLogger.except(ex);
        }

        // Setup connection manager
        connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        
        // Increase max total connection to 200
        connectionManager.setMaxTotal(200);
        
        // Increase default max connection per route to 20
        connectionManager.setDefaultMaxPerRoute(20);
        
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        connectionManager.setMaxPerRoute(new HttpRoute(localhost), 50);

        // Setup http client
        httpClient = new DefaultHttpClient(connectionManager);
        
        // Setup cookie cache
        //((DefaultHttpClient)httpClient).setCookieStore(WebNGCache.instanceCookieCache());
        
        // Setup cache
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(WebNGCache.config.MAX_ENTRIES_MEM);
        cacheConfig.setMaxObjectSize(WebNGCache.config.MAX_OBJECT_SIZE);
        
        cachingClient = new CachingHttpClient(httpClient, WebNGCache.instanceDiskCacheStorage(), cacheConfig);
        
    }
    
    public static void download(String url, WebRequestListener l) {
        try {
            GETThread t = new GETThread(cachingClient, new WebRequest(url).addListener(l));
            t.start();
        } catch (MalformedURLException ex) {
            l.httpFailed(ex);
        } catch (URISyntaxException ex) {
            l.httpFailed(ex);
        }
    }
    
    public static void download(String url, int responseType, WebRequestListener l) {
        try {
            GETThread t = new GETThread(cachingClient, new WebRequest(url).addListener(l).setResponseType(responseType));
            t.start();
        } catch (MalformedURLException ex) {
            l.httpFailed(ex);
        } catch (URISyntaxException ex) {
            l.httpFailed(ex);
        }
    }
    
    public static void download(String url, String file, WebRequestListener l) {
        try {
            GETThread t = new GETThread(cachingClient, new WebRequest(url, file).addListener(l));
            t.start();
        } catch (MalformedURLException ex) {
            l.httpFailed(ex);
        } catch (URISyntaxException ex) {
            l.httpFailed(ex);
        }
    }
    
    public static HttpResponse httpGET(URL url, HashMap<String,String> headers) throws IOException {
        try {
            
            // WebRequest connection
            ClientConnectionRequest connRequest = connectionManager.requestConnection(
                    new HttpRoute(new HttpHost(url.getHost(), url.getPort(), url.getProtocol())), null);
            
            ManagedClientConnection conn = connRequest.getConnection(10, TimeUnit.SECONDS);
            try {
                
                // Prepare request
                BasicHttpRequest request = new BasicHttpRequest("GET", url.getPath());
                
                // Setup headers
                if (headers != null) {
                    for (String k: headers.keySet()) {
                        request.addHeader(k, headers.get(k));
                    }
                }
                
                // Send request
                conn.sendRequestHeader(request);
                
                // Fetch response
                HttpResponse response = conn.receiveResponseHeader();
                conn.receiveResponseEntity(response);
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BasicManagedEntity managedEntity = new BasicManagedEntity(entity, conn, true);
                    // Replace entity
                    response.setEntity(managedEntity);
                }
                
                // Do something useful with the response
                // The connection will be released automatically 
                // as soon as the response content has been consumed
                return response;
                
            } catch (IOException ex) {
                // Abort connection upon an I/O error.
                conn.abortConnection();
                throw ex;
            }

        } catch (HttpException ex) {
            throw new IOException("HTTP Exception occured", ex);
        } catch (InterruptedException ex) {
            throw new IOException("InterruptedException", ex);
        } catch (ConnectionPoolTimeoutException ex) {
            throw new IOException("ConnectionPoolTimeoutException", ex);
        }
        
    }
    
    protected static void handleResponse(HttpResponse response, WebRequest request) {
        
        // Handle error responses
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            Exception ex = new IOException("Unable to fetch the specified resource");
            for (WebRequestListener l: request.listeners) {
                l.httpFailed(ex);
            }
            return;
        }
        
        // Process proper response
        WebResponse r = null;
        try {
            
            // Fetch some common info
            long bTotal=entity.getContentLength();
            
            // Notify beginning of download
            for (WebRequestListener l: request.listeners) {
                l.httpProgress(0l, bTotal);
            }
            
            // Process response based on it's kind
            if (request.responseType == WebRequest.RESPONSE_FILE) {
                systemLogger.info("Downloading ",request.url," to ",request.file);
                
                // Download to file
                InputStream is = entity.getContent();
                FileOutputStream fos = new FileOutputStream(request.file);
                byte[] b = new byte[IO_BUFFER_SIZE];  
                int read;
                int count = 0;
                long bs=0;
                while ((read = is.read(b)) != -1) {  
                    fos.write(b, 0, read);
                    bs+=read;
                    
                    // Skip unknown sizes from progress update
                    if (bTotal > 0) {
                        if (++count >= IO_PROGRESS_DELAY) {
                            count=0;
                            for (WebRequestListener l: request.listeners) {
                                l.httpProgress(bs, bTotal);
                            }
                        }
                    }
                    
                }
                
                // Close streams
                fos.close();
                is.close();
                
                // On unknown size, update bTotal
                bTotal = bs;
                
                // Return resulting response
                r = new FileResponse(request, request.file);
                
                
            } else if (request.responseType == WebRequest.RESPONSE_STREAM) {
                systemLogger.info("Fetching input stream from ",request.url);

                // Just fetch the source stream
                r = new StreamResponse(request, entity.getContent());
                
            } else {
                systemLogger.info("Buffering response from ",request.url);
                
                // Otherwise, the default is to fetch a buffered response
                r = new BufferedResponse(request, EntityUtils.toString(entity, "UTF8"));
                
            }
            
            // Populate common fields
            r.statusCode = response.getStatusLine().getStatusCode();
            r.statusText = response.getStatusLine().getReasonPhrase();
            r.contentSize = entity.getContentLength();
            r.contentType = entity.getContentType().getValue();
            for (Header h: response.getAllHeaders()) {
                r.headers.put(h.getName(), h.getValue());
            }
            
            // Notify completion
            for (WebRequestListener l: request.listeners) {
                l.httpProgress(bTotal,bTotal);
                l.httpCompleted(r);
            }
            
        } catch (IOException ex) {
            systemLogger.except(ex);
            for (WebRequestListener l: request.listeners) {
                l.httpFailed(ex);
            }
            
        } catch (ParseException ex) {
            systemLogger.except(ex);
            for (WebRequestListener l: request.listeners) {
                l.httpFailed(ex);
            }
            
        }
        try {
            // Ensure the connection gets released to the manager
            EntityUtils.consume(entity);
        } catch (IOException ex) {
            systemLogger.except(ex);
        }
    }
    
}
