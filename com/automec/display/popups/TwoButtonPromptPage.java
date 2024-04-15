package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TwoButtonPromptPage {
   private JFrame twoButtonPromptFrame;

   public TwoButtonPromptPage(String title, String text, String buttonOneText, ActionListener actionOne, String buttonTwoText, ActionListener actionTwo) {
      this.twoButtonPromptFrame = new JFrame(title);
      this.initialize(title, text, buttonOneText, actionOne, buttonTwoText, actionTwo, true);
   }

   public TwoButtonPromptPage(String title, String text, String buttonOneText, ActionListener actionOne, String buttonTwoText, ActionListener actionTwo, boolean closeVisible) {
      this.twoButtonPromptFrame = new JFrame(title);
      this.initialize(title, text, buttonOneText, actionOne, buttonTwoText, actionTwo, closeVisible);
   }

   private void initialize(final String title, final String text, String buttonOneText, ActionListener actionOne, String buttonTwoText, ActionListener actionTwo, boolean closeVisible) {
      this.twoButtonPromptFrame.setBounds(212, 234, 600, 300);
      this.twoButtonPromptFrame.setDefaultCloseOperation(3);
      this.twoButtonPromptFrame.setUndecorated(true);
      this.twoButtonPromptFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.twoButtonPromptFrame.setAlwaysOnTop(true);
      this.twoButtonPromptFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null) {
               TwoButtonPromptPage.this.twoButtonPromptFrame.dispose();
               Settings.log.finest(title + " two button prompt page disposed: " + text);
            }

         }
      });
      JLabel titleLabel = new JLabel(title);
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFontSmall);
      this.twoButtonPromptFrame.getContentPane().add(titleLabel, "North");
      titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      JTextArea textLabel = new JTextArea(text);
      textLabel.setFont(DisplayComponents.pageHeaderFont);
      textLabel.setLineWrap(true);
      textLabel.setWrapStyleWord(true);
      textLabel.setEditable(false);
      textLabel.setBackground(DisplayComponents.Background);
      this.twoButtonPromptFrame.getContentPane().add(textLabel, "Center");
      JPanel buttonPanel = new JPanel();
      this.twoButtonPromptFrame.getContentPane().add(buttonPanel, "South");
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      gbl_buttonPanel.columnWidths = new int[2];
      gbl_buttonPanel.rowHeights = new int[2];
      gbl_buttonPanel.columnWeights = new double[]{1.0D, 1.0D, Double.MIN_VALUE};
      gbl_buttonPanel.rowWeights = new double[]{1.0D, 1.0D, Double.MIN_VALUE};
      buttonPanel.setLayout(gbl_buttonPanel);
      JButton buttonOne = new JCustomButton(buttonOneText);
      buttonOne.setBackground(Color.GRAY);
      buttonOne.setPreferredSize(DisplayComponents.minimumButtonSize);
      GridBagConstraints gbc_btnButton = new GridBagConstraints();
      gbc_btnButton.fill = 1;
      gbc_btnButton.insets = new Insets(0, 0, 5, 5);
      gbc_btnButton.gridx = 0;
      gbc_btnButton.gridy = 0;
      buttonPanel.add(buttonOne, gbc_btnButton);
      buttonOne.addActionListener(actionOne);
      JButton buttonTwo = new JCustomButton(buttonTwoText);
      buttonTwo.setPreferredSize(DisplayComponents.minimumButtonSize);
      buttonTwo.setBackground(Color.GRAY);
      GridBagConstraints gbc_btnButton_1 = new GridBagConstraints();
      gbc_btnButton_1.fill = 1;
      gbc_btnButton_1.insets = new Insets(0, 0, 5, 0);
      gbc_btnButton_1.gridx = 1;
      gbc_btnButton_1.gridy = 0;
      buttonPanel.add(buttonTwo, gbc_btnButton_1);
      buttonTwo.addActionListener(actionTwo);
      JButton closeButton = new JCustomButton("Close");
      closeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      closeButton.setBackground(Color.GRAY);
      GridBagConstraints gbc_closeButton = new GridBagConstraints();
      gbc_closeButton.fill = 1;
      gbc_closeButton.gridwidth = 2;
      gbc_closeButton.anchor = 18;
      gbc_closeButton.gridx = 0;
      gbc_closeButton.gridy = 1;
      if (closeVisible) {
         buttonPanel.add(closeButton, gbc_closeButton);
      }

      closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TwoButtonPromptPage.this.twoButtonPromptFrame.dispose();
         }
      });
      this.twoButtonPromptFrame.setVisible(true);
   }
}
