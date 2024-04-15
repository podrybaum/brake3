package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DataInputField;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.BendOptionsPopup;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.AutoMode;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class EditJobPage extends JFrame {
   private static final long serialVersionUID = -2519863662364046718L;
   public static Units units;
   public static EditJobPage existingPage;
   private JTextField jobNameTextField;
   private DataInputField slowPositionValueLabel;
   private Job job;
   public int runBend = 0;
   MathContext mc;
   private String oldBends;
   public JPanel scrollPanePanel;
   public static ArrayList<Integer> widths;

   public EditJobPage(Job job) {
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      this.job = job;
      if (SystemCommands.jobExists(job.getName())) {
         this.oldBends = (new Gson()).toJson(SystemCommands.getJob(job.getName()).getBends());
      }

      existingPage = this;
      this.initialize();
   }

   public EditJobPage() {
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      ArrayList<Bend> bends = new ArrayList();
      ArrayList<Axis> axes = Settings.axes;
      Job job = new Job("", Mode.DEPTH, 0.0D, Settings.units, bends, axes);
      this.oldBends = "";
      this.job = job;
      existingPage = this;
      this.initialize();
   }

   private void initialize() {
      this.setAlwaysOnTop(false);
      RunJobPage.autoMode = AutoMode.OFF;
      this.setDefaultCloseOperation(3);
      this.setSize(1024, 768);
      this.setUndecorated(true);
      this.getContentPane().setLayout(new BorderLayout(0, 0));
      Settings.activeFrame = this;
      this.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      units = this.job.getUnits();

      for(int i = 0; i < this.job.getBends().size(); ++i) {
         ((Bend)this.job.getBends().get(i)).setRetractEnabled(this.job.getRetractEnabled());
         ((Bend)this.job.getBends().get(i)).setBaEnabled(this.job.getBaEnabled());
      }

      JPanel buttonPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout)buttonPanel.getLayout();
      flowLayout.setAlignment(1);
      flowLayout.setHgap(30);
      this.getContentPane().add(buttonPanel, "South");
      buttonPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      JButton homeButton = new JBottomButton("      ", "home.png");
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            if (!EditJobPage.this.checkBendsEdited()) {
               EditJobPage.this.job.saveJob(EditJobPage.this.job.getLocation());
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new HomePage();
            } else {
               new TwoButtonPromptPage("Unsaved Changes", "Are you sure you want to leave and discard your changes?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (SystemCommands.jobExists(EditJobPage.this.job.getName())) {
                        Job old = SystemCommands.getJob(EditJobPage.this.job.getName());
                        old.setRan(EditJobPage.this.job.getRan());
                        old.saveJob(EditJobPage.this.job.getLocation());
                     }

                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     EditJobPage.this.dispose();
                     Settings.log.finest("edit job page disposed");
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
      buttonPanel.add(homeButton);
      JButton runButton = new JBottomButton("Run", "run.png", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (((EditJobPage)SwingUtilities.getRoot((JButton)e.getSource())).checkJobSanity()) {
               if (EditJobPage.this.job.getMode() != Mode.ANGLE && EditJobPage.this.job.getMode() != Mode.DEPTHFC) {
                  Settings.log.finest("run job button pressed");
                  EditJobPage.this.dispose();
                  Settings.log.finest("edit job page disposed");
                  if (EditJobPage.this.runBend != 0) {
                     new RunJobPage(EditJobPage.this.job, EditJobPage.this.runBend);
                  } else {
                     new RunJobPage(EditJobPage.this.job);
                  }
               } else if (EditJobPage.this.job.getPunch() != null && EditJobPage.this.job.getDie() != null && EditJobPage.this.job.getThickness() != 0.0D) {
                  Settings.log.finest("run job button pressed");
                  EditJobPage.this.dispose();
                  Settings.log.finest("edit job page disposed");
                  if (EditJobPage.this.runBend != 0) {
                     new RunJobPage(EditJobPage.this.job, EditJobPage.this.runBend);
                  } else {
                     new RunJobPage(EditJobPage.this.job);
                  }
               } else if (EditJobPage.this.job.getPunch() != null && EditJobPage.this.job.getDie() != null) {
                  new NotificationPage("Warning", "<html>Angle mode is selected and the material thickness is not set</html>", 2000);
               } else {
                  new NotificationPage("Warning", "<html>Angle mode is selected and no tools exist for the job,<br/> please enter the tool library and select some tools</html>", 2000);
               }
            }

         }
      });
      buttonPanel.add(runButton);
      JButton imageButton = new JBottomButton("Image", (String)null);
      imageButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      JButton saveJobButton = new JBottomButton("<html>Save</html>", "save.png", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (Settings.selectedUSB != null) {
               new TwoButtonPromptPage("Save Location", "Where would you like to save to?", "Local", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     EditJobPage.this.job.setLocation(Location.LOCAL);
                     EditJobPage.this.job.updateImagePaths();
                     EditJobPage.this.job.saveJob(Location.LOCAL);
                     EditJobPage.this.oldBends = (new Gson()).toJson(EditJobPage.this.job.getBends());
                     new NotificationPage("Job Saved", EditJobPage.this.job.getName() + " was saved", 3000);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, "USB", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     EditJobPage.this.job.setLocation(Location.USB);
                     EditJobPage.this.job.updateImagePaths();
                     EditJobPage.this.job.saveJob(Location.USB);
                     EditJobPage.this.oldBends = (new Gson()).toJson(EditJobPage.this.job.getBends());
                     new NotificationPage("Job Saved", EditJobPage.this.job.getName() + " was saved", 3000);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, true);
            } else {
               EditJobPage.this.job.saveJob(Location.LOCAL);
               EditJobPage.this.oldBends = (new Gson()).toJson(EditJobPage.this.job.getBends());
               new NotificationPage("Job Saved", EditJobPage.this.job.getName() + " was saved", 3000);
            }

         }
      });
      buttonPanel.add(saveJobButton);
      JButton toolLibButton = new JBottomButton("Tool Lib", "toolLibrary.png");
      if (this.job.getMode().equals(Mode.ANGLE) || this.job.getMode().equals(Mode.DEPTHFC)) {
         buttonPanel.add(toolLibButton);
      }

      toolLibButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new ToolLibraryPage(EditJobPage.this.job);
            EditJobPage.this.dispose();
         }
      });
      JButton deleteButton = new JBottomButton("Delete", "delete.png");
      buttonPanel.add(deleteButton);
      deleteButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Delete Confirmation", "Are you sure you want to delete " + EditJobPage.this.job.getName() + "?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Settings.log.finest("delete job button pressed");
                  EditJobPage.this.dispose();
                  Settings.log.finest("edit job page disposed");
                  SystemCommands.deleteJob(EditJobPage.this.job.getName());
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  new RecallJobPage();
               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, false);
         }
      });
      JPanel topPanel = new JPanel();
      this.getContentPane().add(topPanel, "North");
      GridBagLayout gbl_topPanel = new GridBagLayout();
      gbl_topPanel.columnWidths = new int[8];
      gbl_topPanel.rowHeights = new int[4];
      if (this.job.getMode().equals(Mode.ANGLE)) {
         gbl_topPanel.columnWeights = new double[]{0.0D, 0.8D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D};
      } else {
         gbl_topPanel.columnWeights = new double[]{0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      }

      gbl_topPanel.rowWeights = new double[]{0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      topPanel.setLayout(gbl_topPanel);
      JLabel createJobLabel = new JLabel("Create Job");
      createJobLabel.setFont(DisplayComponents.pageTitleFont);
      topPanel.add(createJobLabel, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 0.0D, 7, 1, 11, new Insets(0, 0, 0, 0), 0));
      if (!this.job.getName().equalsIgnoreCase("")) {
         createJobLabel.setText("Edit Job");
      }

      final JButton modeLabel = new JCustomButton("Mode Label");
      JLabel jobNameLabel = new JLabel("<html>Job<br/>Name</html>");
      jobNameLabel.setFont(DisplayComponents.editJobPageText);
      topPanel.add(jobNameLabel, DisplayComponents.GenerateConstraints(0, 1, 0.0D, 0.0D, 1, 1, 13, new Insets(0, 0, 5, 5), 2));
      this.jobNameTextField = new JTextField();
      this.jobNameTextField.setFont(DisplayComponents.bendPanelFont);
      topPanel.add(this.jobNameTextField, DisplayComponents.GenerateConstraints(1, 1, 0.0D, 0.0D, 1, 1, 17, new Insets(0, 0, 5, 5), 2));
      this.jobNameTextField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            if (SystemCommands.jobExists(EditJobPage.this.jobNameTextField.getText())) {
               Settings.log.fine("job: " + EditJobPage.this.jobNameTextField.getText() + " exists already, opening job");
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new EditJobPage(SystemCommands.getJob(EditJobPage.this.jobNameTextField.getText()));
            } else if (!EditJobPage.this.job.getName().equals("") && !EditJobPage.this.jobNameTextField.getText().equals(EditJobPage.this.job.getName())) {
               Settings.log.info("Copying job from " + EditJobPage.this.job.getName() + " to " + EditJobPage.this.jobNameTextField.getText());
               EditJobPage.this.job = EditJobPage.this.job.copyJob(EditJobPage.this.jobNameTextField.getText());
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new EditJobPage(EditJobPage.this.job);
            } else if (modeLabel.getText().equals("Angle Mode")) {
               EditJobPage.this.job = new Job(EditJobPage.this.jobNameTextField.getText(), Mode.ANGLE, ((Axis)Settings.axes.get(1)).getSlowDistance(), Settings.units, new ArrayList(), Settings.axes);
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new EditJobPage(EditJobPage.this.job);
            } else if (modeLabel.getText().equals("Depth Mode (FC)")) {
               EditJobPage.this.job = new Job(EditJobPage.this.jobNameTextField.getText(), Mode.DEPTHFC, ((Axis)Settings.axes.get(1)).getSlowDistance(), Settings.units, new ArrayList(), Settings.axes);
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new EditJobPage(EditJobPage.this.job);
            } else {
               EditJobPage.this.job = new Job(EditJobPage.this.jobNameTextField.getText(), Mode.DEPTH, ((Axis)Settings.axes.get(1)).getSlowDistance(), Settings.units, new ArrayList(), Settings.axes);
               EditJobPage.this.dispose();
               Settings.log.finest("edit job page disposed");
               new EditJobPage(EditJobPage.this.job);
            }

         }
      });
      this.jobNameTextField.addMouseListener(DisplayComponents.KeyboardPopup());
      this.jobNameTextField.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
         }

         public void focusGained(FocusEvent e) {
            EditJobPage.this.jobNameTextField.getCaret().setVisible(true);
         }
      });
      JLabel ramSlowPositionLabel = new JLabel("<html>Ram Slow<br/>Position</html>");
      ramSlowPositionLabel.setFont(DisplayComponents.editJobPageText);
      GridBagConstraints gbc_ramSlowPositionLabel = new GridBagConstraints();
      gbc_ramSlowPositionLabel.insets = new Insets(0, 0, 5, 5);
      gbc_ramSlowPositionLabel.gridx = 2;
      gbc_ramSlowPositionLabel.gridy = 1;
      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         topPanel.add(ramSlowPositionLabel, gbc_ramSlowPositionLabel);
      }

      this.slowPositionValueLabel = new DataInputField(((Axis)Settings.axes.get(1)).getSlowDistance(), 0.0D, 1.5D);
      GridBagConstraints gbc_slowPositionValueLabel = new GridBagConstraints();
      this.slowPositionValueLabel.setFont(DisplayComponents.editJobPageValue);
      gbc_slowPositionValueLabel.insets = new Insets(0, 0, 5, 5);
      gbc_slowPositionValueLabel.gridx = 3;
      gbc_slowPositionValueLabel.gridy = 1;
      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         topPanel.add(this.slowPositionValueLabel, gbc_slowPositionValueLabel);
      }

      this.slowPositionValueLabel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               Units units;
               if (Settings.activeFrame instanceof EditJobPage) {
                  units = EditJobPage.existingPage.getUnits();
               } else if (Settings.activeFrame instanceof RunJobPage) {
                  units = RunJobPage.job.getUnits();
               } else {
                  units = Settings.units;
               }

               if (units == Units.INCHES) {
                  EditJobPage.this.job.setRamSlowPosition(((DataInputField)e.getSource()).number);
               } else {
                  BigDecimal mm = BigDecimal.valueOf(25.4D);
                  EditJobPage.this.job.setRamSlowPosition(BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText())).divide(mm, EditJobPage.this.mc).doubleValue());
               }

               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
            }

            ((DataInputField)e.getSource()).setNumber(EditJobPage.this.job.getRamSlowPosition());
         }
      });
      this.slowPositionValueLabel.addMouseListener(DisplayComponents.CalculatorPopup());
      this.slowPositionValueLabel.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
         }

         public void focusLost(FocusEvent e) {
            DataInputField tmp = (DataInputField)e.getSource();
            tmp.setNumber(EditJobPage.this.job.getRamSlowPosition());
         }
      });
      modeLabel.setFont(DisplayComponents.editJobPageText);
      GridBagConstraints gbc_modeLabel = new GridBagConstraints();
      gbc_modeLabel.gridx = 4;
      gbc_modeLabel.gridy = 1;
      gbc_modeLabel.insets = new Insets(0, 0, 5, 5);
      topPanel.add(modeLabel, gbc_modeLabel);
      if (!this.job.getName().equals("")) {
         if (this.job.getMode() == Mode.DEPTH) {
            modeLabel.setText("Depth Mode");
         } else if (this.job.getMode() == Mode.ANGLE) {
            modeLabel.setText("Angle Mode");
         } else if (this.job.getMode() == Mode.DEPTHFC) {
            modeLabel.setText("Depth Mode (FC)");
         }
      } else if (Settings.defaultMode == Mode.DEPTH && ((Axis)Settings.axes.get(1)).getEnabled() && Settings.floatingCalibration) {
         modeLabel.setText("Depth Mode (FC)");
      } else if (Settings.defaultMode != Mode.DEPTH && ((Axis)Settings.axes.get(1)).getEnabled()) {
         if (Settings.defaultMode == Mode.ANGLE) {
            modeLabel.setText("Angle Mode");
         }
      } else {
         modeLabel.setText("Depth Mode");
      }

      if (!((Axis)Settings.axes.get(1)).getEnabled()) {
         modeLabel.setEnabled(false);
      }

      if (this.job.getName().equals("")) {
         modeLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
               if (modeLabel.getText().equals("Depth Mode")) {
                  if (Settings.floatingCalibration) {
                     modeLabel.setText("Depth Mode (FC)");
                  } else {
                     modeLabel.setText("Angle Mode");
                  }
               } else if (modeLabel.getText().equals("Depth Mode (FC)")) {
                  modeLabel.setText("Angle Mode");
               } else {
                  modeLabel.setText("Depth Mode");
               }

            }
         });
      } else {
         modeLabel.setEnabled(false);
      }

      if (modeLabel.getText().equals("Angle Mode") || modeLabel.getText().equals("Depth Mode (FC)")) {
         JLabel thicknessLabel = new JLabel("Thickness: ");
         thicknessLabel.setFont(DisplayComponents.editJobPageText);
         GridBagConstraints gbc_thicknessLabel = new GridBagConstraints();
         gbc_thicknessLabel.gridx = 5;
         gbc_thicknessLabel.gridy = 1;
         gbc_thicknessLabel.insets = new Insets(0, 0, 5, 5);
         topPanel.add(thicknessLabel, gbc_thicknessLabel);
         DataInputField thicknessValue = new DataInputField(this.job.getThickness(), 0.0D, 5.0D);
         GridBagConstraints gbc_thicknessValueLabel = new GridBagConstraints();
         thicknessValue.setFont(DisplayComponents.editJobPageValue);
         gbc_thicknessValueLabel.insets = new Insets(0, 0, 5, 5);
         gbc_thicknessValueLabel.gridx = 6;
         gbc_thicknessValueLabel.gridy = 1;
         topPanel.add(thicknessValue, gbc_thicknessValueLabel);
         thicknessValue.setNumber(this.job.getThickness());
         thicknessValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (SystemCommands.validInput((DataInputField)e.getSource())) {
                  if (EditJobPage.this.job.getUnits() == Units.INCHES) {
                     EditJobPage.this.job.setThickness(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                  } else {
                     BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                     BigDecimal mm = BigDecimal.valueOf(25.4D);
                     EditJobPage.this.job.setThickness(v.divide(mm, EditJobPage.this.mc).doubleValue());
                  }

                  ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               } else {
                  ((DataInputField)e.getSource()).setBackground(Color.RED);
               }

               ((DataInputField)e.getSource()).setNumber(EditJobPage.this.job.getThickness());
            }
         });
         thicknessValue.addMouseListener(DisplayComponents.CalculatorPopup());
         thicknessValue.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
               JTextField tmp = (JTextField)e.getSource();
               if (EditJobPage.units == Units.INCHES) {
                  tmp.setText(String.format("%.3f", EditJobPage.this.job.getThickness()));
               } else {
                  tmp.setText(String.format("%.2f", EditJobPage.this.job.getThickness() * 25.4D));
               }

            }
         });
         JLabel toolsLabel = new JLabel("Punch: " + (this.job.getPunch() == null ? "none" : this.job.getPunch().getName()) + " Die: " + (this.job.getDie() == null ? "none" : this.job.getDie().getName()));
         topPanel.add(toolsLabel, DisplayComponents.GenerateConstraints(1, 2, 0.0D, 0.0D, new Insets(0, 0, 5, 0)));
      }

      JPanel jobDataPanel = new JPanel();
      this.getContentPane().add(jobDataPanel, "Center");
      jobDataPanel.setLayout(new BoxLayout(jobDataPanel, 1));
      JPanel jobHeader = new JPanel();
      jobDataPanel.add(jobHeader);
      GridBagLayout gbl_jobHeader = new GridBagLayout();
      jobHeader.setLayout(gbl_jobHeader);
      JLabel unitsLabel = new JLabel("UNITS:");
      unitsLabel.setFont(DisplayComponents.editJobPageText);
      unitsLabel.setHorizontalAlignment(0);
      unitsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      jobHeader.add(unitsLabel, DisplayComponents.GenerateConstraints(0, 0));
      String u = "";
      if (units == Units.INCHES) {
         u = "Inch";
      } else {
         u = "MM";
      }

      final JButton unitsValueLabel = new JButton(u);
      unitsValueLabel.setFont(DisplayComponents.editJobPageText);
      unitsValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      jobHeader.add(unitsValueLabel, DisplayComponents.GenerateConstraints(1, 0));
      unitsValueLabel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (unitsValueLabel.getText().equals("Inch")) {
               unitsValueLabel.setText("MM");
               EditJobPage.units = Units.MM;
               EditJobPage.this.job.setUnits(Units.MM);
               EditJobPage.this.dispose();
               new EditJobPage(EditJobPage.this.job);
            } else {
               unitsValueLabel.setText("Inch");
               EditJobPage.units = Units.INCHES;
               EditJobPage.this.job.setUnits(Units.INCHES);
               EditJobPage.this.dispose();
               new EditJobPage(EditJobPage.this.job);
            }

         }
      });
      ArrayList<Integer> test = new ArrayList();
      widths = new ArrayList();
      JLabel bendNumberLabel = new JLabel("<html>BEND<br/>NUMBER</html>");
      bendNumberLabel.setFont(DisplayComponents.editJobPageText);
      bendNumberLabel.setHorizontalAlignment(0);
      bendNumberLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      jobHeader.add(bendNumberLabel, DisplayComponents.GenerateConstraints(0, 1, 1, 2));
      test.add(jobHeader.getComponentCount() - 1);
      JLabel numberCyclesLabel = new JLabel("CYCLES");
      numberCyclesLabel.setFont(DisplayComponents.editJobPageText);
      numberCyclesLabel.setHorizontalAlignment(0);
      numberCyclesLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      jobHeader.add(numberCyclesLabel, DisplayComponents.GenerateConstraints(1, 1, 1, 2));
      test.add(jobHeader.getComponentCount() - 1);
      JLabel offsetLabel = new JLabel("Offset");
      offsetLabel.setFont(DisplayComponents.editJobPageText);
      offsetLabel.setHorizontalAlignment(0);
      offsetLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      jobHeader.add(offsetLabel, DisplayComponents.GenerateConstraints(0, 3));
      int currentXVal = 2;
      this.job.getOffsets().add(((Axis)Settings.axes.get(0)).getDefaultOffset());
      this.job.getOffsets().add(((Axis)Settings.axes.get(1)).getDefaultOffset());
      this.job.getOffsets().add(((Axis)Settings.axes.get(2)).getDefaultOffset());

      int i;
      final JLabel fillerLabel;
      JLabel depthTitle;
      for(i = 0; i < Settings.axes.size(); ++i) {
         Axis axis = (Axis)Settings.axes.get(i);
         if (axis.getEnabled()) {
            JLabel angleCorrectTitle;
            JLabel heightTitle;
            if (axis.getAxisType() == AxisType.BACKGAUGE) {
               fillerLabel = new JLabel("BACKGAUGE (" + axis.getShortName() + ")");
               fillerLabel.setFont(DisplayComponents.editJobPageText);
               fillerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(fillerLabel, DisplayComponents.GenerateConstraints(currentXVal, 0, 5, 1));
               fillerLabel.setHorizontalAlignment(0);
               final JButton retractTitle = new JButton("RETRACT");
               retractTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               if (this.job.getRetractEnabled()) {
                  retractTitle.setBackground(DisplayComponents.Active);
               } else {
                  retractTitle.setBackground(DisplayComponents.Inactive);
               }

               retractTitle.setFont(DisplayComponents.editJobPageText);
               jobHeader.add(retractTitle, DisplayComponents.GenerateConstraints(currentXVal, 1, 3, 1));
               retractTitle.setHorizontalAlignment(0);
               retractTitle.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (EditJobPage.this.job.getRetractEnabled()) {
                        EditJobPage.this.job.setRetractEnabled(false);
                        retractTitle.setBackground(DisplayComponents.Inactive);
                        ((EditJobPage)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                        new EditJobPage(EditJobPage.this.job);
                     } else {
                        EditJobPage.this.job.setRetractEnabled(true);
                        retractTitle.setBackground(DisplayComponents.Active);
                        ((EditJobPage)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                        new EditJobPage(EditJobPage.this.job);
                     }

                  }
               });
               angleCorrectTitle = new JLabel("DISTANCE");
               angleCorrectTitle.setFont(DisplayComponents.editJobPageText);
               angleCorrectTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(angleCorrectTitle, DisplayComponents.GenerateConstraints(currentXVal, 2));
               angleCorrectTitle.setHorizontalAlignment(0);
               test.add(jobHeader.getComponentCount() - 1);
               heightTitle = new JLabel("TYPE");
               heightTitle.setFont(DisplayComponents.editJobPageText);
               heightTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(heightTitle, DisplayComponents.GenerateConstraints(currentXVal + 1, 2));
               heightTitle.setHorizontalAlignment(0);
               test.add(jobHeader.getComponentCount() - 1);
               JLabel delayTitle = new JLabel("DELAY");
               delayTitle.setFont(DisplayComponents.editJobPageText);
               delayTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(delayTitle, DisplayComponents.GenerateConstraints(currentXVal + 2, 2));
               delayTitle.setHorizontalAlignment(0);
               test.add(jobHeader.getComponentCount() - 1);
               JLabel dimensionTitle = new JLabel("DIMENSION");
               dimensionTitle.setFont(DisplayComponents.editJobPageText);
               dimensionTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(dimensionTitle, DisplayComponents.GenerateConstraints(currentXVal + 3, 1, 1, 2));
               dimensionTitle.setHorizontalAlignment(0);
               test.add(jobHeader.getComponentCount() - 1);
               final JButton bendAllowanceTitle = new JButton("<html>BEND<br/>ALLOWANCE</html>");
               bendAllowanceTitle.setFont(DisplayComponents.editJobPageText);
               bendAllowanceTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(bendAllowanceTitle, DisplayComponents.GenerateConstraints(currentXVal + 4, 1, 1, 2));
               bendAllowanceTitle.setHorizontalAlignment(0);
               if (this.job.getBaEnabled()) {
                  bendAllowanceTitle.setBackground(DisplayComponents.Active);
               } else {
                  bendAllowanceTitle.setBackground(DisplayComponents.Inactive);
               }

               test.add(jobHeader.getComponentCount() - 1);
               bendAllowanceTitle.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (EditJobPage.this.job.getBaEnabled()) {
                        EditJobPage.this.job.setBaEnabled(false);
                        bendAllowanceTitle.setBackground(DisplayComponents.Inactive);
                        ((EditJobPage)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                        new EditJobPage(EditJobPage.this.job);
                     } else {
                        EditJobPage.this.job.setBaEnabled(true);
                        bendAllowanceTitle.setBackground(DisplayComponents.Active);
                        ((EditJobPage)SwingUtilities.getRoot((JButton)e.getSource())).dispose();
                        new EditJobPage(EditJobPage.this.job);
                     }

                  }
               });
               DataInputField backgaugeOffsetValueLabel = new DataInputField((Double)this.job.getOffsets().get(0), -5.0D, 5.0D);
               backgaugeOffsetValueLabel.setFont(DisplayComponents.editJobPageValue);
               this.job.getOffsets().set(axis.getAddress(), (Double)this.job.getOffsets().get(0));
               backgaugeOffsetValueLabel.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (SystemCommands.validInput((DataInputField)e.getSource())) {
                        if (EditJobPage.units == Units.INCHES) {
                           EditJobPage.this.job.getOffsets().set(0, Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        } else {
                           BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                           BigDecimal mm = BigDecimal.valueOf(25.4D);
                           System.out.println(v.divide(mm, EditJobPage.this.mc));
                           EditJobPage.this.job.getOffsets().set(0, v.divide(mm, EditJobPage.this.mc).doubleValue());
                        }

                        ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
                     } else {
                        ((DataInputField)e.getSource()).setBackground(Color.RED);
                     }

                     ((DataInputField)e.getSource()).setNumber((Double)EditJobPage.this.job.getOffsets().get(0));
                  }
               });
               backgaugeOffsetValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(backgaugeOffsetValueLabel, DisplayComponents.GenerateConstraints(currentXVal + 3, 3));
               backgaugeOffsetValueLabel.addMouseListener(DisplayComponents.CalculatorPopup());
               backgaugeOffsetValueLabel.setHorizontalAlignment(0);
               currentXVal += 5;
            } else if (axis.getAxisType() == AxisType.RAM) {
               if (this.job.getMode().equals(Mode.ANGLE)) {
                  fillerLabel = new JLabel("RAM (" + axis.getShortName() + ")");
                  fillerLabel.setFont(DisplayComponents.editJobPageText);
                  fillerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(fillerLabel, DisplayComponents.GenerateConstraints(currentXVal, 0, 3, 1));
                  fillerLabel.setHorizontalAlignment(0);
                  depthTitle = new JLabel("DEPTH");
                  depthTitle.setFont(DisplayComponents.editJobPageText);
                  depthTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(depthTitle, DisplayComponents.GenerateConstraints(currentXVal, 1, 1, 2));
                  depthTitle.setHorizontalAlignment(0);
                  if (this.job.getMode() == Mode.ANGLE) {
                     depthTitle.setText("ANGLE");
                  }

                  test.add(jobHeader.getComponentCount() - 1);
                  angleCorrectTitle = new JLabel("CORRECTION");
                  angleCorrectTitle.setFont(DisplayComponents.editJobPageText);
                  angleCorrectTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(angleCorrectTitle, DisplayComponents.GenerateConstraints(currentXVal + 1, 1, 1, 2));
                  angleCorrectTitle.setHorizontalAlignment(0);
                  test.add(jobHeader.getComponentCount() - 1);
                  heightTitle = new JLabel("HEIGHT");
                  heightTitle.setFont(DisplayComponents.editJobPageText);
                  heightTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(heightTitle, DisplayComponents.GenerateConstraints(currentXVal + 2, 1, 1, 2));
                  heightTitle.setHorizontalAlignment(0);
                  test.add(jobHeader.getComponentCount() - 1);
                  DataInputField ramOffsetValueLabel = new DataInputField((Double)this.job.getOffsets().get(1), -5.0D, 5.0D);
                  ramOffsetValueLabel.setFont(DisplayComponents.editJobPageValue);
                  this.job.getOffsets().set(axis.getAddress(), (Double)this.job.getOffsets().get(1));
                  ramOffsetValueLabel.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        if (SystemCommands.validInput((DataInputField)e.getSource())) {
                           if (EditJobPage.units == Units.INCHES) {
                              EditJobPage.this.job.getOffsets().set(1, Double.parseDouble(((DataInputField)e.getSource()).getText()));
                           } else {
                              BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                              BigDecimal mm = BigDecimal.valueOf(25.4D);
                              EditJobPage.this.job.getOffsets().set(1, v.divide(mm, EditJobPage.this.mc).doubleValue());
                           }

                           ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
                        } else {
                           ((DataInputField)e.getSource()).setBackground(Color.RED);
                        }

                        ((DataInputField)e.getSource()).setNumber((Double)EditJobPage.this.job.getOffsets().get(1));
                     }
                  });
                  ramOffsetValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(ramOffsetValueLabel, DisplayComponents.GenerateConstraints(currentXVal, 3));
                  ramOffsetValueLabel.addMouseListener(DisplayComponents.CalculatorPopup());
                  ramOffsetValueLabel.setHorizontalAlignment(0);
                  currentXVal += 3;
               } else {
                  fillerLabel = new JLabel("RAM (" + axis.getShortName() + ")");
                  fillerLabel.setFont(DisplayComponents.editJobPageText);
                  fillerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(fillerLabel, DisplayComponents.GenerateConstraints(currentXVal, 0, 2, 1));
                  fillerLabel.setHorizontalAlignment(0);
                  depthTitle = new JLabel("DEPTH");
                  depthTitle.setFont(DisplayComponents.editJobPageText);
                  depthTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(depthTitle, DisplayComponents.GenerateConstraints(currentXVal, 1, 1, 2));
                  depthTitle.setHorizontalAlignment(0);
                  if (this.job.getMode() == Mode.ANGLE) {
                     depthTitle.setText("ANGLE");
                  }

                  test.add(jobHeader.getComponentCount() - 1);
                  angleCorrectTitle = new JLabel("HEIGHT");
                  angleCorrectTitle.setFont(DisplayComponents.editJobPageText);
                  angleCorrectTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(angleCorrectTitle, DisplayComponents.GenerateConstraints(currentXVal + 1, 1, 1, 2));
                  angleCorrectTitle.setHorizontalAlignment(0);
                  test.add(jobHeader.getComponentCount() - 1);
                  DataInputField ramOffsetValueLabel = new DataInputField((Double)this.job.getOffsets().get(1), -5.0D, 5.0D);
                  ramOffsetValueLabel.setFont(DisplayComponents.editJobPageValue);
                  this.job.getOffsets().set(axis.getAddress(), (Double)this.job.getOffsets().get(1));
                  ramOffsetValueLabel.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        if (SystemCommands.validInput((DataInputField)e.getSource())) {
                           if (EditJobPage.units == Units.INCHES) {
                              EditJobPage.this.job.getOffsets().set(1, Double.parseDouble(((DataInputField)e.getSource()).getText()));
                           } else {
                              BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                              BigDecimal mm = BigDecimal.valueOf(25.4D);
                              EditJobPage.this.job.getOffsets().set(1, v.divide(mm, EditJobPage.this.mc).doubleValue());
                           }

                           ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
                        } else {
                           ((DataInputField)e.getSource()).setBackground(Color.RED);
                        }

                        ((DataInputField)e.getSource()).setNumber((Double)EditJobPage.this.job.getOffsets().get(1));
                     }
                  });
                  ramOffsetValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  jobHeader.add(ramOffsetValueLabel, DisplayComponents.GenerateConstraints(currentXVal, 3));
                  ramOffsetValueLabel.addMouseListener(DisplayComponents.CalculatorPopup());
                  ramOffsetValueLabel.setHorizontalAlignment(0);
                  currentXVal += 2;
               }
            } else {
               fillerLabel = new JLabel("(" + axis.getShortName() + ")");
               fillerLabel.setFont(DisplayComponents.editJobPageText);
               fillerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(fillerLabel, DisplayComponents.GenerateConstraints(currentXVal, 0));
               fillerLabel.setHorizontalAlignment(0);
               depthTitle = new JLabel("HEIGHT");
               depthTitle.setFont(DisplayComponents.editJobPageText);
               depthTitle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(depthTitle, DisplayComponents.GenerateConstraints(currentXVal, 1, 1, 2));
               depthTitle.setHorizontalAlignment(0);
               test.add(jobHeader.getComponentCount() - 1);
               DataInputField axisOffsetValue = new DataInputField((Double)this.job.getOffsets().get(2), -5.0D, 5.0D);
               axisOffsetValue.setFont(DisplayComponents.editJobPageValue);
               this.job.getOffsets().set(axis.getAddress(), (Double)this.job.getOffsets().get(2));
               axisOffsetValue.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (SystemCommands.validInput((DataInputField)e.getSource())) {
                        if (EditJobPage.units == Units.INCHES) {
                           EditJobPage.this.job.getOffsets().set(2, Double.parseDouble(((DataInputField)e.getSource()).getText()));
                        } else {
                           BigDecimal v = BigDecimal.valueOf(Double.parseDouble(((DataInputField)e.getSource()).getText()));
                           BigDecimal mm = BigDecimal.valueOf(25.4D);
                           EditJobPage.this.job.getOffsets().set(2, v.divide(mm, EditJobPage.this.mc).doubleValue());
                        }

                        ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
                     } else {
                        ((DataInputField)e.getSource()).setBackground(Color.RED);
                     }

                     ((DataInputField)e.getSource()).setNumber((Double)EditJobPage.this.job.getOffsets().get(2));
                  }
               });
               axisOffsetValue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
               jobHeader.add(axisOffsetValue, DisplayComponents.GenerateConstraints(currentXVal, 3));
               axisOffsetValue.addMouseListener(DisplayComponents.CalculatorPopup());
               axisOffsetValue.setHorizontalAlignment(0);
               ++currentXVal;
            }
         }
      }

      this.setVisible(true);

      for(i = 0; i < test.size(); ++i) {
         widths.add(jobHeader.getComponent((Integer)test.get(i)).getWidth());
      }

      final JScrollPane jobDataScrollPane = new JScrollPane();
      jobHeader.add(jobDataScrollPane, DisplayComponents.GenerateConstraints(0, 4, 1.0D, 1.0D, currentXVal + 1, 1));
      jobDataScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 1));
      jobDataScrollPane.setVerticalScrollBarPolicy(20);
      jobDataScrollPane.setHorizontalScrollBarPolicy(31);
      this.scrollPanePanel = new JPanel();
      fillerLabel = new JLabel();
      depthTitle = new JLabel();
      depthTitle.setPreferredSize(new Dimension(10, 1));
      jobHeader.add(depthTitle, DisplayComponents.GenerateConstraints(currentXVal, 0, Double.MIN_VALUE, 0.0D, 1, 3));
      JButton addBendButton = new JCustomButton("Add Bend", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (EditJobPage.this.job != null) {
               if (EditJobPage.this.job.getBends().size() > 0) {
                  ArrayList<AxisValues> v = new ArrayList();
                  ArrayList<String> x = new ArrayList();
                  ArrayList<String> y = new ArrayList();
                  ArrayList<String> r = new ArrayList();
                  x.add("");
                  x.add("  ");
                  x.add("");
                  x.add("");
                  x.add("");
                  y.add("");
                  if (EditJobPage.this.job.getMode().equals(Mode.ANGLE)) {
                     y.add("0.0");
                     y.add((String)((AxisValues)((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).getAxisValues().get(1)).getValues().get(2));
                  } else {
                     y.add((String)((AxisValues)((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).getAxisValues().get(1)).getValues().get(1));
                  }

                  r.add((String)((AxisValues)((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).getAxisValues().get(2)).getValues().get(0));
                  v.add(new AxisValues(((Axis)Settings.axes.get(0)).getAxisType(), ((Axis)Settings.axes.get(0)).getShortName(), x, EditJobPage.this.job.getMode()));
                  v.add(new AxisValues(((Axis)Settings.axes.get(1)).getAxisType(), ((Axis)Settings.axes.get(1)).getShortName(), y, EditJobPage.this.job.getMode()));
                  v.add(new AxisValues(((Axis)Settings.axes.get(2)).getAxisType(), ((Axis)Settings.axes.get(2)).getShortName(), r, EditJobPage.this.job.getMode()));
                  EditJobPage.this.job.getBends().add(new Bend(EditJobPage.this.job, v));
                  ((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).setUnits(EditJobPage.this.job.getUnits());
               } else {
                  EditJobPage.this.job.getBends().add(new Bend(EditJobPage.this.job));
                  ((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).setUnits(EditJobPage.this.job.getUnits());
               }

               JPanel temp = ((Bend)EditJobPage.this.job.getBends().get(EditJobPage.this.job.getBends().size() - 1)).getBendPanel();
               EditJobPage.this.scrollPanePanel.remove((JButton)e.getSource());
               EditJobPage.this.scrollPanePanel.remove(fillerLabel);
               EditJobPage.this.scrollPanePanel.add(temp, DisplayComponents.GenerateConstraints(0, EditJobPage.this.job.getBends().size() - 1));
               EditJobPage.this.scrollPanePanel.add((JButton)e.getSource(), DisplayComponents.GenerateConstraints(0, EditJobPage.this.job.getBends().size()));
               EditJobPage.this.scrollPanePanel.add(fillerLabel, DisplayComponents.GenerateConstraints(0, EditJobPage.this.job.getBends().size() + 1, 0.0D, 1.0D));
               EditJobPage.this.scrollPanePanel.revalidate();
               jobDataScrollPane.getViewport().scrollRectToVisible(((JButton)e.getSource()).getBounds());
               Settings.log.finest("one added at: " + EditJobPage.this.job.getBends().size());
            } else {
               Settings.log.finest("Job is null button");
            }

         }
      });
      addBendButton.setFont(DisplayComponents.editJobPageText);
      if (this.job.getName().equals("")) {
         addBendButton.setEnabled(false);
      }

      GridBagLayout gbl_scrollPanel = new GridBagLayout();
      this.scrollPanePanel.setLayout(gbl_scrollPanel);
      if (this.job != null) {
         Settings.log.fine("Opened job: " + this.job.getName());
         this.jobNameTextField.setText(this.job.getName());
         this.slowPositionValueLabel.setNumber(this.job.getRamSlowPosition());

         for(int i = 0; i < this.job.getBends().size(); ++i) {
            this.scrollPanePanel.add(((Bend)this.job.getBends().get(i)).getBendPanel(), DisplayComponents.GenerateConstraints(0, i));
            this.scrollPanePanel.revalidate();
            this.scrollPanePanel.repaint();
         }
      } else {
         this.slowPositionValueLabel.setText(String.valueOf(((Axis)Settings.axes.get(1)).getSlowDistance()));
         Settings.log.fine("Job is null initially");
      }

      this.scrollPanePanel.add(addBendButton, DisplayComponents.GenerateConstraints(0, this.job.getBends().size()));
      this.scrollPanePanel.add(fillerLabel, DisplayComponents.GenerateConstraints(0, this.job.getBends().size() + 1, 0.0D, 1.0D));
      if (this.jobNameTextField.getText().equals("")) {
         runButton.setEnabled(false);
      }

      jobDataScrollPane.setViewportView(this.scrollPanePanel);
      Settings.log.finest("Initialized edit job page");
   }

   public Units getUnits() {
      return units;
   }

   public void openBendPopup(int bendNo, int locX, int locY) {
      new BendOptionsPopup(this.job, (Bend)this.job.getBends().get(bendNo - 1), bendNo, locX, locY);
   }

   private boolean checkBendsEdited() {
      if (this.jobNameTextField.getText().isEmpty()) {
         return false;
      } else if (SystemCommands.jobExists(this.job.getName())) {
         return !this.oldBends.equals((new Gson()).toJson(this.job.getBends()));
      } else {
         return true;
      }
   }

   public void highlightBend(Component s) {
      for(int i = 0; i < this.scrollPanePanel.getComponentCount() - 2; ++i) {
         if (this.scrollPanePanel.getComponent(i).equals(s.getParent())) {
            this.scrollPanePanel.getComponent(i).setBackground(DisplayComponents.Active);
            this.runBend = i;
         } else {
            this.scrollPanePanel.getComponent(i).setBackground(DisplayComponents.Background);
         }
      }

   }

   public void highlightBend(int bendNo) {
      Component s = this.scrollPanePanel.getComponent(bendNo - 1);

      for(int i = 0; i < this.scrollPanePanel.getComponentCount() - 2; ++i) {
         if (this.scrollPanePanel.getComponent(i).equals(s)) {
            System.out.println("Highlight: " + i);
            this.scrollPanePanel.getComponent(i).setBackground(DisplayComponents.Active);
            this.runBend = i;
         } else {
            System.out.println("UnHighlight: " + i);
            this.scrollPanePanel.getComponent(i).setBackground(DisplayComponents.Background);
         }
      }

   }

   public boolean checkJobSanity() {
      boolean sane = true;
      if (this.job.getBends().size() < 1) {
         sane = false;
         if (!NotificationPage.containsKey("Error")) {
            new NotificationPage("Error", "Job has no bends");
         }

         return sane;
      } else if (!Settings.calibrated) {
         sane = false;
         if (!NotificationPage.containsKey("Error")) {
            new NotificationPage("Error", "System is not calibrated, cannot run");
         }

         return sane;
      } else if (Settings.autoAdvanceMode.equals(AdvanceMode.INTERNAL) && !((Axis)Settings.axes.get(1)).getEnabled()) {
         sane = false;
         if (!NotificationPage.containsKey("Error")) {
            new NotificationPage("Error", "Auto advance is set to internal, but no Y axis is installed, swap to external auto advance to continue");
         }

         return sane;
      } else {
         int cnt = 2;

         int i;
         for(i = 0; i < this.job.getBends().size(); ++i) {
            if (((Bend)this.job.getBends().get(i)).getCycles() < 1) {
               ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(1).setBackground(Color.RED);
               sane = false;
            }
         }

         if (Settings.autoAdvanceMode.equals(AdvanceMode.EXTERNAL) && Settings.autoAdvancePosition.equals(AdvancePosition.TOS)) {
            for(i = 0; i < this.job.getBends().size(); ++i) {
               if (((Axis)Settings.axes.get(0)).getEnabled() && this.job.getRetractEnabled() && ((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(1)).equals("PP")) {
                  new NotificationPage("Warning", "You can't have PP retract and EXT advance at TOP", 5000);
                  ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(3).setBackground(Color.RED);
                  sane = false;
               }
            }
         }

         for(i = 0; i < this.job.getBends().size(); ++i) {
            if (((Axis)Settings.axes.get(0)).getEnabled()) {
               if (this.job.getRetractEnabled() && !((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(1)).equals("  ")) {
                  if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(0)).isEmpty()) {
                     System.out.println("X axis bad value at : " + i + " cnt: " + 2);
                     ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(2).setBackground(Color.RED);
                     sane = false;
                  }

                  if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(2)).isEmpty()) {
                     System.out.println("X axis bad value at : " + i + " cnt: " + 4);
                     ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(4).setBackground(Color.RED);
                     sane = false;
                  }
               }

               if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(3)).isEmpty()) {
                  ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(5).setBackground(Color.RED);
                  sane = false;
               }

               if (this.job.getBaEnabled() && ((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(0)).getValues().get(4)).isEmpty()) {
                  ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(6).setBackground(Color.RED);
                  sane = false;
               }

               cnt = 7;
            }

            if (((Axis)Settings.axes.get(1)).getEnabled()) {
               int j;
               if (this.job.getMode().equals(Mode.ANGLE)) {
                  for(j = 0; j < 3; ++j) {
                     if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(1)).getValues().get(j)).isEmpty()) {
                        ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(cnt).setBackground(Color.RED);
                        sane = false;
                     }

                     ++cnt;
                  }
               } else {
                  for(j = 0; j < 2; ++j) {
                     if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(1)).getValues().get(j)).isEmpty()) {
                        System.out.println("Y axis bad value at : " + i + " cnt: " + cnt);
                        ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(cnt).setBackground(Color.RED);
                        sane = false;
                     }

                     ++cnt;
                  }
               }
            }

            if (((Axis)Settings.axes.get(2)).getEnabled()) {
               if (this.job.getMode().equals(Mode.ANGLE)) {
                  if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(2)).getValues().get(0)).isEmpty()) {
                     ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(cnt).setBackground(Color.RED);
                     sane = false;
                  }

                  ++cnt;
               } else {
                  if (((String)((AxisValues)((Bend)this.job.getBends().get(i)).getAxisValues().get(2)).getValues().get(0)).isEmpty()) {
                     System.out.println("R axis bad value at : " + i + " cnt: " + cnt);
                     ((JPanel)this.scrollPanePanel.getComponent(i)).getComponent(cnt).setBackground(Color.RED);
                     sane = false;
                  }

                  ++cnt;
               }
            }
         }

         if (this.job.getMode().equals(Mode.ANGLE) || this.job.getMode().equals(Mode.DEPTHFC)) {
            if (this.job.getThickness() == 0.0D) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Job has no thickness set, can not run");
               }
            }

            if (this.job.getPunch() == null) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Job has no punch set, can not run");
               }
            }

            if (this.job.getDie() == null) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Job has no die set, can not run");
               }
            }
         }

         if (Settings.floatingCalibration && ((Axis)Settings.axes.get(1)).getEnabled() && (this.job.getMode().equals(Mode.ANGLE) || this.job.getMode().equals(Mode.DEPTHFC))) {
            if (this.job.getThickness() != Settings.calThickness) {
               new TwoButtonPromptPage("Calibration", "Your calibration thickness does not match the job thickness, have you changed your material?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(EditJobPage.this.job.getThickness(), Settings.calPunch, Settings.calDie);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, "No", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, false);
               sane = false;
               return sane;
            }

            if (this.job.getPunch().getHeight() != Settings.calPunch.getHeight()) {
               new TwoButtonPromptPage("Calibration", "Your calibration punch does not match the job punch, have you changed the tools on the machine?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(Settings.calThickness, EditJobPage.this.job.getPunch(), Settings.calDie);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, "No", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, false);
               sane = false;
               return sane;
            }

            if (this.job.getDie().getHeight() != Settings.calDie.getHeight()) {
               new TwoButtonPromptPage("Calibration", "Your calibration die does not match the job die, have you changed the tools on the machine?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(Settings.calThickness, Settings.calPunch, EditJobPage.this.job.getDie());
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, "No", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  }
               }, false);
               sane = false;
               return sane;
            }
         }

         return sane;
      }
   }

   public void createTestJob() {
      ArrayList<Bend> bends = new ArrayList();
      ArrayList<Axis> axes = Settings.axes;
      this.job = new Job("Test", Mode.DEPTH, 0.0D, Settings.units, bends, axes);
   }

   public void createTestJobJson() {
      Gson gson = new Gson();

      try {
         String json = (String)Files.readAllLines(Paths.get("src/job.txt")).get(0);
         this.job = (Job)gson.fromJson(json, Job.class);
      } catch (IOException var3) {
         Settings.log.log(Level.WARNING, "Edit Job Page", var3);
      }

   }
}
