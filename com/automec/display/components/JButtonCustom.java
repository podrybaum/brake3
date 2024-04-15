package com.automec.display.components;

import javax.swing.JButton;

public class JButtonCustom extends JButton {
   private static final long serialVersionUID = 3931480363087333635L;
   private int index1;
   private int index2;

   public JButtonCustom(String text, int index1, int index2) {
      super(text);
      this.index1 = index1;
      this.index2 = index2;
   }

   public int getIndex1() {
      return this.index1;
   }

   public int getIndex2() {
      return this.index2;
   }
}
