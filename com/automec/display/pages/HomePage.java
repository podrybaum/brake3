package com.automec.display.pages;

import com.automec.Communications;
import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.AxisPanel;
import com.automec.display.components.DataInputField;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AutoMode;
import com.automec.objects.enums.Units;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;

public class HomePage {
   private JFrame homeFrame = new JFrame("Home");
   public boolean calibrated = false;
   public static JLabel calibrationLabel;
   private JLabel step1Label;
   private JLabel step2Label;
   private JLabel step3Label;
   static JButton calibrateButton;
   private Color buttonDefault;
   private Timer timerLBCal;
   private Timer timerLBStep;
   private Timer timerLBStep2;
   private Timer timerLBStep3;
   private Timer timerBTCal;
   private Timer timerI2CRead;
   public static ArrayList<AxisPanel> axisPanels;
   private JCustomButton punchButton;
   private JCustomButton dieButton;
   private DataInputField thickness;
   public static boolean recordX = false;
   public static boolean recordR = false;
   public static int calibrationRoutineStep = 0;

   public HomePage() {
      this.initialize();
      this.timerLBCal = new Timer(1000, new LbBlink(calibrationLabel));
      this.timerLBStep = new Timer(1000, new LbBlink(this.step1Label));
      this.timerBTCal = new Timer(1000, new BtBlink(calibrateButton));
      this.timerLBStep2 = new Timer(1000, new LbBlink(this.step2Label));
      this.timerLBStep3 = new Timer(1000, new LbBlink(this.step3Label));
      this.timerI2CRead = new Timer(30, new HomePage.updateDisplayPosition());
      if (SystemCommands.getOS().equals("Linux")) {
         this.timerI2CRead.start();
      }

      if (!Settings.calibrated) {
         if (!Listener.calibrateWatcher.isAlive() && SystemCommands.getOS().equals("Linux")) {
            Listener.calibrateWatcher.start();
         }

         this.timerLBCal.start();
         this.timerLBStep.start();
         this.timerBTCal.start();
      }

   }

   private void initialize() {
      this.homeFrame.setAlwaysOnTop(false);
      RunJobPage.autoMode = AutoMode.OFF;
      this.homeFrame.setDefaultCloseOperation(3);
      this.homeFrame.setSize(1024, 768);
      this.homeFrame.setUndecorated(true);
      Settings.activeFrame = this.homeFrame;
      JPanel titlePanel = new JPanel();
      this.homeFrame.getContentPane().add(titlePanel, "North");
      this.homeFrame.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      String tmp;
      if (!Settings.calibrated) {
         tmp = "System Needs Calibration";
         calibrationRoutineStep = 0;
      } else if (SystemCommands.getOS().equals("Linux") && !Settings.demoMode) {
         tmp = "Calibrated";
         calibrationRoutineStep = 2;
      } else {
         tmp = "Calibrated: DEMO MODE";
         calibrationRoutineStep = 2;
      }

      calibrationLabel = new JLabel(tmp);
      if (!Settings.calibrated) {
         calibrationLabel.setForeground(Color.RED);
      }

      calibrationLabel.setFont(DisplayComponents.pageTitleFont);
      titlePanel.add(calibrationLabel);
      JPanel leftPanel = new JPanel();
      this.homeFrame.getContentPane().add(leftPanel, "West");
      leftPanel.setLayout(new BoxLayout(leftPanel, 1));
      leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      this.step1Label = new JLabel("1. Press the CAL button");
      this.step1Label.setFont(DisplayComponents.pageHeaderFont);
      this.step2Label = new JLabel("<html>2. Bring the punch down<br/> to flat metal</html>");
      this.step2Label.setFont(DisplayComponents.pageHeaderFont);
      this.step3Label = new JLabel("<html>3. Press the CAL button <br/>again to reference the <br/>axis</html>");
      this.step3Label.setFont(DisplayComponents.pageHeaderFont);
      JLabel thicknessLabel = new JLabel("Thickness: ");
      thicknessLabel.setFont(DisplayComponents.pageHeaderFont);
      this.thickness = new DataInputField(Settings.calThickness, 0.0D, 2.0D);
      this.thickness.setFont(DisplayComponents.pageHeaderFont);
      this.thickness.setMaximumSize(new Dimension(120, 50));
      this.thickness.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               if (Settings.units == Units.INCHES) {
                  Settings.calThickness = Double.parseDouble(((DataInputField)e.getSource()).getText());
               } else {
                  BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  BigDecimal mm = BigDecimal.valueOf(25.4D);
                  Settings.calThickness = v.divide(mm, mc).doubleValue();
               }

               ((DataInputField)e.getSource()).setBackground(Color.WHITE);
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
            }

            ((DataInputField)e.getSource()).setNumber(Settings.calThickness);
         }
      });
      if (!Settings.calibrated) {
         this.thickness.addMouseListener(DisplayComponents.CalculatorPopup());
      }

      JPanel thicknessPanel = new JPanel();
      thicknessPanel.setLayout(new BoxLayout(thicknessPanel, 0));
      thicknessPanel.add(thicknessLabel);
      thicknessPanel.add(this.thickness);
      thicknessPanel.setAlignmentX(0.0F);
      JPanel toolButtons = new JPanel();
      if (Settings.calPunch != null) {
         this.punchButton = new JCustomButton("Punch: " + Settings.calPunch.getName());
      } else {
         this.punchButton = new JCustomButton("Select a Punch");
      }

      if (Settings.calDie != null) {
         this.dieButton = new JCustomButton("Die: " + Settings.calDie.getName());
      } else {
         this.dieButton = new JCustomButton("Select a Die");
      }

      this.punchButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.killTimers();
            HomePage.this.homeFrame.dispose();
            Settings.log.finest("Disposing of home page");
            new ToolLibraryPage();
            Settings.log.finest("punch button clicked");
         }
      });
      this.dieButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.killTimers();
            HomePage.this.homeFrame.dispose();
            Settings.log.finest("Disposing of home page");
            new ToolLibraryPage();
            Settings.log.finest("die button clicked");
         }
      });
      this.punchButton.setMaximumSize(new Dimension(150, 80));
      this.dieButton.setMaximumSize(new Dimension(150, 80));
      toolButtons.setLayout(new BoxLayout(toolButtons, 1));
      toolButtons.add(this.punchButton);
      toolButtons.add(new Filler(new Dimension(5, 5), new Dimension(50, 15), new Dimension(50, 30)));
      toolButtons.add(this.dieButton);
      toolButtons.setAlignmentX(0.0F);
      if (!Settings.calibrated) {
         leftPanel.add(this.step1Label);
         leftPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
         if (((Axis)Settings.axes.get(1)).getEnabled()) {
            leftPanel.add(this.step2Label);
            leftPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
            leftPanel.add(this.step3Label);
         }
      }

      if (Settings.floatingCalibration && ((Axis)Settings.axes.get(1)).getEnabled()) {
         leftPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
         leftPanel.add(thicknessPanel);
         leftPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
         leftPanel.add(toolButtons);
      }

      if (Settings.calibrated) {
         this.thickness.setEnabled(false);
         this.punchButton.setEnabled(false);
         this.dieButton.setEnabled(false);
         this.thickness.removeActionListener(this.thickness.getActionListeners()[0]);
      }

      JPanel rightPanel = new JPanel();
      this.homeFrame.getContentPane().add(rightPanel, "Center");
      rightPanel.setLayout(new BoxLayout(rightPanel, 1));
      rightPanel.setMinimumSize(new Dimension(300, 100));
      axisPanels = new ArrayList();

      int i;
      for(i = 0; i < Settings.axes.size(); ++i) {
         if (((Axis)Settings.axes.get(i)).getEnabled()) {
            axisPanels.add(new AxisPanel((Axis)Settings.axes.get(i)));
         }
      }

      for(i = 0; i < axisPanels.size(); ++i) {
         rightPanel.add(((AxisPanel)axisPanels.get(i)).getAxisPanel());
      }

      JPanel buttonPanel = new JPanel();
      this.homeFrame.getContentPane().add(buttonPanel, "South");
      buttonPanel.setLayout(new FlowLayout(1, 30, 0));
      buttonPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
      JButton powerButton = new JBottomButton("Power", "power.png");
      powerButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Power", "Are you sure you want to turn off the CNC600?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  try {
                     Communications.bus.close();
                     SystemCommands.closeDatabase();
                  } catch (Exception var4) {
                     Settings.log.log(Level.SEVERE, "Exit routine encountered critical error", var4);
                  }

                  if (SystemCommands.getOS().equals("Linux")) {
                     try {
                        Runtime.getRuntime().exec("sudo shutdown -h now");
                     } catch (Exception var3) {
                        Settings.log.log(Level.SEVERE, "Shutdown", var3);
                     }
                  }

               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
               }
            }, false);
         }
      });
      buttonPanel.add(powerButton);
      JButton recallButton = new JBottomButton("Recall Job", "recall.png");
      recallButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.killTimers();
            HomePage.this.homeFrame.dispose();
            Settings.log.finest("Disposing of home page");
            new RecallJobPage();
            Settings.log.finest("Recall button clicked");
         }
      });
      buttonPanel.add(recallButton);
      JButton createButton = new JBottomButton("Create Job", "edit.png");
      createButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.killTimers();
            HomePage.this.homeFrame.dispose();
            Settings.log.finest("Disposing of home page");
            new EditJobPage();
            Settings.log.finest("Create Job button clicked");
         }
      });
      buttonPanel.add(createButton);
      JButton settingsButton = new JBottomButton("Settings", "settings.png");
      settingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.killTimers();
            HomePage.this.homeFrame.dispose();
            Settings.log.finest("Disposing of home page");
            new SettingsPage();
            Settings.log.finest("Settings page button clicked");
         }
      });
      buttonPanel.add(settingsButton);
      calibrateButton = new JBottomButton("CAL", "calibrate.png");
      calibrateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            HomePage.this.calibrateButtonPressed();
         }
      });
      this.buttonDefault = calibrateButton.getBackground();
      buttonPanel.add(calibrateButton);
      this.homeFrame.setVisible(true);
      Settings.log.finest("Initalized home page");
   }

   private void calibrateButtonPressed() {
      if (SystemCommands.getOS().equals("Linux") && !Settings.demoMode) {
         this.advanceCalibrationRoutine();
      } else if (Settings.demoMode) {
         Settings.calibrated = true;
         this.step1Label.setVisible(false);
         this.step2Label.setVisible(false);
         this.step3Label.setVisible(false);
         this.timerBTCal.stop();
         this.timerLBCal.stop();
         calibrateButton.setBackground(this.buttonDefault);
         calibrationLabel.setForeground(Color.BLACK);
         calibrationLabel.setText("System Calibrated: DEMO MODE");
         this.thickness.setEnabled(false);
         this.punchButton.setEnabled(false);
         this.dieButton.setEnabled(false);
         this.thickness.removeActionListener(this.thickness.getActionListeners()[0]);
      } else {
         Settings.calibrated = true;
         this.step1Label.setVisible(false);
         this.step2Label.setVisible(false);
         this.step3Label.setVisible(false);
         this.timerBTCal.stop();
         this.timerLBCal.stop();
         calibrateButton.setBackground(this.buttonDefault);
         calibrationLabel.setForeground(Color.BLACK);
         calibrationLabel.setText("System Calibrated: DEMO MODE");
         this.thickness.setEnabled(false);
         this.punchButton.setEnabled(false);
         this.dieButton.setEnabled(false);
         this.thickness.removeActionListener(this.thickness.getActionListeners()[0]);
      }

   }

   private void advanceCalibrationRoutine() {
      if (!Settings.floatingCalibration || !((Axis)Settings.axes.get(1)).getEnabled() || Settings.calibrated || Settings.calThickness != 0.0D && Settings.calPunch != null && Settings.calDie != null) {
         if (!((Axis)Settings.axes.get(0)).getEnabled() && !((Axis)Settings.axes.get(1)).getEnabled() && !((Axis)Settings.axes.get(2)).getEnabled()) {
            new NotificationPage("Notice", "You have no axes enabled, am I new?");
            Settings.calibrated = true;
            this.step1Label.setVisible(false);
            this.step2Label.setVisible(false);
            this.step3Label.setVisible(false);
            this.timerBTCal.stop();
            this.timerLBCal.stop();
            calibrateButton.setBackground(this.buttonDefault);
            calibrationLabel.setForeground(Color.BLACK);
            calibrationLabel.setText("System Calibrated: SETUP MODE");
         }

         if (!((Axis)Settings.axes.get(1)).getEnabled() && calibrationRoutineStep != 2) {
            calibrationRoutineStep = 1;
         }

         switch(calibrationRoutineStep) {
         case 0:
            Settings.log.finer("Calibrate button pressed first time");
            Communications.reset();
            if (((Axis)Settings.axes.get(1)).getEnabled()) {
               Communications.initializeYAxis();
            }

            this.timerLBStep.stop();
            this.step1Label.setForeground(Color.GRAY);
            this.timerLBStep2.start();
            this.timerLBStep3.start();
            ++calibrationRoutineStep;
            break;
         case 1:
            recordX = true;
            Settings.log.finer("Calibrate button pressed second time");
            calibrateButton.setEnabled(false);
            new NotificationPage("Running Calibration", "please wait", true);
            NotificationPage.bar.setValue(1);
            this.timerLBStep.stop();
            this.timerLBStep2.stop();
            this.timerLBStep3.stop();
            this.step2Label.setForeground(Color.GRAY);
            this.step3Label.setForeground(Color.GRAY);
            Communications.reset();
            int s;
            if (((Axis)Settings.axes.get(1)).getEnabled()) {
               s = ((Axis)Settings.axes.get(1)).getPosition();
               Communications.setEncoderValue((Axis)Settings.axes.get(1), 0);
               if (s == 0) {
                  new TwoButtonPromptPage("Notice", "The system detected that the ram has not moved, is this true?", "Yes", new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        ((JFrame)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                     }
                  }, "No", new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        new NotificationPage("Warning", "Linear scale did not report that the ram moved, is it plugged in?");
                        ((JFrame)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                     }
                  }, false);
               }
            }

            if (!((Axis)Settings.axes.get(0)).getEnabled()) {
               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  Communications.calibrateRAxis();
               }
            } else {
               s = 0;
               Communications.calibrateXAxis();
               if (SystemCommands.isRPi()) {
                  try {
                     while(!Settings.sxmove) {
                        Thread.sleep(25L);
                     }
                  } catch (Exception var4) {
                     var4.printStackTrace();
                  }
               } else {
                  while(!Settings.sxmove) {
                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException var3) {
                        var3.printStackTrace();
                     }

                     ++s;
                     if (Settings.xMotStall) {
                        break;
                     }
                  }
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  Timer timer = new Timer(5000, new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        Communications.calibrateRAxis();
                        ((Timer)e.getSource()).stop();
                     }
                  });
                  timer.start();
               }
            }

            this.step1Label.setVisible(false);
            this.step2Label.setVisible(false);
            this.step3Label.setVisible(false);
            this.timerBTCal.stop();
            this.timerLBCal.stop();
            calibrateButton.setBackground(this.buttonDefault);
            calibrationLabel.setForeground(Color.BLACK);
            Listener.watched = false;
            if (((Axis)Settings.axes.get(0)).getEnabled()) {
               if (Settings.advancedCalibration) {
                  calibrationLabel.setText("System Calibrated: running tests");
                  (new calibrateRoutine(3, ((Axis)Settings.axes.get(0)).getAxisLength() - 2.1D, Settings.advancedCalibrationPosition)).start();
               } else {
                  calibrationLabel.setText("System Calibrated");
                  recordX = false;
                  Settings.calibrated = true;
                  calibrateButton.setEnabled(true);
                  if (NotificationPage.containsKey("Running Calibration")) {
                     NotificationPage.removePage("Running Calibration");
                  }
               }
            } else {
               calibrationLabel.setText("System Calibrated");
               recordX = false;
               Settings.calibrated = true;
               calibrateButton.setEnabled(true);
               if (NotificationPage.containsKey("Running Calibration")) {
                  NotificationPage.removePage("Running Calibration");
               }
            }

            this.thickness.setEnabled(false);
            this.punchButton.setEnabled(false);
            this.dieButton.setEnabled(false);
            this.thickness.removeActionListener(this.thickness.getActionListeners()[0]);
            ++calibrationRoutineStep;
            break;
         case 2:
            Settings.log.finer("Calibrate button pressed - System no longer calibrated!");
            Settings.calibrated = false;
            calibrationRoutineStep = 0;
            this.homeFrame.dispose();
            new HomePage();
         }

      } else {
         new NotificationPage("Notice", "Floating calibration is enabled, Thickness and tools need to be set");
      }
   }

   public void killTimers() {
      Settings.log.finest("Killing Timers");
      this.timerLBCal.stop();
      this.timerLBStep.stop();
      this.timerBTCal.stop();
      this.timerI2CRead.stop();
      this.timerLBStep2.stop();
      this.timerLBStep3.stop();
   }

   class updateDisplayPosition implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         try {
            int cnt = 0;
            if (Settings.displayCounts) {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(0)).getPosition()));
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(1)).getPosition()));
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(2)).getPosition()));
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
                  ++cnt;
               }
            } else {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(0)).getPositionInches());
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(1)).getPositionInches());
                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  if (((Axis)Settings.axes.get(2)).getZeroAdjust()) {
                     if (Settings.calibrated && Settings.floatingCalibration) {
                        ((AxisPanel)HomePage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() + ((Axis)Settings.axes.get(2)).getZeroOffset() - Settings.calDie.getHeight());
                     } else {
                        ((AxisPanel)HomePage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() + ((Axis)Settings.axes.get(2)).getZeroOffset());
                     }
                  } else {
                     ((AxisPanel)HomePage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches());
                  }

                  ((AxisPanel)HomePage.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
               }
            }
         } catch (Exception var3) {
            Settings.log.log(Level.SEVERE, "Exception thrown while updating axis position: ", var3);
         }

      }
   }
}
