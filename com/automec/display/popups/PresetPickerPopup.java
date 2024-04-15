package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DisplayComponents;
import com.automec.display.pages.AxisFactorySettingsPage;
import com.automec.objects.Axis;
import com.automec.objects.IncorrectAxisException;
import com.automec.objects.enums.XAxisPreset;
import com.automec.objects.enums.YAxisPreset;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class PresetPickerPopup {
   private Axis axis;
   private JFrame presetOptionsFrame;
   private JFrame source;
   private int locX;
   private int locY;
   private static boolean exists = false;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$XAxisPreset;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$YAxisPreset;

   public PresetPickerPopup(JFrame source, Axis axis, int locX, int locY) {
      if (!exists) {
         this.axis = axis;
         this.locX = locX;
         this.locY = locY;
         this.source = source;
         this.presetOptionsFrame = new JFrame();
         this.initialize();
      }

   }

   public void initialize() {
      exists = true;
      this.presetOptionsFrame.setBounds(this.locX, this.locY, 300, 300);
      this.presetOptionsFrame.setDefaultCloseOperation(3);
      this.presetOptionsFrame.setUndecorated(true);
      this.presetOptionsFrame.setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
      this.presetOptionsFrame.getContentPane().setLayout(new BoxLayout(this.presetOptionsFrame.getContentPane(), 1));
      this.presetOptionsFrame.setAlwaysOnTop(true);
      int var2;
      int var3;
      JButton temp;
      if (this.axis.equals((Axis)Settings.axes.get(0))) {
         XAxisPreset[] var4;
         var3 = (var4 = XAxisPreset.values()).length;

         for(var2 = 0; var2 < var3; ++var2) {
            final XAxisPreset x = var4[var2];
            temp = new JButton(x.toString());
            temp.setMinimumSize(new Dimension(250, 50));
            temp.setMaximumSize(new Dimension(250, 50));
            temp.setPreferredSize(new Dimension(250, 50));
            temp.setBackground(DisplayComponents.Active);
            temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            switch($SWITCH_TABLE$com$automec$objects$enums$XAxisPreset()[x.ordinal()]) {
            case 1:
               temp.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     try {
                        ((Axis)Settings.axes.get(0)).setSlowDistance(0.25D);
                        ((Axis)Settings.axes.get(0)).setAxisLength(24.1D);
                        ((Axis)Settings.axes.get(0)).setInLimit(0.0D);
                        ((Axis)Settings.axes.get(0)).setEncoderCountPerInch(4000.0D);
                        ((Axis)Settings.axes.get(0)).setAwDistance(0.005D);
                        ((Axis)Settings.axes.get(0)).setxPreset(x);
                        PresetPickerPopup.this.presetOptionsFrame.dispose();
                        PresetPickerPopup.exists = false;
                        PresetPickerPopup.this.source.dispose();
                        new AxisFactorySettingsPage((Axis)Settings.axes.get(0));
                     } catch (IncorrectAxisException var3) {
                        Settings.log.info("This axis is not an X axis, was it misconfigured?");
                     } catch (Exception var4) {
                        Settings.log.log(Level.SEVERE, "PresetPicker encountered unexpected error", var4);
                     }

                  }
               });
               this.presetOptionsFrame.add(temp);
               break;
            case 2:
               temp.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     try {
                        ((Axis)Settings.axes.get(0)).setSlowDistance(0.25D);
                        ((Axis)Settings.axes.get(0)).setAxisLength(20.1D);
                        ((Axis)Settings.axes.get(0)).setInLimit(0.0D);
                        ((Axis)Settings.axes.get(0)).setEncoderCountPerInch(4000.0D);
                        ((Axis)Settings.axes.get(0)).setAwDistance(0.005D);
                        ((Axis)Settings.axes.get(0)).setxPreset(x);
                        PresetPickerPopup.this.presetOptionsFrame.dispose();
                        PresetPickerPopup.exists = false;
                        PresetPickerPopup.this.source.dispose();
                        new AxisFactorySettingsPage((Axis)Settings.axes.get(0));
                     } catch (IncorrectAxisException var3) {
                        Settings.log.info("This axis is not an X axis, was it misconfigured?");
                     } catch (Exception var4) {
                        Settings.log.log(Level.SEVERE, "PresetPicker encountered unexpected error", var4);
                     }

                  }
               });
               this.presetOptionsFrame.add(temp);
               break;
            case 3:
               temp.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     try {
                        ((Axis)Settings.axes.get(0)).setxPreset(x);
                        PresetPickerPopup.this.presetOptionsFrame.dispose();
                        PresetPickerPopup.exists = false;
                        PresetPickerPopup.this.source.dispose();
                        new AxisFactorySettingsPage((Axis)Settings.axes.get(0));
                     } catch (IncorrectAxisException var3) {
                        Settings.log.info("This axis is not an X axis, was it misconfigured?");
                     } catch (Exception var4) {
                        Settings.log.log(Level.SEVERE, "PresetPicker encountered unexpected error", var4);
                     }

                  }
               });
               this.presetOptionsFrame.add(temp);
               break;
            default:
               Settings.log.warning("this value hasn't been defined yet: " + x.toString());
            }
         }
      } else if (this.axis.equals((Axis)Settings.axes.get(1))) {
         YAxisPreset[] var7;
         var3 = (var7 = YAxisPreset.values()).length;

         for(var2 = 0; var2 < var3; ++var2) {
            final YAxisPreset y = var7[var2];
            temp = new JButton(y.toString());
            temp.setMinimumSize(new Dimension(250, 50));
            temp.setMaximumSize(new Dimension(250, 50));
            temp.setPreferredSize(new Dimension(250, 50));
            temp.setBackground(DisplayComponents.Active);
            temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            switch($SWITCH_TABLE$com$automec$objects$enums$YAxisPreset()[y.ordinal()]) {
            case 1:
               temp.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     try {
                        ((Axis)Settings.axes.get(1)).setBottomTollerence(0.005D);
                        ((Axis)Settings.axes.get(1)).setAwDistance(0.1D);
                        ((Axis)Settings.axes.get(1)).setSlowDistance(1.5D);
                        ((Axis)Settings.axes.get(1)).setEncoderCountPerInch(1023.0D);
                        ((Axis)Settings.axes.get(1)).setyPreset(y);
                        PresetPickerPopup.this.presetOptionsFrame.dispose();
                        PresetPickerPopup.exists = false;
                        PresetPickerPopup.this.source.dispose();
                        new AxisFactorySettingsPage((Axis)Settings.axes.get(1));
                     } catch (IncorrectAxisException var3) {
                        Settings.log.info("This axis is not an Y axis, was it misconfigured?");
                     } catch (Exception var4) {
                        Settings.log.log(Level.SEVERE, "PresetPicker encountered unexpected error", var4);
                     }

                  }
               });
               this.presetOptionsFrame.add(temp);
               break;
            case 2:
               temp.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     try {
                        ((Axis)Settings.axes.get(1)).setBottomTollerence(0.005D);
                        ((Axis)Settings.axes.get(1)).setAwDistance(0.1D);
                        ((Axis)Settings.axes.get(1)).setSlowDistance(1.5D);
                        ((Axis)Settings.axes.get(1)).setEncoderCountPerInch(1000.0D);
                        ((Axis)Settings.axes.get(1)).setyPreset(y);
                        PresetPickerPopup.this.presetOptionsFrame.dispose();
                        PresetPickerPopup.exists = false;
                        PresetPickerPopup.this.source.dispose();
                        new AxisFactorySettingsPage((Axis)Settings.axes.get(1));
                     } catch (IncorrectAxisException var3) {
                        Settings.log.info("This axis is not an Y axis, was it misconfigured?");
                     } catch (Exception var4) {
                        Settings.log.log(Level.SEVERE, "PresetPicker encountered unexpected error", var4);
                     }

                  }
               });
               this.presetOptionsFrame.add(temp);
            }
         }
      }

      this.presetOptionsFrame.setVisible(true);
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

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$objects$enums$YAxisPreset() {
      int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$YAxisPreset;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[YAxisPreset.values().length];

         try {
            var0[YAxisPreset.CAT1.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[YAxisPreset.CAT6.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$objects$enums$YAxisPreset = var0;
         return var0;
      }
   }
}
