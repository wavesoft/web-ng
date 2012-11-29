/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.repos.wagons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.resource.Resource;

/**
 *
 * @author icharala
 */
public class SharedHTTPWagon extends StreamWagon {

    private String previousProxyExclusions;
    private String previousHttpProxyHost;
    private String previousHttpProxyPort;
    private HttpURLConnection putConnection;


        /**
     * Whether to use any proxy cache or not.
     * 
     * @plexus.configuration default="false"
     */
    private boolean useCache;

    /** @plexus.configuration */
    private Properties httpHeaders;

    /**
     * Builds a complete URL string from the repository URL and the relative path passed.
     * 
     * @param path the relative path
     * @return the complete URL
     */
    private String buildUrl( String path ) {
        final String repoUrl = getRepository().getUrl();
        path = path.replace( ' ', '+' );
        
        if ( repoUrl.charAt( repoUrl.length() - 1 ) != '/' ) {
            return repoUrl + '/' + path;
        }

        return repoUrl + path;
    }

    
    private void addHeaders( URLConnection urlConnection )
    {
        if ( httpHeaders != null )
        {
            for ( Iterator i = httpHeaders.keySet().iterator(); i.hasNext(); )
            {
                String header = (String) i.next();
                urlConnection.setRequestProperty( header, httpHeaders.getProperty( header ) );
            }
        }
    }

    
    @Override
    public void fillInputData(InputData inputData) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        Resource resource = inputData.getResource();
        try {
            URL url = new URL( buildUrl( resource.getName() ) );
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty( "Accept-Encoding", "gzip" );
            if ( !useCache ) {
                urlConnection.setRequestProperty( "Pragma", "no-cache" );
            }

            addHeaders( urlConnection );

            // TODO: handle all response codes
            int responseCode = urlConnection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_FORBIDDEN
                || responseCode == HttpURLConnection.HTTP_UNAUTHORIZED ) {
                throw new AuthorizationException( "Access denied to: " + buildUrl( resource.getName() ) );
            }

            InputStream is = urlConnection.getInputStream();
            String contentEncoding = urlConnection.getHeaderField( "Content-Encoding" );
            boolean isGZipped = contentEncoding == null ? false : "gzip".equalsIgnoreCase( contentEncoding );
            if ( isGZipped )
            {
                is = new GZIPInputStream( is );
            }
            inputData.setInputStream( is );
            resource.setLastModified( urlConnection.getLastModified() );
            resource.setContentLength( urlConnection.getContentLength() );
        }
        catch ( MalformedURLException e )
        {
            throw new ResourceDoesNotExistException( "Invalid repository URL", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new ResourceDoesNotExistException( "Unable to locate resource in repository", e );
        }
        catch ( IOException e )
        {
            throw new TransferFailedException( "Error transferring file: " + e.getMessage(), e );
        }

    }

    @Override
    public void fillOutputData(OutputData od) throws TransferFailedException {
        System.out.println("SharedHTTPWagon: fillOutputData"+od.toString());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void closeConnection() throws ConnectionException {
        System.out.println("SharedHTTPWagon: closeConnection");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void openConnectionInternal() throws ConnectionException, AuthenticationException {
        System.out.println("SharedHTTPWagon: openConnectionInternal");
        previousHttpProxyHost = System.getProperty( "http.proxyHost" );
        previousHttpProxyPort = System.getProperty( "http.proxyPort" );
        previousProxyExclusions = System.getProperty( "http.nonProxyHosts" );

        final ProxyInfo proxyInfo = getProxyInfo( "http", getRepository().getHost() );
        if ( proxyInfo != null )
        {
            System.setProperty( "http.proxyHost", proxyInfo.getHost() );
            System.setProperty( "http.proxyPort", String.valueOf( proxyInfo.getPort() ) );
            if ( proxyInfo.getNonProxyHosts() != null )
            {
                System.setProperty( "http.nonProxyHosts", proxyInfo.getNonProxyHosts() );
            }
            else
            {
                System.getProperties().remove( "http.nonProxyHosts" );
            }
        }
        else
        {
            System.getProperties().remove( "http.proxyHost" );
            System.getProperties().remove( "http.proxyPort" );
        }

        final boolean hasProxy = ( proxyInfo != null && proxyInfo.getUserName() != null );
        final boolean hasAuthentication = ( authenticationInfo != null && authenticationInfo.getUserName() != null );
        if ( hasProxy || hasAuthentication )
        {
            Authenticator.setDefault( new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    // TODO: ideally use getRequestorType() from JDK1.5 here...
                    if ( hasProxy && getRequestingHost().equals( proxyInfo.getHost() )
                        && getRequestingPort() == proxyInfo.getPort() )
                    {
                        String password = "";
                        if ( proxyInfo.getPassword() != null )
                        {
                            password = proxyInfo.getPassword();
                        }
                        return new PasswordAuthentication( proxyInfo.getUserName(), password.toCharArray() );
                    }

                    if ( hasAuthentication )
                    {
                        String password = "";
                        if ( authenticationInfo.getPassword() != null )
                        {
                            password = authenticationInfo.getPassword();
                        }
                        return new PasswordAuthentication( authenticationInfo.getUserName(), password.toCharArray() );
                    }

                    return super.getPasswordAuthentication();
                }
            } );
        }
        else
        {
            Authenticator.setDefault( null );
        }

    }
    
}
