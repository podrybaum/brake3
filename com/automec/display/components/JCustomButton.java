package com.automec.display.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class JCustomButton extends JButton {
   private static final long serialVersionUID = -570349289655167865L;

   public JCustomButton(String text) {
      super(text);
      this.setContentAreaFilled(false);
      this.setFocusPainted(false);
   }

   public JCustomButton(String text, ActionListener action) {
      super(text);
      this.addActionListener(action);
      this.setContentAreaFilled(false);
      this.setFocusPainted(false);
   }

   protected void paintComponent(Graphics g) {
      if (this.isEnabled()) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setPaint(new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, this.getHeight()), DisplayComponents.Active));
         g2.fillRect(0, 0, this.getWidth(), this.getHeight());
         g2.dispose();
         super.paintComponent(g);
      } else {
         super.paintComponent(g);
      }

   }
}
