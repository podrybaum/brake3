package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DataInputField;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.CalculatorPage;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.PasswordPromptPage;
import com.automec.display.popups.PresetPickerPopup;
import com.automec.objects.Axis;
import com.automec.objects.IncorrectAxisException;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Units;
import com.automec.objects.enums.XAxisPreset;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AxisFactorySettingsPage {
   private JFrame axisSettingsFrame = new JFrame("Axis Settings");
   private JPanel axisSettingsPanel;
   public Axis axis;
   private Color defaultBackground;
   MathContext mc;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType;

   public AxisFactorySettingsPage(Axis axis) {
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      this.axis = axis;
      this.initialize(axis);
   }

   private void initialize(final Axis axis) {
      this.axisSettingsFrame.setDefaultCloseOperation(3);
      this.axisSettingsFrame.setSize(1024, 768);
      this.axisSettingsFrame.setUndecorated(true);
      this.axisSettingsPanel = new JPanel();
      this.axisSettingsFrame.getContentPane().add(this.axisSettingsPanel);
      this.axisSettingsPanel.setLayout(new BorderLayout(0, 0));
      Settings.activeFrame = this.axisSettingsFrame;
      this.axisSettingsFrame.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      JLabel lblaxisSettings = new JLabel(axis.getFullName() + " Settings");
      lblaxisSettings.setFont(DisplayComponents.pageTitleFont);
      lblaxisSettings.setHorizontalAlignment(0);
      this.axisSettingsPanel.add(lblaxisSettings, "North");
      this.defaultBackground = DisplayComponents.Active;
      JPanel settingsPanel = new JPanel();
      this.axisSettingsPanel.add(settingsPanel, "Center");
      settingsPanel.setLayout(new BorderLayout(0, 0));
      JPanel ySettingsPanel;
      JPanel yPositionPanel;
      GridBagLayout gblSettings;
      JLabel ySettingsLabel;
      JButton xAxisTypeButton;
      JLabel yAxisTypeLabel;
      DataInputField slowDistanceValue;
      JLabel downSlowOnlabel;
      final DataInputField downSlowOnValue;
      JLabel rScaleFactorLabel;
      final DataInputField rScaleFactorValue;
      JLabel yScaleFactorLabel;
      DataInputField yScaleFactorValue;
      JLabel minimumAngleOverrideLabel;
      DataInputField rDefaultOffsetValue;
      JLabel yDefaultOffsetLabel;
      DataInputField xDeadzone;
      JLabel xDeadzoneLabel;
      switch($SWITCH_TABLE$com$automec$objects$enums$AxisType()[axis.getAxisType().ordinal()]) {
      case 1:
         try {
            ySettingsPanel = new JPanel();
            yPositionPanel = new JPanel();
            gblSettings = new GridBagLayout();
            gblSettings.columnWidths = new int[2];
            gblSettings.rowHeights = new int[5];
            gblSettings.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
            ySettingsPanel.setLayout(gblSettings);
            yPositionPanel.setLayout(gblSettings);
            ySettingsPanel.setPreferredSize(new Dimension(500, 500));
            yPositionPanel.setPreferredSize(new Dimension(500, 500));
            ySettingsLabel = new JLabel("<html>Backgauge<br/>Type</html>");
            xAxisTypeButton = new JButton(axis.getxPreset().toString());
            xAxisTypeButton.setBackground(DisplayComponents.Active);
            xAxisTypeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            ySettingsLabel.setFont(DisplayComponents.pageHeaderFont);
            xAxisTypeButton.setFont(DisplayComponents.pageHeaderFont);
            ySettingsLabel.setPreferredSize(new Dimension(80, 80));
            xAxisTypeButton.setPreferredSize(new Dimension(80, 80));
            xAxisTypeButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (Settings.locked) {
                     new PasswordPromptPage();
                  } else {
                     new PresetPickerPopup(AxisFactorySettingsPage.this.axisSettingsFrame, axis, ((JButton)e.getSource()).getLocationOnScreen().x, ((JButton)e.getSource()).getLocationOnScreen().y);
                  }

               }
            });
            xAxisTypeButton.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            yAxisTypeLabel = new JLabel("Slow Distance");
            slowDistanceValue = new DataInputField(axis.getSlowDistance(), 0.0D, 0.5D);
            slowDistanceValue.setBackground(DisplayComponents.Active);
            yAxisTypeLabel.setFont(DisplayComponents.pageHeaderFont);
            slowDistanceValue.setFont(DisplayComponents.pageHeaderFont);
            yAxisTypeLabel.setPreferredSize(new Dimension(80, 80));
            slowDistanceValue.setPreferredSize(new Dimension(80, 80));
            slowDistanceValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setSlowDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setSlowDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                     Settings.calibrated = false;
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getSlowDistance());
               }
            });
            slowDistanceValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            ySettingsPanel.add(ySettingsLabel, DisplayComponents.GenerateConstraints(0, 0));
            ySettingsPanel.add(xAxisTypeButton, DisplayComponents.GenerateConstraints(1, 0));
            ySettingsPanel.add(yAxisTypeLabel, DisplayComponents.GenerateConstraints(0, 1));
            ySettingsPanel.add(slowDistanceValue, DisplayComponents.GenerateConstraints(1, 1));
            downSlowOnlabel = new JLabel("X-Axis Travel");
            downSlowOnValue = new DataInputField(axis.getAxisLength(), 0.0D, 120.1D);
            downSlowOnValue.setBackground(DisplayComponents.Active);
            downSlowOnlabel.setFont(DisplayComponents.pageHeaderFont);
            downSlowOnValue.setFont(DisplayComponents.pageHeaderFont);
            downSlowOnlabel.setPreferredSize(new Dimension(80, 80));
            downSlowOnValue.setPreferredSize(new Dimension(80, 80));
            downSlowOnValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            rScaleFactorLabel = new JLabel("In Limit");
            rScaleFactorValue = new DataInputField(axis.getInLimit(), 0.0D, axis.getAxisLength());
            rScaleFactorValue.setBackground(DisplayComponents.Active);
            rScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
            rScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
            rScaleFactorLabel.setPreferredSize(new Dimension(80, 80));
            rScaleFactorValue.setPreferredSize(new Dimension(80, 80));
            rScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            downSlowOnValue.addActionListener(new ActionListener() {
               // $FF: synthetic field
               private static int[] $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset;

               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setAxisLength(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setAxisLength(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     try {
                        switch($SWITCH_TABLE$com$automec$objects$enums$XAxisPreset()[axis.getxPreset().ordinal()]) {
                        case 1:
                           if (axis.getAxisLength() - axis.getInLimit() > 24.1D) {
                              axis.setInLimit(axis.getAxisLength() - 24.1D);
                           }
                           break;
                        case 2:
                           if (axis.getAxisLength() - axis.getInLimit() > 20.1D) {
                              axis.setInLimit(axis.getAxisLength() - 20.1D);
                           }
                        case 3:
                        }
                     } catch (IncorrectAxisException var4) {
                        var4.printStackTrace();
                     }

                     Settings.calibrated = false;
                     rScaleFactorValue.setNumber(axis.getInLimit());
                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getAxisLength());
               }

               // $FF: synthetic method
               static int[] $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset() {
                  int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset;
                  if (var10000 != null) {
                     return var10000;
                  } else {
                     int[] var0 = new int[XAxisPreset.values().length];

                     try {
                        var0[XAxisPreset.G24.ordinal()] = 1;
                     } catch (NoSuchFieldError var3) {
                     }

                     try {
                        var0[XAxisPreset.MINIBRUTE.ordinal()] = 2;
                     } catch (NoSuchFieldError var2) {
                     }

                     try {
                        var0[XAxisPreset.OTHER.ordinal()] = 3;
                     } catch (NoSuchFieldError var1) {
                     }

                     $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset = var0;
                     return var0;
                  }
               }
            });
            rScaleFactorValue.addActionListener(new ActionListener() {
               // $FF: synthetic field
               private static int[] $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset;

               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     axis.setInLimit(Double.parseDouble(((DataInputField)e.getSource()).getText()));

                     try {
                        switch($SWITCH_TABLE$com$automec$objects$enums$XAxisPreset()[axis.getxPreset().ordinal()]) {
                        case 1:
                           if (axis.getAxisLength() - axis.getInLimit() > 24.1D) {
                              axis.setAxisLength(axis.getInLimit() + 24.1D);
                              Settings.calibrated = false;
                           }
                           break;
                        case 2:
                           if (axis.getAxisLength() - axis.getInLimit() > 20.1D) {
                              axis.setInLimit(axis.getInLimit() + 20.1D);
                              Settings.calibrated = false;
                           }
                        case 3:
                        }
                     } catch (IncorrectAxisException var3) {
                        var3.printStackTrace();
                     }

                     downSlowOnValue.setNumber(axis.getAxisLength());
                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getInLimit());
               }

               // $FF: synthetic method
               static int[] $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset() {
                  int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset;
                  if (var10000 != null) {
                     return var10000;
                  } else {
                     int[] var0 = new int[XAxisPreset.values().length];

                     try {
                        var0[XAxisPreset.G24.ordinal()] = 1;
                     } catch (NoSuchFieldError var3) {
                     }

                     try {
                        var0[XAxisPreset.MINIBRUTE.ordinal()] = 2;
                     } catch (NoSuchFieldError var2) {
                     }

                     try {
                        var0[XAxisPreset.OTHER.ordinal()] = 3;
                     } catch (NoSuchFieldError var1) {
                     }

                     $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset = var0;
                     return var0;
                  }
               }
            });
            ySettingsPanel.add(downSlowOnlabel, DisplayComponents.GenerateConstraints(0, 2));
            ySettingsPanel.add(downSlowOnValue, DisplayComponents.GenerateConstraints(1, 2));
            ySettingsPanel.add(rScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 3));
            ySettingsPanel.add(rScaleFactorValue, DisplayComponents.GenerateConstraints(1, 3));
            yScaleFactorLabel = new JLabel("Scale Factor");
            yScaleFactorValue = new DataInputField(axis.getEncoderCountPerInch(), 1000.0D, 50000.0D, true);
            yScaleFactorValue.setBackground(DisplayComponents.Active);
            yScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
            yScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
            yScaleFactorLabel.setPreferredSize(new Dimension(80, 80));
            yScaleFactorValue.setPreferredSize(new Dimension(80, 80));
            yScaleFactorValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setEncoderCountPerInch(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setEncoderCountPerInch(v.multiply(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                     Settings.calibrated = false;
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getEncoderCountPerInch());
               }
            });
            yScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            minimumAngleOverrideLabel = new JLabel("<html>Approach<br/>Distance</html>");
            rDefaultOffsetValue = new DataInputField(axis.getStopDistance(), 0.0D, 0.999D);
            rDefaultOffsetValue.setBackground(DisplayComponents.Active);
            minimumAngleOverrideLabel.setFont(DisplayComponents.pageHeaderFont);
            rDefaultOffsetValue.setFont(DisplayComponents.pageHeaderFont);
            minimumAngleOverrideLabel.setPreferredSize(new Dimension(80, 80));
            rDefaultOffsetValue.setPreferredSize(new Dimension(80, 80));
            rDefaultOffsetValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setStopDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setStopDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getStopDistance());
               }
            });
            rDefaultOffsetValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            ySettingsPanel.add(yScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 4));
            ySettingsPanel.add(yScaleFactorValue, DisplayComponents.GenerateConstraints(1, 4));
            yPositionPanel.add(minimumAngleOverrideLabel, DisplayComponents.GenerateConstraints(0, 0));
            yPositionPanel.add(rDefaultOffsetValue, DisplayComponents.GenerateConstraints(1, 0));
            final DataInputField xAdvancedCalPos = new DataInputField(Settings.advancedCalibrationPosition, 2.0D, ((Axis)Settings.axes.get(0)).getAxisLength() - 2.1D);
            JLabel xAdvancedCalLabel = new JLabel("<html>Advanced<br/>Calibration</html>");
            final JButton xAdvancedCalButton = new JCustomButton("ON");
            if (Settings.advancedCalibration) {
               xAdvancedCalButton.setText("ON");
               xAdvancedCalPos.setEnabled(true);
               xAdvancedCalPos.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            } else {
               xAdvancedCalButton.setText("OFF");
               xAdvancedCalPos.setEnabled(false);
            }

            xAdvancedCalLabel.setFont(DisplayComponents.pageHeaderFont);
            xAdvancedCalButton.setFont(DisplayComponents.pageHeaderFont);
            xAdvancedCalLabel.setPreferredSize(new Dimension(80, 80));
            xAdvancedCalButton.setPreferredSize(new Dimension(80, 80));
            xAdvancedCalButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (Settings.locked) {
                     new PasswordPromptPage();
                  } else if (xAdvancedCalButton.getText().equals("ON")) {
                     xAdvancedCalButton.setText("OFF");
                     Settings.advancedCalibration = false;
                     xAdvancedCalPos.setEnabled(false);
                     xAdvancedCalPos.setBackground(DisplayComponents.Background);
                     xAdvancedCalPos.removeMouseListener(xAdvancedCalPos.getMouseListeners()[0]);
                  } else {
                     xAdvancedCalButton.setText("ON");
                     Settings.advancedCalibration = true;
                     xAdvancedCalPos.setEnabled(true);
                     xAdvancedCalPos.setBackground(DisplayComponents.Active);
                     xAdvancedCalPos.addMouseListener(DisplayComponents.CalculatorPopupSettings());
                  }

               }
            });
            yPositionPanel.add(xAdvancedCalLabel, DisplayComponents.GenerateConstraints(0, 1));
            yPositionPanel.add(xAdvancedCalButton, DisplayComponents.GenerateConstraints(1, 1));
            JLabel xAdvancedCalPosLabel = new JLabel("<html>Advanced<br/>Calibration Position</html>");
            xAdvancedCalPos.setBackground(DisplayComponents.Active);
            xAdvancedCalPos.setFont(DisplayComponents.pageHeaderFont);
            xAdvancedCalPosLabel.setFont(DisplayComponents.pageHeaderFont);
            xAdvancedCalPosLabel.setPreferredSize(new Dimension(80, 120));
            xAdvancedCalPos.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        Settings.advancedCalibrationPosition = Double.parseDouble(((DataInputField)e.getSource()).getText());
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        Settings.advancedCalibrationPosition = v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue();
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(Settings.advancedCalibrationPosition);
               }
            });
            xAdvancedCalPos.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            yPositionPanel.add(xAdvancedCalPosLabel, DisplayComponents.GenerateConstraints(0, 2));
            yPositionPanel.add(xAdvancedCalPos, DisplayComponents.GenerateConstraints(1, 2));
            xDeadzone = new DataInputField(axis.getDeadzone(), 0.0D, 0.05D);
            xDeadzoneLabel = new JLabel("<html>Deadzone</html>");
            xDeadzone.setBackground(DisplayComponents.Active);
            xDeadzone.setFont(DisplayComponents.pageHeaderFont);
            xDeadzoneLabel.setFont(DisplayComponents.pageHeaderFont);
            xDeadzoneLabel.setPreferredSize(new Dimension(80, 80));
            xDeadzone.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setDeadzone(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setDeadzone(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getDeadzone());
               }
            });
            xDeadzone.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            yPositionPanel.add(xDeadzoneLabel, DisplayComponents.GenerateConstraints(0, 3));
            yPositionPanel.add(xDeadzone, DisplayComponents.GenerateConstraints(1, 3));
            ySettingsPanel.setBorder(new EmptyBorder(0, 30, 0, 15));
            yPositionPanel.setBorder(new EmptyBorder(0, 15, 0, 30));
            settingsPanel.add(ySettingsPanel, "West");
            settingsPanel.add(yPositionPanel, "East");
         } catch (IncorrectAxisException var25) {
            Settings.log.log(Level.WARNING, "somehow this page got dereferenced from the proper axis", var25);
         } catch (Exception var26) {
            Settings.log.log(Level.SEVERE, "axisfactorysettings page encountered unhandled error in x", var26);
         }
         break;
      case 2:
         try {
            ySettingsPanel = new JPanel();
            yPositionPanel = new JPanel();
            ySettingsPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 30, 0, 15), BorderFactory.createLineBorder(Color.BLACK)));
            yPositionPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 15, 0, 30), BorderFactory.createLineBorder(Color.BLACK)));
            gblSettings = new GridBagLayout();
            gblSettings.columnWidths = new int[2];
            gblSettings.columnWeights = new double[]{0.0D, 0.0D};
            gblSettings.rowHeights = new int[5];
            gblSettings.rowWeights = new double[]{1.0D, 1.0D, 1.0D, 1.0D, 1.0D};
            ySettingsPanel.setLayout(gblSettings);
            yPositionPanel.setLayout(gblSettings);
            ySettingsPanel.setPreferredSize(new Dimension(500, 500));
            yPositionPanel.setPreferredSize(new Dimension(500, 500));
            ySettingsLabel = new JLabel("<html><U>Y-Axis Settings</U></html>");
            JLabel yPositionLabel = new JLabel("<html><U>Y-Axis Positions</U></html>");
            ySettingsPanel.add(ySettingsLabel, DisplayComponents.GenerateConstraints(0, 0, 2, 1));
            yPositionPanel.add(yPositionLabel, DisplayComponents.GenerateConstraints(0, 0, 2, 1));
            ySettingsLabel.setFont(DisplayComponents.pageTitleFont);
            yPositionLabel.setFont(DisplayComponents.pageTitleFont);
            yAxisTypeLabel = new JLabel("Y Axis Type");
            JButton yAxisTypeButton = new JButton(((Axis)Settings.axes.get(1)).getyPreset().toString());
            yAxisTypeButton.setBackground(DisplayComponents.Active);
            yAxisTypeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            yAxisTypeLabel.setFont(DisplayComponents.pageHeaderFont);
            yAxisTypeButton.setFont(DisplayComponents.pageHeaderFont);
            yAxisTypeLabel.setPreferredSize(new Dimension(80, 80));
            yAxisTypeButton.setPreferredSize(new Dimension(80, 80));
            yAxisTypeButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (Settings.locked) {
                     new PasswordPromptPage();
                  } else {
                     new PresetPickerPopup(AxisFactorySettingsPage.this.axisSettingsFrame, axis, ((JButton)e.getSource()).getLocationOnScreen().x, ((JButton)e.getSource()).getLocationOnScreen().y);
                  }

               }
            });
            downSlowOnlabel = new JLabel("<html>Down Slow<br/>On</html>");
            downSlowOnValue = new DataInputField(axis.getSlowDistance(), 0.0D, 3.0D);
            downSlowOnValue.setBackground(DisplayComponents.Active);
            downSlowOnlabel.setFont(DisplayComponents.pageHeaderFont);
            downSlowOnValue.setFont(DisplayComponents.pageHeaderFont);
            downSlowOnlabel.setPreferredSize(new Dimension(80, 80));
            downSlowOnValue.setPreferredSize(new Dimension(80, 80));
            downSlowOnValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setSlowDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setSlowDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getSlowDistance());
               }
            });
            downSlowOnValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            ySettingsPanel.add(yAxisTypeLabel, DisplayComponents.GenerateConstraints(0, 1));
            ySettingsPanel.add(yAxisTypeButton, DisplayComponents.GenerateConstraints(1, 1));
            yPositionPanel.add(downSlowOnlabel, DisplayComponents.GenerateConstraints(0, 1));
            yPositionPanel.add(downSlowOnValue, DisplayComponents.GenerateConstraints(1, 1));
            rScaleFactorLabel = new JLabel("A/W Position");
            rScaleFactorValue = new DataInputField(axis.getAwDistance(), 0.0D, 0.999D);
            rScaleFactorValue.setBackground(DisplayComponents.Active);
            rScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
            rScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
            rScaleFactorLabel.setPreferredSize(new Dimension(80, 80));
            rScaleFactorValue.setPreferredSize(new Dimension(80, 80));
            rScaleFactorValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setAwDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setAwDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getAwDistance());
               }
            });
            rScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            yPositionPanel.add(rScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 2));
            yPositionPanel.add(rScaleFactorValue, DisplayComponents.GenerateConstraints(1, 2));
            yScaleFactorLabel = new JLabel("Scale Factor");
            yScaleFactorValue = new DataInputField(axis.getEncoderCountPerInch(), 1000.0D, 50000.0D, true);
            yScaleFactorValue.setBackground(DisplayComponents.Active);
            yScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
            yScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
            yScaleFactorLabel.setPreferredSize(new Dimension(80, 80));
            yScaleFactorValue.setPreferredSize(new Dimension(80, 80));
            yScaleFactorValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setEncoderCountPerInch(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setEncoderCountPerInch(v.multiply(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     Settings.calibrated = false;
                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getEncoderCountPerInch());
               }
            });
            yScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            minimumAngleOverrideLabel = new JLabel("<html>Minimum<br/>Angle Override</html>");
            final JCustomButton minimumAngleOverrideButton = new JCustomButton("OFF");
            if (axis.getMinimumAngleOverride()) {
               minimumAngleOverrideButton.setText("ON");
            } else {
               minimumAngleOverrideButton.setText("OFF");
            }

            minimumAngleOverrideButton.setBackground(DisplayComponents.Active);
            minimumAngleOverrideLabel.setFont(DisplayComponents.pageHeaderFont);
            minimumAngleOverrideButton.setFont(DisplayComponents.pageHeaderFont);
            minimumAngleOverrideLabel.setPreferredSize(new Dimension(80, 80));
            minimumAngleOverrideButton.setPreferredSize(new Dimension(80, 80));
            minimumAngleOverrideButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (Settings.locked) {
                     new PasswordPromptPage();
                  } else if (minimumAngleOverrideButton.getText().equals("ON")) {
                     minimumAngleOverrideButton.setText("OFF");
                     axis.setMinimumAngleOverride(false);
                  } else {
                     minimumAngleOverrideButton.setText("ON");
                     new NotificationPage("WARNING", "This feature is for testing purposes only");
                     axis.setMinimumAngleOverride(true);
                  }

               }
            });
            ySettingsPanel.add(yScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 2));
            ySettingsPanel.add(yScaleFactorValue, DisplayComponents.GenerateConstraints(1, 2));
            ySettingsPanel.add(minimumAngleOverrideLabel, DisplayComponents.GenerateConstraints(0, 3));
            ySettingsPanel.add(minimumAngleOverrideButton, DisplayComponents.GenerateConstraints(1, 3));
            yDefaultOffsetLabel = new JLabel("<html>Default Offset</html>");
            DataInputField yDefaultOffsetValue = new DataInputField(axis.getDefaultOffset(), -1.0D, 1.0D);
            yDefaultOffsetValue.setBackground(DisplayComponents.Active);
            yDefaultOffsetLabel.setFont(DisplayComponents.pageHeaderFont);
            yDefaultOffsetValue.setFont(DisplayComponents.pageHeaderFont);
            yDefaultOffsetLabel.setPreferredSize(new Dimension(80, 80));
            yDefaultOffsetValue.setPreferredSize(new Dimension(80, 80));
            yDefaultOffsetValue.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  if (SystemCommands.validInput((DataInputField)e.getSource())) {
                     if (Settings.units == Units.INCHES) {
                        axis.setDefaultOffset(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     } else {
                        BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        axis.setDefaultOffset(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                     }

                     ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  } else {
                     ((DataInputField)e.getSource()).setBackground(Color.RED);
                  }

                  ((DataInputField)e.getSource()).setNumber(axis.getDefaultOffset());
               }
            });
            yDefaultOffsetValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
            yPositionPanel.add(yDefaultOffsetLabel, DisplayComponents.GenerateConstraints(0, 3));
            yPositionPanel.add(yDefaultOffsetValue, DisplayComponents.GenerateConstraints(1, 3));
            settingsPanel.add(ySettingsPanel, "West");
            settingsPanel.add(yPositionPanel, "East");
         } catch (IncorrectAxisException var27) {
            Settings.log.log(Level.WARNING, "somehow this page got dereferenced from the proper axis", var27);
         } catch (Exception var28) {
            Settings.log.log(Level.SEVERE, "axisfactorysettings page encountered unhandled error in y", var28);
         }
         break;
      case 3:
         ySettingsPanel = new JPanel();
         yPositionPanel = new JPanel();
         gblSettings = new GridBagLayout();
         gblSettings.columnWidths = new int[2];
         gblSettings.rowHeights = new int[5];
         ySettingsPanel.setLayout(gblSettings);
         yPositionPanel.setLayout(gblSettings);
         ySettingsPanel.setPreferredSize(new Dimension(500, 500));
         yPositionPanel.setPreferredSize(new Dimension(500, 500));
         ySettingsLabel = new JLabel("R-Axis Installed");
         xAxisTypeButton = new JButton("Yes");
         xAxisTypeButton.setBackground(DisplayComponents.Active);
         xAxisTypeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
         ySettingsLabel.setFont(DisplayComponents.pageHeaderFont);
         xAxisTypeButton.setFont(DisplayComponents.pageHeaderFont);
         ySettingsLabel.setPreferredSize(new Dimension(80, 80));
         xAxisTypeButton.setPreferredSize(new Dimension(80, 80));
         xAxisTypeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
         });
         yAxisTypeLabel = new JLabel("Slow Distance");
         slowDistanceValue = new DataInputField(axis.getSlowDistance(), 0.0D, 0.5D);
         slowDistanceValue.setBackground(DisplayComponents.Active);
         yAxisTypeLabel.setFont(DisplayComponents.pageHeaderFont);
         slowDistanceValue.setFont(DisplayComponents.pageHeaderFont);
         yAxisTypeLabel.setPreferredSize(new Dimension(80, 80));
         slowDistanceValue.setPreferredSize(new Dimension(80, 80));
         slowDistanceValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setSlowDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setSlowDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  Settings.calibrated = false;
                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getSlowDistance());
            }
         });
         slowDistanceValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         ySettingsPanel.add(yAxisTypeLabel, DisplayComponents.GenerateConstraints(0, 1));
         ySettingsPanel.add(slowDistanceValue, DisplayComponents.GenerateConstraints(1, 1));
         downSlowOnlabel = new JLabel("R-Axis Travel");
         downSlowOnValue = new DataInputField(axis.getAxisLength(), 0.0D, 99.0D);
         downSlowOnValue.setBackground(DisplayComponents.Active);
         downSlowOnlabel.setFont(DisplayComponents.pageHeaderFont);
         downSlowOnValue.setFont(DisplayComponents.pageHeaderFont);
         downSlowOnlabel.setPreferredSize(new Dimension(80, 80));
         downSlowOnValue.setPreferredSize(new Dimension(80, 80));
         downSlowOnValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setAxisLength(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setAxisLength(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  Settings.calibrated = false;
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getAxisLength());
            }
         });
         downSlowOnValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         ySettingsPanel.add(downSlowOnlabel, DisplayComponents.GenerateConstraints(0, 2));
         ySettingsPanel.add(downSlowOnValue, DisplayComponents.GenerateConstraints(1, 2));
         rScaleFactorLabel = new JLabel("Scale Factor");
         rScaleFactorValue = new DataInputField(axis.getEncoderCountPerInch(), 1000.0D, 50000.0D, true);
         rScaleFactorValue.setBackground(DisplayComponents.Active);
         rScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
         rScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
         rScaleFactorLabel.setPreferredSize(new Dimension(80, 80));
         rScaleFactorValue.setPreferredSize(new Dimension(80, 80));
         rScaleFactorValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setEncoderCountPerInch(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setEncoderCountPerInch(v.multiply(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
                  Settings.calibrated = false;
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getEncoderCountPerInch());
            }
         });
         rScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         yScaleFactorLabel = new JLabel("<html>Min Approach<br/>Distance</html>");
         yScaleFactorValue = new DataInputField(axis.getStopDistance(), 0.0D, 0.009D);
         yScaleFactorValue.setBackground(DisplayComponents.Active);
         yScaleFactorLabel.setFont(DisplayComponents.pageHeaderFont);
         yScaleFactorValue.setFont(DisplayComponents.pageHeaderFont);
         yScaleFactorLabel.setPreferredSize(new Dimension(80, 100));
         yScaleFactorValue.setPreferredSize(new Dimension(80, 100));
         yScaleFactorValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setStopDistance(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setStopDistance(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getStopDistance());
            }
         });
         yScaleFactorValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         minimumAngleOverrideLabel = new JLabel("<html>Default Offset</html>");
         rDefaultOffsetValue = new DataInputField(axis.getDefaultOffset(), 0.0D, 8.1D);
         rDefaultOffsetValue.setBackground(DisplayComponents.Active);
         minimumAngleOverrideLabel.setFont(DisplayComponents.pageHeaderFont);
         rDefaultOffsetValue.setFont(DisplayComponents.pageHeaderFont);
         minimumAngleOverrideLabel.setPreferredSize(new Dimension(80, 80));
         rDefaultOffsetValue.setPreferredSize(new Dimension(80, 80));
         rDefaultOffsetValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setDefaultOffset(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setDefaultOffset(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getDefaultOffset());
            }
         });
         rDefaultOffsetValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         yDefaultOffsetLabel = new JLabel("<html>Zero<br/>Adjust</html>");
         final JButton rZeroAdjustButton = new JCustomButton("ON");
         if (axis.getZeroAdjust()) {
            rZeroAdjustButton.setText("ON");
         } else {
            rZeroAdjustButton.setText("OFF");
         }

         yDefaultOffsetLabel.setFont(DisplayComponents.pageHeaderFont);
         rZeroAdjustButton.setFont(DisplayComponents.pageHeaderFont);
         yDefaultOffsetLabel.setPreferredSize(new Dimension(80, 80));
         rZeroAdjustButton.setPreferredSize(new Dimension(80, 80));
         rZeroAdjustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (Settings.locked) {
                  new PasswordPromptPage();
               } else if (rZeroAdjustButton.getText().equals("ON")) {
                  rZeroAdjustButton.setText("OFF");
                  axis.setZeroAdjust(false);
               } else {
                  rZeroAdjustButton.setText("ON");
                  axis.setZeroAdjust(true);
               }

            }
         });
         JLabel rZeroOffsetLabel = new JLabel("<html>Zero Offset</html>");
         DataInputField rZeroOffsetValue = new DataInputField(axis.getZeroOffset(), -8.1D, 8.1D);
         rZeroOffsetValue.setBackground(DisplayComponents.Active);
         rZeroOffsetLabel.setFont(DisplayComponents.pageHeaderFont);
         rZeroOffsetValue.setFont(DisplayComponents.pageHeaderFont);
         rZeroOffsetLabel.setPreferredSize(new Dimension(80, 80));
         rZeroOffsetValue.setPreferredSize(new Dimension(80, 80));
         rZeroOffsetValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setZeroOffset(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setZeroOffset(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getZeroOffset());
            }
         });
         rZeroOffsetValue.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         xDeadzone = new DataInputField(axis.getDeadzone(), 0.0D, 0.05D);
         xDeadzoneLabel = new JLabel("<html>Deadzone</html>");
         xDeadzone.setBackground(DisplayComponents.Active);
         xDeadzone.setFont(DisplayComponents.pageHeaderFont);
         xDeadzoneLabel.setFont(DisplayComponents.pageHeaderFont);
         xDeadzoneLabel.setPreferredSize(new Dimension(80, 80));
         xDeadzone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (Settings.units == Units.INCHES) {
                     axis.setDeadzone(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     axis.setDeadzone(v.divide(mm, AxisFactorySettingsPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(AxisFactorySettingsPage.this.defaultBackground);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(axis.getDeadzone());
            }
         });
         xDeadzone.addMouseListener(DisplayComponents.CalculatorPopupSettings());
         ySettingsPanel.add(rScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 3));
         ySettingsPanel.add(rScaleFactorValue, DisplayComponents.GenerateConstraints(1, 3));
         ySettingsPanel.add(yScaleFactorLabel, DisplayComponents.GenerateConstraints(0, 4));
         ySettingsPanel.add(yScaleFactorValue, DisplayComponents.GenerateConstraints(1, 4));
         ySettingsPanel.add(minimumAngleOverrideLabel, DisplayComponents.GenerateConstraints(0, 5));
         ySettingsPanel.add(rDefaultOffsetValue, DisplayComponents.GenerateConstraints(1, 5));
         yPositionPanel.add(yDefaultOffsetLabel, DisplayComponents.GenerateConstraints(0, 0));
         yPositionPanel.add(rZeroAdjustButton, DisplayComponents.GenerateConstraints(1, 0));
         yPositionPanel.add(rZeroOffsetLabel, DisplayComponents.GenerateConstraints(0, 1));
         yPositionPanel.add(rZeroOffsetValue, DisplayComponents.GenerateConstraints(1, 1));
         yPositionPanel.add(xDeadzoneLabel, DisplayComponents.GenerateConstraints(0, 2));
         yPositionPanel.add(xDeadzone, DisplayComponents.GenerateConstraints(1, 2));
         ySettingsPanel.setBorder(new EmptyBorder(0, 30, 0, 15));
         yPositionPanel.setBorder(new EmptyBorder(0, 15, 0, 30));
         settingsPanel.add(ySettingsPanel, "West");
         settingsPanel.add(yPositionPanel, "East");
      }

      ySettingsPanel = new JPanel();
      this.axisSettingsPanel.add(ySettingsPanel, "South");
      ySettingsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      JButton homeButton = new JBottomButton("      ", "home.png");
      homeButton.setVerticalTextPosition(3);
      homeButton.setHorizontalTextPosition(0);
      ySettingsPanel.add(homeButton);
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            AxisFactorySettingsPage.this.axisSettingsFrame.dispose();
            SystemCommands.writeSettingsFile();
            Settings.log.finest("axis factory settings page disposed");
            new HomePage();
         }
      });
      JButton settingsButton = new JBottomButton("Settings", "settings.png");
      settingsButton.setVerticalTextPosition(3);
      settingsButton.setHorizontalTextPosition(0);
      ySettingsPanel.add(settingsButton);
      settingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Settings button pressed");
            AxisFactorySettingsPage.this.axisSettingsFrame.dispose();
            Settings.log.finest("axis factory settings page disposed");
            new SettingsPage();
         }
      });
      JButton axisSettingsButton = new JBottomButton("Axis settings", "axisSettings.png");
      axisSettingsButton.setVerticalTextPosition(3);
      axisSettingsButton.setHorizontalTextPosition(0);
      ySettingsPanel.add(axisSettingsButton);
      axisSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("axisSettings button pressed");
            AxisFactorySettingsPage.this.axisSettingsFrame.dispose();
            Settings.log.finest("axis factory settings page disposed");
            new AxisSettingsPage();
         }
      });
      this.axisSettingsFrame.setVisible(true);
      Settings.log.finest("axis factory settings frame initialized");
   }

   public JFrame getFrame() {
      return this.axisSettingsFrame;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType() {
      int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$AxisType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[AxisType.values().length];

         try {
            var0[AxisType.BACKGAUGE.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[AxisType.OTHER.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[AxisType.RAM.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$objects$enums$AxisType = var0;
         return var0;
      }
   }

   class genericListener implements MouseListener {
      public void mouseClicked(MouseEvent e) {
         new CalculatorPage((JTextField)e.getSource());
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }
   }
}
