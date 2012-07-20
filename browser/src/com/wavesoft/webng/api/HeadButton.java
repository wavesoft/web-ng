/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.api;

import javax.swing.Icon;

/**
 *
 * @author icharala
 */
public abstract class HeadButton {
    
    /**
     * Return the icon you want the button to have
     * @return Icon The icon object
     */
    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/question.png"));
    } 
    
    /**
     * Return the tool tip for the button
     * @return String The tool tip text for the button
     */
    public String getToolTip() {
        return "";
    }
    
    /**
     * Button click handler
     */
    public abstract void buttonClicked();
    
}
