package com.automec.display.pages;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;

class LbBlink implements ActionListener {
   private JLabel label;
   private Color red;
   private Color black;
   private int count;

   public LbBlink(JLabel label) {
      this.red = Color.RED;
      this.black = Color.BLACK;
      this.label = label;
   }

   public void actionPerformed(ActionEvent arg0) {
      if (this.count % 2 == 0) {
         this.label.setForeground(this.red);
      } else {
         this.label.setForeground(this.black);
      }

      ++this.count;
   }
}
