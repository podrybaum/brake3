package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class NotificationPage {
   private static HashMap<String, NotificationPage> activePages = new HashMap();
   private Timer timer;
   private Timer blockNew;
   private JFrame notificationFrame;
   private boolean progressBar = false;
   public static JProgressBar bar = new JProgressBar();
   private boolean image = false;
   private String imgName = "";

   public NotificationPage(final String title, final String text) {
      if (!activePages.containsKey(title) && !Settings.demoMode) {
         activePages.put(title, this);
         this.notificationFrame = new JFrame(title);
         this.initialize(title, text, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed " + text);
               NotificationPage.activePages.remove(title);
            }
         });
      }

   }

   public NotificationPage(final String title, final String text, int timeout) {
      if (!activePages.containsKey(title) && !Settings.demoMode) {
         activePages.put(title, this);
         this.notificationFrame = new JFrame(title);
         this.timer = new Timer(timeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed: " + text);
               NotificationPage.this.blockNew.start();
               NotificationPage.this.timer.stop();
            }
         });
         this.initialize(title, text, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed: " + text);
               NotificationPage.this.blockNew.start();
               NotificationPage.this.timer.stop();
            }
         });
         this.timer.start();
      }

   }

   public NotificationPage(String title, String text, ActionListener action) {
      if (!activePages.containsKey(title) && !Settings.demoMode) {
         activePages.put(title, this);
         this.notificationFrame = new JFrame(title);
         this.initialize(title, text, action);
      }

   }

   public NotificationPage(final String title, final String text, boolean progressBar) {
      if (!activePages.containsKey(title) && !Settings.demoMode) {
         activePages.put(title, this);
         this.notificationFrame = new JFrame(title);
         this.progressBar = progressBar;
         this.initialize(title, text, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed: " + text);
               NotificationPage.this.blockNew.start();
            }
         });
      }

   }

   public NotificationPage(final String title, final String text, String imgName) {
      if (!activePages.containsKey(title) && !Settings.demoMode) {
         this.imgName = imgName;
         this.image = true;
         activePages.put(title, this);
         this.notificationFrame = new JFrame(title);
         this.initialize(title, text, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed: " + text);
               NotificationPage.this.blockNew.start();
            }
         });
      }

   }

   private void initialize(final String title, final String text, ActionListener action) {
      this.notificationFrame.setBounds(212, 234, 600, 300);
      this.notificationFrame.setDefaultCloseOperation(3);
      this.notificationFrame.setUndecorated(true);
      this.notificationFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.notificationFrame.setAlwaysOnTop(true);
      this.blockNew = new Timer(5000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            NotificationPage.activePages.remove(title);
            NotificationPage.this.blockNew.stop();
         }
      });
      this.notificationFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null) {
               NotificationPage.this.notificationFrame.dispose();
               Settings.log.finest(title + " notification page disposed: " + text);
               NotificationPage.this.blockNew.start();
            }

         }
      });
      JLabel titleLabel = new JLabel(title);
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      this.notificationFrame.getContentPane().add(titleLabel, "North");
      titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      JTextArea textLabel = new JTextArea(text);
      JPanel center = new JPanel();
      textLabel.setFont(DisplayComponents.pageHeaderFont);
      textLabel.setLineWrap(true);
      textLabel.setWrapStyleWord(true);
      textLabel.setEditable(false);
      textLabel.setBackground(DisplayComponents.Background);
      center.setLayout(new GridBagLayout());
      center.add(textLabel, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      this.notificationFrame.getContentPane().add(center, "Center");
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new GridBagLayout());
      if (this.progressBar) {
         bar.setMinimum(0);
         bar.setMaximum(100);
         bar.setValue(1);
         bar.setMaximumSize(new Dimension(500, 50));
         bar.setForeground(DisplayComponents.Active);
         bottomPanel.add(bar, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      }

      if (this.image) {
         JLabel image = new JLabel();
         System.out.println("img: " + this.imgName);
         ImageIcon i = new ImageIcon(DisplayComponents.class.getClassLoader().getResource(this.imgName));
         textLabel.setFont(DisplayComponents.editJobPageValue);
         image.setIcon(new ImageIcon(i.getImage().getScaledInstance(150, 150, 1)));
         image.setMinimumSize(new Dimension(150, 150));
         image.setPreferredSize(new Dimension(150, 150));
         center.add(image, DisplayComponents.GenerateConstraints(1, 0, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      }

      JButton closeButton = new JCustomButton("Close");
      closeButton.setFont(DisplayComponents.buttonFont);
      bottomPanel.add(closeButton, DisplayComponents.GenerateConstraints(0, 1, 1.0D, 1.0D, new Insets(5, 5, 5, 5)));
      closeButton.addActionListener(action);
      closeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      this.notificationFrame.getContentPane().add(bottomPanel, "South");
      this.notificationFrame.setVisible(true);
      Settings.log.finest("notification initialized");
   }

   public static boolean containsKey(String key) {
      return activePages.containsKey(key);
   }

   public static void removePage(String key) {
      ((NotificationPage)activePages.get(key)).dispose();
      ((NotificationPage)activePages.get(key)).blockNew.start();
   }

   public void dispose() {
      this.notificationFrame.dispose();
   }
}
