/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
