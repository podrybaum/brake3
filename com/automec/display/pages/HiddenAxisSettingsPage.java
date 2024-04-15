package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.display.components.HiddenAxisSettingsButtonAction;
import com.automec.display.popups.CalculatorPage;
import com.automec.display.popups.MotionErrorView;
import com.automec.objects.Axis;
import com.automec.objects.IncorrectAxisException;
import com.automec.objects.enums.AxisType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HiddenAxisSettingsPage {
   private JFrame axisSettingsFrame = new JFrame("Axis Settings");
   private JPanel axisSettingsPanel;
   public Axis axis;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType;

   public HiddenAxisSettingsPage(Axis axis) {
      this.axis = axis;
      this.initialize(axis);
   }

   private void initialize(Axis axis) {
      this.axisSettingsFrame.setDefaultCloseOperation(3);
      this.axisSettingsFrame.setSize(1024, 768);
      this.axisSettingsFrame.setUndecorated(true);
      this.axisSettingsPanel = new JPanel();
      this.axisSettingsFrame.getContentPane().add(this.axisSettingsPanel);
      this.axisSettingsPanel.setLayout(new BorderLayout(0, 0));
      JLabel lblaxisSettings = new JLabel(axis.getFullName() + " Settings");
      lblaxisSettings.setFont(new Font("Tahoma", 0, 40));
      lblaxisSettings.setHorizontalAlignment(0);
      this.axisSettingsPanel.add(lblaxisSettings, "North");
      JPanel settingsPanel = new JPanel();
      this.axisSettingsPanel.add(settingsPanel, "Center");
      settingsPanel.setLayout(new BorderLayout(0, 0));
      JPanel buttonPanel;
      JPanel rPanel;
      JLabel inFast;
      final JTextField inFastTxt;
      JLabel inSlow;
      final JTextField inSlowTxt;
      JLabel outFast;
      final JTextField outFastTxt;
      JLabel accelAdjust;
      final JTextField accelAdjustTxt;
      JLabel slowD;
      final JTextField slowDTxt;
      JLabel stopD;
      final JTextField stopDTxt;
      JLabel awD;
      final JTextField awDTxt;
      switch($SWITCH_TABLE$com$automec$objects$enums$AxisType()[axis.getAxisType().ordinal()]) {
      case 1:
         try {
            buttonPanel = new JPanel();
            rPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 2));
            rPanel.setLayout(new GridLayout(0, 2));
            buttonPanel.setPreferredSize(new Dimension(500, 500));
            rPanel.setPreferredSize(new Dimension(500, 500));
            inFast = new JLabel("inFast:");
            inFastTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getInFast()));
            inFastTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  try {
                     ((Axis)Settings.axes.get(0)).setInFast(Double.valueOf(inFastTxt.getText()));
                  } catch (NumberFormatException var3) {
                     var3.printStackTrace();
                  } catch (IncorrectAxisException var4) {
                     var4.printStackTrace();
                  }

               }
            });
            buttonPanel.add(inFast);
            buttonPanel.add(inFastTxt);
            inSlow = new JLabel("inSlow:");
            inSlowTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getInSlow()));
            inSlowTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  try {
                     ((Axis)Settings.axes.get(0)).setInSlow(Double.valueOf(inSlowTxt.getText()));
                  } catch (NumberFormatException var3) {
                     var3.printStackTrace();
                  } catch (IncorrectAxisException var4) {
                     var4.printStackTrace();
                  }

               }
            });
            buttonPanel.add(inSlow);
            buttonPanel.add(inSlowTxt);
            outFast = new JLabel("outFast:");
            outFastTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getOutFast()));
            outFastTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  try {
                     ((Axis)Settings.axes.get(0)).setOutFast(Double.valueOf(outFastTxt.getText()));
                  } catch (NumberFormatException var3) {
                     var3.printStackTrace();
                  } catch (IncorrectAxisException var4) {
                     var4.printStackTrace();
                  }

               }
            });
            buttonPanel.add(outFast);
            buttonPanel.add(outFastTxt);
            accelAdjust = new JLabel("motion error ACC ADJUST:");
            accelAdjustTxt = new JTextField(String.valueOf(Listener.xAxisAccelerationAdj));
            accelAdjustTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Listener.xAxisAccelerationAdj = Double.valueOf(accelAdjustTxt.getText());
               }
            });
            buttonPanel.add(accelAdjust);
            buttonPanel.add(accelAdjustTxt);
            slowD = new JLabel("slow distance");
            slowDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getSlowDistance()));
            slowDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(0)).setStopDistance(Double.valueOf(slowDTxt.getText()));
               }
            });
            buttonPanel.add(slowD);
            buttonPanel.add(slowDTxt);
            stopD = new JLabel("stop distance");
            stopDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getStopDistance()));
            stopDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(0)).setStopDistance(Double.valueOf(stopDTxt.getText()));
               }
            });
            buttonPanel.add(stopD);
            buttonPanel.add(stopDTxt);
            awD = new JLabel("aw distance");
            awDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(0)).getAwDistance()));
            awDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(0)).setStopDistance(Double.valueOf(awDTxt.getText()));
               }
            });
            buttonPanel.add(awD);
            buttonPanel.add(awDTxt);
            settingsPanel.add(buttonPanel, "West");
            settingsPanel.add(rPanel, "East");
         } catch (IncorrectAxisException var21) {
            Settings.log.log(Level.WARNING, "somehow this page got dereferenced from the proper axis", var21);
         } catch (Exception var22) {
            Settings.log.log(Level.SEVERE, "axisfactorysettings page encountered unhandled error in x", var22);
         }
         break;
      case 2:
         try {
            buttonPanel = new JPanel();
            rPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 2));
            rPanel.setLayout(new GridLayout(0, 2));
            buttonPanel.setPreferredSize(new Dimension(500, 500));
            rPanel.setPreferredSize(new Dimension(500, 500));
            inFast = new JLabel("");
            inFastTxt = new JTextField();
            buttonPanel.add(inFast);
            buttonPanel.add(inFastTxt);
            inSlow = new JLabel("");
            inSlowTxt = new JTextField();
            buttonPanel.add(inSlow);
            buttonPanel.add(inSlowTxt);
            outFast = new JLabel("");
            outFastTxt = new JTextField();
            buttonPanel.add(outFast);
            buttonPanel.add(outFastTxt);
            accelAdjust = new JLabel("");
            accelAdjustTxt = new JTextField();
            buttonPanel.add(accelAdjust);
            buttonPanel.add(accelAdjustTxt);
            slowD = new JLabel("slow distance");
            slowDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(1)).getSlowDistance()));
            slowDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(1)).setSlowDistance(Double.valueOf(slowDTxt.getText()));
               }
            });
            buttonPanel.add(slowD);
            buttonPanel.add(slowDTxt);
            stopD = new JLabel("stop distance");
            stopDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(1)).getStopDistance()));
            stopDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(1)).setStopDistance(Double.valueOf(stopDTxt.getText()));
               }
            });
            buttonPanel.add(stopD);
            buttonPanel.add(stopDTxt);
            awD = new JLabel("aw distance");
            awDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(1)).getAwDistance()));
            awDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(1)).setAwDistance(Double.valueOf(awDTxt.getText()));
               }
            });
            buttonPanel.add(awD);
            buttonPanel.add(awDTxt);
            settingsPanel.add(buttonPanel, "West");
            settingsPanel.add(rPanel, "East");
         } catch (Exception var23) {
            Settings.log.log(Level.SEVERE, "axisfactorysettings page encountered unhandled error in y", var23);
         }
         break;
      case 3:
         try {
            buttonPanel = new JPanel();
            rPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 2));
            rPanel.setLayout(new GridLayout(0, 2));
            buttonPanel.setPreferredSize(new Dimension(500, 500));
            rPanel.setPreferredSize(new Dimension(500, 500));
            inFast = new JLabel();
            inFastTxt = new JTextField();
            buttonPanel.add(inFast);
            buttonPanel.add(inFastTxt);
            inSlow = new JLabel();
            inSlowTxt = new JTextField();
            buttonPanel.add(inSlow);
            buttonPanel.add(inSlowTxt);
            outFast = new JLabel();
            outFastTxt = new JTextField();
            buttonPanel.add(outFast);
            buttonPanel.add(outFastTxt);
            accelAdjust = new JLabel();
            accelAdjustTxt = new JTextField();
            buttonPanel.add(accelAdjust);
            buttonPanel.add(accelAdjustTxt);
            slowD = new JLabel("slow distance");
            slowDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(2)).getSlowDistance()));
            slowDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(2)).setStopDistance(Double.valueOf(slowDTxt.getText()));
               }
            });
            buttonPanel.add(slowD);
            buttonPanel.add(slowDTxt);
            stopD = new JLabel("stop distance");
            stopDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(2)).getStopDistance()));
            stopDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(2)).setStopDistance(Double.valueOf(stopDTxt.getText()));
               }
            });
            buttonPanel.add(stopD);
            buttonPanel.add(stopDTxt);
            awD = new JLabel("aw distance");
            awDTxt = new JTextField(String.valueOf(((Axis)Settings.axes.get(2)).getAwDistance()));
            awDTxt.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((Axis)Settings.axes.get(2)).setStopDistance(Double.valueOf(awDTxt.getText()));
               }
            });
            buttonPanel.add(awD);
            buttonPanel.add(awDTxt);
            settingsPanel.add(buttonPanel, "West");
            settingsPanel.add(rPanel, "East");
         } catch (Exception var20) {
            Settings.log.log(Level.SEVERE, "axisfactorysettings page encountered unhandled error in x", var20);
         }
      }

      buttonPanel = new JPanel();
      this.axisSettingsPanel.add(buttonPanel, "South");
      JButton homeButton = new JButton("      ");
      homeButton.setVerticalTextPosition(3);
      homeButton.setHorizontalTextPosition(0);
      buttonPanel.add(homeButton);
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            HiddenAxisSettingsPage.this.axisSettingsFrame.dispose();
            Settings.log.finest("axis factory settings page disposed");
            new HomePage();
         }
      });
      JButton settingsButton = new JButton("HiddenSettings");
      settingsButton.setVerticalTextPosition(3);
      settingsButton.setHorizontalTextPosition(0);
      buttonPanel.add(settingsButton);
      settingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Settings button pressed");
            HiddenAxisSettingsPage.this.axisSettingsFrame.dispose();
            Settings.log.finest("axis factory settings page disposed");
            new HiddenSettingsPage();
         }
      });
      ArrayList<JButton> axisButtons = new ArrayList();

      for(int i = 0; i < Settings.axes.size(); ++i) {
         if (((Axis)Settings.axes.get(i)).equals(axis)) {
            axisButtons.add(new JButton("<html>" + ((Axis)Settings.axes.get(i)).getShortName() + "-Axis<br/>Settings</html>"));
            ((JButton)axisButtons.get(i)).setVerticalTextPosition(3);
            ((JButton)axisButtons.get(i)).setHorizontalTextPosition(0);
            buttonPanel.add((Component)axisButtons.get(i));
            ((JButton)axisButtons.get(i)).addActionListener(new HiddenAxisSettingsButtonAction((Axis)Settings.axes.get(i), this.getFrame()));
         } else {
            axisButtons.add(new JButton("<html>" + ((Axis)Settings.axes.get(i)).getShortName() + "-Axis<br/>Settings</html"));
            ((JButton)axisButtons.get(i)).setVerticalTextPosition(3);
            ((JButton)axisButtons.get(i)).setHorizontalTextPosition(0);
            buttonPanel.add((Component)axisButtons.get(i));
            ((JButton)axisButtons.get(i)).addActionListener(new HiddenAxisSettingsButtonAction((Axis)Settings.axes.get(i), this.getFrame()));
         }
      }

      new MotionErrorView("test");
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
