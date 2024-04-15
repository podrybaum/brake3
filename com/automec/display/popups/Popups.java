package com.automec.display.popups;

import com.automec.Communications;
import com.automec.Settings;
import com.automec.display.pages.HomePage;
import com.automec.display.pages.RunJobPage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Popups {
   public static void eStop() {
      if (!NotificationPage.containsKey("ESTOP")) {
         new NotificationPage("ESTOP", "The emergency stop button has been pressed", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.log.finest("estop popup disposed");
            }
         });
      }

   }

   public static void powerCycled() {
      if (!NotificationPage.containsKey("MAC POWER")) {
         new NotificationPage("MAC POWER", "The MAC lost power, and has reset itself", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               NotificationPage.removePage("MAC POWER");
               Settings.log.finest("mac power popup disposed");
            }
         });
      }

   }

   public static void xUnplugged() {
      if (!NotificationPage.containsKey("X Axis Unplugged") && !PasswordPromptPage.exists) {
         new NotificationPage("X Axis Unplugged", "The X axis has come unplugged, please plug it back in to continue", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.calibrated = false;
               Communications.reset();
               if (Settings.activeFrame.getTitle().equals("Run Job")) {
                  ((RunJobPage)Settings.activeFrame).exitPage();
               } else if (Settings.activeFrame.getTitle().equals("Home")) {
                  Settings.activeFrame.dispose();
                  new HomePage();
               }

               NotificationPage.removePage("X Axis Unplugged");
               Settings.log.finest("x axis popup unplugged popup disposed");
            }
         });
      }

   }

   public static void yUnplugged() {
      if (!NotificationPage.containsKey("Y Axis Unplugged") && !PasswordPromptPage.exists) {
         new NotificationPage("Y Axis Unplugged", "The Y axis has come unplugged, please plug it back in to continue", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.calibrated = false;
               Communications.reset();
               if (Settings.activeFrame.getTitle().equals("Run Job")) {
                  ((RunJobPage)Settings.activeFrame).exitPage();
               } else if (Settings.activeFrame.getTitle().equals("Home")) {
                  Settings.activeFrame.dispose();
                  new HomePage();
               }

               NotificationPage.removePage("Y Axis Unplugged");
               Settings.log.finest("y axis popup unplugged popup disposed");
            }
         });
      }

   }

   public static void rUnplugged() {
      if (!NotificationPage.containsKey("R Axis Unplugged") && !PasswordPromptPage.exists) {
         new NotificationPage("R Axis Unplugged", "The R axis has come unplugged, please plug it back in to continue", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.calibrated = false;
               Communications.reset();
               if (Settings.activeFrame.getTitle().equals("Run Job")) {
                  ((RunJobPage)Settings.activeFrame).exitPage();
               } else if (Settings.activeFrame.getTitle().equals("Home")) {
                  Settings.activeFrame.dispose();
                  new HomePage();
               }

               NotificationPage.removePage("R Axis Unplugged");
               Settings.log.finest("r axis popup unplugged popup disposed");
            }
         });
      }

   }

   public static void rStalled() {
      if (!NotificationPage.containsKey("R Axis Stalled")) {
         Settings.log.warning("R motor Stalled");
         RunJobPage.disableAuto();
         Settings.printRecording();
         new NotificationPage("R Axis Stalled", "The R axis is stalled, you will need to recalibrate.", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.calibrated = false;
               Communications.reset();
               if (Settings.activeFrame.getTitle().equals("Run Job")) {
                  ((RunJobPage)Settings.activeFrame).exitPage();
               } else if (Settings.activeFrame.getTitle().equals("Home")) {
                  Settings.activeFrame.dispose();
                  new HomePage();
               }

               NotificationPage.removePage("R Axis Stalled");
               Settings.log.finest("r axis popup stalled popup disposed");
            }
         });
      }

   }

   public static void xStalled() {
      if (!NotificationPage.containsKey("X Axis Stalled")) {
         Settings.log.warning("X motor Stalled");
         RunJobPage.disableAuto();
         Settings.printRecording();
         new NotificationPage("X Axis Stalled", "The X axis is stalled, you will need to recalibrate.", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               Settings.calibrated = false;
               Communications.reset();
               if (Settings.activeFrame.getTitle().equals("Run Job")) {
                  ((RunJobPage)Settings.activeFrame).exitPage();
               } else if (Settings.activeFrame.getTitle().equals("Home")) {
                  Settings.activeFrame.dispose();
                  new HomePage();
               }

               NotificationPage.removePage("X Axis Stalled");
               Settings.log.finest("x axis popup stalled popup disposed");
            }
         });
      }

   }

   public static void rNotStalled() {
      NotificationPage.containsKey("R Axis Stalled");
   }

   public static void xNotStalled() {
      NotificationPage.containsKey("X Axis Stalled");
   }

   public static void rPluggedIn() {
      if (NotificationPage.containsKey("R Axis Unplugged")) {
         Settings.log.finest("r axis popup unplugged popup disposed, r axis plugged back in");
      }

   }

   public static void yPluggedIn() {
      if (NotificationPage.containsKey("Y Axis Unplugged")) {
         Settings.log.finest("y axis popup unplugged popup disposed, y axis plugged back in");
      }

   }

   public static void xPluggedIn() {
      if (NotificationPage.containsKey("X Axis Unplugged")) {
         Settings.log.finest("x axis popup unplugged popup disposed, x axis plugged back in");
      }

   }
}
