package com.automec.display.pages;

import com.automec.Communications;
import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.AxisFactorySettingsButtonAction;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.PasswordPromptPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AxisType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AxisSettingsPage {
   private JFrame axisSettingsFrame = new JFrame("Axis Settings");
   private JPanel axisSettingsPanel;
   public Axis axis;

   public AxisSettingsPage(Axis axis) {
      this.axis = axis;
      this.initialize(axis);
   }

   public AxisSettingsPage() {
      this.axis = null;
      this.initialize(this.axis);
   }

   private void initialize(Axis axis) {
      this.axisSettingsFrame.setDefaultCloseOperation(3);
      this.axisSettingsFrame.setSize(1024, 768);
      this.axisSettingsFrame.setUndecorated(true);
      this.axisSettingsPanel = new JPanel();
      this.axisSettingsFrame.getContentPane().add(this.axisSettingsPanel);
      this.axisSettingsPanel.setLayout(new BorderLayout(0, 0));
      Settings.activeFrame = this.axisSettingsFrame;
      this.axisSettingsFrame.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      this.axisSettingsFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
            PasswordPromptPage.passwordPromptFrame.dispose();
         }

         public void windowLostFocus(WindowEvent arg0) {
         }
      });
      JLabel lblAxisSettings = new JLabel("Axis Settings");
      lblAxisSettings.setFont(DisplayComponents.pageTitleFont);
      lblAxisSettings.setHorizontalAlignment(0);
      JPanel titlePanel = new JPanel();
      titlePanel.add(lblAxisSettings);
      this.axisSettingsPanel.add(titlePanel, "North");
      JPanel settingsPanel = new JPanel();
      this.axisSettingsPanel.add(settingsPanel, "Center");
      GridBagLayout gbl_settingsPanel = new GridBagLayout();
      settingsPanel.setLayout(gbl_settingsPanel);
      settingsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      ArrayList<JPanel> axisButtons = new ArrayList();

      int i;
      for(i = 0; i < Settings.axes.size(); ++i) {
         final Axis a = (Axis)Settings.axes.get(i);
         JPanel p = new JPanel();
         JLabel l = new JLabel(a.getFullName() + " Drive State");
         final JButton e = new JButton("Enabled");
         final JButton d = new JButton("Disabled");
         JButton f = new JCustomButton("<html>Factory<br/>Settings</html>");
         if (a.getEnabled()) {
            e.setBackground(DisplayComponents.Active);
            d.setBackground(DisplayComponents.Inactive);
         } else {
            d.setBackground(DisplayComponents.Active);
            e.setBackground(DisplayComponents.Inactive);
         }

         e.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
               if (!Settings.locked) {
                  e.setBackground(DisplayComponents.Active);
                  d.setBackground(DisplayComponents.Inactive);
                  a.setEnabled(true);
                  Settings.calibrated = false;
               } else {
                  new PasswordPromptPage();
               }

            }
         });
         d.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
               if (!Settings.locked) {
                  d.setBackground(DisplayComponents.Active);
                  e.setBackground(DisplayComponents.Inactive);
                  a.setEnabled(false);
                  if (a.getAxisType().equals(AxisType.RAM)) {
                     Communications.setYControlParam((Axis)Settings.axes.get(1), 2147483.647D, -2147483.647D, 0.0D, -2147483.647D, -2147483.647D);
                     Settings.autoAdvanceMode = AdvanceMode.EXTERNAL;
                  }
               } else {
                  new PasswordPromptPage();
               }

            }
         });
         f.addActionListener(new AxisFactorySettingsButtonAction((Axis)Settings.axes.get(i), this.getFrame()));
         e.setPreferredSize(new Dimension(120, 80));
         d.setPreferredSize(new Dimension(120, 80));
         f.setPreferredSize(new Dimension(120, 80));
         l.setFont(DisplayComponents.pageHeaderFont);
         p.setLayout(new GridBagLayout());
         p.add(l, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 0.0D, new Insets(15, 30, 15, 30)));
         p.add(e, DisplayComponents.GenerateConstraints(1, 0, 1.0D, 0.0D, new Insets(15, 30, 15, 30)));
         p.add(d, DisplayComponents.GenerateConstraints(2, 0, 1.0D, 0.0D, new Insets(15, 30, 15, 30)));
         p.add(f, DisplayComponents.GenerateConstraints(3, 0, 1.0D, 0.0D, new Insets(15, 30, 15, 30)));
         axisButtons.add(p);
      }

      for(i = 0; i < axisButtons.size(); ++i) {
         settingsPanel.add((Component)axisButtons.get(i), DisplayComponents.GenerateConstraints(0, i, 0.0D, 0.0D, 1, 1, 11));
      }

      settingsPanel.add(new JPanel(), DisplayComponents.GenerateConstraints(0, axisButtons.size(), 1.0D, 1.0D, 1, 1));
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout(1, 30, 0));
      this.axisSettingsPanel.add(buttonPanel, "South");
      buttonPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
      JButton homeButton = new JBottomButton("      ", "home.png");
      homeButton.setVerticalTextPosition(3);
      homeButton.setHorizontalTextPosition(0);
      buttonPanel.add(homeButton);
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            AxisSettingsPage.this.axisSettingsFrame.dispose();
            SystemCommands.writeSettingsFile();
            Settings.log.finest("axis settings page disposed");
            new HomePage();
         }
      });
      JButton settingsButton = new JBottomButton("Settings", "settings.png");
      settingsButton.setVerticalTextPosition(3);
      settingsButton.setHorizontalTextPosition(0);
      buttonPanel.add(settingsButton);
      settingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("settings button pressed");
            AxisSettingsPage.this.axisSettingsFrame.dispose();
            Settings.log.finest("axis settings page disposed");
            new SettingsPage();
         }
      });
      this.axisSettingsFrame.setVisible(true);
      Settings.log.finest("axis settings frame initialized");
   }

   public JFrame getFrame() {
      return this.axisSettingsFrame;
   }
}
