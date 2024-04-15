package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.Timer;

public class PasswordPromptPage {
   public static JFrame passwordPromptFrame = new JFrame("Password Prompt");
   private JPanel passwordPromptPanel;
   private JPasswordField passwordField;
   public static boolean exists = false;
   public static Timer passwordTimer = new Timer(300000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         Settings.locked = true;
      }
   });

   public PasswordPromptPage() {
      if (Settings.locked) {
         this.initialize();
      }

   }

   public void initialize() {
      passwordPromptFrame.setDefaultCloseOperation(3);
      passwordPromptFrame.setBounds(212, 30, 600, 300);
      passwordPromptFrame.setUndecorated(true);
      this.passwordPromptPanel = new JPanel();
      passwordPromptFrame.getContentPane().add(this.passwordPromptPanel);
      this.passwordPromptPanel.setLayout(new BorderLayout(0, 0));
      this.passwordPromptPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      exists = true;
      JLabel lblWarning = new JLabel("WARNING!");
      lblWarning.setHorizontalAlignment(0);
      this.passwordPromptPanel.add(lblWarning, "North");
      lblWarning.setFont(DisplayComponents.pageTitleFont);
      JLabel lblPasswordRequiredTo = new JLabel("Password Required to Edit This Value");
      lblPasswordRequiredTo.setHorizontalAlignment(0);
      this.passwordPromptPanel.add(lblPasswordRequiredTo, "Center");
      lblPasswordRequiredTo.setFont(DisplayComponents.pageHeaderFont);
      JPanel bottom = new JPanel();
      bottom.setLayout(new GridBagLayout());
      this.passwordField = new JPasswordField();
      this.passwordField.setToolTipText("Enter Password Here");
      bottom.add(this.passwordField, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      this.passwordField.setFont(DisplayComponents.pageHeaderFont);
      this.passwordField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            boolean attempt = Settings.checkPassword(PasswordPromptPage.this.passwordField.getPassword());
            if (attempt) {
               PasswordPromptPage.passwordPromptFrame.dispose();
               PasswordPromptPage.this.passwordField.setText("");
               Settings.locked = false;
               Settings.log.finest("password page disposed");
               new NotificationPage("WARNING!", "Setup Mode Unlocked", 2000);
               Settings.log.warning("Password entered, settings unlocked");
               PasswordPromptPage.passwordTimer.restart();
               PasswordPromptPage.exists = false;
            } else {
               new NotificationPage("ERROR", "Incorrect Password");
               Settings.log.warning("Incorrect Password entered");
               PasswordPromptPage.this.passwordField.setText("");
            }

         }
      });
      this.passwordField.addMouseListener(DisplayComponents.KeyboardPopup());
      this.passwordField.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
         }

         public void focusGained(FocusEvent e) {
            PasswordPromptPage.this.passwordField.getCaret().setVisible(true);
         }
      });
      JButton closeButton = new JCustomButton("Close");
      closeButton.setFont(DisplayComponents.buttonFont);
      bottom.add(closeButton, DisplayComponents.GenerateConstraints(0, 1, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            PasswordPromptPage.passwordPromptFrame.dispose();
            PasswordPromptPage.exists = false;
         }
      });
      closeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      this.passwordPromptPanel.add(bottom, "South");
      passwordPromptFrame.setVisible(true);
      Settings.log.finest("password page initialized");
      this.passwordField.dispatchEvent(new MouseEvent(this.passwordField, 500, System.currentTimeMillis(), 0, 10, 10, 1, false));
   }
}
