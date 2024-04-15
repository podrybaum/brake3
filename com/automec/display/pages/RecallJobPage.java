package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.SortMethod;
import com.automec.objects.enums.Units;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

public class RecallJobPage {
   JFrame recallJobFrame = new JFrame("Recall Job");
   static JList<Job> searchList;
   static DefaultListModel<Job> list;
   public static SortMethod sorting;
   public static boolean reverse;
   public static boolean localOn;
   public static boolean usbOn;

   static {
      sorting = SortMethod.RAN;
      reverse = true;
      localOn = true;
      usbOn = false;
   }

   public RecallJobPage() {
      this.initialize();
   }

   private void initialize() {
      this.recallJobFrame.setDefaultCloseOperation(3);
      this.recallJobFrame.setSize(1024, 768);
      this.recallJobFrame.setUndecorated(true);
      this.recallJobFrame.getContentPane().setLayout(new BorderLayout(0, 0));
      this.recallJobFrame.setFocusable(true);
      Settings.activeFrame = this.recallJobFrame;
      this.recallJobFrame.addMouseMotionListener(new MouseMotionListener() {
         public void mouseDragged(MouseEvent arg0) {
         }

         public void mouseMoved(MouseEvent arg0) {
            if (!Settings.screensaver) {
               Listener.screenSaverStopper.restart();
            }

         }
      });
      JLabel titleLabel = new JLabel("Recall Job");
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      this.recallJobFrame.getContentPane().add(titleLabel, "North");
      JPanel rightPanel = new JPanel();
      this.recallJobFrame.getContentPane().add(rightPanel, "East");
      rightPanel.setLayout(new BoxLayout(rightPanel, 1));
      rightPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      JLabel sortLabel = new JLabel("Sort By");
      sortLabel.setFont(DisplayComponents.pageHeaderFont);
      sortLabel.setHorizontalAlignment(0);
      rightPanel.add(sortLabel);
      final JButton nameButton = DisplayComponents.GenerateButton("<html>Name</html>");
      rightPanel.add(nameButton);
      nameButton.setBackground(DisplayComponents.Inactive);
      nameButton.setMaximumSize(new Dimension(110, 54));
      rightPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
      final JButton creationDateButton = DisplayComponents.GenerateButton("<html>Creation<br/>Date</html>");
      rightPanel.add(creationDateButton);
      creationDateButton.setBackground(DisplayComponents.Inactive);
      creationDateButton.setMaximumSize(new Dimension(110, 54));
      rightPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
      final JButton editDateButton = DisplayComponents.GenerateButton("<html>Edit<br/>Date</html>");
      rightPanel.add(editDateButton);
      editDateButton.setBackground(DisplayComponents.Inactive);
      editDateButton.setMaximumSize(new Dimension(110, 54));
      rightPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
      final JButton ranDateButton = DisplayComponents.GenerateButton("<html>Last<br/>Ran &uarr;</html>");
      rightPanel.add(ranDateButton);
      ranDateButton.setBackground(DisplayComponents.Active);
      ranDateButton.setMaximumSize(new Dimension(110, 54));
      rightPanel.add(new Filler(new Dimension(50, 5), new Dimension(50, 15), new Dimension(50, 30)));
      nameButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            RecallJobPage.sorting = SortMethod.NAME;
            if (nameButton.getText().equals("<html>Name &darr;</html>")) {
               nameButton.setText("<html>Name &uarr;</html>");
               RecallJobPage.reverse = true;
               RecallJobPage.updateList();
            } else if (nameButton.getText().equals("<html>Name &uarr;</html>")) {
               nameButton.setText("<html>Name &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            } else {
               nameButton.setText("<html>Name &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            }

            nameButton.setBackground(DisplayComponents.Active);
            creationDateButton.setBackground(DisplayComponents.Inactive);
            creationDateButton.setText("<html>Creation<br/>Date</html>");
            ranDateButton.setText("<html>Last<br/>Ran</html>");
            ranDateButton.setBackground(DisplayComponents.Inactive);
            editDateButton.setText("<html>Edit<br/>Date</html>");
            editDateButton.setBackground(DisplayComponents.Inactive);
         }
      });
      creationDateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            RecallJobPage.sorting = SortMethod.CREATED;
            if (creationDateButton.getText().equals("<html>Creation<br/>Date &darr;</html>")) {
               creationDateButton.setText("<html>Creation<br/>Date &uarr;</html>");
               RecallJobPage.reverse = true;
               RecallJobPage.updateList();
            } else if (creationDateButton.getText().equals("<html>Creation<br/>Date &uarr;</html>")) {
               creationDateButton.setText("<html>Creation<br/>Date &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            } else {
               creationDateButton.setText("<html>Creation<br/>Date &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            }

            nameButton.setBackground(DisplayComponents.Inactive);
            nameButton.setText("Name");
            creationDateButton.setBackground(DisplayComponents.Active);
            ranDateButton.setText("<html>Last<br/>Ran</html>");
            ranDateButton.setBackground(DisplayComponents.Inactive);
            editDateButton.setText("<html>Edit<br/>Date</html>");
            editDateButton.setBackground(DisplayComponents.Inactive);
         }
      });
      editDateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            RecallJobPage.sorting = SortMethod.EDITED;
            if (editDateButton.getText().equals("<html>Edit<br/>Date &darr;</html>")) {
               editDateButton.setText("<html>Edit<br/>Date &uarr;</html>");
               RecallJobPage.reverse = true;
               RecallJobPage.updateList();
            } else if (editDateButton.getText().equals("<html>Edit<br/>Date &uarr;</html>")) {
               editDateButton.setText("<html>Edit<br/>Date &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            } else {
               editDateButton.setText("<html>Edit<br/>Date &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            }

            nameButton.setBackground(DisplayComponents.Inactive);
            nameButton.setText("Name");
            creationDateButton.setBackground(DisplayComponents.Inactive);
            creationDateButton.setText("<html>Creation<br/>Date</html>");
            editDateButton.setBackground(DisplayComponents.Active);
            ranDateButton.setBackground(DisplayComponents.Inactive);
            ranDateButton.setText("<html>Last<br/>Ran</html>");
         }
      });
      ranDateButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            RecallJobPage.sorting = SortMethod.RAN;
            if (ranDateButton.getText().equals("<html>Last<br/>Ran &darr;</html>")) {
               ranDateButton.setText("<html>Last<br/>Ran &uarr;</html>");
               RecallJobPage.reverse = true;
               RecallJobPage.updateList();
            } else if (ranDateButton.getText().equals("<html>Last<br/>Ran &uarr;</html>")) {
               ranDateButton.setText("<html>Last<br/>Ran &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            } else {
               ranDateButton.setText("<html>Last<br/>Ran &darr;</html>");
               RecallJobPage.reverse = false;
               RecallJobPage.updateList();
            }

            nameButton.setBackground(DisplayComponents.Inactive);
            nameButton.setText("Name");
            creationDateButton.setBackground(DisplayComponents.Inactive);
            creationDateButton.setText("<html>Creation<br/>Date</html>");
            ranDateButton.setBackground(DisplayComponents.Active);
            editDateButton.setText("<html>Edit<br/>Date</html>");
            editDateButton.setBackground(DisplayComponents.Inactive);
         }
      });
      final JButton usb = new JButton("USB");
      final JButton local = new JButton("LOCAL");
      usb.setMaximumSize(new Dimension(110, 54));
      local.setMaximumSize(new Dimension(110, 54));
      rightPanel.add(usb);
      rightPanel.add(local);
      if (Settings.selectedUSB != null) {
         if (usbOn) {
            usb.setBackground(DisplayComponents.Active);
         } else {
            usb.setBackground(DisplayComponents.Inactive);
         }

         if (localOn) {
            local.setBackground(DisplayComponents.Active);
         } else {
            local.setBackground(DisplayComponents.Inactive);
         }
      } else {
         local.setBackground(DisplayComponents.Active);
         usb.setEnabled(false);
         usbOn = false;
         localOn = true;
      }

      local.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (local.getBackground().equals(DisplayComponents.Active)) {
               local.setBackground(DisplayComponents.Inactive);
               RecallJobPage.localOn = false;
               RecallJobPage.updateList();
            } else {
               local.setBackground(DisplayComponents.Active);
               RecallJobPage.localOn = true;
               RecallJobPage.updateList();
            }

         }
      });
      usb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (usb.getBackground().equals(DisplayComponents.Active)) {
               usb.setBackground(DisplayComponents.Inactive);
               RecallJobPage.usbOn = false;
               RecallJobPage.updateList();
            } else {
               usb.setBackground(DisplayComponents.Active);
               RecallJobPage.usbOn = true;
               RecallJobPage.updateList();
            }

         }
      });
      JPanel buttonPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout)buttonPanel.getLayout();
      flowLayout.setAlignment(1);
      flowLayout.setHgap(30);
      buttonPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
      this.recallJobFrame.getContentPane().add(buttonPanel, "South");
      JButton homeButton = new JBottomButton("      ", "home.png");
      homeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("home button pressed");
            RecallJobPage.this.recallJobFrame.dispose();
            Settings.log.finest("disposed of recall job page");
            new HomePage();
         }
      });
      buttonPanel.add(homeButton);
      final JButton runButton = new JBottomButton("Run", "run.png");
      buttonPanel.add(runButton);
      runButton.setEnabled(false);
      final JButton editButton = new JBottomButton("Edit", "edit.png");
      buttonPanel.add(editButton);
      editButton.setEnabled(false);
      final JButton recallJobButton = new JBottomButton("<html>Recall From<br/>Local Storage</html>", "save.png");
      recallJobButton.setEnabled(false);
      final JButton deleteButton = new JBottomButton("Delete Job", "delete.png");
      buttonPanel.add(deleteButton);
      deleteButton.setEnabled(false);
      JPanel centerPanel = new JPanel();
      this.recallJobFrame.getContentPane().add(centerPanel, "West");
      centerPanel.setLayout(new BoxLayout(centerPanel, 1));
      centerPanel.setPreferredSize(new Dimension(400, 600));
      centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      final JTextField searchField = new JTextField();
      centerPanel.add(searchField);
      searchField.setMaximumSize(new Dimension(400, 50));
      searchField.setFont(DisplayComponents.pageHeaderFont);
      searchField.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent arg0) {
         }

         public void insertUpdate(DocumentEvent arg0) {
            try {
               RecallJobPage.updateList(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "recall job page", var3);
            }

         }

         public void removeUpdate(DocumentEvent arg0) {
            try {
               RecallJobPage.updateList(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "recall job page", var3);
            }

         }
      });
      searchField.addMouseListener(DisplayComponents.KeyboardPopup());
      list = new DefaultListModel();
      searchList = new JList(list);
      searchList.setFont(DisplayComponents.pageHeaderFont);
      JScrollPane scroll = new JScrollPane();
      scroll.setMaximumSize(new Dimension(400, 600));
      scroll.setViewportView(searchList);
      scroll.setHorizontalScrollBarPolicy(31);
      centerPanel.add(scroll);
      updateList();
      final JPanel jobPreviewPanel = new JPanel();
      jobPreviewPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
      final JLabel jobName = new JLabel();
      jobName.setFont(DisplayComponents.editJobPageText);
      JLabel lastEdited = new JLabel("Last Edited: ");
      lastEdited.setFont(DisplayComponents.editJobPageText);
      final JLabel editDate = new JLabel();
      editDate.setFont(DisplayComponents.editJobPageText);
      final JScrollPane bends = new JScrollPane();
      bends.setMaximumSize(new Dimension(400, 600));
      final JLabel jobLocation = new JLabel();
      jobLocation.setFont(DisplayComponents.editJobPageText);
      final JLabel thickness = new JLabel();
      thickness.setFont(DisplayComponents.editJobPageText);
      final JLabel punchName = new JLabel();
      punchName.setFont(DisplayComponents.editJobPageText);
      final JLabel dieName = new JLabel();
      dieName.setFont(DisplayComponents.editJobPageText);
      JPanel insidePanel = new JPanel();
      insidePanel.setMaximumSize(new Dimension(400, 400));
      insidePanel.setPreferredSize(new Dimension(400, 400));
      insidePanel.setLayout(new GridBagLayout());
      jobPreviewPanel.add(insidePanel);
      insidePanel.add(jobName, DisplayComponents.GenerateConstraints(0, 0, 0.0D, 0.0D));
      insidePanel.add(jobLocation, DisplayComponents.GenerateConstraints(1, 0, 0.0D, 0.0D));
      insidePanel.add(lastEdited, DisplayComponents.GenerateConstraints(0, 1, 0.0D, 0.0D));
      insidePanel.add(editDate, DisplayComponents.GenerateConstraints(1, 1, 0.0D, 0.0D));
      insidePanel.add(thickness, DisplayComponents.GenerateConstraints(0, 2, 0.0D, 0.0D));
      insidePanel.add(punchName, DisplayComponents.GenerateConstraints(0, 3, 0.0D, 0.0D));
      insidePanel.add(dieName, DisplayComponents.GenerateConstraints(0, 4, 0.0D, 0.0D));
      insidePanel.add(bends, DisplayComponents.GenerateConstraints(0, 5, 1.0D, 1.0D, 2, 1));
      jobPreviewPanel.setVisible(false);
      this.recallJobFrame.getContentPane().add(jobPreviewPanel, "Center");
      searchList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               if (RecallJobPage.searchList.getSelectedIndex() == -1) {
                  Settings.log.finest("nothing selected in list");
                  runButton.setEnabled(false);
                  editButton.setEnabled(false);
                  recallJobButton.setEnabled(false);
                  deleteButton.setEnabled(false);
                  jobPreviewPanel.setVisible(false);
               } else {
                  Settings.log.finest(((Job)((JList)e.getSource()).getSelectedValue()).toString() + " selected");
                  runButton.setEnabled(true);
                  editButton.setEnabled(true);
                  deleteButton.setEnabled(true);
                  recallJobButton.setEnabled(true);
                  Job t = (Job)((JList)e.getSource()).getSelectedValue();
                  jobName.setText(t.getName());
                  editDate.setText(t.getEdited().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
                  if (!t.getMode().equals(Mode.ANGLE) && !t.getMode().equals(Mode.DEPTHFC)) {
                     punchName.setText("");
                     dieName.setText("");
                     thickness.setText("");
                  } else {
                     if (Settings.units == Units.INCHES) {
                        thickness.setText("Thickness: " + String.format("%.3f", t.getThickness()) + " in");
                     } else {
                        BigDecimal mm = BigDecimal.valueOf(25.4D);
                        MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
                        thickness.setText("Thickness: " + String.format("%.2f", BigDecimal.valueOf(t.getThickness()).divide(mm, mc).doubleValue()) + " mm");
                     }

                     if (t.getPunch() != null) {
                        punchName.setText("Punch: " + t.getPunch().getName());
                     } else {
                        punchName.setText("Punch: ");
                     }

                     if (t.getDie() != null) {
                        dieName.setText("Die: " + t.getDie().getName());
                     } else {
                        dieName.setText("Die: ");
                     }
                  }

                  JPanel temp = new JPanel();
                  temp.setLayout(new BoxLayout(temp, 1));
                  temp.add(RecallJobPage.getTopPreview());

                  for(int i = 0; i < t.getBends().size(); ++i) {
                     temp.add(((Bend)t.getBends().get(i)).getBendPreview(t.getUnits(), t.getMode().equals(Mode.ANGLE)));
                  }

                  bends.setViewportView(temp);
                  if (t.getLocation().equals(Location.LOCAL)) {
                     jobLocation.setText("Location: Local");
                  } else {
                     jobLocation.setText("Location: USB");
                  }

                  bends.setViewportView(temp);
                  deleteButton.setEnabled(true);
                  jobPreviewPanel.setVisible(true);
               }
            }

         }
      });
      recallJobButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("pressed recall job memory button");
            RecallJobPage.this.recallJobFrame.dispose();
            Settings.log.finest("disposed of recall job page");
            new EditJobPage((Job)RecallJobPage.searchList.getSelectedValue());
         }
      });
      editButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("pressed edit button");
            RecallJobPage.this.recallJobFrame.dispose();
            Settings.log.finest("disposed of recall job page");
            new EditJobPage((Job)RecallJobPage.searchList.getSelectedValue());
         }
      });
      runButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.finest("pressed run button");
            if (RecallJobPage.this.checkJobSanity()) {
               RecallJobPage.this.recallJobFrame.dispose();
               Settings.log.finest("disposed of recall job page");
               new RunJobPage((Job)RecallJobPage.searchList.getSelectedValue());
            } else {
               new NotificationPage("Error", "Job has invalid parameters, please edit these to run this job", 5000);
            }

         }
      });
      deleteButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Delete Confirmation", "Are you sure you want to delete " + RecallJobPage.searchList.getSelectedValue() + "?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Settings.log.finest("Pressed delete button");
                  Settings.log.fine("deleting job: " + RecallJobPage.searchList.getSelectedValue());
                  ((Job)RecallJobPage.searchList.getSelectedValue()).delete();
                  if (searchField.getText().isEmpty()) {
                     RecallJobPage.updateList();
                  } else {
                     RecallJobPage.updateList(searchField.getText());
                  }

                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, false);
         }
      });
      this.recallJobFrame.setVisible(true);
      Settings.log.finest("Recall job page initialized");
   }

   private static JPanel getTopPreview() {
      JPanel retVal = new JPanel();
      retVal.setLayout(new GridBagLayout());
      int i = 0;
      JLabel number = new JLabel("Bend Number");
      number.setFont(DisplayComponents.editJobPageText);
      int var4 = i + 1;
      retVal.add(number, DisplayComponents.GenerateConstraints(i, 0, 0.1D, 0.1D));
      JLabel rDim;
      if (((Axis)Settings.axes.get(0)).getEnabled()) {
         rDim = new JLabel("X-Axis");
         rDim.setFont(DisplayComponents.editJobPageText);
         retVal.add(rDim, DisplayComponents.GenerateConstraints(var4++, 0, 0.333D, 0.333D));
      }

      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         rDim = new JLabel("Y-Axis");
         rDim.setFont(DisplayComponents.editJobPageText);
         retVal.add(rDim, DisplayComponents.GenerateConstraints(var4++, 0, 0.333D, 0.333D));
      }

      if (((Axis)Settings.axes.get(2)).getEnabled()) {
         rDim = new JLabel("R-Axis");
         rDim.setFont(DisplayComponents.editJobPageText);
         retVal.add(rDim, DisplayComponents.GenerateConstraints(var4++, 0, 0.233D, 0.233D));
      }

      retVal.setBorder(new EmptyBorder(5, 5, 0, 0));
      retVal.setMaximumSize(new Dimension(400, 80));
      return retVal;
   }

   public static void updateList() {
      ArrayList<Job> jobs = new ArrayList();
      if (localOn) {
         jobs.addAll(SystemCommands.getJobs());
      }

      if (usbOn) {
         ArrayList<Job> usbJobs = Settings.selectedUSB.getJobs();
         jobs.addAll(usbJobs);
      }

      try {
         if (reverse) {
            Collections.sort(jobs, Collections.reverseOrder());
         } else {
            Collections.sort(jobs);
         }
      } catch (Exception var3) {
         Settings.log.log(Level.WARNING, "Sorting failed, bad data in database" + var3.getMessage());
         var3.printStackTrace();
      }

      list.removeAllElements();
      Iterator var2 = jobs.iterator();

      while(var2.hasNext()) {
         Job j = (Job)var2.next();
         list.addElement(j);
      }

      searchList.setModel(list);
   }

   public static void updateList(String search) {
      ArrayList<Job> jobs = new ArrayList();
      if (localOn) {
         jobs.addAll(SystemCommands.getJobs(search));
      }

      if (usbOn) {
         ArrayList<Job> usbJobs = Settings.selectedUSB.getJobs(search);
         jobs.addAll(usbJobs);
      }

      if (reverse) {
         Collections.sort(jobs, Collections.reverseOrder());
      } else {
         Collections.sort(jobs);
      }

      list.removeAllElements();
      Iterator var3 = jobs.iterator();

      while(var3.hasNext()) {
         Job j = (Job)var3.next();
         list.addElement(j);
      }

      searchList.setModel(list);
   }

   public boolean checkJobSanity() {
      boolean sane = true;
      int cnt = 1;
      if (!Settings.calibrated) {
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
         int i;
         for(i = 0; i < ((Job)searchList.getSelectedValue()).getBends().size(); ++i) {
            if (((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getCycles() < 1) {
               sane = false;
            }
         }

         if (Settings.autoAdvanceMode.equals(AdvanceMode.EXTERNAL) && Settings.autoAdvancePosition.equals(AdvancePosition.TOS)) {
            for(i = 0; i < ((Job)searchList.getSelectedValue()).getBends().size(); ++i) {
               if (((Axis)Settings.axes.get(0)).getEnabled() && ((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(1)).equals("PP")) {
                  new NotificationPage("Warning", "You can't have PP retract and EXT advance at TOP", 5000);
                  sane = false;
               }
            }
         }

         for(i = 0; i < ((Job)searchList.getSelectedValue()).getBends().size(); ++i) {
            if (((Axis)Settings.axes.get(0)).getEnabled()) {
               if (((Job)searchList.getSelectedValue()).getRetractEnabled() && !((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(1)).equals("  ")) {
                  if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(0)).isEmpty()) {
                     sane = false;
                  }

                  if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(2)).isEmpty()) {
                     sane = false;
                  }
               }

               if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(3)).isEmpty()) {
                  sane = false;
               }

               if (((Job)searchList.getSelectedValue()).getBaEnabled() && ((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(0)).getValues().get(4)).isEmpty()) {
                  sane = false;
               }

               cnt = 7;
            }

            if (((Axis)Settings.axes.get(1)).getEnabled()) {
               int j;
               if (((Job)searchList.getSelectedValue()).getMode().equals(Mode.ANGLE)) {
                  for(j = 0; j < 3; ++j) {
                     if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(1)).getValues().get(j)).isEmpty()) {
                        sane = false;
                     }

                     ++cnt;
                  }
               } else {
                  for(j = 0; j < 2; ++j) {
                     if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(1)).getValues().get(j)).isEmpty()) {
                        sane = false;
                     }

                     ++cnt;
                  }
               }
            }

            if (((Axis)Settings.axes.get(2)).getEnabled()) {
               if (((Job)searchList.getSelectedValue()).getMode().equals(Mode.ANGLE)) {
                  if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(2)).getValues().get(0)).isEmpty()) {
                     sane = false;
                  }

                  ++cnt;
               } else {
                  if (((String)((AxisValues)((Bend)((Job)searchList.getSelectedValue()).getBends().get(i)).getAxisValues().get(2)).getValues().get(0)).isEmpty()) {
                     sane = false;
                  }

                  ++cnt;
               }
            }
         }

         if (((Job)searchList.getSelectedValue()).getMode().equals(Mode.ANGLE) || ((Job)searchList.getSelectedValue()).getMode().equals(Mode.DEPTHFC)) {
            if (((Job)searchList.getSelectedValue()).getThickness() == 0.0D) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Selected job is angle mode, and has no thickness set");
               }
            }

            if (((Job)searchList.getSelectedValue()).getPunch() == null) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Selected job is angle mode, and has no punch set");
               }
            }

            if (((Job)searchList.getSelectedValue()).getDie() == null) {
               sane = false;
               if (!NotificationPage.containsKey("Error")) {
                  new NotificationPage("Error", "Selected job is angle mode, and has no die set");
               }
            }
         }

         if (Settings.floatingCalibration && ((Axis)Settings.axes.get(1)).getEnabled() && (((Job)searchList.getSelectedValue()).getMode().equals(Mode.ANGLE) || ((Job)searchList.getSelectedValue()).getMode().equals(Mode.DEPTHFC))) {
            if (((Job)searchList.getSelectedValue()).getThickness() != Settings.calThickness) {
               new TwoButtonPromptPage("Calibration", "Your calibration thickness does not match the job thickness, have you changed your material?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(((Job)RecallJobPage.searchList.getSelectedValue()).getThickness(), Settings.calPunch, Settings.calDie);
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

            if (((Job)searchList.getSelectedValue()).getPunch().getHeight() != Settings.calPunch.getHeight()) {
               new TwoButtonPromptPage("Calibration", "Your calibration punch does not match the job punch, have you changed the tools on the machine?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(Settings.calThickness, ((Job)RecallJobPage.searchList.getSelectedValue()).getPunch(), Settings.calDie);
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

            if (((Job)searchList.getSelectedValue()).getDie().getHeight() != Settings.calDie.getHeight()) {
               new TwoButtonPromptPage("Calibration", "Your calibration die does not match the job die, have you changed the tools on the machine?", "Yes", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     Settings.updateCalibration(Settings.calThickness, Settings.calPunch, ((Job)RecallJobPage.searchList.getSelectedValue()).getDie());
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

         if (!sane && !NotificationPage.containsKey("Error")) {
            new NotificationPage("Error", "Selected job has invalid parameters, cannot run");
         }

         return sane;
      }
   }

   class CustomCaret extends DefaultCaret {
      private static final long serialVersionUID = 1L;

      public void focusGained(FocusEvent e) {
         super.focusGained(e);
      }

      public void focusLost(FocusEvent e) {
      }
   }
}
