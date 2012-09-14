/*
 * WebViewConsole.java
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

package com.wavesoft.webng.ui.system;

import com.wavesoft.webng.api.BrowserWindow;
import com.wavesoft.webng.api.HeadButton;
import com.wavesoft.webng.api.WebViewNG;
import com.wavesoft.webng.io.SystemConsole;
import com.wavesoft.webng.io.SystemConsoleListener;
import com.wavesoft.webng.ui.SystemIcons;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Calendar;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author icharala
 */
public class WebViewConsole extends WebViewNG implements SystemConsoleListener, HierarchyListener {


    private static class LogEntry {
        public String message;
        public int level;

        public LogEntry(String message, int level) {
            this.message = message;
            this.level = level;
        }
        
    }
    
    /**
     * Multi-line cell renderer for the log window
     * Based on : http://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height
     */
    private static class MultilineCellRenderer implements ListCellRenderer {

        private JPanel p;
        private JPanel iconPanel;
        private JLabel l;
        private JTextArea ta;

        public MultilineCellRenderer() {
            p = new JPanel();
            p.setLayout(new BorderLayout());

            // icon
            iconPanel = new JPanel(new BorderLayout());
            l = new JLabel(""); // <-- this will be an icon instead of a
                                // text
            l.setBorder(new EmptyBorder(1, 1, 1, 1));
            
            iconPanel.add(l, BorderLayout.NORTH);
            p.add(iconPanel, BorderLayout.WEST);

            // text
            ta = new JTextArea();
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            p.add(ta, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value, final int index, final boolean isSelected,
                final boolean hasFocus) {

            // Update icon
            int level = ((LogEntry) value).level;
            if (level == SystemConsole.LOG_DEBUG) {
                l.setIcon(SystemIcons.iconDebug);
            } else if (level == SystemConsole.LOG_ERROR) {
                l.setIcon(SystemIcons.iconError);
            } else if (level == SystemConsole.LOG_INFO) {
                l.setIcon(SystemIcons.iconInfo);
            } else if (level == SystemConsole.LOG_WARNING) {
                l.setIcon(SystemIcons.iconWarn);
            }
            
            // Update message
            ta.setText(((LogEntry) value).message);
            int width = list.getWidth();
            
            // this is just to lure the ta's internal sizing mechanism into action
            if (width > 0)
                ta.setSize(width, Short.MAX_VALUE);
            
            // Selection
            if (hasFocus || isSelected) {
                ta.setBackground(list.getSelectionBackground());
                ta.setForeground(list.getSelectionForeground());                
            } else {
                ta.setBackground(list.getBackground());
                ta.setForeground(list.getForeground());
            }
            
            // Return panel
            return p;

        }
    }

    // List model
    DefaultListModel model;
    
    /** Creates new form WebViewConsole */
    public WebViewConsole() {
        initComponents();
        SystemConsole.addConsoleListener(this);
        model = new DefaultListModel();
        
        ComponentListener l = new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                // next line possible if list is of type JXList
                // list.invalidateCellSizeCache();
                // for core: force cache invalidation by temporarily setting fixed height
                jList1.setFixedCellHeight(10);
                jList1.setFixedCellHeight(-1);
            }

        };

        jList1.addComponentListener(l);
        jList1.setCellRenderer(new MultilineCellRenderer());
        jList1.setModel(model);
        
        addHierarchyListener(this);
        SystemConsole.info("Console window started");
    }

    @Override
    public void webngSetBrowserWindow(BrowserWindow window) {
        super.webngSetBrowserWindow(window);
        if (window == null) return;
        
        window.setIcon(SystemIcons.tabIconConsole);
        window.setTitle("System console");
        window.setHeadButtons(new HeadButton[] { 
            new HeadButton() {

            @Override
            public void buttonClicked() {
                model.clear();
            }

            @Override
            public Icon getIcon() {
                return new javax.swing.ImageIcon(getClass().getResource("/com/wavesoft/webng/resources/delete.png"));
            }

            @Override
            public String getToolTip() {
                return "Clear the log messages";
            }
            
        }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList(){

            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }

        };

        jScrollPane1.setBorder(null);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void log(int level, String message) {
        model.addElement(new LogEntry(message, level));
        jList1.ensureIndexIsVisible(model.getSize());
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        System.out.println(e.toString());
      if (e.getID() == HierarchyEvent.HIERARCHY_CHANGED
          && e.getChanged().equals(this)
          && (e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
        
            SystemConsole.removeConsoleListener(this);
            SystemConsole.info("Console window closed");

      }
    }

}
