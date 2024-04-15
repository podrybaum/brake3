package com.automec.display.pages;

import com.automec.Communications;
import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.AxisPanel;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.CalculatorPage;
import com.automec.display.popups.ImagePopupPage;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.AutoMode;
import com.automec.objects.enums.AxisDirection;
import com.automec.objects.enums.AxisRelayState;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class RunJobPage extends JFrame {
   private static final long serialVersionUID = 8899455788470016309L;
   public static ArrayList<AxisPanel> axisPanels;
   private Timer timerPositionUpdate;
   public static int currentBend = 0;
   public static int currentCycle = 1;
   private static int parts;
   public static boolean firstAdvance = true;
   public static Job job;
   public static boolean xAxisMoving = false;
   public static Long xAxisMovingStart = 0L;
   public static int xAxisTargetPosition = -1;
   public static int xAxisInitialPosition = -1;
   public static AxisDirection xAxisTargetDirection = null;
   public static AutoMode autoMode;
   private static JLabel bendValueLabel;
   private static JLabel cycleValueLabel;
   private static JLabel partsValueLabel;
   static JLabel topLabel;
   static JLabel slowLabel;
   static JLabel awLabel;
   static JLabel bottomLabel;
   public static JLabel retractDistanceValueLabel;
   static JLabel retractTypeValueLabel;
   public static JLabel retractDelayValueLabel;
   static JButton manualButton;
   static JButton autoButton;
   static JButton advanceButton;
   static JPanel retractPanel;
   static JLabel imageLabel;
   public static Units displayUnits;
   static ArrayList<Double> depths;
   private int flashCount = 0;
   private String oldBends;
   private static Timer wakeup;

   static {
      autoMode = AutoMode.OFF;
      displayUnits = Units.INCHES;
      depths = new ArrayList();
   }

   public RunJobPage(Job job) {
      super("Run Job");
      currentBend = 0;
      parts = job.getParts();
      displayUnits = job.getUnits();

      try {
         this.initialize(job, 0);
         this.timerPositionUpdate = new Timer(10, new RunJobPage.UpdateDisplayPosition());
         this.timerPositionUpdate.start();
      } catch (Exception var3) {
         Settings.log.log(Level.SEVERE, "Exception thrown during job init: ", var3);
      }

   }

   public RunJobPage(Job job, int bend) {
      super("Run Job");

      try {
         displayUnits = job.getUnits();
         parts = job.getParts();
         this.initialize(job, bend);
         this.timerPositionUpdate = new Timer(10, new RunJobPage.UpdateDisplayPosition());
         this.timerPositionUpdate.start();
      } catch (Exception var4) {
         Settings.log.log(Level.SEVERE, "Exception thrown during job init: ", var4);
      }

   }

   private void initialize(final Job job, int bend) throws Exception {
      RunJobPage.job = job;
      job.setRan();
      if (SystemCommands.jobExists(job.getName())) {
         this.oldBends = (new Gson()).toJson(SystemCommands.getJob(job.getName()).getBends());
      } else {
         this.oldBends = "";
      }

      this.setDefaultCloseOperation(3);
      this.setSize(1024, 768);
      this.setUndecorated(true);
      Settings.activeFrame = this;
      wakeup = new Timer(300000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               (new Robot()).mouseMove(0, 0);
               Thread.sleep(50L);
               (new Robot()).mouseMove(1, 1);
            } catch (Exception var3) {
               var3.printStackTrace();
            }

         }
      });
      this.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      autoMode = AutoMode.OFF;
      firstAdvance = true;
      currentBend = bend;
      currentCycle = ((Bend)job.getBends().get(currentBend)).getCycles();
      JPanel titlePanel = new JPanel();
      JLabel titleLabel = new JLabel();
      titleLabel.setText("Run: Depth Mode");
      if (job.getMode() == Mode.ANGLE) {
         titleLabel.setText("Run: Angle Mode");
      }

      if (job.getMode() == Mode.DEPTHFC) {
         titleLabel.setText("Run: Depth Mode (FC)");
      }

      titleLabel.setFont(DisplayComponents.pageTitleFont);
      titlePanel.add(titleLabel);
      this.getContentPane().add(titlePanel, "North");
      JPanel rightPanel = new JPanel();
      this.getContentPane().add(rightPanel, "East");
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
      if (job.getUnits() == Units.INCHES) {
         unitsButton.setText("Inch");
      } else {
         unitsButton.setText("mm");
      }

      unitsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            if (RunJobPage.displayUnits == Units.INCHES) {
               unitsButton.setText("mm");
               RunJobPage.displayUnits = Units.MM;
               RunJobPage.updateRetractDimension();
               Settings.log.finest("Units set to Metric");
            } else {
               unitsButton.setText("Inch");
               RunJobPage.displayUnits = Units.INCHES;
               RunJobPage.updateRetractDimension();
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
      JLabel jobLabel = new JLabel("Job");
      jobLabel.setFont(DisplayComponents.pageHeaderFont);
      leftTopPanel.add(jobLabel, DisplayComponents.GenerateConstraints(0, 0));
      JLabel jobNameLabel = new JLabel(job.getName());
      jobNameLabel.setFont(DisplayComponents.pageHeaderFont);
      jobNameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      leftTopPanel.add(jobNameLabel, DisplayComponents.GenerateConstraints(1, 0, 1.0D, 0.0D, 2, 1, 18, new Insets(0, 0, 5, 5), 1));
      JLabel bendLabel = new JLabel("Bend");
      bendLabel.setFont(DisplayComponents.pageHeaderFont);
      leftTopPanel.add(bendLabel, DisplayComponents.GenerateConstraints(0, 1));
      bendValueLabel = new JLabel(String.format("%s", currentBend + 1));
      bendValueLabel.setFont(DisplayComponents.pageHeaderFont);
      bendValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      leftTopPanel.add(bendValueLabel, DisplayComponents.GenerateConstraints(1, 1, 0.0D, 0.0D, 1, 1, 18, new Insets(0, 0, 5, 5), 3));
      JLabel cycleLabel = new JLabel("Cycle");
      cycleLabel.setFont(DisplayComponents.pageHeaderFont);
      leftTopPanel.add(cycleLabel, DisplayComponents.GenerateConstraints(0, 2));
      cycleValueLabel = new JLabel(String.format("%s", currentCycle));
      cycleValueLabel.setFont(DisplayComponents.pageHeaderFont);
      cycleValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      leftTopPanel.add(cycleValueLabel, DisplayComponents.GenerateConstraints(1, 2, 0.0D, 0.0D, 1, 1, 18, new Insets(0, 0, 5, 5), 3));
      JLabel partsLabel = new JLabel("Parts");
      partsLabel.setFont(DisplayComponents.pageHeaderFont);
      leftTopPanel.add(partsLabel, DisplayComponents.GenerateConstraints(0, 3));
      partsValueLabel = new JLabel(String.format("%s", parts));
      partsValueLabel.setFont(DisplayComponents.pageHeaderFont);
      partsValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      leftTopPanel.add(partsValueLabel, DisplayComponents.GenerateConstraints(1, 3, 0.0D, 0.0D, 1, 1, 18, new Insets(0, 0, 5, 5), 3));
      partsValueLabel.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
         }

         public void mouseReleased(MouseEvent arg0) {
            new TwoButtonPromptPage("Warning", "Are you sure you want to reset the parts counter?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  job.resetParts();
                  RunJobPage.parts = 0;
                  RunJobPage.partsValueLabel.setText("0");
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, false);
         }
      });
      ImageIcon image = new ImageIcon(getBendImage());
      System.out.println("imagepath: " + getBendImage());

      try {
         imageLabel = new JLabel(new ImageIcon(image.getImage().getScaledInstance(200, 150, 1)));
      } catch (Exception var37) {
         imageLabel = new JLabel();
         var37.printStackTrace();
      }

      leftTopPanel.add(imageLabel, DisplayComponents.GenerateConstraints(2, 1, 1, 3));
      imageLabel.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            new ImagePopupPage(job, RunJobPage.currentBend);
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

      retractPanel = new JPanel();
      retractPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      GridBagLayout retractPanelGBL = new GridBagLayout();
      retractPanelGBL.columnWidths = new int[2];
      retractPanelGBL.rowHeights = new int[4];
      retractPanelGBL.columnWeights = new double[]{0.0D, 0.0D};
      retractPanelGBL.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
      retractPanel.setLayout(retractPanelGBL);
      JLabel retractLabel = new JLabel("RETRACT");
      retractLabel.setFont(DisplayComponents.pageHeaderFont);
      retractLabel.setHorizontalAlignment(0);
      JLabel distanceLabel = new JLabel("Distance");
      distanceLabel.setFont(DisplayComponents.pageHeaderFont);
      JLabel typeLabel = new JLabel("Type");
      typeLabel.setFont(DisplayComponents.pageHeaderFont);
      JLabel delayLabel = new JLabel("Delay");
      delayLabel.setFont(DisplayComponents.pageHeaderFont);
      String tmp = "";
      if (displayUnits == Units.INCHES) {
         tmp = (String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(0);
      } else if (!((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(0)).isEmpty()) {
         BigDecimal v = BigDecimal.valueOf(Double.parseDouble((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(0)));
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
         tmp = String.format("%.2f", v.multiply(mm, mc).doubleValue());
      } else {
         tmp = "0";
      }

      retractDistanceValueLabel = new JLabel(tmp);
      retractDistanceValueLabel.setFont(DisplayComponents.pageHeaderFont);
      retractTypeValueLabel = new JLabel((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1));
      retractTypeValueLabel.setFont(DisplayComponents.pageHeaderFont);
      retractDelayValueLabel = new JLabel((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(2));
      retractDelayValueLabel.setFont(DisplayComponents.pageHeaderFont);
      retractDistanceValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      retractTypeValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      retractDelayValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      retractPanel.add(retractLabel, DisplayComponents.GenerateConstraints(0, 0, 2, 1));
      retractPanel.add(distanceLabel, DisplayComponents.GenerateConstraints(0, 1));
      retractPanel.add(retractDistanceValueLabel, DisplayComponents.GenerateConstraints(1, 1));
      retractPanel.add(typeLabel, DisplayComponents.GenerateConstraints(0, 2));
      retractPanel.add(retractTypeValueLabel, DisplayComponents.GenerateConstraints(1, 2));
      retractPanel.add(delayLabel, DisplayComponents.GenerateConstraints(0, 3));
      retractPanel.add(retractDelayValueLabel, DisplayComponents.GenerateConstraints(1, 3));
      retractDistanceValueLabel.setOpaque(true);
      retractDelayValueLabel.setOpaque(true);
      leftBottomPanel.add(retractPanel);
      if (!job.getRetractEnabled() || ((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("  ")) {
         retractPanel.setVisible(false);
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
      this.getContentPane().add(leftPanel, "West");
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BorderLayout(0, 0));
      buttonPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
      JPanel leftButtons = new JPanel();
      leftButtons.setLayout(new FlowLayout(0, 30, 0));
      JButton homeButton = new JBottomButton("Home", "home.png");
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Home button pressed");
            if (!RunJobPage.this.checkBendsEdited()) {
               job.saveJob(job.getLocation());
               RunJobPage.this.timerPositionUpdate.stop();
               RunJobPage.this.dispose();
               new HomePage();
            } else {
               new TwoButtonPromptPage("Unsaved Changes", "Are you sure you want to leave and discard your changes?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (SystemCommands.jobExists(job.getName())) {
                        Job old = SystemCommands.getJob(job.getName());
                        old.setParts(job);
                        old.setRan(job.getRan());
                        old.saveJob(job.getLocation());
                     }

                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     RunJobPage.this.timerPositionUpdate.stop();
                     RunJobPage.this.dispose();
                     new HomePage();
                  }
               }, "No", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, false);
            }

         }
      });
      leftButtons.add(homeButton);
      JButton editButton = new JBottomButton("Edit", "edit.png");
      editButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("Edit button pressed");
            if (SystemCommands.jobExists(job.getName())) {
               Job old = SystemCommands.getJob(job.getName());
               old.setRan(job.getRan());
               old.saveJob(job.getLocation());
            }

            RunJobPage.this.timerPositionUpdate.stop();
            RunJobPage.this.dispose();
            new EditJobPage(job);
         }
      });
      leftButtons.add(editButton);
      JButton saveMButton = new JBottomButton("<html>Save</html>", "save.png");
      saveMButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.selectedUSB != null) {
               new TwoButtonPromptPage("Save Location", "Where would you like to save to?", "Local", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     job.saveJob(Location.LOCAL);
                     RunJobPage.this.oldBends = (new Gson()).toJson(job.getBends());
                     new NotificationPage("Job Saved", job.getName() + " was saved", 3000);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, "USB", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     job.saveJob(Location.USB);
                     RunJobPage.this.oldBends = (new Gson()).toJson(job.getBends());
                     new NotificationPage("Job Saved", job.getName() + " was saved", 3000);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, true);
            } else {
               job.saveJob(Location.LOCAL);
               RunJobPage.this.oldBends = (new Gson()).toJson(job.getBends());
               new NotificationPage("Job Saved", job.getName() + " was saved", 3000);
            }

         }
      });
      leftButtons.add(saveMButton);
      buttonPanel.add(leftButtons, "West");
      JPanel rightButtons = new JPanel();
      rightButtons.setLayout(new FlowLayout(2, 15, 0));
      advanceButton = new JBottomButton("<html>ADV</html>", (String)null, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (RunJobPage.firstAdvance) {
               System.out.println("Advance Button");
               if ((((String)((AxisValues)((Bend)job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("PP") || ((String)((AxisValues)((Bend)job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("U")) && job.getRetractEnabled()) {
                  Listener.retract = true;
                  Listener.retractType = (String)((AxisValues)((Bend)job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(1);
               } else {
                  Listener.retract = false;
                  Listener.retractType = "";
               }

               if (Settings.autoAdvanceMode == AdvanceMode.INTERNAL) {
                  if (job.getMode().equals(Mode.ANGLE)) {
                     Listener.topPosition = (int)(Double.valueOf((String)((AxisValues)((Bend)job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(1)).getValues().get(2)) * ((Axis)Settings.axes.get(1)).getEncoderCountPerInch() * 0.5D);
                  } else {
                     Listener.topPosition = (int)(Double.valueOf((String)((AxisValues)((Bend)job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(1)).getValues().get(1)) * ((Axis)Settings.axes.get(1)).getEncoderCountPerInch() * 0.5D);
                  }
               } else {
                  Listener.topPosition = Integer.MAX_VALUE;
               }

               if (job.getMode().equals(Mode.ANGLE)) {
                  RunJobPage.calculateAngleArray();
               }

               RunJobPage.firstAdvance = false;
               System.out.println("firstAdvance: " + RunJobPage.firstAdvance);
            } else {
               RunJobPage.advanceBend();
               Settings.log.fine("Manual advance triggered");
            }

            RunJobPage.driveToBend((Bend)job.getBends().get(RunJobPage.currentBend));
            Settings.log.finest("Advance button pressed");
         }
      });
      advanceButton.setEnabled(false);
      autoButton = new JButton("<html>AUTO</html>");
      autoButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (RunJobPage.autoMode == AutoMode.ON) {
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(false);
               RunJobPage.firstAdvance = true;
               Settings.log.finest("Auto button pressed, auto disabled");
               RunJobPage.wakeup.stop();
            } else if (Settings.autoAdvanceMode == AdvanceMode.INTERNAL && ((Axis)Settings.axes.get(1)).getPosition() <= 0) {
               RunJobPage.this.flashModeButton((JButton)e.getSource());
               Settings.log.warning("Auto button pressed, auto in inproper position");
            } else if (Settings.autoAdvanceMode == AdvanceMode.EXTERNAL && Settings.getExtAdvSwitch() && Settings.autoAdvancePosition == AdvancePosition.PP) {
               RunJobPage.this.flashModeButton((JButton)e.getSource());
               Settings.log.warning("Auto button pressed, auto in inproper position");
            } else {
               RunJobPage.wakeup.start();
               RunJobPage.manualButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(true);
               ((JButton)e.getSource()).setBackground(DisplayComponents.Active);
               RunJobPage.autoMode = AutoMode.ON;
               RunJobPage.firstAdvance = true;
               Settings.log.finest("auto button pressed, auto enabled");
            }

         }
      });
      autoButton.setPreferredSize(DisplayComponents.bottomButtonSize);
      autoButton.setBackground(DisplayComponents.Inactive);
      autoMode = AutoMode.OFF;
      manualButton = new JButton("<html>MAN</html>");
      manualButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (RunJobPage.manualButton.getBackground() == DisplayComponents.Active) {
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.manualButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(false);
               Settings.log.finest("Manual button pressed, manual mode disabled");
            } else {
               RunJobPage.manualButton.setBackground(DisplayComponents.Active);
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.advanceButton.setEnabled(true);
               Settings.log.finest("Manual Button pressed, manual mode enabled");
            }

         }
      });
      manualButton.setPreferredSize(DisplayComponents.bottomButtonSize);
      manualButton.setBackground(DisplayComponents.Inactive);
      rightButtons.add(manualButton);
      rightButtons.add(autoButton);
      rightButtons.add(advanceButton);
      buttonPanel.add(rightButtons, "East");
      this.getContentPane().add(buttonPanel, "South");
      int cnt = 0;
      if (((Axis)Settings.axes.get(0)).getEnabled()) {
         ((AxisPanel)axisPanels.get(cnt)).getAxisPanel().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
               new CalculatorPage(((AxisPanel)RunJobPage.axisPanels.get(0)).getAxisValueLabel(), job, (Axis)Settings.axes.get(0), RunJobPage.displayUnits, RunJobPage.currentBend);
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.firstAdvance = true;
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.manualButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(false);
               Settings.log.finest("Adjustment made, auto disabled");
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
               byte c;
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  c = 1;
               } else {
                  c = 0;
               }

               new CalculatorPage(((AxisPanel)RunJobPage.axisPanels.get(c)).getAxisValueLabel(), job, (Axis)Settings.axes.get(1), RunJobPage.displayUnits, RunJobPage.currentBend);
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.firstAdvance = true;
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.manualButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(false);
               Settings.log.finest("Adjustment made, auto disabled");
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
               byte c;
               if (((Axis)Settings.axes.get(0)).getEnabled() && ((Axis)Settings.axes.get(1)).getEnabled()) {
                  c = 2;
               } else if (!((Axis)Settings.axes.get(0)).getEnabled() && !((Axis)Settings.axes.get(1)).getEnabled()) {
                  c = 0;
               } else {
                  c = 1;
               }

               new CalculatorPage(((AxisPanel)RunJobPage.axisPanels.get(c)).getAxisValueLabel(), job, (Axis)Settings.axes.get(2), RunJobPage.displayUnits, RunJobPage.currentBend);
               RunJobPage.autoMode = AutoMode.OFF;
               RunJobPage.firstAdvance = true;
               RunJobPage.autoButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.manualButton.setBackground(DisplayComponents.Inactive);
               RunJobPage.advanceButton.setEnabled(false);
               Settings.log.finest("Adjustment made, auto disabled");
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

      this.setVisible(true);
   }

   private boolean checkBendsEdited() {
      return !this.oldBends.equals((new Gson()).toJson(job.getBends()));
   }

   protected void flashModeButton(final JButton jButton) {
      this.flashCount = 0;
      int cnt = 1;
      if (!((Axis)Settings.axes.get(0)).getEnabled()) {
         cnt = 0;
      }

      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         ((AxisPanel)axisPanels.get(cnt)).getAxisValueLabel().setOpaque(true);
         (new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               RunJobPage var10000 = RunJobPage.this;
               var10000.flashCount = var10000.flashCount + 1;
               if (RunJobPage.this.flashCount % 2 != 0) {
                  jButton.setBackground(DisplayComponents.Inactive);
                  ((AxisPanel)RunJobPage.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Background);
               } else {
                  jButton.setBackground(DisplayComponents.Active);
                  ((AxisPanel)RunJobPage.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Active);
               }

               if (RunJobPage.this.flashCount == 5) {
                  jButton.setBackground(DisplayComponents.Inactive);
                  ((AxisPanel)RunJobPage.axisPanels.get(1)).getAxisValueLabel().setBackground(DisplayComponents.Background);
                  RunJobPage.this.flashCount = 0;
                  ((Timer)e.getSource()).stop();
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var3) {
                  var3.printStackTrace();
               }

            }
         })).start();
      } else {
         new NotificationPage("Error", "External advance is enabled with pinch point position, and is currently down. Please move the ram up to continue.");
      }

   }

   static void driveToBend(Bend bend) {
      driveToBend(bend, true);
   }

   static void driveToBend(Bend bend, boolean wait) {
      double xPosition = 0.0D;
      double rPosition = 0.0D;
      double top = 0.0D;
      double slow = 0.0D;
      double metal = 0.0D;
      double aw = 0.0D;
      double bottom = 0.0D;
      advanceButton.setEnabled(false);
      AxisValues a = (AxisValues)bend.getAxisValues().get(0);
      if (((Axis)Settings.axes.get(0)).getEnabled()) {
         if (job.getBaEnabled()) {
            xPosition = Double.parseDouble((String)a.getValues().get(3)) + (Double)job.getOffsets().get(0) + Double.parseDouble((String)a.getValues().get(4));
         } else {
            xPosition = Double.parseDouble((String)a.getValues().get(3)) + (Double)job.getOffsets().get(0);
         }
      }

      a = (AxisValues)bend.getAxisValues().get(1);
      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         if (job.getMode().equals(Mode.ANGLE)) {
            top = Double.parseDouble((String)a.getValues().get(2));
            slow = job.getRamSlowPosition();
            metal = 0.0D;
            if (((Axis)Settings.axes.get(1)).getAwDistance() == 0.0D) {
               aw = -2147480.0D;
            } else {
               aw = -1.0D * calculateTargetDepth(Double.parseDouble((String)a.getValues().get(0)) + Double.parseDouble((String)a.getValues().get(1))) + ((Axis)Settings.axes.get(1)).getAwDistance() - (Double)job.getOffsets().get(1);
               if (aw >= -0.002D && aw <= 0.002D) {
                  aw = 0.003D;
               }
            }

            bottom = -1.0D * calculateTargetDepth(Double.parseDouble((String)a.getValues().get(0)) + Double.parseDouble((String)a.getValues().get(1))) - (Double)job.getOffsets().get(1);
         } else {
            top = Double.parseDouble((String)a.getValues().get(1));
            slow = job.getRamSlowPosition();
            metal = 0.0D;
            if (((Axis)Settings.axes.get(1)).getAwDistance() == 0.0D) {
               aw = -2147480.0D;
            } else {
               aw = -1.0D * Double.parseDouble((String)a.getValues().get(0)) + ((Axis)Settings.axes.get(1)).getAwDistance() - (Double)job.getOffsets().get(1);
               if (aw >= -0.002D && aw <= 0.002D) {
                  aw = 0.003D;
               }
            }

            bottom = -1.0D * Double.parseDouble((String)a.getValues().get(0)) - (Double)job.getOffsets().get(1);
         }
      }

      a = (AxisValues)bend.getAxisValues().get(2);
      if (((Axis)Settings.axes.get(2)).getEnabled()) {
         if (((Axis)Settings.axes.get(2)).getZeroAdjust()) {
            if (Settings.floatingCalibration) {
               rPosition = Double.parseDouble((String)a.getValues().get(0)) + (Double)job.getOffsets().get(2) + Settings.calDie.getHeight() - ((Axis)Settings.axes.get(2)).getZeroOffset();
            } else {
               rPosition = Double.parseDouble((String)a.getValues().get(0)) + (Double)job.getOffsets().get(2) - ((Axis)Settings.axes.get(2)).getZeroOffset();
            }
         } else {
            rPosition = Double.parseDouble((String)a.getValues().get(0)) + (Double)job.getOffsets().get(2);
         }
      }

      Communications.xyrCombinedCommand(xPosition, rPosition, top, slow, metal, aw, bottom);
      if (wait) {
         (new waitForMove(xPosition, rPosition, currentBend)).start();
      }

   }

   public void exitPage() {
      Settings.log.finest("Home button pressed");
      if (!this.checkBendsEdited()) {
         job.saveJob(job.getLocation());
         this.dispose();
         new HomePage();
      } else {
         new TwoButtonPromptPage("Unsaved Changes", "Do you want to save your changes?", "Yes", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               RunJobPage.job.saveJob(RunJobPage.job.getLocation());
               RunJobPage.this.dispose();
               new HomePage();
            }
         }, "No", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.jobExists(RunJobPage.job.getName())) {
                  Job old = SystemCommands.getJob(RunJobPage.job.getName());
                  old.setParts(RunJobPage.job);
                  old.setRan(RunJobPage.job.getRan());
                  old.saveJob(RunJobPage.job.getLocation());
               }

               ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               RunJobPage.this.dispose();
               new HomePage();
            }
         }, false);
      }

   }

   private static void updateRetractDimension() {
      if (job.getRetractEnabled()) {
         if (displayUnits == Units.INCHES) {
            retractDistanceValueLabel.setText((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(0));
         } else {
            BigDecimal v = BigDecimal.valueOf(Double.parseDouble((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(0)));
            BigDecimal mm = BigDecimal.valueOf(25.4D);
            MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
            retractDistanceValueLabel.setText(String.format("%.2f", v.multiply(mm, mc).doubleValue()));
         }
      }

   }

   private static void advanceBend() {
      if (currentCycle == 1) {
         ++currentBend;
         if (currentBend == job.getBends().size()) {
            currentBend = 0;
            ++parts;
            job.addPart();
         }

         currentCycle = ((Bend)job.getBends().get(currentBend)).getCycles();
      } else {
         --currentCycle;
      }

      bendValueLabel.setText(String.valueOf(currentBend + 1));
      cycleValueLabel.setText(String.valueOf(currentCycle));
      partsValueLabel.setText(String.valueOf(parts));
      updateRetractDimension();
      retractTypeValueLabel.setText((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1));
      retractDelayValueLabel.setText((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(2));
      ImageIcon image = new ImageIcon(getBendImage());

      try {
         imageLabel.setIcon(new ImageIcon(image.getImage().getScaledInstance(200, 150, 1)));
      } catch (Exception var2) {
         imageLabel.setIcon((Icon)null);
         var2.printStackTrace();
      }

      if (!((Axis)Settings.axes.get(0)).getEnabled() || !job.getRetractEnabled() || !((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("PP") && !((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("U")) {
         Listener.retract = false;
         Listener.retractType = "";
      } else {
         Listener.retract = true;
         Listener.retractType = (String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1);
      }

      if (Settings.autoAdvanceMode == AdvanceMode.INTERNAL) {
         if (job.getMode() == Mode.ANGLE) {
            Listener.topPosition = (int)(Double.valueOf((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(1)).getValues().get(2)) * ((Axis)Settings.axes.get(1)).getEncoderCountPerInch());
         } else {
            Listener.topPosition = (int)(Double.valueOf((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(1)).getValues().get(1)) * ((Axis)Settings.axes.get(1)).getEncoderCountPerInch());
         }
      } else {
         Listener.topPosition = Integer.MAX_VALUE;
      }

      if (job.getRetractEnabled() && !((String)((AxisValues)((Bend)job.getBends().get(currentBend)).getAxisValues().get(0)).getValues().get(1)).equals("  ") && ((Axis)Settings.axes.get(0)).getEnabled()) {
         retractPanel.setVisible(true);
      } else {
         retractPanel.setVisible(false);
      }

   }

   public static String getBendImage() {
      System.out.println(((Bend)job.getBends().get(currentBend)).getBendImage());
      String path = ((Bend)job.getBends().get(currentBend)).getBendImage();
      if (File.separatorChar == '\\') {
         path = path.replace('/', File.separatorChar);
      } else {
         path = path.replace('\\', File.separatorChar);
      }

      if (job.getLocation().equals(Location.LOCAL)) {
         if ((new File(SystemCommands.getWorkingDirectory() + File.separator + path)).exists()) {
            return SystemCommands.getWorkingDirectory() + File.separator + path;
         }
      } else if (job.getLocation().equals(Location.USB) && (new File(Settings.selectedUSB.path + File.separator + path)).exists()) {
         return Settings.selectedUSB.path + File.separator + path;
      }

      return "";
   }

   public static void autoAdvance() {
      if (!firstAdvance) {
         advanceBend();
         driveToBend((Bend)job.getBends().get(currentBend));
      }

   }

   private static double calculateTargetDepth(double angle) {
      double rangle = angle;
      if (angle < 2.0D) {
         rangle = 2.0D;
      }

      BigDecimal width = new BigDecimal(String.format("%.3f", job.getDie().getDieWidth()));
      BigDecimal thick = new BigDecimal(String.format("%.3f", job.getThickness()));
      BigDecimal radius = new BigDecimal(String.format("%.3f", job.getDie().getDieRadius()));

      BigDecimal depth;
      try {
         depth = (new BigDecimal(width.doubleValue() / (2.0D * Math.tan(Math.toRadians(rangle / 2.0D))) - (radius.doubleValue() + thick.doubleValue()) * (1.0D / Math.sin(Math.toRadians(rangle) / 2.0D) - 1.0D))).setScale(5, 6);
      } catch (Exception var9) {
         System.out.println("big dick lary doesnt like my number");
         return 9999.0D;
      }

      return depth.doubleValue();
   }

   private static void calculateAngleArray() {
      ArrayList<Double> angleArray = new ArrayList();

      for(int i = 1790; i > 0; --i) {
         angleArray.add(calculateTargetDepth((double)i / 10.0D));
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
         }
      }

      return lowest;
   }

   private static double calculateCurrentAngle() {
      return 180.0D - (double)findClosest(((Axis)Settings.axes.get(1)).getPositionInches()) / 10.0D;
   }

   public static void disableAuto() {
      if (Settings.activeFrame instanceof RunJobPage) {
         autoMode = AutoMode.OFF;
         autoButton.setBackground(DisplayComponents.Inactive);
         firstAdvance = true;
         advanceButton.setEnabled(false);
      }

   }

   class UpdateDisplayPosition implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         try {
            if (((Axis)Settings.axes.get(1)).getTopStopSOV() == AxisRelayState.ON) {
               RunJobPage.topLabel.setBackground(DisplayComponents.Active);
            } else {
               RunJobPage.topLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getSlowSpeedSOV() == AxisRelayState.ON) {
               RunJobPage.slowLabel.setBackground(DisplayComponents.Active);
            } else {
               RunJobPage.slowLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getAntiWhipSOV() == AxisRelayState.ON) {
               RunJobPage.awLabel.setBackground(DisplayComponents.Active);
            } else {
               RunJobPage.awLabel.setBackground(DisplayComponents.Background);
            }

            if (((Axis)Settings.axes.get(1)).getBottomStopSOV() == AxisRelayState.ON) {
               RunJobPage.bottomLabel.setBackground(DisplayComponents.Active);
            } else {
               RunJobPage.bottomLabel.setBackground(DisplayComponents.Background);
            }

            int cnt = 0;
            if (Settings.displayCounts) {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(0)).getPosition()));
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(1)).getPosition()));
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).getAxisValueLabel().setText(String.format("%d", ((Axis)Settings.axes.get(2)).getPosition()));
                  ++cnt;
               }
            } else if (RunJobPage.job.getMode().equals(Mode.ANGLE)) {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(0)).getPositionInches());
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).getAxisPanel().setOpaque(true);
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  if (((Axis)Settings.axes.get(1)).getPositionInches() < 0.0D) {
                     ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(1)).getPositionInches());
                  } else {
                     ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(RunJobPage.calculateCurrentAngle(), true);
                  }

                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  if (((Axis)Settings.axes.get(2)).getZeroAdjust()) {
                     if (Settings.floatingCalibration) {
                        ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() + ((Axis)Settings.axes.get(2)).getZeroOffset() - Settings.calDie.getHeight());
                     } else {
                        ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() - ((Axis)Settings.axes.get(2)).getZeroOffset());
                     }
                  } else {
                     ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches());
                  }

                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).getAxisPanel().setOpaque(true);
                  ++cnt;
               }
            } else {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(0)).getPositionInches());
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(1)).getEnabled()) {
                  ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(1)).getPositionInches());
                  ++cnt;
               }

               if (((Axis)Settings.axes.get(2)).getEnabled()) {
                  if (((Axis)Settings.axes.get(2)).getZeroAdjust()) {
                     if (Settings.floatingCalibration) {
                        ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() + ((Axis)Settings.axes.get(2)).getZeroOffset() - Settings.calDie.getHeight());
                     } else {
                        ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches() - ((Axis)Settings.axes.get(2)).getZeroOffset());
                     }
                  } else {
                     ((AxisPanel)RunJobPage.axisPanels.get(cnt)).setText(((Axis)Settings.axes.get(2)).getPositionInches());
                  }

                  ++cnt;
               }
            }

            for(int i = 0; i < RunJobPage.axisPanels.size(); ++i) {
               ((AxisPanel)RunJobPage.axisPanels.get(i)).getAxisValueLabel().setHorizontalAlignment(4);
            }
         } catch (Exception var4) {
            Settings.log.log(Level.SEVERE, "Exception thrown while updating display position ", var4);
         }

      }
   }
}
