package com.automec.display.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class JBottomButton extends JButton {
   private static final long serialVersionUID = -570349289655167865L;

   public JBottomButton(String text, String icon) {
      super(text);
      if (icon != null) {
         this.setText("");
         ImageIcon i = new ImageIcon(DisplayComponents.class.getClassLoader().getResource(icon));
         this.setIcon(new ImageIcon(i.getImage().getScaledInstance(DisplayComponents.bottomButtonSize.width - 20, DisplayComponents.bottomButtonSize.height - 20, 1)));
      }

      this.setHorizontalAlignment(0);
      this.setPreferredSize(DisplayComponents.bottomButtonSize);
      this.setContentAreaFilled(false);
      this.setFocusPainted(false);
   }

   public JBottomButton(String text, String icon, ActionListener action) {
      super(text);
      if (icon != null) {
         this.setText("");
         ImageIcon i = new ImageIcon(DisplayComponents.class.getClassLoader().getResource(icon));
         this.setIcon(new ImageIcon(i.getImage().getScaledInstance(DisplayComponents.bottomButtonSize.width - 20, DisplayComponents.bottomButtonSize.height - 20, 1)));
      }

      this.setHorizontalAlignment(0);
      this.setPreferredSize(DisplayComponents.bottomButtonSize);
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
