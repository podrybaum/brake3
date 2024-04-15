package com.automec.display.pages;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.UpdateListPopup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SoftwarePage {
   private JFrame softwareFrame = new JFrame("Software");

   public SoftwarePage() {
      this.initialize();
   }

   public void initialize() {
      this.softwareFrame.setDefaultCloseOperation(3);
      this.softwareFrame.setBounds(112, 234, 800, 300);
      this.softwareFrame.setUndecorated(true);
      this.softwareFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null) {
               SoftwarePage.this.softwareFrame.dispose();
            }

         }
      });
      JLabel title = new JLabel("Software");
      title.setHorizontalAlignment(0);
      this.softwareFrame.getContentPane().add(title, "North");
      title.setFont(DisplayComponents.pageTitleFont);
      JLabel bodyText = new JLabel("<html>CNC600 GUI VERSION: 2.0.14<br/>MAC VERSION: " + Settings.MACREV + "</html>");
      bodyText.setHorizontalAlignment(0);
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new BorderLayout());
      centerPanel.add(bodyText, "Center");
      bodyText.setFont(DisplayComponents.pageTitleFontSmall);
      final JLabel odometer = new JLabel("<html>Odometer (inches): Backgauge: " + String.format("%.1f", Settings.xOdometer) + " Ram: " + String.format("%.1f", Settings.yOdometer) + " R: " + String.format("%.1f", Settings.rOdometer) + "<br/>" + "BuildVer: " + Settings.commit + "</html>");
      odometer.setHorizontalAlignment(0);
      odometer.setFont(DisplayComponents.pageTextFont);
      odometer.setForeground(DisplayComponents.Background);
      odometer.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            odometer.setForeground(Color.BLACK);
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      });
      centerPanel.add(odometer, "South");
      this.softwareFrame.getContentPane().add(centerPanel, "Center");
      JPanel buttonPanel = new JPanel();
      JButton upgrade = new JCustomButton("Upgrade");
      JButton downgrade = new JCustomButton("Downgrade");
      JButton cancel = new JCustomButton("Cancel");
      JButton debug = new JCustomButton("Debug");
      buttonPanel.setLayout(new FlowLayout());
      buttonPanel.add(upgrade);
      buttonPanel.add(downgrade);
      buttonPanel.add(debug);
      buttonPanel.add(cancel);
      this.softwareFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.softwareFrame.getContentPane().add(buttonPanel, "South");
      upgrade.setFont(DisplayComponents.pageHeaderFont);
      downgrade.setFont(DisplayComponents.pageHeaderFont);
      debug.setFont(DisplayComponents.pageHeaderFont);
      cancel.setFont(DisplayComponents.pageHeaderFont);
      if (Settings.selectedUSB == null) {
         upgrade.setEnabled(false);
         debug.setEnabled(false);
      }

      debug.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            SystemCommands.dumpLogs();
            SoftwarePage.this.softwareFrame.dispose();
         }
      });
      upgrade.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.selectedUSB.findUpdate();
            new UpdateListPopup("Upgrade", Settings.selectedUSB.listUpgrades());
            SoftwarePage.this.softwareFrame.dispose();
         }
      });
      downgrade.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.selectedUSB.findUpdate();
            new UpdateListPopup("Downgrade", SystemCommands.listDowngrades());
            SoftwarePage.this.softwareFrame.dispose();
         }
      });
      cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            SoftwarePage.this.softwareFrame.dispose();
         }
      });
      this.softwareFrame.setVisible(true);
      Settings.log.finest("software page initialized");
   }
}
