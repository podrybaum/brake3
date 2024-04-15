package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.display.popups.USBPopup;
import com.automec.objects.Axis;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.ExtAdvPolarity;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.Timestamp;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingsPage {
   private JFrame settingsFrame = new JFrame("Settings");
   public JButton autoAdvanceSourceButton;
   public static Timestamp pressed;

   public SettingsPage() {
      this.initialize();
   }

   private void initialize() {
      this.settingsFrame.setDefaultCloseOperation(3);
      this.settingsFrame.setSize(1024, 768);
      this.settingsFrame.setUndecorated(true);
      this.settingsFrame.setFocusable(true);
      this.settingsFrame.getContentPane().setLayout(new BorderLayout(0, 0));
      Settings.activeFrame = this.settingsFrame;
      this.settingsFrame.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      JPanel titlePanel = new JPanel();
      this.settingsFrame.getContentPane().add(titlePanel, "North");
      JLabel titleLabel = new JLabel("Settings");
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
      leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      int yIndex = 0;
      JLabel unitsLabel = new JLabel("Units");
      unitsLabel.setFont(DisplayComponents.pageTextFont);
      unitsLabel.setHorizontalAlignment(0);
      GridBagConstraints gbc_lblUnits = new GridBagConstraints();
      gbc_lblUnits.anchor = 17;
      gbc_lblUnits.insets = new Insets(30, 0, 0, 5);
      gbc_lblUnits.gridx = 0;
      gbc_lblUnits.gridy = yIndex;
      leftPanel.add(unitsLabel, gbc_lblUnits);
      final JButton unitsInchesButton = new JButton("inches");
      unitsInchesButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
      gbc_btnNewButton.fill = 1;
      gbc_btnNewButton.insets = new Insets(30, 0, 0, 5);
      gbc_btnNewButton.gridx = 1;
      gbc_btnNewButton.gridy = yIndex;
      leftPanel.add(unitsInchesButton, gbc_btnNewButton);
      unitsInchesButton.setBackground(DisplayComponents.Inactive);
      final JButton unitsMmButton = new JButton("mm");
      unitsMmButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
      gbc_btnNewButton_1.fill = 1;
      gbc_btnNewButton_1.insets = new Insets(30, 0, 0, 5);
      gbc_btnNewButton_1.gridx = 2;
      int yIndex = yIndex + 1;
      gbc_btnNewButton_1.gridy = yIndex;
      leftPanel.add(unitsMmButton, gbc_btnNewButton_1);
      unitsMmButton.setBackground(DisplayComponents.Inactive);
      if (Settings.units == Units.INCHES) {
         unitsInchesButton.setBackground(DisplayComponents.Active);
      } else {
         unitsMmButton.setBackground(DisplayComponents.Active);
      }

      unitsInchesButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            unitsInchesButton.setBackground(DisplayComponents.Active);
            unitsMmButton.setBackground(DisplayComponents.Inactive);
            Settings.units = Units.INCHES;
         }
      });
      unitsMmButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            unitsMmButton.setBackground(DisplayComponents.Active);
            unitsInchesButton.setBackground(DisplayComponents.Inactive);
            Settings.units = Units.MM;
         }
      });
      JLabel modeLabel = new JLabel("Default Mode");
      modeLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblMode = new GridBagConstraints();
      gbc_lblMode.anchor = 17;
      gbc_lblMode.insets = new Insets(30, 0, 0, 5);
      gbc_lblMode.gridx = 0;
      gbc_lblMode.gridy = yIndex;
      leftPanel.add(modeLabel, gbc_lblMode);
      final JButton modeDepthButton = new JButton("Depth");
      modeDepthButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDepth = new GridBagConstraints();
      gbc_btnDepth.fill = 1;
      gbc_btnDepth.insets = new Insets(30, 0, 0, 5);
      gbc_btnDepth.gridx = 1;
      gbc_btnDepth.gridy = yIndex;
      leftPanel.add(modeDepthButton, gbc_btnDepth);
      final JButton modeAngleButton = new JButton("Angle");
      modeAngleButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnAngle = new GridBagConstraints();
      gbc_btnAngle.fill = 1;
      gbc_btnAngle.insets = new Insets(30, 0, 0, 5);
      gbc_btnAngle.gridx = 2;
      gbc_btnAngle.gridy = yIndex++;
      leftPanel.add(modeAngleButton, gbc_btnAngle);
      if (!((Axis)Settings.axes.get(1)).getEnabled()) {
         modeAngleButton.setEnabled(false);
         modeDepthButton.setEnabled(false);
      } else if (Settings.defaultMode.equals(Mode.DEPTH)) {
         modeDepthButton.setBackground(DisplayComponents.Active);
         modeAngleButton.setBackground(DisplayComponents.Inactive);
      } else {
         modeDepthButton.setBackground(DisplayComponents.Inactive);
         modeAngleButton.setBackground(DisplayComponents.Active);
      }

      modeDepthButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            modeDepthButton.setBackground(DisplayComponents.Active);
            modeAngleButton.setBackground(DisplayComponents.Inactive);
            Settings.defaultMode = Mode.DEPTH;
         }
      });
      modeAngleButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            modeAngleButton.setBackground(DisplayComponents.Active);
            modeDepthButton.setBackground(DisplayComponents.Inactive);
            Settings.defaultMode = Mode.ANGLE;
         }
      });
      JLabel angleAdjustByLabel = new JLabel("Angle Adjust By");
      angleAdjustByLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblAngleAdjustBy = new GridBagConstraints();
      gbc_lblAngleAdjustBy.anchor = 17;
      gbc_lblAngleAdjustBy.insets = new Insets(30, 0, 0, 5);
      gbc_lblAngleAdjustBy.gridx = 0;
      gbc_lblAngleAdjustBy.gridy = 2;
      final JButton angleAdjustDepthButton = new JButton("Depth");
      angleAdjustDepthButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDepth_1 = new GridBagConstraints();
      gbc_btnDepth_1.fill = 1;
      gbc_btnDepth_1.insets = new Insets(30, 0, 0, 5);
      gbc_btnDepth_1.gridx = 1;
      gbc_btnDepth_1.gridy = 2;
      angleAdjustDepthButton.setBackground(Color.LIGHT_GRAY);
      angleAdjustDepthButton.setEnabled(false);
      final JButton angleAdjustAngleButton = new JButton("Angle");
      angleAdjustAngleButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnAngle_1 = new GridBagConstraints();
      gbc_btnAngle_1.fill = 1;
      gbc_btnAngle_1.insets = new Insets(30, 0, 0, 5);
      gbc_btnAngle_1.gridx = 2;
      gbc_btnAngle_1.gridy = 2;
      angleAdjustAngleButton.setBackground(Color.LIGHT_GRAY);
      angleAdjustAngleButton.setEnabled(false);
      angleAdjustDepthButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            angleAdjustDepthButton.setBackground(DisplayComponents.Active);
            angleAdjustAngleButton.setBackground(DisplayComponents.Inactive);
         }
      });
      angleAdjustAngleButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            angleAdjustAngleButton.setBackground(DisplayComponents.Active);
            angleAdjustDepthButton.setBackground(DisplayComponents.Inactive);
         }
      });
      JLabel floatingCalibrationLabel = new JLabel("Floating Calibration");
      floatingCalibrationLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_floatingCalibrationLabel = new GridBagConstraints();
      gbc_floatingCalibrationLabel.anchor = 17;
      gbc_floatingCalibrationLabel.insets = new Insets(30, 0, 0, 5);
      gbc_floatingCalibrationLabel.gridx = 0;
      gbc_floatingCalibrationLabel.gridy = yIndex;
      leftPanel.add(floatingCalibrationLabel, gbc_floatingCalibrationLabel);
      final JButton floatingCalibrationOnButton = new JButton("On");
      floatingCalibrationOnButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_floatingCalibrationOnButton = new GridBagConstraints();
      gbc_floatingCalibrationOnButton.fill = 1;
      gbc_floatingCalibrationOnButton.insets = new Insets(30, 0, 0, 5);
      gbc_floatingCalibrationOnButton.gridx = 1;
      gbc_floatingCalibrationOnButton.gridy = yIndex;
      leftPanel.add(floatingCalibrationOnButton, gbc_floatingCalibrationOnButton);
      floatingCalibrationOnButton.setBackground(Color.LIGHT_GRAY);
      final JButton floatingCalibrationOffButton = new JButton("Off");
      floatingCalibrationOffButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_floatingCalibrationOffButton = new GridBagConstraints();
      gbc_floatingCalibrationOffButton.fill = 1;
      gbc_floatingCalibrationOffButton.insets = new Insets(30, 0, 0, 5);
      gbc_floatingCalibrationOffButton.gridx = 2;
      gbc_floatingCalibrationOffButton.gridy = yIndex++;
      leftPanel.add(floatingCalibrationOffButton, gbc_floatingCalibrationOffButton);
      floatingCalibrationOffButton.setBackground(Color.LIGHT_GRAY);
      floatingCalibrationOnButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            floatingCalibrationOnButton.setBackground(DisplayComponents.Active);
            floatingCalibrationOffButton.setBackground(DisplayComponents.Inactive);
            Settings.floatingCalibration = true;
            Settings.calibrated = false;
         }
      });
      floatingCalibrationOffButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            floatingCalibrationOffButton.setBackground(DisplayComponents.Active);
            floatingCalibrationOnButton.setBackground(DisplayComponents.Inactive);
            Settings.floatingCalibration = false;
         }
      });
      if (!((Axis)Settings.axes.get(1)).getEnabled()) {
         floatingCalibrationOnButton.setEnabled(false);
         floatingCalibrationOffButton.setEnabled(false);
         floatingCalibrationOnButton.setBackground(DisplayComponents.Background);
         floatingCalibrationOffButton.setBackground(DisplayComponents.Background);
      } else if (Settings.floatingCalibration) {
         floatingCalibrationOnButton.setBackground(DisplayComponents.Active);
         floatingCalibrationOffButton.setBackground(DisplayComponents.Inactive);
      } else {
         floatingCalibrationOffButton.setBackground(DisplayComponents.Active);
         floatingCalibrationOnButton.setBackground(DisplayComponents.Inactive);
      }

      JLabel screenSaverLabel = new JLabel("Screen Saver");
      screenSaverLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblScreenSaver = new GridBagConstraints();
      gbc_lblScreenSaver.anchor = 17;
      gbc_lblScreenSaver.insets = new Insets(30, 0, 0, 5);
      gbc_lblScreenSaver.gridx = 0;
      gbc_lblScreenSaver.gridy = yIndex;
      leftPanel.add(screenSaverLabel, gbc_lblScreenSaver);
      final JButton screenSaverButton = new JButton("ON");
      screenSaverButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnOn = new GridBagConstraints();
      gbc_btnOn.fill = 1;
      gbc_btnOn.insets = new Insets(30, 0, 0, 5);
      gbc_btnOn.gridx = 1;
      gbc_btnOn.gridy = yIndex;
      leftPanel.add(screenSaverButton, gbc_btnOn);
      screenSaverButton.setBackground(DisplayComponents.Active);
      final JButton screenSaverOffButton = new JButton("OFF");
      screenSaverOffButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnExit = new GridBagConstraints();
      gbc_btnExit.fill = 1;
      gbc_btnExit.insets = new Insets(30, 0, 0, 5);
      gbc_btnExit.gridx = 2;
      gbc_btnExit.gridy = yIndex++;
      leftPanel.add(screenSaverOffButton, gbc_btnExit);
      screenSaverOffButton.setBackground(DisplayComponents.Inactive);
      screenSaverButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            screenSaverOffButton.setBackground(DisplayComponents.Inactive);
            screenSaverButton.setBackground(DisplayComponents.Active);
            Settings.screensaver = true;
            SystemCommands.enableScreensaver();
         }
      });
      screenSaverOffButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            screenSaverOffButton.setBackground(DisplayComponents.Active);
            screenSaverButton.setBackground(DisplayComponents.Inactive);
            Settings.screensaver = false;
            SystemCommands.disableScreensaver();
         }
      });
      if (Settings.screensaver) {
         screenSaverOffButton.setBackground(DisplayComponents.Inactive);
         screenSaverButton.setBackground(DisplayComponents.Active);
      } else {
         screenSaverOffButton.setBackground(DisplayComponents.Active);
         screenSaverButton.setBackground(DisplayComponents.Inactive);
      }

      JLabel lastRecallJobLabel = new JLabel("Last Recall Job");
      lastRecallJobLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblLastRecallJob = new GridBagConstraints();
      gbc_lblLastRecallJob.anchor = 17;
      gbc_lblLastRecallJob.insets = new Insets(30, 0, 0, 5);
      gbc_lblLastRecallJob.gridx = 0;
      gbc_lblLastRecallJob.gridy = 4;
      final JButton lastRecallJobEnableButton = new JButton("ENABLE");
      lastRecallJobEnableButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnEnable = new GridBagConstraints();
      gbc_btnEnable.fill = 1;
      gbc_btnEnable.insets = new Insets(30, 0, 0, 5);
      gbc_btnEnable.gridx = 1;
      gbc_btnEnable.gridy = 4;
      lastRecallJobEnableButton.setBackground(DisplayComponents.Inactive);
      final JButton lastRecallJobDisableButton = new JButton("DISABLE");
      lastRecallJobDisableButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnDisable = new GridBagConstraints();
      gbc_btnDisable.fill = 1;
      gbc_btnDisable.insets = new Insets(30, 0, 0, 5);
      gbc_btnDisable.gridx = 2;
      gbc_btnDisable.gridy = 4;
      lastRecallJobDisableButton.setBackground(DisplayComponents.Active);
      lastRecallJobEnableButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            lastRecallJobEnableButton.setBackground(DisplayComponents.Active);
            lastRecallJobDisableButton.setBackground(DisplayComponents.Inactive);
         }
      });
      lastRecallJobDisableButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            lastRecallJobDisableButton.setBackground(DisplayComponents.Active);
            lastRecallJobEnableButton.setBackground(DisplayComponents.Inactive);
         }
      });
      JLabel screenBrightnessLabel = new JLabel("Screen Brightness");
      screenBrightnessLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblScreenBrightness = new GridBagConstraints();
      gbc_lblScreenBrightness.anchor = 17;
      gbc_lblScreenBrightness.insets = new Insets(30, 0, 0, 5);
      gbc_lblScreenBrightness.gridx = 0;
      gbc_lblScreenBrightness.gridy = yIndex;
      leftPanel.add(screenBrightnessLabel, gbc_lblScreenBrightness);
      final JSlider brightness = new JSlider(0, 1, 10, Settings.screenBrightness);
      GridBagConstraints gbc_slide = new GridBagConstraints();
      gbc_slide.fill = 1;
      gbc_slide.insets = new Insets(30, 0, 0, 5);
      gbc_slide.gridx = 1;
      gbc_slide.gridy = yIndex++;
      gbc_slide.gridwidth = 2;
      leftPanel.add(brightness, gbc_slide);
      brightness.setSnapToTicks(true);
      brightness.setMajorTickSpacing(1);
      brightness.setPaintTicks(true);
      brightness.setPaintLabels(true);
      brightness.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent arg0) {
            SystemCommands.changeBrightness((double)brightness.getValue() / 10.0D);
         }
      });
      JButton screenBrightnessMoreButton = new JButton("+ (More)");
      screenBrightnessMoreButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnmore = new GridBagConstraints();
      gbc_btnmore.fill = 1;
      gbc_btnmore.insets = new Insets(30, 0, 0, 5);
      gbc_btnmore.gridx = 1;
      gbc_btnmore.gridy = 4;
      JButton screenBrightnessLessButton = new JButton("- (Less)");
      screenBrightnessLessButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnless = new GridBagConstraints();
      gbc_btnless.fill = 1;
      gbc_btnless.insets = new Insets(30, 0, 0, 5);
      gbc_btnless.gridx = 2;
      gbc_btnless.gridy = 4;
      final JLabel brightnessLabel = new JLabel(String.valueOf(Settings.screenBrightness));
      brightnessLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
      gbc_lblNewLabel_1.insets = new Insets(30, 0, 0, 5);
      gbc_lblNewLabel_1.anchor = 12;
      gbc_lblNewLabel_1.gridx = 2;
      gbc_lblNewLabel_1.gridy = 3;
      screenBrightnessMoreButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.screenBrightness < 10) {
               ++Settings.screenBrightness;
               double brightness = (double)Settings.screenBrightness / 10.0D;
               SystemCommands.changeBrightness(brightness);
               brightnessLabel.setText(String.valueOf(Settings.screenBrightness));
            }

         }
      });
      screenBrightnessLessButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.screenBrightness > 1) {
               --Settings.screenBrightness;
               double brightness = (double)Settings.screenBrightness / 10.0D;
               SystemCommands.changeBrightness(brightness);
               brightnessLabel.setText(String.valueOf(Settings.screenBrightness));
            }

         }
      });
      JLabel lockDefaultLabel = new JLabel("Lock Default");
      lockDefaultLabel.setFont(DisplayComponents.pageTextFont);
      GridBagConstraints gbc_lblLockDefault = new GridBagConstraints();
      gbc_lblLockDefault.anchor = 17;
      gbc_lblLockDefault.insets = new Insets(30, 0, 0, 5);
      gbc_lblLockDefault.gridx = 0;
      gbc_lblLockDefault.gridy = 5;
      final JButton lockDefaultButton = new JButton("Lock");
      lockDefaultButton.setFont(DisplayComponents.buttonFont);
      GridBagConstraints gbc_btnLock = new GridBagConstraints();
      gbc_btnLock.fill = 1;
      gbc_btnLock.insets = new Insets(30, 0, 0, 5);
      gbc_btnLock.gridx = 1;
      gbc_btnLock.gridy = 6;
      lockDefaultButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (lockDefaultButton.getText().equals("Lock")) {
               lockDefaultButton.setText("Unlock");
            } else {
               lockDefaultButton.setText("Lock");
            }

         }
      });
      JPanel rightPanel = new JPanel();
      this.settingsFrame.getContentPane().add(rightPanel, "East");
      GridBagLayout gbl_rightPanel = new GridBagLayout();
      gbl_rightPanel.columnWidths = new int[3];
      gbl_rightPanel.rowHeights = new int[5];
      gbl_rightPanel.columnWeights = new double[]{0.0D, 0.0D, Double.MIN_VALUE};
      gbl_rightPanel.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      rightPanel.setLayout(gbl_rightPanel);
      rightPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      JLabel lblNewLabel = new JLabel("Auto Advance Settings");
      lblNewLabel.setFont(DisplayComponents.pageHeaderFont);
      GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
      gbc_lblNewLabel.gridwidth = 2;
      gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
      gbc_lblNewLabel.gridx = 0;
      gbc_lblNewLabel.gridy = 0;
      rightPanel.add(lblNewLabel, gbc_lblNewLabel);
      JLabel lblAutoAdvSource = new JLabel("Auto Adv Source");
      GridBagConstraints gbc_lblAutoAdvSource = new GridBagConstraints();
      gbc_lblAutoAdvSource.insets = new Insets(30, 0, 0, 5);
      gbc_lblAutoAdvSource.gridx = 0;
      gbc_lblAutoAdvSource.gridy = 1;
      rightPanel.add(lblAutoAdvSource, gbc_lblAutoAdvSource);
      String tmp;
      if (Settings.autoAdvanceMode.equals(AdvanceMode.INTERNAL)) {
         tmp = "Internal";
      } else {
         tmp = "External";
      }

      final JButton autoAdvanceSourceButton = new JCustomButton(tmp);
      if (Settings.extAdvPolarity.equals(ExtAdvPolarity.NO)) {
         tmp = "Normally Open";
      } else {
         tmp = "Normally Closed";
      }

      final JButton autoAdvancePolarityButton = new JCustomButton(tmp);
      GridBagConstraints gbc_btnInternal = new GridBagConstraints();
      gbc_btnInternal.fill = 1;
      gbc_btnInternal.insets = new Insets(30, 0, 0, 5);
      gbc_btnInternal.gridx = 1;
      gbc_btnInternal.gridy = 1;
      rightPanel.add(autoAdvanceSourceButton, gbc_btnInternal);
      autoAdvanceSourceButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Auto Adv. Setting", "Select Auto Advance Source", "Internal", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoAdvanceSourceButton.setText("Internal");
                  Settings.autoAdvanceMode = AdvanceMode.INTERNAL;
                  autoAdvancePolarityButton.setEnabled(false);
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, "External", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoAdvanceSourceButton.setText("External");
                  Settings.autoAdvanceMode = AdvanceMode.EXTERNAL;
                  autoAdvancePolarityButton.setEnabled(true);
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            });
         }
      });
      JLabel lblAutoAdvPolarity = new JLabel("Auto Adv Polarity");
      GridBagConstraints gbc_lblAutoAdvPolarity = new GridBagConstraints();
      gbc_lblAutoAdvPolarity.insets = new Insets(30, 0, 0, 5);
      gbc_lblAutoAdvPolarity.gridx = 0;
      gbc_lblAutoAdvPolarity.gridy = 2;
      rightPanel.add(lblAutoAdvPolarity, gbc_lblAutoAdvPolarity);
      if (Settings.autoAdvanceMode.equals(AdvanceMode.INTERNAL)) {
         autoAdvancePolarityButton.setEnabled(false);
      }

      if (!((Axis)Settings.axes.get(1)).getEnabled()) {
         autoAdvanceSourceButton.setEnabled(false);
      }

      GridBagConstraints gbc_btnNormallyOpen = new GridBagConstraints();
      gbc_btnNormallyOpen.fill = 1;
      gbc_btnNormallyOpen.insets = new Insets(30, 0, 0, 5);
      gbc_btnNormallyOpen.gridx = 1;
      gbc_btnNormallyOpen.gridy = 2;
      rightPanel.add(autoAdvancePolarityButton, gbc_btnNormallyOpen);
      autoAdvancePolarityButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Auto Adv. Setting", "Select Auto Advance Polarity", "Normally Open", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoAdvancePolarityButton.setText("Normally Open");
                  Settings.extAdvPolarity = ExtAdvPolarity.NO;
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, "Normaly Closed", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoAdvancePolarityButton.setText("Normaly Closed");
                  Settings.extAdvPolarity = ExtAdvPolarity.NC;
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            });
         }
      });
      JLabel lblAutoIndexSwitch = new JLabel("<html>Auto Index<br/> Switch Position</html>");
      GridBagConstraints gbc_lblAutoIndexSwitch = new GridBagConstraints();
      gbc_lblAutoIndexSwitch.insets = new Insets(30, 0, 0, 5);
      gbc_lblAutoIndexSwitch.gridx = 0;
      gbc_lblAutoIndexSwitch.gridy = 3;
      rightPanel.add(lblAutoIndexSwitch, gbc_lblAutoIndexSwitch);
      if (Settings.autoAdvancePosition.equals(AdvancePosition.TOS)) {
         tmp = "Top of Stroke";
      } else {
         tmp = "Pinch Point";
      }

      final JButton autoIndexSwitchPositionButton = new JCustomButton(tmp);
      GridBagConstraints gbc_btnTopOfStroke = new GridBagConstraints();
      gbc_btnTopOfStroke.insets = new Insets(30, 0, 0, 5);
      gbc_btnTopOfStroke.fill = 1;
      gbc_btnTopOfStroke.gridx = 1;
      gbc_btnTopOfStroke.gridy = 3;
      rightPanel.add(autoIndexSwitchPositionButton, gbc_btnTopOfStroke);
      autoIndexSwitchPositionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Auto Adv. Setting", "Select Auto Advance Polarity", "Top of Stroke", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoIndexSwitchPositionButton.setText("Top of Stroke");
                  Settings.autoAdvancePosition = AdvancePosition.TOS;
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, "Pinch Point", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  autoIndexSwitchPositionButton.setText("Pinch Point");
                  Settings.autoAdvancePosition = AdvancePosition.PP;
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            });
         }
      });
      JPanel buttonPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout)buttonPanel.getLayout();
      flowLayout.setAlignment(1);
      flowLayout.setHgap(30);
      buttonPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      this.settingsFrame.getContentPane().add(buttonPanel, "South");
      JButton homeButton = new JBottomButton("Home", "home.png");
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            SettingsPage.this.settingsFrame.dispose();
            Settings.log.finest("settings page disposed");
            SystemCommands.writeSettingsFile();
            new HomePage();
         }
      });
      buttonPanel.add(homeButton);
      buttonPanel.add(new JBottomButton("<html>Axis<br/>Settings</html>", "axisSettings.png", new SettingsButtonAction(this, (Axis)Settings.axes.get(0))));
      JButton softwareSettingsButton = new JBottomButton("SOFTWARE", "systemInfo.png");
      buttonPanel.add(softwareSettingsButton);
      softwareSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("software button pressed");
            new SoftwarePage();
         }
      });
      JButton clockSettingsButton = new JBottomButton("<html>Set<br/>Clock</html>", (String)null);
      clockSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("set clock button pressed");
            SystemCommands.setClock();
         }
      });
      JButton ethernetSettingsButton = new JBottomButton("Ethernet", (String)null);
      ethernetSettingsButton.setEnabled(false);
      JButton touchScreenCalibrateButton = new JBottomButton("<html>Touch<br/>Screen<br/>Calibrate</html>", "touchCal.png", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("calibrate button pressed");
            new TwoButtonPromptPage("Touch Screen Calibration", "Are you sure you want to calibrate the touch screen?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  SystemCommands.calibrateScreen();
               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
               }
            }, false);
         }
      });
      buttonPanel.add(touchScreenCalibrateButton);
      JButton usbButton = new JBottomButton("USB", "USB.png", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("USB button pressed");
            new USBPopup();
         }
      });
      buttonPanel.add(usbButton);
      JButton toolLibrary = new JBottomButton("<html>Tool<br/>Library</html>", "toolLibrary.png", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Tool library button pressed");
            SettingsPage.this.settingsFrame.dispose();
            new ToolLibraryPage();
         }
      });
      buttonPanel.add(toolLibrary);
      JButton hiddenSettings = new JButton();
      hiddenSettings.setBackground(new Color(238, 238, 238));
      hiddenSettings.setContentAreaFilled(false);
      hiddenSettings.setBorder((Border)null);
      hiddenSettings.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      hiddenSettings.setPreferredSize(DisplayComponents.bottomButtonSize);
      hiddenSettings.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            SettingsPage.pressed = new Timestamp(System.currentTimeMillis() + 2000L);
         }

         public void mouseReleased(MouseEvent arg0) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.after(SettingsPage.pressed)) {
               SettingsPage.this.settingsFrame.dispose();
               new HiddenSettingsPage();
            }

         }
      });
      buttonPanel.add(hiddenSettings);
      this.settingsFrame.setVisible(true);
      Settings.log.finest("settings page initialized");
   }

   public JFrame getFrame() {
      return this.settingsFrame;
   }
}
