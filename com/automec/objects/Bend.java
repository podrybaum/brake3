package com.automec.objects;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DataDisplayLabel;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JButtonCustom;
import com.automec.display.components.JTextFieldCustom;
import com.automec.display.pages.EditJobPage;
import com.automec.display.pages.RunJobPage;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Bend {
   private String jobName;
   private int number;
   private int cycles;
   private ArrayList<AxisValues> axisValues;
   private String bendImage = "";
   private Units units;
   private boolean retractEnabled;
   private boolean baEnabled;
   private Mode mode;
   private Location location;

   public Bend(Job job, int cycles, ArrayList<AxisValues> axisValues) {
      this.units = Units.INCHES;
      this.retractEnabled = false;
      this.baEnabled = false;
      this.mode = Mode.DEPTH;
      this.location = Location.MEMORY;
      this.jobName = job.getName();
      this.number = job.getBendNo();
      this.cycles = cycles;
      this.axisValues = axisValues;
      this.retractEnabled = job.getRetractEnabled();
      this.baEnabled = job.getBaEnabled();
      this.mode = job.getMode();
      this.location = job.getLocation();
      job.getBendNotes().add(this.number - 1, "");
   }

   public Bend(Job job, ArrayList<AxisValues> axisValues) {
      this.units = Units.INCHES;
      this.retractEnabled = false;
      this.baEnabled = false;
      this.mode = Mode.DEPTH;
      this.location = Location.MEMORY;
      this.jobName = job.getName();
      this.number = job.getBendNo();
      this.cycles = 1;
      this.retractEnabled = job.getRetractEnabled();
      this.baEnabled = job.getBaEnabled();
      this.mode = job.getMode();
      this.location = job.getLocation();
      job.getBendNotes().add(this.number - 1, "");
      this.axisValues = axisValues;
   }

   public Bend(Job job) {
      this.units = Units.INCHES;
      this.retractEnabled = false;
      this.baEnabled = false;
      this.mode = Mode.DEPTH;
      this.location = Location.MEMORY;
      this.jobName = job.getName();
      this.number = job.getBendNo();
      this.cycles = 1;
      this.axisValues = new ArrayList();
      this.retractEnabled = job.getRetractEnabled();
      this.baEnabled = job.getBaEnabled();
      this.mode = job.getMode();
      this.location = job.getLocation();
      job.getBendNotes().add(this.number - 1, "");

      for(int i = 0; i < Settings.axes.size(); ++i) {
         this.axisValues.add(new AxisValues(((Axis)Settings.axes.get(i)).getAxisType(), ((Axis)Settings.axes.get(i)).getShortName(), this.mode));
      }

   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof Bend) {
            String one = (new Gson()).toJson(this.axisValues);
            String two = (new Gson()).toJson(((Bend)o).getAxisValues());
            if (one.equals(two)) {
               return true;
            }
         }

         return false;
      }
   }

   public void setLocation(Location loc) {
      this.location = loc;
   }

   public int getCycles() {
      return this.cycles;
   }

   public ArrayList<AxisValues> getAxisValues() {
      return this.axisValues;
   }

   public JPanel getBendPanel() {
      int count = 0;
      JPanel bendPanel = new JPanel();
      bendPanel.setLayout(new GridBagLayout());
      int cellHeight = 30;
      JLabel bendNumber = new JLabel(String.valueOf(this.number));
      bendNumber.setFont(DisplayComponents.bendPanelFont);
      bendNumber.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      bendPanel.add(bendNumber, DisplayComponents.GenerateConstraints(0, 0, 0.0D, 0.0D, 1, 1, 13, new Insets(0, 0, 0, 5)));
      int var12 = count + 1;
      bendNumber.setPreferredSize(new Dimension((Integer)EditJobPage.widths.get(count), cellHeight));
      bendNumber.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            ((EditJobPage)SwingUtilities.getRoot((Component)arg0.getSource())).openBendPopup(Bend.this.number, arg0.getXOnScreen(), arg0.getYOnScreen());
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
      JTextField cycleNumber = new JTextField(String.valueOf(this.cycles));
      cycleNumber.setFont(DisplayComponents.bendPanelFont);
      cycleNumber.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      cycleNumber.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JTextField temp = (JTextField)e.getSource();
            Bend.this.cycles = Integer.parseInt(temp.getText());
         }
      });
      cycleNumber.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent arg0) {
         }

         public void focusLost(FocusEvent arg0) {
            JTextField temp = (JTextField)arg0.getSource();
            temp.setText(String.valueOf(Bend.this.cycles));
         }
      });
      bendPanel.add(cycleNumber, DisplayComponents.GenerateConstraints(1, 0, 0.0D, 0.0D, 1, 1, 13, new Insets(5, 0, 5, 5)));
      cycleNumber.addMouseListener(DisplayComponents.CalculatorPopup());
      cycleNumber.setPreferredSize(new Dimension((Integer)EditJobPage.widths.get(var12++), cellHeight));
      int counter = 2;

      for(int i = 0; i < this.axisValues.size(); ++i) {
         if (((Axis)Settings.axes.get(i)).getEnabled()) {
            for(int j = 0; j < ((AxisValues)this.axisValues.get(i)).getValues().size(); ++j) {
               if (((AxisValues)this.axisValues.get(i)).getAxisType() == AxisType.BACKGAUGE && j == 1) {
                  JButtonCustom temp = new JButtonCustom((String)((AxisValues)this.axisValues.get(i)).getValues().get(j), i, j);
                  temp.setPreferredSize(new Dimension((Integer)EditJobPage.widths.get(var12++), cellHeight));
                  temp.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        JButtonCustom temp = (JButtonCustom)e.getSource();
                        temp.setFont(DisplayComponents.bendPanelFont);
                        String var3;
                        switch((var3 = (String)((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().get(temp.getIndex2())).hashCode()) {
                        case 85:
                           if (var3.equals("U")) {
                              temp.setText("PP");
                              ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), "PP");
                              return;
                           }
                           break;
                        case 1024:
                           if (var3.equals("  ")) {
                              temp.setText("U ");
                              ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), "U");
                              return;
                           }
                           break;
                        case 2560:
                           if (var3.equals("PP")) {
                              temp.setText("  ");
                              ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), "  ");
                              return;
                           }
                        }

                        temp.setText("  ");
                        ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), "  ");
                     }
                  });
                  temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  if (!this.retractEnabled) {
                     temp.setEnabled(false);
                     temp.setBackground(DisplayComponents.Background);
                  }

                  bendPanel.add(temp, DisplayComponents.GenerateConstraints(counter++, 0, 0.0D, 0.0D, 1, 1, 13, new Insets(5, 0, 5, 5)));
               } else {
                  final JTextFieldCustom temp = new JTextFieldCustom((String)((AxisValues)this.axisValues.get(i)).getValues().get(j), i, j, this.number, 0.0D, 0.0D);
                  temp.setFont(DisplayComponents.bendPanelFont);
                  temp.setPreferredSize(new Dimension((Integer)EditJobPage.widths.get(var12++), cellHeight));
                  final MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
                  switch(temp.getIndex1() * 10 + temp.getIndex2()) {
                  case 0:
                     temp.setMinMax(0.0D, ((Axis)Settings.axes.get(0)).getAxisLength());
                     temp.setFormat("%.3f");
                     break;
                  case 2:
                     temp.setMinMax(0.0D, 60.0D);
                     temp.setFormat("%.1f");
                     temp.setAngle(true);
                     temp.setAngleFormat("%.1f");
                     break;
                  case 3:
                     temp.setMinMax(0.0D, ((Axis)Settings.axes.get(0)).getAxisLength());
                     temp.setFormat("%.3f");
                     break;
                  case 4:
                     temp.setMinMax(-1.0D, 1.0D);
                     temp.setFormat("%.3f");
                     break;
                  case 10:
                     if (this.mode == Mode.ANGLE) {
                        temp.setMinMax(70.0D, 180.0D);
                        temp.setFormat("%.1f");
                        temp.setAngle(true);
                     } else {
                        temp.setMinMax(0.0D, 99.0D);
                        temp.setFormat("%.3f");
                     }
                     break;
                  case 11:
                     if (this.mode.equals(Mode.ANGLE)) {
                        temp.setMinMax(-20.0D, 20.0D);
                        temp.setFormat("%.1f");
                        temp.setAngle(true);
                     } else {
                        temp.setMinMax(0.0D, 32.0D);
                        temp.setFormat("%.3f");
                     }
                     break;
                  case 12:
                     temp.setMinMax(0.0D, 32.0D);
                     temp.setFormat("%.3f");
                     break;
                  case 20:
                     if (((Axis)Settings.axes.get(2)).getZeroAdjust()) {
                        if (Settings.floatingCalibration) {
                           temp.setMinMax(((Axis)Settings.axes.get(2)).getZeroOffsetMin(), ((Axis)Settings.axes.get(2)).getZeroOffsetMax());
                        } else {
                           temp.setMinMax(((Axis)Settings.axes.get(2)).getZeroOffsetMin(), ((Axis)Settings.axes.get(2)).getZeroOffsetMax());
                        }
                     } else {
                        temp.setMinMax(0.0D, ((Axis)Settings.axes.get(2)).getAxisLength() - 0.1D);
                     }

                     temp.setFormat("%.3f");
                  }

                  temp.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        if (SystemCommands.validInput((JTextFieldCustom)e.getSource())) {
                           Units units;
                           if (Settings.activeFrame instanceof EditJobPage) {
                              units = EditJobPage.existingPage.getUnits();
                           } else if (Settings.activeFrame instanceof RunJobPage) {
                              units = RunJobPage.job.getUnits();
                           } else {
                              units = Settings.units;
                           }

                           if (((JTextFieldCustom)e.getSource()).getAngle()) {
                              if (temp.getAngleFormat().equals("%d")) {
                                 ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format(temp.getAngleFormat(), Integer.parseInt(temp.getText())));
                              } else {
                                 ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format(temp.getFormat(), Double.parseDouble(temp.getText())));
                              }
                           } else if (units == Units.INCHES) {
                              ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format(temp.getFormat(), Double.parseDouble(temp.getText())));
                           } else {
                              BigDecimal mm = BigDecimal.valueOf(25.4D);
                              ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format("%.9f", BigDecimal.valueOf(Double.parseDouble(temp.getText())).divide(mm, mc).doubleValue()));
                           }
                        } else if (((JTextFieldCustom)e.getSource()).getAngle()) {
                           if (((Axis)Settings.axes.get(1)).getMinimumAngleOverride()) {
                              if (Double.parseDouble(temp.getText()) > 180.0D) {
                                 ((JTextFieldCustom)e.getSource()).setBackground(Color.RED);
                              } else {
                                 new NotificationPage("Angle Notification", "You typed a " + ((JTextFieldCustom)e.getSource()).getText() + " degrees acute (closed) angle. Did you mean " + (180.0D - Double.valueOf(((JTextFieldCustom)e.getSource()).getText())) + " degree obtuse (open) angle?", "ACUTE2.PNG");
                                 if (Double.valueOf(((JTextFieldCustom)e.getSource()).getText()) > 2.0D) {
                                    ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format(temp.getFormat(), Double.parseDouble(temp.getText())));
                                 } else {
                                    ((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().set(temp.getIndex2(), String.format(temp.getFormat(), Double.parseDouble("2.0")));
                                 }
                              }
                           } else {
                              if (!(((JTextFieldCustom)e.getSource()).getMin() < 0.0D) && temp.getIndex1() != 0) {
                                 new NotificationPage("Angle Error", "You typed: " + ((JTextFieldCustom)e.getSource()).getText() + " degrees acute (closed), did you mean: " + (180.0D - Double.valueOf(((JTextFieldCustom)e.getSource()).getText())) + " degrees obtuse (open)?", 4000);
                              }

                              ((JTextFieldCustom)e.getSource()).setBackground(Color.RED);
                           }
                        } else {
                           ((JTextFieldCustom)e.getSource()).setBackground(Color.RED);
                        }

                        ((JTextFieldCustom)e.getSource()).setNumber((String)((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().get(temp.getIndex2()));
                     }
                  });
                  temp.addFocusListener(new FocusListener() {
                     public void focusGained(FocusEvent arg0) {
                     }

                     public void focusLost(FocusEvent arg0) {
                        JTextFieldCustom temp = (JTextFieldCustom)arg0.getSource();
                        if (!((String)((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().get(temp.getIndex2())).isEmpty()) {
                           temp.setNumber((String)((AxisValues)Bend.this.axisValues.get(temp.getIndex1())).getValues().get(temp.getIndex2()));
                        }

                     }
                  });
                  int v = temp.getIndex1() * 10 + temp.getIndex2();
                  if (!this.retractEnabled && (v == 0 || v == 2)) {
                     temp.setEnabled(false);
                     temp.setBackground(DisplayComponents.Background);
                  }

                  if (!this.baEnabled && v == 4) {
                     temp.setEnabled(false);
                     temp.setBackground(DisplayComponents.Background);
                  }

                  if (temp.isEnabled()) {
                     temp.addMouseListener(DisplayComponents.CalculatorPopup());
                  }

                  temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  bendPanel.add(temp, DisplayComponents.GenerateConstraints(counter++, 0, 0.0D, 0.0D, 1, 1, 13, new Insets(5, 0, 5, 5)));
               }
            }
         }
      }

      return bendPanel;
   }

   public JPanel getBendPreview(Units units, boolean angle) {
      JPanel retVal = new JPanel();
      JLabel number = new JLabel(String.format("%-20s", this.number));
      number.setFont(DisplayComponents.editJobPageText);
      DataDisplayLabel xDim = new DataDisplayLabel((String)((AxisValues)this.getAxisValues().get(0)).getValues().get(3), units, false);
      xDim.setFont(DisplayComponents.editJobPageText);
      DataDisplayLabel yDim = new DataDisplayLabel((String)((AxisValues)this.getAxisValues().get(1)).getValues().get(0), units, angle);
      yDim.setFont(DisplayComponents.editJobPageText);
      DataDisplayLabel rDim = new DataDisplayLabel((String)((AxisValues)this.getAxisValues().get(2)).getValues().get(0), units, false);
      rDim.setFont(DisplayComponents.editJobPageText);
      retVal.setLayout(new GridBagLayout());
      int count = 0;
      int var9 = count + 1;
      retVal.add(number, DisplayComponents.GenerateConstraints(count, 0, 1.0D, 1.0D));
      if (((Axis)Settings.axes.get(0)).getEnabled()) {
         retVal.add(xDim, DisplayComponents.GenerateConstraints(var9++, 0, 1.0D, 1.0D));
      }

      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         retVal.add(yDim, DisplayComponents.GenerateConstraints(var9++, 0, 1.0D, 1.0D));
      }

      if (((Axis)Settings.axes.get(2)).getEnabled()) {
         retVal.add(rDim, DisplayComponents.GenerateConstraints(var9++, 0, 1.0D, 1.0D));
      }

      retVal.setMaximumSize(new Dimension(400, 20));
      return retVal;
   }

   public void setBendNo(int bendNo) {
      this.number = bendNo;
   }

   public int getBendNo() {
      return this.number;
   }

   public Location getLocation() {
      return this.location;
   }

   public void setRetractEnabled(boolean s) {
      this.retractEnabled = s;
   }

   public void setBaEnabled(boolean s) {
      this.baEnabled = s;
   }

   public String getBendImage() {
      return this.bendImage;
   }

   public void setBendImage(String path) {
      this.bendImage = path;
   }

   public void addBendImage(String path) {
      try {
         String ext = path.substring(path.length() - 4);
         String path2 = "";
         System.out.println(path);
         if (path.startsWith("CNC600")) {
            if (this.location.equals(Location.LOCAL)) {
               path2 = SystemCommands.getWorkingDirectory() + File.separator + path;
            } else {
               path2 = Settings.selectedUSB.path + File.separator + path;
            }
         } else {
            path2 = path;
         }

         if (this.location.equals(Location.LOCAL)) {
            (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName)).mkdirs();
            Files.copy((new File(path2)).toPath(), (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.setBendImage("CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext);
         } else if (this.location.equals(Location.USB)) {
            (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName)).mkdirs();
            Files.copy((new File(path2)).toPath(), (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.setBendImage("CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext);
         } else {
            (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName)).mkdirs();
            Files.copy((new File(path2)).toPath(), (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.setBendImage("CNC600" + File.separator + "Images" + File.separator + this.jobName + File.separator + "Bend" + this.number + ext);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public Units getUnits() {
      return this.units;
   }

   public void setUnits(Units units) {
      this.units = units;
   }
}
