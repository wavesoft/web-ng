/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author icharala
 */
public class SystemIcons {
    
    public static final Image tabDefaultIcon;
    public static final Image tabErrorIcon;
    
    static {
        
        Image im1 = null;
        try {
            im1 = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/page-icon.png"));
        } catch (IOException ex) {
        }
        tabDefaultIcon = im1;
        
        Image im2 = null;
        try {
            im2 = ImageIO.read(SystemIcons.class.getResource("/com/wavesoft/webng/resources/error-icon.png"));
        } catch (IOException ex) {
        }
        tabErrorIcon = im2;

    }
    
}
