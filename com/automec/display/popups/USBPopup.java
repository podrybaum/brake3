package com.automec.display.popups;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import com.automec.objects.USB;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class USBPopup {
   DefaultListModel<String> list;
   JList<String> searchList;
   JFrame usbPopupFrame = new JFrame();

   public USBPopup() {
      this.initialize();
   }

   public void initialize() {
      this.usbPopupFrame.setBounds(212, 184, 600, 400);
      this.usbPopupFrame.setDefaultCloseOperation(3);
      this.usbPopupFrame.setUndecorated(true);
      this.usbPopupFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.usbPopupFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null) {
               USBPopup.this.usbPopupFrame.dispose();
            }

         }
      });
      JLabel titleLabel = new JLabel("USB Selector");
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      this.usbPopupFrame.getContentPane().add(titleLabel, "North");
      titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      JPanel bottomPanel = new JPanel();
      this.usbPopupFrame.getContentPane().add(bottomPanel, "South");
      GridBagLayout gblbotmgr = new GridBagLayout();
      gblbotmgr.columnWidths = new int[2];
      gblbotmgr.rowHeights = new int[2];
      bottomPanel.setLayout(gblbotmgr);
      final JCustomButton selectButton = new JCustomButton("Select");
      selectButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      selectButton.setEnabled(false);
      selectButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.fine("selecting " + ((String)USBPopup.this.searchList.getSelectedValue()).substring(12, ((String)USBPopup.this.searchList.getSelectedValue()).length()) + " as usb device");
            Iterator var3 = Settings.connectedUSB.iterator();

            while(var3.hasNext()) {
               USB u = (USB)var3.next();
               if (u.path.equals(((String)USBPopup.this.searchList.getSelectedValue()).substring(12, ((String)USBPopup.this.searchList.getSelectedValue()).length()))) {
                  Settings.selectedUSB = u;
                  USBPopup.this.usbPopupFrame.dispose();
                  return;
               }
            }

            Settings.log.warning("unconfigured usb got passed to the wrong place");
            USBPopup.this.usbPopupFrame.dispose();
         }
      });
      final JCustomButton initializeButton = new JCustomButton("Initialize");
      initializeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      initializeButton.setEnabled(false);
      initializeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.info("initializing usb");
            Settings.connectedUSB.add(new USB(((String)USBPopup.this.searchList.getSelectedValue()).substring(14, ((String)USBPopup.this.searchList.getSelectedValue()).length())));
            initializeButton.setEnabled(false);
            selectButton.setEnabled(true);
            USBPopup.this.updateList();
         }
      });
      JCustomButton cancelButton = new JCustomButton("Cancel");
      cancelButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            USBPopup.this.usbPopupFrame.dispose();
         }
      });
      bottomPanel.add(selectButton, DisplayComponents.GenerateConstraints(0, 0));
      bottomPanel.add(initializeButton, DisplayComponents.GenerateConstraints(1, 0));
      bottomPanel.add(cancelButton, DisplayComponents.GenerateConstraints(0, 1, 2, 1));
      this.list = new DefaultListModel();
      this.searchList = new JList(this.list);
      this.searchList.setMaximumSize(new Dimension(240, 300));
      this.searchList.setFont(DisplayComponents.pageHeaderFont);
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(this.searchList);
      this.usbPopupFrame.getContentPane().add(scroll, "Center");
      this.updateList();
      this.searchList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               if (USBPopup.this.searchList.getSelectedIndex() == -1) {
                  Settings.log.finest("nothing selected in list");
                  selectButton.setEnabled(false);
                  initializeButton.setEnabled(false);
               } else {
                  Settings.log.finest(((String)((JList)e.getSource()).getSelectedValue()).toString() + " selected");
                  if (((String)((JList)e.getSource()).getSelectedValue()).toString().startsWith("Configured: ")) {
                     selectButton.setEnabled(true);
                     initializeButton.setEnabled(false);
                  } else {
                     selectButton.setEnabled(false);
                     initializeButton.setEnabled(true);
                  }
               }
            }

         }
      });
      this.usbPopupFrame.setVisible(true);
   }

   public void updateList() {
      ArrayList<String> USBs = SystemCommands.getUSBs();
      this.list.removeAllElements();
      Iterator var3 = USBs.iterator();

      while(var3.hasNext()) {
         String j = (String)var3.next();
         boolean connected = false;
         Iterator var6 = Settings.connectedUSB.iterator();

         while(var6.hasNext()) {
            USB u = (USB)var6.next();
            if (u.path.equals(j)) {
               connected = true;
            }
         }

         if (connected) {
            if (Settings.selectedUSB != null) {
               if (Settings.selectedUSB.path.equals(j)) {
                  this.list.addElement("Selected: " + j);
               } else {
                  this.list.addElement("Configured: " + j);
               }
            } else {
               this.list.addElement("Configured: " + j);
            }
         } else {
            this.list.addElement("Unconfigured: " + j);
         }
      }

      this.searchList.setModel(this.list);
   }
}
