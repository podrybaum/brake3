package com.automec;

import com.automec.display.components.DisplayComponents;
import com.automec.display.pages.HomePage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AxisType;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Launcher {
   HomePage window;

   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               Settings.setupLogs();
               SystemCommands.loadDatabase();
               SystemCommands.loadJobTable();
               SystemCommands.loadToolTable();
               SystemCommands.makeImageDirectories();
               String settingsJson = SystemCommands.getSettingsFile();
               if (!settingsJson.isEmpty()) {
                  Settings.loadSettings(settingsJson);
                  Settings.log.info("loading settings from file");
               } else {
                  Settings.log.info("generating settings from defaults");
                  Settings.axes.add(new Axis("X-Axis", "X", 0, true, AxisType.BACKGAUGE));
                  Settings.axes.add(new Axis("Y-Axis", "Y", 1, true, AxisType.RAM));
                  Settings.axes.add(new Axis("R-Axis", "R", 2, true));
                  ((Axis)Settings.axes.get(0)).setAxisLength(24.1D);
                  ((Axis)Settings.axes.get(1)).setAxisLength(0.0D);
                  ((Axis)Settings.axes.get(2)).setAxisLength(8.1D);
                  ((Axis)Settings.axes.get(0)).setEncoderCountPerInch(4000.0D);
                  ((Axis)Settings.axes.get(1)).setEncoderCountPerInch(1023.0D);
                  ((Axis)Settings.axes.get(2)).setEncoderCountPerInch(13333.0D);
                  ((Axis)Settings.axes.get(0)).setSlowDistance(0.25D);
                  ((Axis)Settings.axes.get(1)).setSlowDistance(1.5D);
                  ((Axis)Settings.axes.get(2)).setSlowDistance(0.1D);
                  ((Axis)Settings.axes.get(0)).setStopDistance(0.006D);
                  ((Axis)Settings.axes.get(2)).setStopDistance(0.001D);
                  ((Axis)Settings.axes.get(1)).setAwDistance(0.004D);
                  Settings.log.info("saving settings");
                  SystemCommands.writeSettingsFile();
               }

               final JFrame background = new JFrame("CNC600");
               background.setDefaultCloseOperation(3);
               background.setSize(1024, 768);
               background.setFocusable(false);
               background.addMouseListener(new MouseListener() {
                  public void mouseClicked(MouseEvent arg0) {
                     background.toBack();
                  }

                  public void mouseEntered(MouseEvent arg0) {
                  }

                  public void mouseExited(MouseEvent arg0) {
                  }

                  public void mousePressed(MouseEvent arg0) {
                  }

                  public void mouseReleased(MouseEvent arg0) {
                  }
               });
               background.setUndecorated(true);
               background.getContentPane().setBackground(DisplayComponents.Background);
               background.setVisible(true);
               Settings.log.finest("background initialized");
               new HomePage();
               double brightness = (double)(Settings.screenBrightness / 10);
               SystemCommands.changeBrightness(brightness);
               if (SystemCommands.getOS().equals("Linux")) {
                  Settings.log.info("CNC600 running on Linux");
                  Settings.log.fine("Initializing I2C");
                  Communications.initI2C();
                  Listener.advance.start();
                  Listener.motionErrorX.start();
               }

               Listener.usbDetector.start();
               Listener.screenSaverStopper = new Timer(599000, new ActionListener() {
                  public void actionPerformed(ActionEvent arg0) {
                     try {
                        if (!Settings.screensaver) {
                           (new Robot()).mouseMove(0, 0);
                           Thread.sleep(50L);
                           (new Robot()).mouseMove(1, 1);
                           Thread.sleep(50L);
                           (new Robot()).mouseMove(0, 0);
                           System.out.println("Moving cursor");
                        }
                     } catch (Exception var3) {
                        var3.printStackTrace();
                     }

                  }
               });
               if (!Settings.screensaver) {
                  Listener.screenSaverStopper.start();
               }
            } catch (Exception var5) {
               var5.printStackTrace();
               Settings.log.log(Level.SEVERE, "Launcher", var5);
            }

         }
      });
   }
}
