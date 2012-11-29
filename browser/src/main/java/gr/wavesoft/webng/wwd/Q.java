/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.wwd;

/**
 *
 * @author icharala
 */
public class Q {
    
    public static Q and(Q ... filters) {
        return null;
    }
    
    public static Q or(Q ... filters) {
        return null;
    }
    
    public static Q not(Q query) {
        return null;
    }
    
    public static Q v(Object value) {
        return new Qv(value);
    }
    
    public static Q ref(Class objectClass, String field) {
        return new Qref(objectClass, field);
    }
    
    public Q eq(Q what) {
        return this;
    }
    
    public Q ne(Q what) {
        return this;
    }
    
    public Q lt(Q what) {
        return this;
    }
    
    public Q le(Q what) {
        return this;
    }
    
    public Q gt(Q what) {
        return this;
    }
    
    public Q ge(Q what) {
        return this;
    }
    
    public Q like(Q what) {
        return this;
    }
    
}
