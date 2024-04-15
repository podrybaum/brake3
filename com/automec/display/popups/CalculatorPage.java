package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DataInputField;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JTextFieldCustom;
import com.automec.display.pages.EditJobPage;
import com.automec.display.pages.RunJobPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
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
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class CalculatorPage {
   private JFrame calculatorFrame;
   private static Point location = new Point(212, 234);
   private BigDecimal holdValue = BigDecimal.valueOf(0.0D);
   private Job job = null;
   private Axis axis = null;
   private int bendNo = 0;
   public static JTextField valueLabel;
   private Component source;
   private static boolean exists = false;
   private static boolean first = true;
   private Color background;
   private Units units;
   private boolean axisPanel = false;

   public CalculatorPage(String value) {
      if (!exists) {
         this.calculatorFrame = new JFrame("Calc");
         this.source = null;
         this.initialize(value);
      }

   }

   public CalculatorPage(JTextField source) {
      if (!exists) {
         this.calculatorFrame = new JFrame("Calc");
         this.source = source;
         this.background = source.getBackground();
         this.initialize(source.getText());
      }

   }

   public CalculatorPage(JLabel source) {
      if (!exists) {
         this.calculatorFrame = new JFrame("Calc");
         this.source = source;
         this.background = source.getBackground();
         this.initialize(source.getText());
      }

   }

   public CalculatorPage(Component source) {
      if (!exists) {
         this.calculatorFrame = new JFrame("Calc");
         this.source = source;
         this.background = source.getBackground();
         if (source != null) {
            if (source instanceof JTextField) {
               this.initialize(((JTextField)source).getText());
            } else if (source instanceof JLabel) {
               this.initialize(((JLabel)source).getText());
            } else if (source instanceof DataInputField) {
               this.initialize(((DataInputField)source).getText());
            } else {
               Settings.log.fine("not either init");
            }
         }
      }

   }

   public CalculatorPage(Component source, Job job, Axis axis, Units units, int bendNo) {
      if (!exists) {
         this.calculatorFrame = new JFrame("Calc");
         this.source = source;
         this.job = job;
         this.axis = axis;
         this.bendNo = bendNo;
         this.units = units;
         this.background = source.getBackground();
         this.axisPanel = true;
         if (source != null) {
            BigDecimal mm;
            MathContext mc;
            if (axis.equals((Axis)Settings.axes.get(0))) {
               if (units.equals(Units.INCHES)) {
                  this.initialize((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(0)).getValues().get(3));
               } else {
                  mm = BigDecimal.valueOf(25.4D);
                  mc = new MathContext(3, RoundingMode.HALF_EVEN);
                  this.initialize(String.valueOf((new BigDecimal((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(0)).getValues().get(3))).multiply(mm, mc).doubleValue()));
               }
            } else if (axis.equals((Axis)Settings.axes.get(1))) {
               if (job.getMode().equals(Mode.DEPTH)) {
                  if (units.equals(Units.INCHES)) {
                     this.initialize((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(1)).getValues().get(0));
                  } else {
                     mm = BigDecimal.valueOf(25.4D);
                     mc = new MathContext(3, RoundingMode.HALF_EVEN);
                     this.initialize(String.valueOf((new BigDecimal((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(1)).getValues().get(0))).multiply(mm, mc).doubleValue()));
                  }
               } else {
                  this.initialize((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(1)).getValues().get(1));
               }
            } else if (axis.equals((Axis)Settings.axes.get(2))) {
               if (units.equals(Units.INCHES)) {
                  this.initialize((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(2)).getValues().get(0));
               } else {
                  mm = BigDecimal.valueOf(25.4D);
                  mc = new MathContext(3, RoundingMode.HALF_EVEN);
                  this.initialize(String.valueOf((new BigDecimal((String)((AxisValues)((Bend)job.getBends().get(bendNo)).getAxisValues().get(2)).getValues().get(0))).multiply(mm, mc).doubleValue()));
               }
            }
         }
      }

   }

   public void initialize(String value) {
      first = true;
      exists = true;
      this.calculatorFrame.setBounds(212, 234, 200, 315);
      this.calculatorFrame.setLocation(location);
      this.calculatorFrame.setDefaultCloseOperation(3);
      this.calculatorFrame.setUndecorated(true);
      this.calculatorFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.calculatorFrame.setAlwaysOnTop(true);
      this.calculatorFrame.setFocusable(true);
      this.calculatorFrame.setResizable(false);
      if (Settings.activeFrame instanceof EditJobPage) {
         this.units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         this.units = RunJobPage.displayUnits;
      } else {
         this.units = Settings.units;
      }

      this.source.setBackground(DisplayComponents.Active);
      CalculatorPage.FrameDragListener fd = new CalculatorPage.FrameDragListener(this.calculatorFrame);
      this.calculatorFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (CalculatorPage.this.source != null && CalculatorPage.this.source instanceof JTextField && SwingUtilities.getRoot(CalculatorPage.this.source) instanceof EditJobPage) {
               ((EditJobPage)SwingUtilities.getRoot(CalculatorPage.this.source)).highlightBend(CalculatorPage.this.source);
            }

            CalculatorPage.this.source.setBackground(CalculatorPage.this.background);
            CalculatorPage.exists = false;
            CalculatorPage.location = CalculatorPage.this.calculatorFrame.getLocation();
            CalculatorPage.this.calculatorFrame.dispose();
         }
      });
      this.calculatorFrame.addMouseListener(fd);
      this.calculatorFrame.addMouseMotionListener(fd);
      valueLabel = new JTextField(value);
      valueLabel.setHorizontalAlignment(4);
      valueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      valueLabel.setFont(DisplayComponents.pageTitleFontSmall);
      JPanel northPanel = new JPanel();
      GridBagLayout gblnorth = new GridBagLayout();
      gblnorth.columnWeights = new double[]{0.0D, 0.0D, 1.0D};
      gblnorth.rowWeights = new double[]{1.0D, 1.0D};
      northPanel.setLayout(gblnorth);
      JLabel unitsLabel;
      if (this.source instanceof DataInputField) {
         northPanel.add(valueLabel, DisplayComponents.GenerateConstraints(2, 0, 1, 3, 1.0D, 1.0D, new Insets(0, 0, 0, 0)));
         unitsLabel = new JLabel("Max");
         JLabel minLabel = new JLabel("Min");
         JLabel unitsLabel = new JLabel("", 0);
         unitsLabel.setFont(new Font("Tahoma", 0, 8));
         minLabel.setFont(new Font("Tahoma", 0, 8));
         unitsLabel.setFont(new Font("Tahoma", 0, 12));
         String format;
         if (this.units == Units.INCHES) {
            format = "%.3f";
            unitsLabel.setText("in");
         } else {
            format = "%.2f";
            unitsLabel.setText("mm");
         }

         JLabel maxValue = new JLabel(String.format(format + " >=", ((DataInputField)this.source).getMax()), 0);
         JLabel minValue = new JLabel(String.format(format + " <=", ((DataInputField)this.source).getMin()), 0);
         maxValue.setFont(new Font("Tahoma", 0, 12));
         minValue.setFont(new Font("Tahoma", 0, 12));
         northPanel.add(maxValue, DisplayComponents.GenerateConstraints(1, 0, 0.0D, 1.0D, new Insets(0, 0, 0, 0)));
         northPanel.add(minValue, DisplayComponents.GenerateConstraints(1, 1, 0.0D, 1.0D, new Insets(0, 0, 0, 0)));
         northPanel.add(unitsLabel, DisplayComponents.GenerateConstraints(1, 2, 0.0D, 0.5D, new Insets(0, 0, 0, 0)));
      } else if (this.axisPanel) {
         northPanel.add(valueLabel, DisplayComponents.GenerateConstraints(2, 0, 1, 3, 1.0D, 1.0D, new Insets(0, 0, 0, 0)));
         unitsLabel = new JLabel("", 0);
         unitsLabel.setFont(new Font("Tahoma", 0, 12));
         String format;
         if (this.units == Units.INCHES) {
            format = "%.3f";
            unitsLabel.setText("in");
         } else {
            format = "%.2f";
            unitsLabel.setText("mm");
         }

         double min = 0.0D;
         double max = 0.0D;
         BigDecimal mm;
         MathContext mc;
         if (this.axis.getAxisType().equals(AxisType.BACKGAUGE)) {
            if (this.units.equals(Units.INCHES)) {
               min = ((Axis)Settings.axes.get(0)).getInLimit() + (Double)this.job.getOffsets().get(0);
               max = ((Axis)Settings.axes.get(0)).getAxisLength() - (Double)this.job.getOffsets().get(0);
            } else {
               mm = BigDecimal.valueOf(25.4D);
               mc = new MathContext(10, RoundingMode.HALF_EVEN);
               min = BigDecimal.valueOf(((Axis)Settings.axes.get(0)).getInLimit() + (Double)this.job.getOffsets().get(0)).multiply(mm, mc).doubleValue();
               max = BigDecimal.valueOf(((Axis)Settings.axes.get(0)).getAxisLength() - (Double)this.job.getOffsets().get(0)).multiply(mm, mc).doubleValue();
            }
         } else if (this.axis.getAxisType().equals(AxisType.RAM)) {
            if (this.job.getMode().equals(Mode.ANGLE)) {
               min = Double.valueOf((String)((AxisValues)((Bend)this.job.getBends().get(this.bendNo)).getAxisValues().get(1)).getValues().get(0)) + Double.valueOf((String)((AxisValues)((Bend)this.job.getBends().get(this.bendNo)).getAxisValues().get(1)).getValues().get(1)) - 20.0D;
               min = Double.valueOf((String)((AxisValues)((Bend)this.job.getBends().get(this.bendNo)).getAxisValues().get(1)).getValues().get(0)) + Double.valueOf((String)((AxisValues)((Bend)this.job.getBends().get(this.bendNo)).getAxisValues().get(1)).getValues().get(1)) + 20.0D;
            } else if (this.units.equals(Units.INCHES)) {
               min = 0.0D;
               max = 99.0D;
            } else {
               mm = BigDecimal.valueOf(25.4D);
               mc = new MathContext(10, RoundingMode.HALF_EVEN);
               min = BigDecimal.valueOf(0L).multiply(mm, mc).doubleValue();
               max = BigDecimal.valueOf(99L).multiply(mm, mc).doubleValue();
            }
         } else if (this.axis.getAxisType().equals(AxisType.OTHER)) {
            if (this.units.equals(Units.INCHES)) {
               min = -(Double)this.job.getOffsets().get(2);
               max = ((Axis)Settings.axes.get(2)).getAxisLength() - (Double)this.job.getOffsets().get(2);
            } else {
               mm = BigDecimal.valueOf(25.4D);
               mc = new MathContext(10, RoundingMode.HALF_EVEN);
               min = BigDecimal.valueOf(-(Double)this.job.getOffsets().get(2)).multiply(mm, mc).doubleValue();
               max = BigDecimal.valueOf(((Axis)Settings.axes.get(2)).getAxisLength() - (Double)this.job.getOffsets().get(2)).multiply(mm, mc).doubleValue();
            }
         }

         JLabel maxValue = new JLabel(String.format(format + " >=", max), 0);
         JLabel minValue = new JLabel(String.format(format + " <=", min), 0);
         maxValue.setFont(new Font("Tahoma", 0, 12));
         minValue.setFont(new Font("Tahoma", 0, 12));
         northPanel.add(maxValue, DisplayComponents.GenerateConstraints(1, 0, 0.0D, 1.0D, new Insets(0, 0, 0, 0)));
         northPanel.add(minValue, DisplayComponents.GenerateConstraints(1, 1, 0.0D, 1.0D, new Insets(0, 0, 0, 0)));
         northPanel.add(unitsLabel, DisplayComponents.GenerateConstraints(1, 2, 0.0D, 0.5D, new Insets(0, 0, 0, 0)));
      } else {
         northPanel.add(valueLabel, DisplayComponents.GenerateConstraints(0, 0, 3, 2, 1.0D, 1.0D, new Insets(0, 0, 0, 0)));
      }

      this.calculatorFrame.getContentPane().add(northPanel, "North");
      this.holdValue = BigDecimal.valueOf(0.0D);
      JButton close = new JButton("x");
      close.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CalculatorPage.location = ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).getLocation();
            ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
         }
      });
      valueLabel.addMouseListener(fd);
      valueLabel.addMouseMotionListener(fd);
      JPanel buttonPanel = new JPanel();
      GridBagLayout buttonPanelGBL = new GridBagLayout();
      buttonPanelGBL.columnWidths = new int[]{50, 50, 50, 50};
      buttonPanelGBL.rowHeights = new int[]{50, 50, 50, 50, 50};
      buttonPanel.setLayout(buttonPanelGBL);
      buttonPanel.add(DisplayComponents.GenerateCalcButton("7", new CalculatorPage.CalcButtonActionListener("7")), DisplayComponents.GenerateConstraints(0, 0, 1.0D, 1.0D));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("8", new CalculatorPage.CalcButtonActionListener("8")), DisplayComponents.GenerateConstraints(1, 0, 1.0D, 1.0D));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("9", new CalculatorPage.CalcButtonActionListener("9")), DisplayComponents.GenerateConstraints(2, 0, 1.0D, 1.0D));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("=", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               BigDecimal tmp = BigDecimal.valueOf(Double.parseDouble(CalculatorPage.valueLabel.getText()));
               BigDecimal ret = CalculatorPage.this.holdValue.add(tmp);
               CalculatorPage.valueLabel.setText(String.valueOf(ret));
            } catch (Exception var4) {
               Settings.log.log(Level.SEVERE, "Calculator", var4);
            }

         }
      }), DisplayComponents.GenerateConstraints(3, 0, 1.0D, 1.0D));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("4", new CalculatorPage.CalcButtonActionListener("4")), DisplayComponents.GenerateConstraints(0, 1));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("5", new CalculatorPage.CalcButtonActionListener("5")), DisplayComponents.GenerateConstraints(1, 1));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("6", new CalculatorPage.CalcButtonActionListener("6")), DisplayComponents.GenerateConstraints(2, 1));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("+", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               if (!CalculatorPage.valueLabel.getText().startsWith("-") && !CalculatorPage.valueLabel.getText().startsWith("+")) {
                  CalculatorPage.this.holdValue = BigDecimal.valueOf(Double.parseDouble(CalculatorPage.valueLabel.getText()));
               } else {
                  BigDecimal tmp = BigDecimal.valueOf(Double.parseDouble(CalculatorPage.valueLabel.getText()));
                  BigDecimal ret = CalculatorPage.this.holdValue.add(tmp);
                  CalculatorPage.this.holdValue = ret;
               }

               CalculatorPage.valueLabel.setText("+");
               CalculatorPage.valueLabel.setCaretPosition(1);
               CalculatorPage.first = false;
            } catch (Exception var4) {
               Settings.log.log(Level.SEVERE, "Calculator", var4);
            }

         }
      }), DisplayComponents.GenerateConstraints(3, 1));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("1", new CalculatorPage.CalcButtonActionListener("1")), DisplayComponents.GenerateConstraints(0, 2));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("2", new CalculatorPage.CalcButtonActionListener("2")), DisplayComponents.GenerateConstraints(1, 2));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("3", new CalculatorPage.CalcButtonActionListener("3")), DisplayComponents.GenerateConstraints(2, 2));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("-", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               if (!CalculatorPage.valueLabel.getText().startsWith("-") && !CalculatorPage.valueLabel.getText().startsWith("+")) {
                  CalculatorPage.this.holdValue = BigDecimal.valueOf(Double.parseDouble(CalculatorPage.valueLabel.getText()));
               } else {
                  BigDecimal tmp = BigDecimal.valueOf(Double.parseDouble(CalculatorPage.valueLabel.getText()));
                  BigDecimal ret = CalculatorPage.this.holdValue.add(tmp);
                  CalculatorPage.this.holdValue = ret;
               }

               CalculatorPage.valueLabel.setText("-");
               CalculatorPage.valueLabel.setCaretPosition(1);
               CalculatorPage.first = false;
            } catch (NumberFormatException var4) {
               CalculatorPage.valueLabel.setText("-");
               CalculatorPage.valueLabel.setCaretPosition(1);
            } catch (Exception var5) {
               Settings.log.log(Level.SEVERE, "Calculator", var5);
            }

         }
      }), DisplayComponents.GenerateConstraints(3, 2));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("<-", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (CalculatorPage.valueLabel.getText().length() > 0) {
               CalculatorPage.valueLabel.setText(CalculatorPage.valueLabel.getText().substring(0, CalculatorPage.valueLabel.getText().length() - 1));
            }

            CalculatorPage.first = false;
         }
      }), DisplayComponents.GenerateConstraints(0, 3));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("0", new CalculatorPage.CalcButtonActionListener("0")), DisplayComponents.GenerateConstraints(1, 3, 2, 1));
      buttonPanel.add(DisplayComponents.GenerateCalcButton(".", new CalculatorPage.CalcButtonActionListener(".")), DisplayComponents.GenerateConstraints(3, 3));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("C", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CalculatorPage.valueLabel.setText("");
            CalculatorPage.this.holdValue = BigDecimal.valueOf(0.0D);
         }
      }), DisplayComponents.GenerateConstraints(0, 4));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("X", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (CalculatorPage.this.source != null && CalculatorPage.this.source instanceof JTextField && SwingUtilities.getRoot(CalculatorPage.this.source) instanceof EditJobPage) {
               ((EditJobPage)SwingUtilities.getRoot(CalculatorPage.this.source)).highlightBend(CalculatorPage.this.source);
            }

            CalculatorPage.this.source.setBackground(CalculatorPage.this.background);
            CalculatorPage.exists = false;
            CalculatorPage.location = ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).getLocation();
            ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
         }
      }), DisplayComponents.GenerateConstraints(1, 4));
      buttonPanel.add(DisplayComponents.GenerateCalcButton("ENTER", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (CalculatorPage.this.source != null) {
               BigDecimal mm;
               MathContext mc;
               double max;
               int var5;
               ActionListener[] var6;
               ActionEvent ev;
               ActionListener ax;
               int var21;
               if (CalculatorPage.this.source instanceof JTextFieldCustom) {
                  if (((JTextFieldCustom)CalculatorPage.this.source).isAngle()) {
                     ((JTextFieldCustom)CalculatorPage.this.source).setNumber(CalculatorPage.valueLabel.getText());
                  } else if (CalculatorPage.this.units == Units.INCHES) {
                     ((JTextFieldCustom)CalculatorPage.this.source).setNumber(CalculatorPage.valueLabel.getText());
                  } else {
                     mm = BigDecimal.valueOf(25.4D);
                     mc = new MathContext(10, RoundingMode.HALF_EVEN);
                     max = (new BigDecimal(CalculatorPage.valueLabel.getText())).divide(mm, mc).doubleValue();
                     ((JTextFieldCustom)CalculatorPage.this.source).setNumber(String.valueOf(max));
                  }

                  CalculatorPage.this.background = Color.WHITE;
                  ev = new ActionEvent((JTextFieldCustom)CalculatorPage.this.source, 1001, CalculatorPage.valueLabel.getText());
                  var5 = (var6 = ((JTextFieldCustom)CalculatorPage.this.source).getActionListeners()).length;

                  for(var21 = 0; var21 < var5; ++var21) {
                     ax = var6[var21];
                     ax.actionPerformed(ev);
                  }
               } else if (CalculatorPage.this.source instanceof DataInputField) {
                  System.out.println(CalculatorPage.valueLabel.getText() + " " + CalculatorPage.this.units);
                  if (CalculatorPage.this.units == Units.INCHES) {
                     ((DataInputField)CalculatorPage.this.source).setNumber(Double.valueOf(CalculatorPage.valueLabel.getText()));
                  } else {
                     mm = BigDecimal.valueOf(25.4D);
                     mc = new MathContext(10, RoundingMode.HALF_EVEN);
                     max = (new BigDecimal(CalculatorPage.valueLabel.getText())).divide(mm, mc).doubleValue();
                     ((DataInputField)CalculatorPage.this.source).setNumber(max);
                  }

                  ev = new ActionEvent((DataInputField)CalculatorPage.this.source, 1001, CalculatorPage.valueLabel.getText());
                  var5 = (var6 = ((DataInputField)CalculatorPage.this.source).getActionListeners()).length;

                  for(var21 = 0; var21 < var5; ++var21) {
                     ax = var6[var21];
                     ax.actionPerformed(ev);
                  }
               } else if (CalculatorPage.this.source instanceof JLabel) {
                  ((JLabel)CalculatorPage.this.source).setText(CalculatorPage.valueLabel.getText());
                  if (CalculatorPage.this.job != null) {
                     double min = 0.0D;
                     max = 0.0D;
                     double value = Double.parseDouble(CalculatorPage.valueLabel.getText());
                     BigDecimal mmx;
                     MathContext mcx;
                     if (CalculatorPage.this.axis.equals((Axis)Settings.axes.get(0))) {
                        if (CalculatorPage.this.units.equals(Units.INCHES)) {
                           min = ((Axis)Settings.axes.get(0)).getInLimit() + (Double)CalculatorPage.this.job.getOffsets().get(0);
                           max = ((Axis)Settings.axes.get(0)).getAxisLength() - (Double)CalculatorPage.this.job.getOffsets().get(0);
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(0)).getValues().set(3, String.valueOf(value));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(0)).getDecimals().set(3, BigDecimal.valueOf(value));
                           }
                        } else {
                           mmx = BigDecimal.valueOf(25.4D);
                           mcx = new MathContext(10, RoundingMode.HALF_EVEN);
                           min = BigDecimal.valueOf(((Axis)Settings.axes.get(0)).getInLimit() + (Double)CalculatorPage.this.job.getOffsets().get(0)).multiply(mmx, mcx).doubleValue();
                           max = BigDecimal.valueOf(((Axis)Settings.axes.get(0)).getAxisLength() - (Double)CalculatorPage.this.job.getOffsets().get(0)).multiply(mmx, mcx).doubleValue();
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(0)).getValues().set(3, String.valueOf(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(0)).getDecimals().set(3, new BigDecimal(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                           }
                        }
                     } else if (CalculatorPage.this.axis.equals((Axis)Settings.axes.get(1))) {
                        if (CalculatorPage.this.job.getMode().equals(Mode.ANGLE)) {
                           double a = Double.parseDouble((String)((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getValues().get(0));
                           double c = Double.parseDouble((String)((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getValues().get(1));
                           double i = Double.parseDouble(CalculatorPage.valueLabel.getText());
                           double c1 = a - i;
                           if (Math.abs(c1) < Math.abs(i)) {
                              c += c1;
                           } else {
                              c += i;
                           }

                           if (Double.parseDouble(CalculatorPage.valueLabel.getText()) >= -180.0D && Double.parseDouble(CalculatorPage.valueLabel.getText()) <= 180.0D) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getValues().set(1, String.format("%.1f", c));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getDecimals().set(1, new BigDecimal(String.valueOf(c)));
                           }
                        } else if (CalculatorPage.this.units.equals(Units.INCHES)) {
                           min = 0.0D;
                           max = 99.0D;
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getValues().set(0, String.valueOf(value));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getDecimals().set(0, BigDecimal.valueOf(value));
                           }
                        } else {
                           mmx = BigDecimal.valueOf(25.4D);
                           mcx = new MathContext(10, RoundingMode.HALF_EVEN);
                           min = BigDecimal.valueOf(0L).multiply(mmx, mcx).doubleValue();
                           max = BigDecimal.valueOf(99L).multiply(mmx, mcx).doubleValue();
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getValues().set(0, String.valueOf(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(1)).getDecimals().set(0, new BigDecimal(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                           }
                        }
                     } else if (CalculatorPage.this.axis.equals((Axis)Settings.axes.get(2))) {
                        if (CalculatorPage.this.units.equals(Units.INCHES)) {
                           min = -(Double)CalculatorPage.this.job.getOffsets().get(2);
                           max = ((Axis)Settings.axes.get(2)).getAxisLength() - (Double)CalculatorPage.this.job.getOffsets().get(2);
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(2)).getValues().set(0, String.valueOf(value));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(2)).getDecimals().set(0, BigDecimal.valueOf(value));
                           }
                        } else {
                           mmx = BigDecimal.valueOf(25.4D);
                           mcx = new MathContext(10, RoundingMode.HALF_EVEN);
                           min = BigDecimal.valueOf(-(Double)CalculatorPage.this.job.getOffsets().get(2)).multiply(mmx, mcx).doubleValue();
                           max = BigDecimal.valueOf(((Axis)Settings.axes.get(2)).getAxisLength() - (Double)CalculatorPage.this.job.getOffsets().get(2)).multiply(mmx, mcx).doubleValue();
                           if (value >= min && value <= max) {
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(2)).getValues().set(0, String.valueOf(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                              ((AxisValues)((Bend)CalculatorPage.this.job.getBends().get(CalculatorPage.this.bendNo)).getAxisValues().get(2)).getDecimals().set(0, new BigDecimal(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(25.4D), mcx).doubleValue()));
                           }
                        }
                     }
                  }
               } else if (CalculatorPage.this.source instanceof JTextField) {
                  ((JTextField)CalculatorPage.this.source).setText(CalculatorPage.valueLabel.getText());
                  ev = new ActionEvent((JTextField)CalculatorPage.this.source, 1001, CalculatorPage.valueLabel.getText());
                  if (SwingUtilities.getRoot(CalculatorPage.this.source) instanceof EditJobPage && CalculatorPage.this.source instanceof JTextFieldCustom) {
                     EditJobPage root = (EditJobPage)SwingUtilities.getRoot(CalculatorPage.this.source);
                     root.runBend = ((JTextFieldCustom)CalculatorPage.this.source).getBendNo() - 1;
                     ((EditJobPage)SwingUtilities.getRoot(CalculatorPage.this.source)).highlightBend(CalculatorPage.this.source);
                  }

                  ((JTextField)CalculatorPage.this.source).setBackground(CalculatorPage.this.background);
                  var5 = (var6 = ((JTextField)CalculatorPage.this.source).getActionListeners()).length;

                  for(var21 = 0; var21 < var5; ++var21) {
                     ax = var6[var21];
                     ax.actionPerformed(ev);
                  }
               } else {
                  Settings.log.warning("source is not a textfield or a label");
               }
            }

            CalculatorPage.this.source.setBackground(CalculatorPage.this.background);
            CalculatorPage.location = ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).getLocation();
            CalculatorPage.exists = false;
            ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
         }
      }), DisplayComponents.GenerateConstraints(2, 4, 2, 1));
      this.calculatorFrame.getContentPane().add(buttonPanel, "South");
      this.calculatorFrame.setVisible(true);
      this.checkOverlaping();
      Settings.log.log(Level.FINEST, "Calculator initialized: " + location);
   }

   private void checkOverlaping() {
      Point calcp = this.calculatorFrame.getLocationOnScreen();
      Rectangle calc = new Rectangle(calcp.x, calcp.y, this.calculatorFrame.getWidth(), this.calculatorFrame.getHeight());
      Point boxp = this.source.getLocationOnScreen();
      Rectangle box = new Rectangle(boxp.x, boxp.y, this.source.getWidth(), this.source.getHeight());
      if (calc.intersects(box)) {
         double x = calc.getCenterX();
         double x2 = box.getCenterX();
         if (x > x2) {
            if (location.x + box.width < 1024 - calc.width) {
               location = new Point(boxp.x + this.source.getWidth(), location.y);
            } else {
               location = new Point(boxp.x - calc.width, location.y);
            }
         } else if (location.x - box.width > 0) {
            location = new Point(boxp.x - calc.width, location.y);
         } else {
            location = new Point(boxp.x + this.source.getWidth(), location.y);
         }

         this.calculatorFrame.setLocation(location);
      }

   }

   // $FF: synthetic method
   static boolean access$4() {
      return exists;
   }

   static class CalcButtonActionListener implements ActionListener {
      private String value;

      CalcButtonActionListener(String value) {
         this.value = value;
      }

      public void actionPerformed(ActionEvent arg0) {
         try {
            if (!CalculatorPage.valueLabel.getCaret().isVisible() && CalculatorPage.first) {
               CalculatorPage.valueLabel.setText(this.value);
               CalculatorPage.valueLabel.getCaret().setVisible(true);
               CalculatorPage.first = false;
            } else {
               CalculatorPage.valueLabel.getDocument().insertString(CalculatorPage.valueLabel.getCaretPosition(), this.value, (AttributeSet)null);
               CalculatorPage.first = false;
            }
         } catch (BadLocationException var3) {
            Settings.log.log(Level.WARNING, "Calculator", var3);
         }

      }
   }

   static class FrameDragListener extends MouseAdapter {
      private final JFrame frame;
      private Point mouseDownCompCoords = null;

      public FrameDragListener(JFrame frame) {
         this.frame = frame;
      }

      public void mouseReleased(MouseEvent e) {
         this.mouseDownCompCoords = null;
      }

      public void mousePressed(MouseEvent e) {
         this.mouseDownCompCoords = e.getPoint();
      }

      public void mouseDragged(MouseEvent e) {
         Point currCoords = e.getLocationOnScreen();
         this.frame.setLocation(currCoords.x - this.mouseDownCompCoords.x, currCoords.y - this.mouseDownCompCoords.y);
      }
   }
}
