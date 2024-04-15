package com.automec;

import com.automec.display.components.AxisPanel;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.pages.HomePage;
import com.automec.display.pages.ToolLibraryPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AxisRelayState;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Units;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class AngleModeTest {
   private JFrame runFrame = new JFrame("CNC600");
   public static ArrayList<AxisPanel> axisPanels;
   private Timer timerPositionUpdate;
   static JLabel topLabel;
   static JLabel slowLabel;
   static JLabel awLabel;
   static JLabel bottomLabel;
   static JLabel retractDistanceValueLabel;
   static JLabel retractTypeValueLabel;
   static JLabel retractDelayValueLabel;
   static JButton manualButton;
   static JButton autoButton;
   static JPanel retractPanel;
   static JLabel imageLabel;
   private static double backgaugePosition = 12.0D;
   private static double rAxisPosition = 4.0D;
   private static double topPosition;
   private static double slowPosition;
   private static double awPosition;
   private static double angleTarget = 0.0D;
   private static double angleCorrection = 0.0D;
   private static double thicknessV = 0.0D;
   private static double overshootOffset = 0.0D;
   private static ArrayList<Double> depths;
   static Units displayUnits;
   private int flashCount = 0;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType;

   static {
      displayUnits = Units.INCHES;
   }

   public AngleModeTest() {
      try {
         this.initialize();
         this.timerPositionUpdate = new Timer(10, new AngleModeTest.UpdateDisplayPosition());
         this.timerPositionUpdate.start();
      } catch (Exception var2) {
         Settings.log.log(Level.SEVERE, "Exception thrown during job init: ", var2);
      }

   }

   private void initialize() throws Exception {
      calculateAngleArray();
      this.runFrame.setDefaultCloseOperation(3);
      this.runFrame.setSize(1024, 768);
      this.runFrame.setUndecorated(true);
      JPanel titlePanel = new JPanel();
      JLabel titleLabel = new JLabel();
      titleLabel.setText("Run: Angle Mode");
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      titlePanel.add(titleLabel);
      this.runFrame.getContentPane().add(titlePanel, "North");
      JPanel rightPanel = new JPanel();
      this.runFrame.getContentPane().add(rightPanel, "East");
      GridBagLayout rightPanelGBL = new GridBagLayout();
      rightPanel.setLayout(rightPanelGBL);
      rightPanel.setMinimumSize(new Dimension(300, 100));
      axisPanels = new ArrayList();

      int blankI;
      for(blankI = 0; blankI < Settings.axes.size(); ++blankI) {
         if (((Axis)Settings.axes.get(blankI)).getEnabled()) {
            axisPanels.add(new AxisPanel((Axis)Settings.axes.get(blankI)));
         }
      }

      blankI = 0;

      for(int i = 0; i < axisPanels.size(); ++i) {
         blankI = i;
         rightPanel.add(((AxisPanel)axisPanels.get(i)).getAxisPanel(), DisplayComponents.GenerateConstraints(0, i));
      }

      final JButton unitsButton = new JCustomButton("Inches");
      unitsButton.setBackground(Color.LIGHT_GRAY);
      if (Settings.units == Units.INCHES) {
         unitsButton.setText("Inch");
      } else {
         unitsButton.setText("mm");
      }

      unitsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            if (AngleModeTest.displayUnits == Units.INCHES) {
               unitsButton.setText("mm");
               AngleModeTest.displayUnits = Units.MM;
               Settings.log.finest("Units set to Metric");
            } else {
               unitsButton.setText("Inch");
               AngleModeTest.displayUnits = Units.INCHES;
               Settings.log.finest("Units set to Imperial");
            }

         }
      });
      JPanel blank = new JPanel();
      rightPanel.add(unitsButton, DisplayComponents.GenerateConstraints(0, blankI + 1, 1.0D, 0.0D, 1, 1, 12, new Insets(0, 0, 5, 25), 0));
      rightPanel.add(blank, DisplayComponents.GenerateConstraints(0, blankI + 2, 1.0D, 1.0D));
      JPanel leftTopPanel = new JPanel();
      GridBagLayout leftTopPanelGBL = new GridBagLayout();
      leftTopPanelGBL.columnWidths = new int[3];
      leftTopPanelGBL.rowHeights = new int[4];
      leftTopPanelGBL.columnWeights = new double[]{0.0D, 0.0D, 1.0D};
      leftTopPanelGBL.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
      leftTopPanel.setLayout(leftTopPanelGBL);
      JLabel calculatedDepth = new JLabel("Calc depth: ");
      final JLabel calculatedDepthValue = new JLabel("0");
      JLabel angleLabel = new JLabel("Angle: ");
      final JTextField angleValue = new JTextField("0");
      angleValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.angleTarget = Double.valueOf(angleValue.getText());
            calculatedDepthValue.setText(String.format("%.3f", AngleModeTest.calculateTargetDepth()));
         }
      });
      JLabel angleCorrectLabel = new JLabel("Angle Correction: ");
      final JTextField angleCorrectValue = new JTextField("0");
      angleCorrectValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.angleCorrection = Double.valueOf(angleCorrectValue.getText());
            calculatedDepthValue.setText(String.format("%.3f", AngleModeTest.calculateTargetDepth()));
         }
      });
      JLabel punchLabel = new JLabel("Punch: ");
      JLabel punchValue = new JLabel(Settings.selectedPunch.getName());
      JLabel dieLabel = new JLabel("Die: ");
      JLabel dieValue = new JLabel(Settings.selectedDie.getName());
      JLabel yTop = new JLabel("Y Top: ");
      final JTextField yTopValue = new JTextField(String.format("%.2f", topPosition));
      yTopValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.topPosition = Double.valueOf(yTopValue.getText());
         }
      });
      JLabel ySlow = new JLabel("Y Slow: ");
      final JTextField ySlowValue = new JTextField(String.format("%.2f", slowPosition));
      ySlowValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.slowPosition = Double.valueOf(ySlowValue.getText());
         }
      });
      JLabel yAW = new JLabel("Y AW: ");
      final JTextField yAWValue = new JTextField(String.format("%.2f", awPosition));
      yAWValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.awPosition = Double.valueOf(yAWValue.getText());
         }
      });
      JLabel thickness = new JLabel("Thickness: ");
      final JTextField thicknessValue = new JTextField(String.format("%.3f", thicknessV));
      thicknessValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.thicknessV = Double.valueOf(thicknessValue.getText());
            calculatedDepthValue.setText(String.format("%.3f", AngleModeTest.calculateTargetDepth()));
            AngleModeTest.calculateAngleArray();
         }
      });
      JLabel overshoot = new JLabel("Overshoot offset: ");
      final JTextField overshootValue = new JTextField(String.format("%.3f", overshootOffset));
      overshootValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.overshootOffset = Double.valueOf(overshootValue.getText());
         }
      });
      leftTopPanel.add(angleLabel, DisplayComponents.GenerateConstraints(0, 0));
      leftTopPanel.add(angleValue, DisplayComponents.GenerateConstraints(1, 0));
      leftTopPanel.add(angleCorrectLabel, DisplayComponents.GenerateConstraints(0, 1));
      leftTopPanel.add(angleCorrectValue, DisplayComponents.GenerateConstraints(1, 1));
      leftTopPanel.add(punchLabel, DisplayComponents.GenerateConstraints(0, 2));
      leftTopPanel.add(punchValue, DisplayComponents.GenerateConstraints(1, 2));
      leftTopPanel.add(dieLabel, DisplayComponents.GenerateConstraints(0, 3));
      leftTopPanel.add(dieValue, DisplayComponents.GenerateConstraints(1, 3));
      leftTopPanel.add(yTop, DisplayComponents.GenerateConstraints(0, 4));
      leftTopPanel.add(yTopValue, DisplayComponents.GenerateConstraints(1, 4));
      leftTopPanel.add(ySlow, DisplayComponents.GenerateConstraints(0, 5));
      leftTopPanel.add(ySlowValue, DisplayComponents.GenerateConstraints(1, 5));
      leftTopPanel.add(yAW, DisplayComponents.GenerateConstraints(0, 6));
      leftTopPanel.add(yAWValue, DisplayComponents.GenerateConstraints(1, 6));
      leftTopPanel.add(thickness, DisplayComponents.GenerateConstraints(0, 7));
      leftTopPanel.add(thicknessValue, DisplayComponents.GenerateConstraints(1, 7));
      leftTopPanel.add(overshoot, DisplayComponents.GenerateConstraints(0, 8));
      leftTopPanel.add(overshootValue, DisplayComponents.GenerateConstraints(1, 8));
      leftTopPanel.add(calculatedDepth, DisplayComponents.GenerateConstraints(0, 9));
      leftTopPanel.add(calculatedDepthValue, DisplayComponents.GenerateConstraints(1, 9));
      JPanel leftBottomPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout)leftBottomPanel.getLayout();
      flowLayout.setAlignment(0);
      JPanel relayPanel = new JPanel();
      relayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      GridBagLayout relayPanelGBL = new GridBagLayout();
      relayPanelGBL.columnWidths = new int[1];
      relayPanelGBL.rowHeights = new int[4];
      relayPanelGBL.columnWeights = new double[]{0.0D};
      relayPanelGBL.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
      relayPanel.setLayout(relayPanelGBL);
      topLabel = new JLabel("TOP");
      topLabel.setFont(DisplayComponents.pageHeaderFont);
      topLabel.setOpaque(true);
      slowLabel = new JLabel("SLOW");
      slowLabel.setFont(DisplayComponents.pageHeaderFont);
      slowLabel.setOpaque(true);
      awLabel = new JLabel("A/W");
      awLabel.setFont(DisplayComponents.pageHeaderFont);
      awLabel.setOpaque(true);
      bottomLabel = new JLabel("BOTTOM");
      bottomLabel.setFont(DisplayComponents.pageHeaderFont);
      bottomLabel.setOpaque(true);
      relayPanel.add(topLabel, DisplayComponents.GenerateConstraints(0, 0));
      relayPanel.add(slowLabel, DisplayComponents.GenerateConstraints(0, 1));
      relayPanel.add(awLabel, DisplayComponents.GenerateConstraints(0, 2));
      relayPanel.add(bottomLabel, DisplayComponents.GenerateConstraints(0, 3));
      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         leftBottomPanel.add(relayPanel);
      }

      JPanel leftPanel = new JPanel();
      GridBagLayout leftPanelGBL = new GridBagLayout();
      leftPanelGBL.columnWidths = new int[1];
      leftPanelGBL.rowHeights = new int[3];
      leftPanelGBL.columnWeights = new double[]{0.0D};
      leftPanelGBL.rowWeights = new double[]{0.0D, 0.0D, 1.0D};
      leftPanel.setLayout(leftPanelGBL);
      leftPanel.add(leftTopPanel, DisplayComponents.GenerateConstraints(0, 0));
      leftPanel.add(leftBottomPanel, DisplayComponents.GenerateConstraints(0, 1));
      leftPanel.setBorder(new EmptyBorder(0, 30, 0, 0));
      this.runFrame.getContentPane().add(leftPanel, "West");
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BorderLayout(0, 0));
      buttonPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
      JPanel leftButtons = new JPanel();
      leftButtons.setLayout(new FlowLayout(0, 30, 0));
      JButton homeButton = new JBottomButton("Home", "home.png");
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Home button pressed");
            AngleModeTest.this.runFrame.dispose();
            new HomePage();
         }
      });
      leftButtons.add(homeButton);
      JButton openToolLibrary = new JBottomButton("TOOL LIB", (String)null);
      openToolLibrary.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.this.runFrame.dispose();
            new ToolLibraryPage();
         }
      });
      leftButtons.add(openToolLibrary);
      JPanel rightButtons = new JPanel();
      rightButtons.setLayout(new FlowLayout(2, 15, 0));
      final JButton advanceButton = new JBottomButton("<html>ADV</html>", (String)null, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest.driveToBend();
            Settings.log.finest("Advance button pressed");
         }
      });
      advanceButton.setEnabled(false);
      manualButton = new JButton("<html>MAN</html>");
      manualButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (AngleModeTest.manualButton.getBackground() == DisplayComponents.Active) {
               AngleModeTest.manualButton.setBackground(DisplayComponents.Inactive);
               advanceButton.setEnabled(false);
               Settings.log.finest("Manual button pressed, manual mode disabled");
            } else {
               AngleModeTest.manualButton.setBackground(DisplayComponents.Active);
               advanceButton.setEnabled(true);
               Settings.log.finest("Manual Button pressed, manual mode enabled");
            }

         }
      });
      manualButton.setPreferredSize(DisplayComponents.bottomButtonSize);
      manualButton.setBackground(DisplayComponents.Inactive);
      rightButtons.add(manualButton);
      rightButtons.add(advanceButton);
      buttonPanel.add(rightButtons, "East");
      buttonPanel.add(leftButtons, "West");
      this.runFrame.getContentPane().add(buttonPanel, "South");
      int cnt = 0;
      if (((Axis)Settings.axes.get(0)).getEnabled()) {
         ((AxisPanel)axisPanels.get(cnt)).getAxisPanel().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
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
         ++cnt;
      }

      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         ((AxisPanel)axisPanels.get(cnt)).getAxisPanel().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
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
         ++cnt;
      }

      if (((Axis)Settings.axes.get(2)).getEnabled()) {
         ((AxisPanel)axisPanels.get(cnt)).getAxisPanel().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
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
         ++cnt;
      }

      this.runFrame.setVisible(true);
   }

   protected void flashModeButton(final JButton jButton) {
      this.flashCount = 0;
      ((AxisPanel)axisPanels.get(1)).getAxisValueLabel().setOpaque(true);
      (new Timer(500, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            AngleModeTest var10000 = AngleModeTest.this;
            var10000.flashCount = var10000.flashCount + 1;
            if (AngleModeTest.this.flashCount % 2 != 0) {
               jButton.setBackground(DisplayComponents.Inactive);
               ((AxisPanel)AngleModeTest.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Background);
            } else {
               jButton.setBackground(DisplayComponents.Active);
               ((AxisPanel)AngleModeTest.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Active);
            }

            if (AngleModeTest.this.flashCount == 5) {
               jButton.setBackground(DisplayComponents.Inactive);
               ((AxisPanel)AngleModeTest.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Background);
               AngleModeTest.this.flashCount = 0;
               ((Timer)e.getSource()).stop();
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }

         }
      })).start();
   }

   private static void driveToBend() {
      Iterator var1 = Settings.axes.iterator();

      while(var1.hasNext()) {
         Axis a = (Axis)var1.next();
         switch($SWITCH_TABLE$com$automec$objects$enums$AxisType()[a.getAxisType().ordinal()]) {
         case 1:
            if (a.getEnabled()) {
               Communications.driveToPosition(a, backgaugePosition);
            }
            break;
         case 2:
            if (a.getEnabled()) {
               Communications.setYControlParam(a, topPosition, slowPosition + overshootOffset, 0.0D, -1.0D * calculateTargetDepth() + awPosition + overshootOffset, -1.0D * calculateTargetDepth() + overshootOffset);
            }
            break;
         case 3:
            if (a.getEnabled()) {
               Communications.driveToPosition(a, rAxisPosition);
            }
         }
      }

   }

   private static double calculateTargetDepth() {
      BigDecimal width = new BigDecimal(String.format("%.3f", Settings.selectedDie.getDieWidth()));
      BigDecimal angleTarget = new BigDecimal(String.format("%.3f", AngleModeTest.angleTarget));
      BigDecimal thick = new BigDecimal(String.format("%.3f", thicknessV));
      BigDecimal angleCorrect = new BigDecimal(String.format("%.3f", angleCorrection));
      BigDecimal radius = new BigDecimal(String.format("%.3f", Settings.selectedDie.getDieRadius()));
      BigDecimal angle = angleTarget.add(angleCorrect);
      BigDecimal depth = (new BigDecimal(width.doubleValue() / (2.0D * Math.tan(Math.toRadians(angle.doubleValue() / 2.0D))) - (radius.doubleValue() + thick.doubleValue()) * (1.0D / Math.sin(Math.toRadians(angle.doubleValue()) / 2.0D) - 1.0D))).setScale(5, 6);
      return depth.doubleValue();
   }

   private static double calculateTargetDepth(double angle) {
      BigDecimal width = new BigDecimal(String.format("%.3f", Settings.selectedDie.getDieWidth()));
      BigDecimal thick = new BigDecimal(String.format("%.3f", thicknessV));
      BigDecimal radius = new BigDecimal(String.format("%.3f", Settings.selectedDie.getDieRadius()));

      BigDecimal depth;
      try {
         depth = (new BigDecimal(width.doubleValue() / (2.0D * Math.tan(Math.toRadians(angle / 2.0D))) - (radius.doubleValue() + thick.doubleValue()) * (1.0D / Math.sin(Math.toRadians(angle) / 2.0D) - 1.0D))).setScale(5, 6);
      } catch (Exception var7) {
         System.out.println("big dick lary doesnt like my number");
         return 9999.0D;
      }

      return depth.doubleValue();
   }

   private static void calculateAngleArray() {
      ArrayList<Double> angleArray = new ArrayList();

      for(int i = 179; i > 0; --i) {
         angleArray.add(calculateTargetDepth((double)i));
         System.out.println(calculateTargetDepth((double)i));
      }

      depths = angleArray;
   }

   private static int findClosest(double v) {
      double dist = 99999.0D;
      int lowest = -1;

      for(int i = 0; i < depths.size(); ++i) {
         if (Math.abs((Double)depths.get(i) - v) < dist) {
            dist = Math.abs((Double)depths.get(i) - v);
            lowest = i;
            System.out.println("v: " + v + " i: " + i + "depths: " + depths.get(i));
         }
      }

      return lowest;
   }

   private static double calculateCurrentAngle() {
      return (double)findClosest(((Axis)Settings.axes.get(1)).getPositionInches());
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

   class UpdateDisplayPosition implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         try {
            int cnt = 0;
            if (((Axis)Settings.axes.get(1)).getTopStopSOV() == AxisRelayState.ON) {
               AngleModeTest.topLabel.setBackground(DisplayComponents.Active);
            } else {
               AngleModeTest.topLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getSlowSpeedSOV() == AxisRelayState.ON) {
               AngleModeTest.slowLabel.setBackground(DisplayComponents.Active);
            } else {
               AngleModeTest.slowLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getAntiWhipSOV() == AxisRelayState.ON) {
               AngleModeTest.awLabel.setBackground(DisplayComponents.Active);
            } else {
               AngleModeTest.awLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getBottomStopSOV() == AxisRelayState.ON) {
               AngleModeTest.bottomLabel.setBackground(DisplayComponents.Active);
            } else {
               AngleModeTest.bottomLabel.setBackground(DisplayComponents.Background);
            }

            if (AngleModeTest.displayUnits == Units.INCHES) {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)AngleModeTest.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%.3f", ((Axis)Settings.axes.get(0)).getPositionInches()));
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  if (((Axis)Settings.axes.get(1)).getPosition() > 0) {
                     ((AxisPanel)AngleModeTest.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%.3f", ((Axis)Settings.axes.get(1)).getPositionInches()));
                  } else {
                     ((AxisPanel)AngleModeTest.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%.1f", AngleModeTest.calculateCurrentAngle()));
                  }

                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  ((AxisPanel)AngleModeTest.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%.3f", ((Axis)Settings.axes.get(2)).getPositionInches()));
                  ++cnt;
               }
            } else {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)AngleModeTest.axisPanels.get(0)).getAxisValueLabel().setText(String.format("%.2f", ((Axis)Settings.axes.get(0)).getPositionMM()));
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  ((AxisPanel)AngleModeTest.axisPanels.get(1)).getAxisValueLabel().setText(String.format("%.2f", ((Axis)Settings.axes.get(1)).getPositionMM()));
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  ((AxisPanel)AngleModeTest.axisPanels.get(2)).getAxisValueLabel().setText(String.format("%.2f", ((Axis)Settings.axes.get(2)).getPositionMM()));
               }
            }

            cnt = 0;
            if (((Axis)Settings.axes.get(cnt)).getEnabled()) {
               ((AxisPanel)AngleModeTest.axisPanels.get(cnt)).getAxisValueLabel().setHorizontalAlignment(4);
               ++cnt;
            }

            if (((Axis)Settings.axes.get(cnt)).getEnabled()) {
               ((AxisPanel)AngleModeTest.axisPanels.get(1)).getAxisValueLabel().setHorizontalAlignment(4);
               ++cnt;
            }

            if (((Axis)Settings.axes.get(cnt)).getEnabled()) {
               ((AxisPanel)AngleModeTest.axisPanels.get(2)).getAxisValueLabel().setHorizontalAlignment(4);
               ++cnt;
            }
         } catch (Exception var3) {
            Settings.log.log(Level.SEVERE, "Exception thrown while updating display position ", var3);
         }

      }
   }
}
