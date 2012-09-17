/*
 * WebNGSSLSocketFactory.java
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
package gr.wavesoft.webng.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author icharala
 */
public class WebNGSSLSocketFactory extends SSLSocketFactory {

    @Override
    public String[] getDefaultCipherSuites() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getSupportedCipherSuites() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(String string, int i) throws IOException, UnknownHostException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException, UnknownHostException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
