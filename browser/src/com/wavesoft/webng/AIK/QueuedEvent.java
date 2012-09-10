/*
 * QueuedEvent.java
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
package com.wavesoft.webng.AIK;

/**
 *
 * @author icharala
 */
public class QueuedEvent {
    Session     session;
    String      message;
    Object[]    arguments;
    Object      response;

    public QueuedEvent() {
        session = null;
        message = null;
        arguments = null;
        response = null;
    }

    public QueuedEvent(Session session, String message, Object[] arguments) {
        this.session = session;
        this.message = message;
        this.arguments = arguments;
    }    
    
    public QueuedEvent(QueuedEvent event) {
        this.session = event.session;
        this.message = event.message;
        this.arguments = event.arguments;
    }
    
    public void expandArguments(Object[] additionalArguments) {
        // Clone + resize arguments
        Object[] args = new Object[arguments.length + additionalArguments.length];
        if (arguments.length > 0)
            System.arraycopy(arguments, 0, args, 0, arguments.length);
        if (additionalArguments.length > 0)
            System.arraycopy(additionalArguments, 0, args, arguments.length, additionalArguments.length);
        
        // Replace arguments
        arguments = args;
    }
    
    public Object invoke() {
        return response = session.invoke(message, arguments);
    }
    
}
