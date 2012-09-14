/*
 * PublicKeyEventManager.java
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
package gr.wavesoft.webng.io;

import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author icharala
 */
public class PublicKeyEventManager extends DefaultKeyboardFocusManager {

    private List<Integer> trapKeys;

    public PublicKeyEventManager(Integer[] trapKeys) {
        this.trapKeys = Arrays.asList(trapKeys);
    }
    
    private void dispatchToParents(Component target, KeyEvent ke) {
        Component c = target;
        while (c != null) {
            if (c instanceof PublicKeyEventListener) {
                PublicKeyEventListener l = (PublicKeyEventListener) c;
                
                // Handle event
                if (ke.getID() == KeyEvent.KEY_PRESSED) {
                    l.publicKeyPressed(ke);
                } else if (ke.getID() == KeyEvent.KEY_RELEASED) {
                    l.publicKeyReleased(ke);
                }
                
                // Return if consumed
                if (ke.isConsumed()) return;
            }
            c = c.getParent();
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent ke) {

        // Pick a meta key based on the (assumed) keyboard layout
        boolean metaKey = false;
        if (WebNGSystem.isOSX) {
            metaKey = ke.isMetaDown();
        } else {
            metaKey = ke.isControlDown();
        }

        // Handle some special keys
        if (metaKey && trapKeys.contains(ke.getKeyCode())) {
            dispatchToParents(getFocusOwner(), ke);
        }

        // Otherwise, passthru
        return super.dispatchKeyEvent(ke);
    }
    
}
