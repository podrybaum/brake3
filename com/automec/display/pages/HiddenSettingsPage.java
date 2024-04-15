package com.automec.display.pages;

import com.automec.Communications;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

public class HiddenSettingsPage {
   private JFrame settingsFrame = new JFrame("Settings");
   public JButton autoAdvanceSourceButton;
   public static Timestamp pressed;
   public static boolean ignoreQueue = false;
   public Timer timerI2CRead;
   public static JLabel brightnessLabel;
   public static JLabel extADVSW;
   public static JLabel ecmdValue;
   public static JLabel eargValue;
   public static JLabel ecsumValue;
   public static JLabel efreqValue;
   public static JLabel ermotstallValue;
   public static JLabel exmotstallValue;
   public static JLabel excableValue;
   public static JLabel eycableValue;
   public static JLabel ercableValue;
   public static JLabel eestopValue;
   public static JLabel expowerValue;
   public static JLabel erpowerValue;
   public static JLabel sxmoveValue;
   public static JLabel srmoveValue;
   public static JLabel emacpowerrecycleValue;
   public static JLabel lastbyteValue;

   public HiddenSettingsPage() {
      this.initialize();
   }

   private void initialize() {
      this.settingsFrame.setDefaultCloseOperation(3);
      this.settingsFrame.setSize(1024, 768);
      this.settingsFrame.setUndecorated(true);
      this.settingsFrame.setFocusable(true);
      this.settingsFrame.getContentPane().setLayout(new BorderLayout(0, 0));
      this.timerI2CRead = new Timer(30, new HiddenSettingsPage.updateDisplayPositionHidden());
      this.timerI2CRead.start();
      JPanel titlePanel = new JPanel();
      this.settingsFrame.getContentPane().add(titlePanel, "North");
      JLabel titleLabel = new JLabel("Hidden Settings");
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      titlePanel.add(titleLabel);
      JPanel leftPanel = new JPanel();
      this.settingsFrame.getContentPane().add(leftPanel, "West");
      GridBagLayout leftPanelGridbag = new GridBagLayout();
      leftPanelGridbag.columnWidths = new int[]{150, 110, 110};
      leftPanelGridbag.rowHeights = new int[7];
      leftPanelGridbag.columnWeights = new double[]{0.0D, 0.0D, 0.0D};
      leftPanelGridbag.rowWeights = new double[]{1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D};
      leftPanel.setLayout(leftPanelGridbag);
      JLabel queueLabel = new JLabel("");
      queueLabel.setFont(DisplayComponents.pageTextFont);
      queueLabel.setHorizontalAlignment(0);
      GridBagConstraints gbc_lblUnits = new GridBagConstraints();
      gbc_lblUnits.anchor = 17;
      gbc_lblUnits.insets = new Insets(0, 0, 5, 5);
      gbc_lblUnits.gridx = 0;
      gbc_lblUnits.gridy = 0;
      leftPanel.add(queueLabel, gbc_lblUnits);
      final JButton button1 = new JButton("Display: ");
      button1.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
      gbc_btnNewButton.fill = 1;
      gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
      gbc_btnNewButton.gridx = 1;
      gbc_btnNewButton.gridy = 0;
      leftPanel.add(button1, gbc_btnNewButton);
      button1.setBackground(DisplayComponents.Inactive);
      if (Settings.displayCounts) {
         button1.setText("Display: counts");
      } else {
         button1.setText("Display: units");
      }

      JButton button2 = new JButton("NULL");
      button2.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
      gbc_btnNewButton_1.fill = 1;
      gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
      gbc_btnNewButton_1.gridx = 2;
      gbc_btnNewButton_1.gridy = 0;
      leftPanel.add(button2, gbc_btnNewButton_1);
      button2.setBackground(DisplayComponents.Inactive);
      button1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.displayCounts) {
               button1.setText("Display: units");
               Settings.displayCounts = false;
            } else {
               button1.setText("Display: counts");
               Settings.displayCounts = true;
            }

         }
      });
      button2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      JLabel modeLabel = new JLabel("");
      modeLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblMode = new GridBagConstraints();
      gbc_lblMode.anchor = 17;
      gbc_lblMode.insets = new Insets(0, 0, 5, 5);
      gbc_lblMode.gridx = 0;
      gbc_lblMode.gridy = 1;
      leftPanel.add(modeLabel, gbc_lblMode);
      JButton button3 = new JButton("NULL");
      button3.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDepth = new GridBagConstraints();
      gbc_btnDepth.fill = 1;
      gbc_btnDepth.insets = new Insets(0, 0, 5, 5);
      gbc_btnDepth.gridx = 1;
      gbc_btnDepth.gridy = 1;
      leftPanel.add(button3, gbc_btnDepth);
      button3.setBackground(DisplayComponents.Active);
      JButton button4 = new JButton("NULL");
      button4.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnAngle = new GridBagConstraints();
      gbc_btnAngle.fill = 1;
      gbc_btnAngle.insets = new Insets(0, 0, 5, 0);
      gbc_btnAngle.gridx = 2;
      gbc_btnAngle.gridy = 1;
      leftPanel.add(button4, gbc_btnAngle);
      button4.setBackground(Color.LIGHT_GRAY);
      button3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      button4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      extADVSW = new JLabel("");
      extADVSW.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblAngleAdjustBy = new GridBagConstraints();
      gbc_lblAngleAdjustBy.anchor = 17;
      gbc_lblAngleAdjustBy.insets = new Insets(0, 0, 5, 5);
      gbc_lblAngleAdjustBy.gridx = 0;
      gbc_lblAngleAdjustBy.gridy = 2;
      leftPanel.add(extADVSW, gbc_lblAngleAdjustBy);
      final JButton demoMode = new JButton("Demo Mode");
      demoMode.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDepth_1 = new GridBagConstraints();
      gbc_btnDepth_1.fill = 1;
      gbc_btnDepth_1.insets = new Insets(0, 0, 5, 5);
      gbc_btnDepth_1.gridx = 1;
      gbc_btnDepth_1.gridy = 2;
      leftPanel.add(demoMode, gbc_btnDepth_1);
      demoMode.setBackground(Color.LIGHT_GRAY);
      JButton button6 = new JButton("NULL");
      button6.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnAngle_1 = new GridBagConstraints();
      gbc_btnAngle_1.fill = 1;
      gbc_btnAngle_1.insets = new Insets(0, 0, 5, 0);
      gbc_btnAngle_1.gridx = 2;
      gbc_btnAngle_1.gridy = 2;
      leftPanel.add(button6, gbc_btnAngle_1);
      button6.setBackground(DisplayComponents.Active);
      if (!Settings.demoMode) {
         demoMode.setBackground(DisplayComponents.Inactive);
      } else {
         demoMode.setBackground(DisplayComponents.Active);
      }

      demoMode.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.demoMode) {
               Settings.demoMode = false;
               demoMode.setBackground(DisplayComponents.Inactive);
            } else {
               Settings.demoMode = true;
               demoMode.setBackground(DisplayComponents.Active);
            }

         }
      });
      button6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      JLabel screenSaverLabel = new JLabel("");
      screenSaverLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblScreenSaver = new GridBagConstraints();
      gbc_lblScreenSaver.anchor = 17;
      gbc_lblScreenSaver.insets = new Insets(0, 0, 5, 5);
      gbc_lblScreenSaver.gridx = 0;
      gbc_lblScreenSaver.gridy = 3;
      leftPanel.add(screenSaverLabel, gbc_lblScreenSaver);
      JButton cleanSlate = new JButton("Clean Slate Protocol");
      cleanSlate.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnOn = new GridBagConstraints();
      gbc_btnOn.fill = 1;
      gbc_btnOn.insets = new Insets(0, 0, 5, 5);
      gbc_btnOn.gridx = 1;
      gbc_btnOn.gridy = 3;
      leftPanel.add(cleanSlate, gbc_btnOn);
      cleanSlate.setBackground(DisplayComponents.Active);
      JButton importR25Jobs = new JButton("Import Jobs R25");
      importR25Jobs.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnExit = new GridBagConstraints();
      gbc_btnExit.fill = 1;
      gbc_btnExit.insets = new Insets(0, 0, 5, 0);
      gbc_btnExit.gridx = 2;
      gbc_btnExit.gridy = 3;
      leftPanel.add(importR25Jobs, gbc_btnExit);
      importR25Jobs.setBackground(DisplayComponents.Inactive);
      cleanSlate.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ArrayList<Job> jobs = SystemCommands.getJobs();
            Iterator var4 = jobs.iterator();

            while(var4.hasNext()) {
               Job j = (Job)var4.next();
               SystemCommands.deleteJob(j.getName());
            }

         }
      });
      importR25Jobs.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               String url;
               if (Settings.selectedUSB != null) {
                  url = "jdbc:sqlite:" + File.separator + Settings.selectedUSB.path + File.separator + "r25" + File.separator + "DATABASE";
               } else {
                  url = "jdbc:sqlite:" + File.separator + "usr" + File.separator + "local" + File.separator + "sbin" + File.separator + "DATABASE";
               }

               (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600")).mkdirs();

               try {
                  Connection conn = DriverManager.getConnection(url);
                  if (conn != null) {
                     Connection databaseConnector2 = conn;
                     String sql = "SELECT JOBNAME, RAMSLOWPOS, ID, DATE, EDITDATE, PARTCNTS FROM NEWJOB";
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql);

                     while(rs.next()) {
                        System.out.println("Importing job: " + rs.getString("JOBNAME"));
                        Job j;
                        if (!rs.getString("RAMSLOWPOS").equals("")) {
                           j = new Job(rs.getString("JOBNAME"), Mode.DEPTH, Double.parseDouble(rs.getString("RAMSLOWPOS")), Units.INCHES, new ArrayList(), Settings.axes);
                        } else {
                           j = new Job(rs.getString("JOBNAME"), Mode.DEPTH, ((Axis)Settings.axes.get(1)).getSlowDistance(), Units.INCHES, new ArrayList(), Settings.axes);
                        }

                        String sql2 = "SELECT BEND, CYC, DISTANCE, TYPE, DELAY, BGDISTANCE, ADJUST, DEPTH, RHEIGHT, BHEIGHT FROM CREATEJOB WHERE job_id = ?";
                        PreparedStatement pstmt = databaseConnector2.prepareStatement(sql2);
                        pstmt.setString(1, rs.getString("ID"));
                        ResultSet rs2 = pstmt.executeQuery();
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy");
                        j.setEdited(LocalDateTime.parse(rs.getString("EDITDATE"), dtf));
                        j.setCreated(LocalDateTime.parse(rs.getString("DATE"), dtf));
                        j.setParts(Integer.parseInt(rs.getString("PARTCNTS")));
                        j.setLocation(Location.LOCAL);
                        new ArrayList();

                        while(rs2.next()) {
                           System.out.println("Importing bend: " + rs2.getString("BEND") + " cyc: " + rs2.getString("CYC"));
                           if (Integer.parseInt(rs2.getString("BEND")) == 0) {
                              try {
                                 j.getOffsets().add(Double.parseDouble(rs2.getString("BGDISTANCE")));
                              } catch (Exception var23) {
                                 j.getOffsets().add(0.0D);
                              }

                              try {
                                 j.getOffsets().add(Double.parseDouble(rs2.getString("DEPTH")));
                              } catch (Exception var22) {
                                 j.getOffsets().add(0.0D);
                              }

                              try {
                                 j.getOffsets().add(Double.parseDouble(rs2.getString("BHEIGHT")));
                              } catch (Exception var21) {
                                 j.getOffsets().add(0.0D);
                              }
                           }

                           if (rs2.getString("CYC") != null && !rs2.getString("CYC").equals("")) {
                              ArrayList<AxisValues> a = new ArrayList();
                              ArrayList<String> xv = new ArrayList();
                              xv.add(rs2.getString("DISTANCE"));
                              xv.add(rs2.getString("TYPE"));
                              xv.add(rs2.getString("DELAY"));
                              xv.add(rs2.getString("BGDISTANCE"));
                              xv.add(rs2.getString("ADJUST"));
                              ArrayList<String> yv = new ArrayList();
                              yv.add(rs2.getString("DEPTH"));
                              yv.add(rs2.getString("RHEIGHT"));
                              ArrayList<String> rv = new ArrayList();
                              rv.add(rs2.getString("BHEIGHT"));
                              a.add(new AxisValues(((Axis)Settings.axes.get(0)).getAxisType(), ((Axis)Settings.axes.get(0)).getShortName(), xv, Mode.DEPTH));
                              a.add(new AxisValues(((Axis)Settings.axes.get(1)).getAxisType(), ((Axis)Settings.axes.get(1)).getShortName(), yv, Mode.DEPTH));
                              a.add(new AxisValues(((Axis)Settings.axes.get(2)).getAxisType(), ((Axis)Settings.axes.get(2)).getShortName(), rv, Mode.DEPTH));

                              try {
                                 Bend b = new Bend(j, Integer.parseInt(rs2.getString("CYC")), a);
                                 j.getBends().add(b);
                              } catch (Exception var20) {
                              }
                           }
                        }

                        SystemCommands.writeJob(j.getName(), (new Gson()).toJson(j, Job.class));
                     }
                  }
               } catch (SQLException var24) {
                  var24.printStackTrace();
               }
            } catch (Exception var25) {
               var25.printStackTrace();
            }

         }
      });
      JLabel lastRecallJobLabel = new JLabel("");
      lastRecallJobLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblLastRecallJob = new GridBagConstraints();
      gbc_lblLastRecallJob.anchor = 17;
      gbc_lblLastRecallJob.insets = new Insets(0, 0, 5, 5);
      gbc_lblLastRecallJob.gridx = 0;
      gbc_lblLastRecallJob.gridy = 4;
      leftPanel.add(lastRecallJobLabel, gbc_lblLastRecallJob);
      JButton importJobs = new JButton("Import Jobs USB");
      importJobs.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnEnable = new GridBagConstraints();
      gbc_btnEnable.fill = 1;
      gbc_btnEnable.insets = new Insets(0, 0, 5, 5);
      gbc_btnEnable.gridx = 1;
      gbc_btnEnable.gridy = 4;
      leftPanel.add(importJobs, gbc_btnEnable);
      JButton exportJobs = new JButton("Export Jobs USB");
      exportJobs.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDisable = new GridBagConstraints();
      gbc_btnDisable.fill = 1;
      gbc_btnDisable.insets = new Insets(0, 0, 5, 0);
      gbc_btnDisable.gridx = 2;
      gbc_btnDisable.gridy = 4;
      leftPanel.add(exportJobs, gbc_btnDisable);
      importJobs.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Iterator var3 = Settings.selectedUSB.getJobs().iterator();

            while(var3.hasNext()) {
               Job j = (Job)var3.next();
               j.saveJob(Location.LOCAL);
            }

         }
      });
      exportJobs.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Iterator var3 = SystemCommands.getJobs().iterator();

            while(var3.hasNext()) {
               Job j = (Job)var3.next();
               j.saveJob(Location.USB);
            }

         }
      });
      JLabel screenBrightnessLabel = new JLabel("Uninitialized");
      screenBrightnessLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblScreenBrightness = new GridBagConstraints();
      gbc_lblScreenBrightness.anchor = 17;
      gbc_lblScreenBrightness.insets = new Insets(0, 0, 5, 5);
      gbc_lblScreenBrightness.gridx = 0;
      gbc_lblScreenBrightness.gridy = 5;
      leftPanel.add(screenBrightnessLabel, gbc_lblScreenBrightness);
      JButton clearOdometer = new JButton("Clear Odometer");
      clearOdometer.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnmore = new GridBagConstraints();
      gbc_btnmore.fill = 1;
      gbc_btnmore.insets = new Insets(0, 0, 5, 5);
      gbc_btnmore.gridx = 1;
      gbc_btnmore.gridy = 5;
      leftPanel.add(clearOdometer, gbc_btnmore);
      JButton button12 = new JButton("NULL");
      button12.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnless = new GridBagConstraints();
      gbc_btnless.fill = 1;
      gbc_btnless.insets = new Insets(0, 0, 5, 0);
      gbc_btnless.gridx = 2;
      gbc_btnless.gridy = 5;
      leftPanel.add(button12, gbc_btnless);
      brightnessLabel = new JLabel(String.valueOf(((Axis)Settings.axes.get(1)).getPosition()));
      brightnessLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
      gbc_lblNewLabel_1.insets = new Insets(10, 0, 0, 10);
      gbc_lblNewLabel_1.anchor = 12;
      gbc_lblNewLabel_1.gridx = 2;
      gbc_lblNewLabel_1.gridy = 6;
      leftPanel.add(brightnessLabel, gbc_lblNewLabel_1);
      clearOdometer.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.xOdometer = 0.0D;
            Settings.rOdometer = 0.0D;
            Settings.yOdometer = 0.0D;
         }
      });
      JLabel lockDefaultLabel = new JLabel("Uninitialized");
      lockDefaultLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblLockDefault = new GridBagConstraints();
      gbc_lblLockDefault.anchor = 17;
      gbc_lblLockDefault.insets = new Insets(0, 0, 0, 5);
      gbc_lblLockDefault.gridx = 0;
      gbc_lblLockDefault.gridy = 6;
      leftPanel.add(lockDefaultLabel, gbc_lblLockDefault);
      JButton lockDefaultButton = new JButton("set y 0");
      lockDefaultButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnLock = new GridBagConstraints();
      gbc_btnLock.fill = 1;
      gbc_btnLock.insets = new Insets(0, 0, 0, 5);
      gbc_btnLock.gridx = 1;
      gbc_btnLock.gridy = 6;
      leftPanel.add(lockDefaultButton, gbc_btnLock);
      lockDefaultButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Communications.setEncoderValue((Axis)Settings.axes.get(1), 0);

            try {
               Thread.sleep(500L);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }

            HiddenSettingsPage.brightnessLabel.setText(String.valueOf(((Axis)Settings.axes.get(1)).getPosition()));
            Communications.printNextStatus = true;
         }
      });
      Dimension buttonSize = new Dimension(120, 80);
      JPanel rightPanel = new JPanel();
      this.settingsFrame.getContentPane().add(rightPanel, "East");
      GridBagLayout gbl_rightPanel = new GridBagLayout();
      gbl_rightPanel.columnWidths = new int[4];
      gbl_rightPanel.rowHeights = new int[6];
      gbl_rightPanel.columnWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      gbl_rightPanel.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      rightPanel.setLayout(gbl_rightPanel);
      JLabel lblNewLabel = new JLabel("Error Bytes");
      lblNewLabel.setFont(DisplayComponents.pageHeaderFont);
      GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
      gbc_lblNewLabel.gridwidth = 2;
      gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
      gbc_lblNewLabel.gridx = 0;
      gbc_lblNewLabel.gridy = 0;
      rightPanel.add(lblNewLabel, gbc_lblNewLabel);
      ecmdValue = new JLabel("ecmdValue");
      ecmdValue.setOpaque(true);
      rightPanel.add(ecmdValue, DisplayComponents.GenerateConstraints(0, 1));
      eargValue = new JLabel("eargValue");
      eargValue.setOpaque(true);
      rightPanel.add(eargValue, DisplayComponents.GenerateConstraints(0, 2));
      ecsumValue = new JLabel("ecsumValue");
      ecsumValue.setOpaque(true);
      rightPanel.add(ecsumValue, DisplayComponents.GenerateConstraints(0, 3));
      efreqValue = new JLabel("efreqValue");
      efreqValue.setOpaque(true);
      rightPanel.add(efreqValue, DisplayComponents.GenerateConstraints(0, 4));
      ermotstallValue = new JLabel("ermotstallValue");
      ermotstallValue.setOpaque(true);
      rightPanel.add(ermotstallValue, DisplayComponents.GenerateConstraints(0, 5));
      exmotstallValue = new JLabel("exmotstallValue");
      exmotstallValue.setOpaque(true);
      rightPanel.add(exmotstallValue, DisplayComponents.GenerateConstraints(0, 6));
      excableValue = new JLabel("excableValue");
      excableValue.setOpaque(true);
      rightPanel.add(excableValue, DisplayComponents.GenerateConstraints(0, 7));
      eycableValue = new JLabel("eycableValue");
      eycableValue.setOpaque(true);
      rightPanel.add(eycableValue, DisplayComponents.GenerateConstraints(0, 8));
      ercableValue = new JLabel("ercableValue");
      ercableValue.setOpaque(true);
      rightPanel.add(ercableValue, DisplayComponents.GenerateConstraints(1, 1));
      eestopValue = new JLabel("eestopValue");
      eestopValue.setOpaque(true);
      rightPanel.add(eestopValue, DisplayComponents.GenerateConstraints(1, 2));
      expowerValue = new JLabel("expowerValue");
      expowerValue.setOpaque(true);
      rightPanel.add(expowerValue, DisplayComponents.GenerateConstraints(1, 3));
      erpowerValue = new JLabel("erpowerValue");
      erpowerValue.setOpaque(true);
      rightPanel.add(erpowerValue, DisplayComponents.GenerateConstraints(1, 4));
      sxmoveValue = new JLabel("sxmoveValue");
      sxmoveValue.setOpaque(true);
      rightPanel.add(sxmoveValue, DisplayComponents.GenerateConstraints(1, 5));
      srmoveValue = new JLabel("srmoveValue");
      srmoveValue.setOpaque(true);
      rightPanel.add(srmoveValue, DisplayComponents.GenerateConstraints(1, 6));
      emacpowerrecycleValue = new JLabel("emacpowerrecycleValue");
      emacpowerrecycleValue.setOpaque(true);
      rightPanel.add(emacpowerrecycleValue, DisplayComponents.GenerateConstraints(1, 7));
      lastbyteValue = new JLabel("lastbyte");
      lastbyteValue.setOpaque(true);
      rightPanel.add(lastbyteValue, DisplayComponents.GenerateConstraints(1, 8));
      JPanel buttonPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout)buttonPanel.getLayout();
      flowLayout.setAlignment(0);
      flowLayout.setHgap(0);
      this.settingsFrame.getContentPane().add(buttonPanel, "South");
      JButton homeButton = DisplayComponents.GenerateButton("Home", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            HiddenSettingsPage.this.settingsFrame.dispose();
            Settings.log.finest("settings page disposed");
            SystemCommands.writeSettingsFile();
            new HomePage();
         }
      });
      buttonPanel.add(homeButton);
      homeButton.setPreferredSize(buttonSize);
      buttonPanel.add(DisplayComponents.GenerateButton("<html>Axis<br/>Settings</html>", new HiddenSettingsPage.HiddenSettingsButtonAction(this, (Axis)Settings.axes.get(0))));
      buttonPanel.getComponent(1).setPreferredSize(buttonSize);
      JButton softwareSettingsButton = DisplayComponents.GenerateButton("SOFTWARE");
      buttonPanel.add(softwareSettingsButton);
      softwareSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("software button pressed");
            new SoftwarePage();
         }
      });
      softwareSettingsButton.setPreferredSize(buttonSize);
      JButton clockSettingsButton = DisplayComponents.GenerateButton("<html>Set<br/>Clock</html>");
      clockSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("set clock button pressed");
            SystemCommands.setClock();
         }
      });
      clockSettingsButton.setPreferredSize(buttonSize);
      JButton ethernetSettingsButton = DisplayComponents.GenerateButton("Ethernet");
      buttonPanel.add(ethernetSettingsButton);
      ethernetSettingsButton.setEnabled(false);
      ethernetSettingsButton.setPreferredSize(buttonSize);
      JButton systemTestSettingsButton = DisplayComponents.GenerateButton("<html>System<br/>Test</html>");
      buttonPanel.add(systemTestSettingsButton);
      systemTestSettingsButton.setPreferredSize(buttonSize);
      systemTestSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new ToolLibraryPage();
         }
      });
      JButton touchScreenCalibrateButton = DisplayComponents.GenerateButton("<html>Touch<br/>Screen<br/>Calibrate</html>", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("calibrate button pressed");
            SystemCommands.calibrateScreen();
         }
      });
      buttonPanel.add(touchScreenCalibrateButton);
      touchScreenCalibrateButton.setPreferredSize(buttonSize);
      JButton testButton = DisplayComponents.GenerateButton("Test", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      testButton.setPreferredSize(buttonSize);
      buttonPanel.add(testButton);
      JButton hiddenSettings = new JButton();
      hiddenSettings.setBackground(new Color(238, 238, 238));
      hiddenSettings.setContentAreaFilled(false);
      hiddenSettings.setBorder((Border)null);
      hiddenSettings.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      hiddenSettings.setPreferredSize(buttonSize);
      hiddenSettings.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            SettingsPage.pressed = new Timestamp(System.currentTimeMillis() + 5000L);
         }

         public void mouseReleased(MouseEvent arg0) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.after(SettingsPage.pressed)) {
               System.out.println("success");
            }

         }
      });
      hiddenSettings.setPreferredSize(new Dimension(40, 40));
      buttonPanel.add(hiddenSettings);
      this.settingsFrame.setVisible(true);
      Settings.log.finest("hidden settings page initialized");
   }

   public JFrame getFrame() {
      return this.settingsFrame;
   }

   class HiddenSettingsButtonAction implements ActionListener {
      private HiddenSettingsPage reference;
      private Axis axis;

      public HiddenSettingsButtonAction(HiddenSettingsPage reference, Axis axis) {
         this.reference = reference;
         this.axis = axis;
      }

      public void actionPerformed(ActionEvent arg0) {
         Settings.log.finest("axis settings button pressed");
         new HiddenAxisSettingsPage(this.axis);
         Settings.log.finest("settings page disposed");
         SystemCommands.writeSettingsFile();
         this.reference.getFrame().dispose();
      }
   }

   class updateDisplayPositionHidden implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         try {
            HiddenSettingsPage.brightnessLabel.setText(String.valueOf(((Axis)Settings.axes.get(1)).getPosition()));
            if (Settings.getExtAdvSwitch()) {
               HiddenSettingsPage.extADVSW.setText("EXT ADV: 1");
            } else {
               HiddenSettingsPage.extADVSW.setText("EXT ADV: 0");
            }

            if ((Settings.errorByte2 & 1) == 1) {
               HiddenSettingsPage.ecmdValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.ecmdValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 2) == 2) {
               HiddenSettingsPage.eargValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.eargValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 4) == 4) {
               HiddenSettingsPage.ecsumValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.ecsumValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 8) == 8) {
               HiddenSettingsPage.efreqValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.efreqValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 16) == 16) {
               HiddenSettingsPage.ermotstallValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.ermotstallValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 32) == 32) {
               HiddenSettingsPage.exmotstallValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.exmotstallValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 64) == 64) {
               HiddenSettingsPage.excableValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.excableValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte2 & 128) == 128) {
               HiddenSettingsPage.eycableValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.eycableValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 1) == 1) {
               HiddenSettingsPage.ercableValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.ercableValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 2) == 2) {
               HiddenSettingsPage.eestopValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.eestopValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 4) == 4) {
               HiddenSettingsPage.expowerValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.expowerValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 8) == 8) {
               HiddenSettingsPage.erpowerValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.erpowerValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 16) == 16) {
               HiddenSettingsPage.sxmoveValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.sxmoveValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 32) == 32) {
               HiddenSettingsPage.srmoveValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.srmoveValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 64) == 64) {
               HiddenSettingsPage.emacpowerrecycleValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.emacpowerrecycleValue.setBackground(DisplayComponents.Inactive);
            }

            if ((Settings.errorByte1 & 128) == 128) {
               HiddenSettingsPage.lastbyteValue.setBackground(DisplayComponents.Active);
            } else {
               HiddenSettingsPage.lastbyteValue.setBackground(DisplayComponents.Inactive);
            }
         } catch (Exception var3) {
            Settings.log.log(Level.SEVERE, "Exception thrown while updating axis position: " + var3.getStackTrace().toString(), e);
         }

      }
   }
}
