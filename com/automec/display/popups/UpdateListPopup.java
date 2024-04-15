package com.automec.display.popups;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class UpdateListPopup extends JFrame {
   private static final long serialVersionUID = 5816293457371180704L;
   private List<String> vals;

   public UpdateListPopup(String title, List<String> vals) {
      this.vals = vals;
      this.initialize(title);
   }

   private void initialize(final String title) {
      this.setBounds(212, 234, 600, 300);
      this.setDefaultCloseOperation(3);
      this.setUndecorated(true);
      this.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      JLabel titleLabel = new JLabel(title);
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      this.getContentPane().add(titleLabel, "North");
      titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      DefaultListModel<String> list = new DefaultListModel();
      final JList<String> searchList = new JList(list);
      searchList.setFont(DisplayComponents.pageHeaderFont);
      Iterator var6 = this.vals.iterator();

      while(var6.hasNext()) {
         String v = (String)var6.next();
         list.addElement(v);
      }

      JScrollPane scroll = new JScrollPane();
      scroll.setMaximumSize(new Dimension(400, 600));
      scroll.setViewportView(searchList);
      scroll.setHorizontalScrollBarPolicy(31);
      this.getContentPane().add(scroll, "Center");
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new GridBagLayout());
      final JButton action = new JCustomButton(title);
      action.setFont(DisplayComponents.buttonFont);
      bottomPanel.add(action, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      action.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            File localdir = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Updates" + File.separator + "CNC600_" + (String)searchList.getSelectedValue() + ".jar");
            if (title.equals("Downgrade")) {
               SystemCommands.performDowngrade(localdir.getPath());
            } else {
               Settings.selectedUSB.performUpdate(localdir.getPath());
            }

            UpdateListPopup.this.dispose();
         }
      });
      action.setPreferredSize(DisplayComponents.minimumButtonSize);
      action.setEnabled(false);
      searchList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!searchList.isSelectionEmpty()) {
               action.setEnabled(true);
            }

         }
      });
      JButton closeButton = new JCustomButton("Close");
      closeButton.setFont(DisplayComponents.buttonFont);
      bottomPanel.add(closeButton, DisplayComponents.GenerateConstraints(1, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            UpdateListPopup.this.dispose();
         }
      });
      closeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      this.getContentPane().add(bottomPanel, "South");
      this.setVisible(true);
      Settings.log.finest("notification initialized");
   }
}
