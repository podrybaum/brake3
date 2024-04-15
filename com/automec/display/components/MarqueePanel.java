package com.automec.display.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class MarqueePanel extends JPanel implements ActionListener, AncestorListener, WindowListener {
   protected boolean paintChildren;
   protected boolean scrollingPaused;
   protected int scrollOffset;
   protected int wrapOffset;
   private int preferredWidth;
   private int scrollAmount;
   private int scrollFrequency;
   private boolean wrap;
   private int wrapAmount;
   private boolean scrollWhenFocused;
   private Timer timer;

   public MarqueePanel() {
      this(5, 5);
   }

   public MarqueePanel(int scrollFrequency, int scrollAmount) {
      this.preferredWidth = -1;
      this.wrap = false;
      this.wrapAmount = 50;
      this.scrollWhenFocused = true;
      this.timer = new Timer(1000, this);
      this.setScrollFrequency(scrollFrequency);
      this.setScrollAmount(scrollAmount);
      this.setLayout(new BoxLayout(this, 0));
      this.addAncestorListener(this);
   }

   public void paintChildren(Graphics g) {
      if (this.paintChildren) {
         Graphics2D g2d = (Graphics2D)g;
         g2d.translate(-this.scrollOffset, 0);
         super.paintChildren(g);
         g2d.translate(this.scrollOffset, 0);
         if (this.isWrap()) {
            this.wrapOffset = this.scrollOffset - super.getPreferredSize().width - this.wrapAmount;
            g2d.translate(-this.wrapOffset, 0);
            super.paintChildren(g);
            g2d.translate(this.wrapOffset, 0);
         }

      }
   }

   public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.width = this.preferredWidth == -1 ? d.width / 2 : this.preferredWidth;
      return d;
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public int getPreferredWidth() {
      return this.preferredWidth;
   }

   public void setPreferredWidth(int preferredWidth) {
      this.preferredWidth = preferredWidth;
      this.revalidate();
   }

   public int getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(int scrollAmount) {
      this.scrollAmount = scrollAmount;
   }

   public int getScrollFrequency() {
      return this.scrollFrequency;
   }

   public void setScrollFrequency(int scrollFrequency) {
      this.scrollFrequency = scrollFrequency;
      int delay = 1000 / scrollFrequency;
      this.timer.setInitialDelay(delay);
      this.timer.setDelay(delay);
   }

   public boolean isScrollWhenFocused() {
      return this.scrollWhenFocused;
   }

   public void setScrollWhenFocused(boolean scrollWhenFocused) {
      this.scrollWhenFocused = scrollWhenFocused;
   }

   public boolean isWrap() {
      return this.wrap;
   }

   public void setWrap(boolean wrap) {
      this.wrap = wrap;
   }

   public int getWrapAmount() {
      return this.wrapAmount;
   }

   public void setWrapAmount(int wrapAmount) {
      this.wrapAmount = wrapAmount;
   }

   public void startScrolling() {
      this.paintChildren = true;
      this.scrollOffset = -10;
      this.timer.start();
   }

   public void stopScrolling() {
      this.timer.stop();
      this.paintChildren = false;
      this.repaint();
   }

   public void pauseScrolling() {
      if (this.timer.isRunning()) {
         this.timer.stop();
         this.scrollingPaused = true;
      }

   }

   public void resumeScrolling() {
      if (this.scrollingPaused) {
         this.timer.restart();
         this.scrollingPaused = false;
      }

   }

   public void actionPerformed(ActionEvent ae) {
      this.scrollOffset += this.scrollAmount;
      int width = super.getPreferredSize().width;
      if (this.scrollOffset > width) {
         this.scrollOffset = this.isWrap() ? this.wrapOffset + this.scrollAmount : -this.getSize().width;
      }

      this.repaint();
   }

   public void ancestorAdded(AncestorEvent e) {
      SwingUtilities.windowForComponent(this).addWindowListener(this);
   }

   public void ancestorMoved(AncestorEvent e) {
   }

   public void ancestorRemoved(AncestorEvent e) {
   }

   public void windowActivated(WindowEvent e) {
      if (this.isScrollWhenFocused()) {
         this.resumeScrolling();
      }

   }

   public void windowClosed(WindowEvent e) {
      this.stopScrolling();
   }

   public void windowClosing(WindowEvent e) {
      this.stopScrolling();
   }

   public void windowDeactivated(WindowEvent e) {
      if (this.isScrollWhenFocused()) {
         this.pauseScrolling();
      }

   }

   public void windowDeiconified(WindowEvent e) {
      this.resumeScrolling();
   }

   public void windowIconified(WindowEvent e) {
      this.pauseScrolling();
   }

   public void windowOpened(WindowEvent e) {
      this.startScrolling();
   }
}
