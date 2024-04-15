package com.automec.display.pages;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class BtBlink implements ActionListener {
   private JButton button;
   private Color defaultColor;
   private Color red;
   private int count;

   public BtBlink(JButton button) {
      this.red = Color.RED;
      this.button = button;
      this.defaultColor = button.getForeground();
   }

   public void actionPerformed(ActionEvent arg0) {
      if (this.count % 2 == 0) {
         this.button.setBackground(this.red);
      } else {
         this.button.setBackground(this.defaultColor);
      }

      ++this.count;
   }
}
