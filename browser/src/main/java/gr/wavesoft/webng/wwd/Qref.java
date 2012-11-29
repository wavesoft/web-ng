/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd;

/**
 *
 * @author icharala
 */
public class Qref extends Q {
    
    protected Class objectClass;
    protected String field;

    public Qref(Class objectClass, String field) {
        this.objectClass = objectClass;
        this.field = field;
    }
    
}
