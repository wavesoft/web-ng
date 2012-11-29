/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.repos.wagons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;

/**
 *
 * @author icharala
 */
public class WebNGHTTP implements Wagon {
    
    private Wagon wrapped;
    private Repository repos;
    private Boolean interactive = false;
    private AuthenticationInfo authInfo;
    private ProxyInfo proxyInfo;
    private ProxyInfoProvider proxyInfoProvider;
    
    private ArrayList<TransferListener> transferListeners;
    private ArrayList<SessionListener> sessionListeners;
    private int timeout = 1;

    public WebNGHTTP() {

        transferListeners = new ArrayList<TransferListener>();
        sessionListeners = new ArrayList<SessionListener>();
    }
    
    private ProxyInfo getProxyInfo(String url) {
        if (proxyInfo != null) {
            return proxyInfo;
        } else if (proxyInfoProvider != null) {
            return proxyInfoProvider.getProxyInfo(url);
        } else {
            return null;
        }
    }

    public void get(String string, File file) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println("WebNGHTTP: Get "+string+" to "+file.toString());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean getIfNewer(String string, File file, long l) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println("WebNGHTTP: Get if newer "+string+" to "+file.toString() +", time: "+l);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void put(File file, String string) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println("WebNGHTTP: Put "+string+" from "+file.toString());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void putDirectory(File file, String string) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println("WebNGHTTP: PutDirectory "+string+" from "+file.toString());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean resourceExists(String string) throws TransferFailedException, AuthorizationException {
        System.out.println("WebNGHTTP: resourceExists "+string);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List getFileList(String string) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        System.out.println("WebNGHTTP: getFileList "+string);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean supportsDirectoryCopy() {
        System.out.println("WebNGHTTP: supportsDirectoryCopy");
        return false;
    }

    public Repository getRepository() {
        System.out.println("WebNGHTTP: getRepository");
        return repos;
    }

    public void connect(Repository rpstr) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString());
        repos = rpstr;
        authInfo = null;
        proxyInfo = null;
        proxyInfoProvider = null;
    }

    public void connect(Repository rpstr, ProxyInfo pi) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString()+", ProxyInfo="+pi.toString());
        repos = rpstr;
        authInfo = null;
        proxyInfo = pi;
        proxyInfoProvider = null;
    }

    public void connect(Repository rpstr, ProxyInfoProvider pip) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString()+", ProxyInfoProvider="+pip.toString());
        repos = rpstr;
        authInfo = null;
        proxyInfo = null;
        proxyInfoProvider = pip;
    }

    public void connect(Repository rpstr, AuthenticationInfo ai) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString()+", AuthenticationInfo="+ai.toString());
        repos = rpstr;
        authInfo = ai;
        proxyInfo = null;
        proxyInfoProvider = null;
    }

    public void connect(Repository rpstr, AuthenticationInfo ai, ProxyInfo pi) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString()+", AuthenticationInfo="+ai.toString()+", ProxyInfo="+pi.toString());
        repos = rpstr;
        authInfo = ai;
        proxyInfo = pi;
    }

    public void connect(Repository rpstr, AuthenticationInfo ai, ProxyInfoProvider pip) throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: connect "+rpstr.toString()+", AuthenticationInfo="+ai.toString()+", ProxyInfoProvider="+pip.toString());
        repos = rpstr;
        authInfo = ai;
        proxyInfo = null;
        proxyInfoProvider = pip;
    }

    public void openConnection() throws ConnectionException, AuthenticationException {
        System.out.println("WebNGHTTP: openConnection");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect() throws ConnectionException {
        System.out.println("WebNGHTTP: disconnect");
    }

    public void setTimeout(int i) {
        System.out.println("WebNGHTTP: setTimeout="+i);
        timeout = i;
    }

    public int getTimeout() {
        System.out.println("WebNGHTTP: getTimeout");
        return timeout;
    }

    public void addSessionListener(SessionListener sl) {
        System.out.println("WebNGHTTP: addSessionListener");
        sessionListeners.add(sl);
    }

    public void removeSessionListener(SessionListener sl) {
        System.out.println("WebNGHTTP: removeSessionListener");
        sessionListeners.remove(sl);
    }

    public boolean hasSessionListener(SessionListener sl) {
        System.out.println("WebNGHTTP: hasSessionListener");
        return sessionListeners.contains(sl);
    }

    public void addTransferListener(TransferListener tl) {
        System.out.println("WebNGHTTP: addTransferListener");
        transferListeners.add(tl);
    }

    public void removeTransferListener(TransferListener tl) {
        System.out.println("WebNGHTTP: removeTransferListener");
        transferListeners.remove(tl);
    }

    public boolean hasTransferListener(TransferListener tl) {
        System.out.println("WebNGHTTP: hasTransferListener");
        return transferListeners.contains(tl);
    }

    public boolean isInteractive() {
        System.out.println("WebNGHTTP: isInteractive");
        return interactive;
    }

    public void setInteractive(boolean bln) {
        System.out.println("WebNGHTTP: setInteractive="+bln);
        interactive = bln;
    }
    
}
