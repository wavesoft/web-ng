/*
 * RoundedButton.java
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
 * Created on Oct 2, 2012, 3:07:03 AM
 * 
 */
package gr.wavesoft.webng.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import javax.swing.JComponent;

/**
 *
 * @author icharala
 */
public class RoundedButton extends JComponent {

    int state = 0;
    private static int STATE_NONE = 0;
    private static int STATE_HOVER = 1;
    private static int STATE_DOWN = 2;
    private static int BORDER_RADIUS = 6;
    
    private String text = "";
    private Color textColor = Colorset.COLOR_DARK_TEXT;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    
    public ActionListener[] getActionListeners() {
        return getListeners(ActionListener.class);
    }
    
    private void fireActionPerformed(ActionEvent ae) {
        for (ActionListener l: getActionListeners()) {
            l.actionPerformed(ae);
        }
    }
    
    /** Creates new form RoundedButton */
    public RoundedButton() {
        initComponents();
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs);
        
        // Prepare shape
        Graphics2D g2 = (Graphics2D) grphcs;
        int width = getWidth()-2;
        int height = getHeight()-2;
        GradientPaint gp = null;
        
        // Enable antialias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render frame
        if (state == STATE_NONE) {
            
            // Draw shadow
            g2.setColor(Colorset.COLOR_LIGHT_SHADOW);
            g2.drawRoundRect(1, 1, width, height, BORDER_RADIUS, BORDER_RADIUS);

            // Draw fill
            gp = new GradientPaint(
                0f, -height/4, Colorset.G_SEL_TOP,
                0f, height, Colorset.G_SEL_BOTTOM );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
                
            // Draw frame
            g2.setColor(Colorset.COLOR_SHADOW);
            g2.drawRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
            
        } else if (state == STATE_HOVER) {

            // Draw shadow
            g2.setColor(Colorset.COLOR_LIGHT_SHADOW);
            g2.drawRoundRect(1, 1, width, height, BORDER_RADIUS, BORDER_RADIUS);

            // Draw fill
            gp = new GradientPaint(
                0f, height/8, Colorset.G_SEL_TOP,
                0f, height, Colorset.G_SEL_BOTTOM );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
                
            // Draw frame
            g2.setColor(Colorset.COLOR_SHADOW);
            g2.drawRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
                
        } else if (state == STATE_DOWN) {
            
            // Draw dark fill
            gp = new GradientPaint(
                0f, 0f, Colorset.COLOR_LIGHT_SHADOW,
                0f, height, Colorset.G_UNSEL_BOTTOM );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
            
            // Draw frame
            g2.setColor(Colorset.COLOR_DARK_SHADOW);
            g2.drawRoundRect(0, 0, width, height, BORDER_RADIUS, BORDER_RADIUS);
            
        }
        
        // Render text
        if (text != null) {
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            LineMetrics lm = fm.getLineMetrics(text, g2);
            int txtW = fm.stringWidth(text);
            
            g2.drawString(text, (width-txtW)/2, (height+fm.getHeight())/2-2);
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
        setSize(new java.awt.Dimension(77, 26));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                RoundedButton.this.mousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                RoundedButton.this.mouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RoundedButton.this.mouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                RoundedButton.this.mouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                RoundedButton.this.mouseEntered(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 77, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 26, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseEntered
        state = STATE_HOVER;
        repaint();
    }//GEN-LAST:event_mouseEntered

    private void mouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseExited
        state = STATE_NONE;
        repaint();
    }//GEN-LAST:event_mouseExited

    private void mouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseReleased
        state = STATE_HOVER;
        repaint();
    }//GEN-LAST:event_mouseReleased

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        state = STATE_DOWN;
        repaint();
    }//GEN-LAST:event_mousePressed

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked

        if ((evt.getClickCount() == 1) && (evt.getButton() == MouseEvent.BUTTON1)) {
            fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
        }
        
    }//GEN-LAST:event_mouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
