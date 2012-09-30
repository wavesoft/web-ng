/*
 * HTTPStreams.java
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
package gr.wavesoft.webng.io.http;

import gr.wavesoft.webng.io.http.cache.NGDiskCache;
import gr.wavesoft.webng.io.http.cache.NGCacheConfig;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
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

/**
 *
 * @author icharala
 */
public class HTTPStreams {

    private static SchemeRegistry schemeRegistry;
    private static PoolingClientConnectionManager connectionManager;
    private static HttpClient cachingClient;
    private static HttpClient httpClient;
    
    private static void setupCachedClient(NGCacheConfig cfg) {
        
        /*
        
        // Prepare Ehcache's Cache Manager
        Configuration cmConfig = new Configuration();
        cmConfig.setDynamicConfig(true);
        
        DiskStoreConfiguration cmDisk = new DiskStoreConfiguration();
        cmDisk.setPath(cfg.cacheDir);
        cmConfig.addDiskStore(cmDisk);
        
        CacheManager cm = CacheManager.newInstance(cmConfig);
        
        Cache cDefault = new Cache(new CacheConfiguration("ng-cache", 0)
                .maxEntriesLocalDisk(1000)
                );
        
        cm.addCache(cDefault);
         */
        
        /*
        Configuration ehConfig = new Configuration();
        DiskStoreConfiguration ehDiskConfig = new DiskStoreConfiguration();
        
        CacheConfiguration ehDefault = new CacheConfiguration("default", 50);
        
        ehDiskConfig.setPath(cacheDir);
        
        ehDefault.setMaxBytesLocalDisk(50*1048576l); // 50Mb
        ehDefault.setMaxBytesLocalHeap(16*1024l); // 16Kb
        
        ehConfig.addDiskStore(ehDiskConfig);
        ehConfig.addDefaultCache(ehDefault);
        
        CacheManager cm = CacheManager.getInstance();
        */
        
        // Instantiate ehcache
        //Ehcache c = cm.getEhcache("ng-cache");
        
        // Setup caching client instance
        //cachingClient = new CachingHttpClient(httpClient, new EhcacheHttpCacheStorage(c), cacheConfig);
        
        // Prepare httpClient config
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSize(16*1024);

        cachingClient = new CachingHttpClient(httpClient, new NGDiskCache(cfg), cacheConfig);
        
    }
    
    public static void Initialize(String cache) {
        
        schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(
                 new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

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
        
        // Setup cache
        setupCachedClient(new NGCacheConfig(cache));
    }
    
    public static void download(String url, HTTPRequestListener l) {
        try {
            HTTPGetThread t = new HTTPGetThread(cachingClient, new HTTPRequest(url).setListener(l));
            t.start();
        } catch (MalformedURLException ex) {
            l.httpFailed(ex);
        } catch (URISyntaxException ex) {
            l.httpFailed(ex);
        }
    }
    
    public static HttpResponse httpGET(URL url, HashMap<String,String> headers) throws IOException {
        try {
            
            // Request connection
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
    
}
