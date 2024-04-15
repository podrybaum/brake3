package com.automec.display.pages;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DataInputField;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JBottomButton;
import com.automec.display.components.JCustomButton;
import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.ToolImageSelector;
import com.automec.display.popups.TwoButtonPromptPage;
import com.automec.objects.Job;
import com.automec.objects.Tool;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.ToolType;
import com.automec.objects.enums.Units;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

public class ToolLibraryPage extends JFrame {
   private static final long serialVersionUID = 7352743040236531091L;
   private DefaultListModel<Tool> listPunch;
   private DefaultListModel<Tool> listDie;
   private JList<Tool> searchListPunch;
   private JList<Tool> searchListDie;
   private boolean reverse = false;
   private Job job;
   private Units units;
   private static Tool punch;
   private static Tool die;
   private static ImageIcon pImage;
   private static ImageIcon dImage;
   private static JLabel punchImage;
   private static JLabel dieImage;
   private static JPanel punchData;
   private static JPanel dieData;

   public ToolLibraryPage() {
      super("Tool Library");
      this.units = Settings.units;
      this.initialize();
   }

   public ToolLibraryPage(Job job) {
      super("Tool Library");
      this.job = job;
      this.units = job.getUnits();
      this.initialize();
   }

   private void initialize() {
      this.setSize(1024, 768);
      this.setUndecorated(true);
      punch = new Tool("", ToolType.PUNCH);
      die = new Tool("", ToolType.DIE);
      JLabel title = new JLabel("Tool Library");
      title.setHorizontalAlignment(0);
      title.setFont(DisplayComponents.pageTitleFont);
      this.getContentPane().add(title, "North");
      final JTabbedPane punchPanel = new JTabbedPane();
      JPanel searchPunch = new JPanel();
      punchData = new JPanel();
      punchPanel.setBorder(new EmptyBorder(0, 15, 0, 0));
      JLabel searchTitlePunch = new JLabel("Search Punch Database");
      searchTitlePunch.setFont(DisplayComponents.pageHeaderFont);
      JLabel dataTitlePunch = new JLabel("Punch Data");
      dataTitlePunch.setFont(DisplayComponents.pageHeaderFont);
      punchPanel.setMaximumSize(new Dimension(500, 500));
      punchPanel.setMinimumSize(new Dimension(500, 500));
      punchPanel.setPreferredSize(new Dimension(500, 500));
      searchPunch.add(searchTitlePunch);
      punchPanel.addTab("Search Punches", searchPunch);
      punchPanel.addTab("Punch Data", punchData);
      punchPanel.setFont(DisplayComponents.pageHeaderFont);
      searchPunch.setLayout(new BoxLayout(searchPunch, 1));
      this.getContentPane().add(punchPanel, "West");
      final JTextField searchFieldPunch = new JTextField();
      searchFieldPunch.setHorizontalAlignment(2);
      searchPunch.add(searchFieldPunch);
      searchFieldPunch.setMaximumSize(new Dimension(1000, 50));
      searchFieldPunch.setPreferredSize(new Dimension(1000, 50));
      searchFieldPunch.setFont(DisplayComponents.pageHeaderFont);
      searchFieldPunch.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent arg0) {
         }

         public void insertUpdate(DocumentEvent arg0) {
            try {
               ToolLibraryPage.this.updateListPunch(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "tool library page", var3);
            }

         }

         public void removeUpdate(DocumentEvent arg0) {
            try {
               ToolLibraryPage.this.updateListPunch(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "tool library page", var3);
            }

         }
      });
      searchFieldPunch.addMouseListener(DisplayComponents.KeyboardPopup());
      this.listPunch = new DefaultListModel();
      this.searchListPunch = new JList(this.listPunch);
      this.searchListPunch.setFont(DisplayComponents.pageHeaderFont);
      JScrollPane scrollPunch = new JScrollPane();
      scrollPunch.setMaximumSize(new Dimension(1000, 600));
      scrollPunch.setViewportView(this.searchListPunch);
      scrollPunch.setHorizontalScrollBarPolicy(31);
      searchPunch.add(scrollPunch);
      this.updateListPunch();
      final JButton deletePunch = new JCustomButton("Delete Punch");
      deletePunch.setMaximumSize(new Dimension(1000, 80));
      deletePunch.setAlignmentX(0.5F);
      searchPunch.add(deletePunch);
      punchData.setLayout(new GridBagLayout());
      pImage = new ImageIcon(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Punch" + File.separator + "BIU-016.jpg");
      punchImage = new JLabel(new ImageIcon(pImage.getImage().getScaledInstance(250, 250, 1)));
      punchImage.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            new ToolImageSelector(new Tool("t", ToolType.PUNCH));
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      });
      JLabel punchName = new JLabel("Punch Name: ");
      punchName.setFont(DisplayComponents.editJobPageText);
      JLabel punchHeight = new JLabel("Punch Height: ");
      punchHeight.setFont(DisplayComponents.editJobPageText);
      JLabel punchRadius = new JLabel("Punch Radius: ");
      punchRadius.setFont(DisplayComponents.editJobPageText);
      final JTextField punchNameValue = new JTextField();
      punchNameValue.addMouseListener(DisplayComponents.KeyboardPopup());
      punchNameValue.setFont(DisplayComponents.editJobPageText);
      punchNameValue.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
         }

         public void focusGained(FocusEvent e) {
            punchNameValue.getCaret().setVisible(true);
         }
      });
      final DataInputField punchHeightValue = new DataInputField("", -24.0D, 24.0D);
      punchHeightValue.addMouseListener(DisplayComponents.CalculatorPopup());
      punchHeightValue.setFont(DisplayComponents.editJobPageText);
      punchHeightValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               ((DataInputField)e.getSource()).setText(((DataInputField)e.getSource()).getText());
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
               ((DataInputField)e.getSource()).setText(String.format("%.3f", ToolLibraryPage.punch.getHeight()));
            }

         }
      });
      final DataInputField punchRadiusValue = new DataInputField("", 0.0D, 12.0D);
      punchRadiusValue.addMouseListener(DisplayComponents.CalculatorPopup());
      punchRadiusValue.setFont(DisplayComponents.editJobPageText);
      punchRadiusValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               ((DataInputField)e.getSource()).setText(((DataInputField)e.getSource()).getText());
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
               ((DataInputField)e.getSource()).setText(String.format("%.3f", ToolLibraryPage.punch.getPunchRadius()));
            }

         }
      });
      punchNameValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.getTool(punchNameValue.getText(), ToolType.PUNCH) != null) {
               Tool t = SystemCommands.getTool(punchNameValue.getText(), ToolType.PUNCH);
               punchHeightValue.setNumber(t.getHeight());
               punchRadiusValue.setNumber(t.getPunchRadius());
               ToolLibraryPage.setPImage(t.getImage());
            } else if (punchNameValue.getText() == "") {
               punchHeightValue.setText("");
               punchRadiusValue.setText("");
            }

         }
      });
      JButton punchSave = new JCustomButton("Save punch");
      punchSave.setMinimumSize(new Dimension(100, 80));
      punchSave.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.info("saving punch: " + punchNameValue.getText());
            final Tool t = new Tool(punchNameValue.getText(), ToolType.PUNCH);
            if (!punchHeightValue.getText().isEmpty()) {
               t.setHeight(Double.valueOf(punchHeightValue.getText()));
            }

            if (!punchRadiusValue.getText().isEmpty()) {
               t.setPunchRadius(Double.valueOf(punchRadiusValue.getText()));
            }

            t.setImage(ToolLibraryPage.punch.getImage());
            if (Settings.selectedUSB != null) {
               new TwoButtonPromptPage("Save Location", "Where would you like to save to?", "Local", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     t.save(Location.LOCAL);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
                  }
               }, "USB", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     t.save(Location.USB);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
                  }
               }, true);
            } else {
               t.save(Location.LOCAL);
               new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
            }

            if (searchFieldPunch.getText().isEmpty()) {
               ToolLibraryPage.this.updateListPunch();
            } else {
               ToolLibraryPage.this.updateListPunch(searchFieldPunch.getText());
            }

         }
      });
      JButton punchSelect = new JCustomButton("Select Punch");
      punchSelect.setPreferredSize(new Dimension(100, 80));
      punchSelect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Tool t = new Tool(punchNameValue.getText(), ToolType.PUNCH);
            t.setHeight(Double.valueOf(punchHeightValue.getText()));
            t.setPunchRadius(Double.valueOf(punchRadiusValue.getText()));
            Settings.selectedPunch = t;
            if (ToolLibraryPage.this.job != null) {
               ToolLibraryPage.this.job.setPunch(t);
               new NotificationPage("Punch Selected", "Punch: " + t.getName() + " has been selected for job: " + ToolLibraryPage.this.job.getName(), 1500);
            } else if (Settings.floatingCalibration) {
               Settings.calPunch = t;
               new NotificationPage("Punch Selected", "Punch: " + t.getName() + " has been selected for floating calibration", 1500);
            }

         }
      });
      if (this.job == null) {
         punchSelect.setEnabled(false);
      }

      if (Settings.floatingCalibration && !Settings.calibrated) {
         punchSelect.setEnabled(true);
      }

      punchData.add(punchImage, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 0.5D, 2, 1));
      punchData.add(punchName, DisplayComponents.GenerateConstraints(0, 1, 1.0D, 0.1D));
      punchData.add(punchHeight, DisplayComponents.GenerateConstraints(0, 2, 1.0D, 0.1D));
      punchData.add(punchRadius, DisplayComponents.GenerateConstraints(0, 3, 1.0D, 0.1D));
      punchData.add(punchNameValue, DisplayComponents.GenerateConstraints(1, 1, 1.0D, 0.1D));
      punchData.add(punchHeightValue, DisplayComponents.GenerateConstraints(1, 2, 1.0D, 0.1D));
      punchData.add(punchRadiusValue, DisplayComponents.GenerateConstraints(1, 3, 1.0D, 0.1D));
      punchData.add(punchSave, DisplayComponents.GenerateConstraints(0, 4, 1.0D, 0.2D));
      punchData.add(punchSelect, DisplayComponents.GenerateConstraints(1, 4, 1.0D, 0.2D));
      deletePunch.setEnabled(false);
      this.searchListPunch.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent arg0) {
            if (!ToolLibraryPage.this.searchListPunch.isSelectionEmpty()) {
               ToolLibraryPage.punch = (Tool)ToolLibraryPage.this.searchListPunch.getSelectedValue();
               Tool t = (Tool)ToolLibraryPage.this.searchListPunch.getSelectedValue();
               punchNameValue.setText(t.getName());
               punchHeightValue.setNumber(t.getHeight());
               punchRadiusValue.setNumber(t.getPunchRadius());
               if (t.getImage() == null) {
                  ToolLibraryPage.setPImage(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Punch" + File.separator + "BIU-016.jpg");
               } else {
                  ToolLibraryPage.setPImage(SystemCommands.getWorkingDirectory() + t.getImage());
               }

               punchPanel.setSelectedIndex(1);
               deletePunch.setEnabled(true);
            } else {
               deletePunch.setEnabled(false);
            }

         }
      });
      this.searchListPunch.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            if (ToolLibraryPage.this.searchListPunch.getSelectedIndex() == ToolLibraryPage.this.searchListPunch.locationToIndex(arg0.getPoint())) {
               punchPanel.setSelectedIndex(1);
            }

         }

         public void mouseReleased(MouseEvent arg0) {
         }
      });
      deletePunch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Delete Confirmation", "Are you sure you want to delete " + ToolLibraryPage.this.searchListPunch.getSelectedValue() + "?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Settings.log.finest("Pressed delete button");
                  Settings.log.fine("deleting tool: " + ToolLibraryPage.this.searchListPunch.getSelectedValue());
                  ((Tool)ToolLibraryPage.this.searchListPunch.getSelectedValue()).delete();
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  if (searchFieldPunch.getText().isEmpty()) {
                     ToolLibraryPage.this.updateListPunch();
                  } else {
                     ToolLibraryPage.this.updateListPunch(searchFieldPunch.getText());
                  }

               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, false);
         }
      });
      if (this.job != null) {
         if (this.job.getPunch() != null) {
            punch = this.job.getPunch();
            punchNameValue.setText(punch.getName());
            punchHeightValue.setNumber(punch.getHeight());
            punchRadiusValue.setNumber(punch.getPunchRadius());
            punchPanel.setSelectedIndex(1);
         } else {
            punch = new Tool("", ToolType.PUNCH);
         }
      }

      final JTabbedPane diePanel = new JTabbedPane();
      diePanel.setBorder(new EmptyBorder(0, 0, 0, 15));
      JPanel searchDie = new JPanel();
      dieData = new JPanel();
      JLabel searchTitleDie = new JLabel("Search Die Database");
      searchTitleDie.setFont(DisplayComponents.pageHeaderFont);
      JLabel dataTitleDie = new JLabel("Die Data");
      dataTitleDie.setFont(DisplayComponents.pageHeaderFont);
      searchDie.setLayout(new BoxLayout(searchDie, 1));
      diePanel.setMaximumSize(new Dimension(500, 500));
      diePanel.setMinimumSize(new Dimension(500, 500));
      diePanel.setPreferredSize(new Dimension(500, 500));
      searchTitleDie.setAlignmentX(0.5F);
      searchDie.add(searchTitleDie);
      diePanel.addTab("Search Dies", searchDie);
      diePanel.addTab("Die Data", dieData);
      diePanel.setFont(DisplayComponents.pageHeaderFont);
      this.getContentPane().add(diePanel, "East");
      final JTextField searchFieldDie = new JTextField();
      searchFieldDie.setHorizontalAlignment(2);
      searchDie.add(searchFieldDie);
      searchFieldDie.setMaximumSize(new Dimension(1000, 50));
      searchFieldDie.setPreferredSize(new Dimension(1000, 50));
      searchFieldDie.setFont(DisplayComponents.pageHeaderFont);
      searchFieldDie.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent arg0) {
         }

         public void insertUpdate(DocumentEvent arg0) {
            try {
               ToolLibraryPage.this.updateListDie(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "recall job page", var3);
            }

         }

         public void removeUpdate(DocumentEvent arg0) {
            try {
               ToolLibraryPage.this.updateListDie(arg0.getDocument().getText(0, arg0.getDocument().getLength()));
            } catch (BadLocationException var3) {
               Settings.log.log(Level.WARNING, "recall job page", var3);
            }

         }
      });
      searchFieldDie.addMouseListener(DisplayComponents.KeyboardPopup());
      this.listDie = new DefaultListModel();
      this.searchListDie = new JList(this.listDie);
      this.searchListDie.setFont(DisplayComponents.pageHeaderFont);
      JScrollPane scrollDie = new JScrollPane();
      scrollDie.setMaximumSize(new Dimension(1000, 600));
      scrollDie.setViewportView(this.searchListDie);
      scrollDie.setHorizontalScrollBarPolicy(31);
      searchDie.add(scrollDie);
      this.updateListDie();
      final JButton deleteDie = new JCustomButton("Delete Die");
      deleteDie.setMaximumSize(new Dimension(1000, 80));
      deleteDie.setAlignmentX(0.5F);
      searchDie.add(deleteDie);
      dieData.setLayout(new GridBagLayout());
      dImage = new ImageIcon(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Die" + File.separator + "OZU-013.jpg");
      dieImage = new JLabel(new ImageIcon(dImage.getImage().getScaledInstance(250, 250, 1)));
      dieImage.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            new ToolImageSelector(new Tool("", ToolType.DIE));
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      });
      JLabel dieName = new JLabel("Die Name: ");
      dieName.setFont(DisplayComponents.editJobPageText);
      JLabel dieHeight = new JLabel("Die Height: ");
      dieHeight.setFont(DisplayComponents.editJobPageText);
      JLabel dieRadius = new JLabel("Die Radius: ");
      dieRadius.setFont(DisplayComponents.editJobPageText);
      JLabel dieWidth = new JLabel("Die Width: ");
      dieWidth.setFont(DisplayComponents.editJobPageText);
      final JTextField dieNameValue = new JTextField();
      dieNameValue.setFont(DisplayComponents.editJobPageText);
      dieNameValue.addMouseListener(DisplayComponents.KeyboardPopup());
      final DataInputField dieHeightValue = new DataInputField("", -24.0D, 24.0D);
      dieHeightValue.setFont(DisplayComponents.editJobPageText);
      dieHeightValue.addMouseListener(DisplayComponents.CalculatorPopup());
      dieHeightValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               ((DataInputField)e.getSource()).setText(((DataInputField)e.getSource()).getText());
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
               ((DataInputField)e.getSource()).setText(String.format("%.3f", ToolLibraryPage.die.getHeight()));
            }

         }
      });
      final DataInputField dieRadiusValue = new DataInputField("", 0.0D, 12.0D);
      dieRadiusValue.setFont(DisplayComponents.editJobPageText);
      dieRadiusValue.addMouseListener(DisplayComponents.CalculatorPopup());
      dieRadiusValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               ((DataInputField)e.getSource()).setText(((DataInputField)e.getSource()).getText());
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
               ((DataInputField)e.getSource()).setText(String.format("%.3f", ToolLibraryPage.die.getDieRadius()));
            }

         }
      });
      final DataInputField dieWidthValue = new DataInputField("", 0.0D, 12.0D);
      dieWidthValue.setFont(DisplayComponents.editJobPageText);
      dieWidthValue.addMouseListener(DisplayComponents.CalculatorPopup());
      dieWidthValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.validInput((DataInputField)e.getSource())) {
               ((DataInputField)e.getSource()).setBackground(DisplayComponents.Background);
               ((DataInputField)e.getSource()).setText(((DataInputField)e.getSource()).getText());
            } else {
               ((DataInputField)e.getSource()).setBackground(Color.RED);
               ((DataInputField)e.getSource()).setText(String.format("%.3f", ToolLibraryPage.die.getDieWidth()));
            }

         }
      });
      dieNameValue.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (SystemCommands.getTool(dieNameValue.getText(), ToolType.DIE) != null) {
               Tool t = SystemCommands.getTool(dieNameValue.getText(), ToolType.DIE);
               dieHeightValue.setNumber(t.getHeight());
               dieRadiusValue.setNumber(t.getDieRadius());
               dieWidthValue.setNumber(t.getDieWidth());
               ToolLibraryPage.setDImage(t.getImage());
            } else if (dieNameValue.getText() == "") {
               dieHeightValue.setText("");
               dieRadiusValue.setText("");
               dieWidthValue.setText("");
            }

         }
      });
      JButton dieSave = new JCustomButton("Save die");
      dieSave.setPreferredSize(new Dimension(100, 80));
      dieSave.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Settings.log.info("saving die: " + dieNameValue.getText());
            final Tool t = new Tool(dieNameValue.getText(), ToolType.DIE);
            if (!dieHeightValue.getText().isEmpty()) {
               t.setHeight(Double.valueOf(dieHeightValue.getText()));
            }

            if (!dieRadiusValue.getText().isEmpty()) {
               t.setDieRadius(Double.valueOf(dieRadiusValue.getText()));
            }

            if (!dieWidthValue.getText().isEmpty()) {
               t.setDieWidth(Double.valueOf(dieWidthValue.getText()));
            }

            t.setImage(ToolLibraryPage.die.getImage());
            if (Settings.selectedUSB != null) {
               new TwoButtonPromptPage("Save Location", "Where would you like to save to?", "Local", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     t.save(Location.LOCAL);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
                  }
               }, "USB", new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     t.save(Location.USB);
                     ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                     new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
                  }
               }, true);
            } else {
               t.save(Location.LOCAL);
               new NotificationPage("Tool Saved", t.getName() + " was saved", 3000);
            }

            if (searchFieldPunch.getText().isEmpty()) {
               ToolLibraryPage.this.updateListDie();
            } else {
               ToolLibraryPage.this.updateListDie(searchFieldDie.getText());
            }

         }
      });
      JButton dieSelect = new JCustomButton("Select Die");
      dieSelect.setPreferredSize(new Dimension(100, 80));
      dieSelect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Tool t = new Tool(dieNameValue.getText(), ToolType.DIE);
            t.setHeight(Double.valueOf(dieHeightValue.getText()));
            t.setDieRadius(Double.valueOf(dieRadiusValue.getText()));
            t.setDieWidth(Double.valueOf(dieWidthValue.getText()));
            Settings.selectedDie = t;
            if (ToolLibraryPage.this.job != null) {
               ToolLibraryPage.this.job.setDie(t);
               new NotificationPage("Die Selected", "Die: " + t.getName() + " has been selected for job: " + ToolLibraryPage.this.job.getName(), 1500);
            } else if (Settings.floatingCalibration) {
               Settings.calDie = t;
               new NotificationPage("Die Selected", "Die: " + t.getName() + " has been selected for floating calibration", 1500);
            }

         }
      });
      if (this.job == null) {
         dieSelect.setEnabled(false);
      }

      if (Settings.floatingCalibration && !Settings.calibrated) {
         dieSelect.setEnabled(true);
      }

      dieData.add(dieImage, DisplayComponents.GenerateConstraints(0, 0, 1.0D, 0.4D, 2, 1));
      dieData.add(dieName, DisplayComponents.GenerateConstraints(0, 1, 1.0D, 0.1D));
      dieData.add(dieHeight, DisplayComponents.GenerateConstraints(0, 2, 1.0D, 0.1D));
      dieData.add(dieRadius, DisplayComponents.GenerateConstraints(0, 3, 1.0D, 0.1D));
      dieData.add(dieNameValue, DisplayComponents.GenerateConstraints(1, 1, 1.0D, 0.1D));
      dieData.add(dieHeightValue, DisplayComponents.GenerateConstraints(1, 2, 1.0D, 0.1D));
      dieData.add(dieRadiusValue, DisplayComponents.GenerateConstraints(1, 3, 1.0D, 0.1D));
      dieData.add(dieWidth, DisplayComponents.GenerateConstraints(0, 4, 1.0D, 0.1D));
      dieData.add(dieWidthValue, DisplayComponents.GenerateConstraints(1, 4, 1.0D, 0.1D));
      dieData.add(dieSave, DisplayComponents.GenerateConstraints(0, 5, 1.0D, 0.2D));
      dieData.add(dieSelect, DisplayComponents.GenerateConstraints(1, 5, 1.0D, 0.2D));
      deleteDie.setEnabled(false);
      this.searchListDie.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!ToolLibraryPage.this.searchListDie.isSelectionEmpty()) {
               ToolLibraryPage.die = (Tool)ToolLibraryPage.this.searchListDie.getSelectedValue();
               Tool t = (Tool)ToolLibraryPage.this.searchListDie.getSelectedValue();
               dieNameValue.setText(t.getName());
               dieHeightValue.setNumber(t.getHeight());
               dieRadiusValue.setNumber(t.getDieRadius());
               dieWidthValue.setNumber(t.getDieWidth());
               if (t.getImage() == null) {
                  ToolLibraryPage.setDImage(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Die" + File.separator + "OZU-013.jpg");
               } else {
                  ToolLibraryPage.setDImage(SystemCommands.getWorkingDirectory() + t.getImage());
               }

               diePanel.setSelectedIndex(1);
               deleteDie.setEnabled(true);
            } else {
               deleteDie.setEnabled(false);
            }

         }
      });
      this.searchListDie.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
            if (ToolLibraryPage.this.searchListDie.getSelectedIndex() == ToolLibraryPage.this.searchListDie.locationToIndex(arg0.getPoint())) {
               diePanel.setSelectedIndex(1);
            }

         }

         public void mouseReleased(MouseEvent arg0) {
         }
      });
      deleteDie.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new TwoButtonPromptPage("Delete Confirmation", "Are you sure you want to delete " + ToolLibraryPage.this.searchListPunch.getSelectedValue() + "?", "Yes", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Settings.log.finest("Pressed delete button");
                  Settings.log.fine("deleting tool: " + ToolLibraryPage.this.searchListDie.getSelectedValue());
                  ((Tool)ToolLibraryPage.this.searchListDie.getSelectedValue()).delete();
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
                  if (searchFieldPunch.getText().isEmpty()) {
                     ToolLibraryPage.this.updateListDie();
                  } else {
                     ToolLibraryPage.this.updateListDie(searchFieldDie.getText());
                  }

               }
            }, "No", new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  ((JFrame)SwingUtilities.getRoot((Component)e.getSource())).dispose();
               }
            }, false);
         }
      });
      if (this.job != null) {
         if (this.job.getDie() != null) {
            die = this.job.getDie();
            dieNameValue.setText(die.getName());
            dieHeightValue.setNumber(die.getHeight());
            dieRadiusValue.setNumber(die.getDieRadius());
            dieWidthValue.setNumber(die.getDieWidth());
            diePanel.setSelectedIndex(1);
         } else {
            die = new Tool("", ToolType.DIE);
         }
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
            ToolLibraryPage.this.dispose();
            Settings.log.finest("edit job page disposed");
            new HomePage();
         }
      });
      buttonPanel.add(homeButton);
      JBottomButton applyTools;
      if (this.job != null) {
         applyTools = new JBottomButton("Apply Tools", "edit.png");
         applyTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (ToolLibraryPage.this.job.getPunch() != null && ToolLibraryPage.this.job.getDie() != null) {
                  ToolLibraryPage.this.dispose();
                  new EditJobPage(ToolLibraryPage.this.job);
               } else {
                  new NotificationPage("Warning", "Punch or Die not selected for job, please hit select for each", 2000);
               }

            }
         });
         buttonPanel.add(applyTools);
      } else {
         applyTools = new JBottomButton("Settings", "settings.png");
         applyTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ToolLibraryPage.this.dispose();
               new SettingsPage();
            }
         });
         buttonPanel.add(applyTools);
      }

      this.setVisible(true);
   }

   private void updateListPunch() {
      ArrayList<Tool> tools = new ArrayList();
      tools.addAll(SystemCommands.getTools(ToolType.PUNCH));
      if (Settings.selectedUSB != null) {
         ArrayList<Tool> usbJobs = Settings.selectedUSB.getTools(ToolType.PUNCH);
         tools.addAll(usbJobs);
      }

      if (this.reverse) {
         Collections.sort(tools, Collections.reverseOrder());
      } else {
         Collections.sort(tools);
      }

      this.listPunch.removeAllElements();
      Iterator var3 = tools.iterator();

      while(var3.hasNext()) {
         Tool t = (Tool)var3.next();
         this.listPunch.addElement(t);
      }

      this.searchListPunch.setModel(this.listPunch);
   }

   private void updateListPunch(String s) {
      ArrayList<Tool> tools = new ArrayList();
      tools.addAll(SystemCommands.getTools(s, ToolType.PUNCH));
      if (Settings.selectedUSB != null) {
         ArrayList<Tool> usbJobs = Settings.selectedUSB.getTools(s, ToolType.PUNCH);
         tools.addAll(usbJobs);
      }

      if (this.reverse) {
         Collections.sort(tools, Collections.reverseOrder());
      } else {
         Collections.sort(tools);
      }

      this.listPunch.removeAllElements();
      Iterator var4 = tools.iterator();

      while(var4.hasNext()) {
         Tool t = (Tool)var4.next();
         if (t.getType() == ToolType.PUNCH) {
            this.listPunch.addElement(t);
         }
      }

      this.searchListPunch.setModel(this.listPunch);
   }

   private void updateListDie() {
      ArrayList<Tool> tools = new ArrayList();
      tools.addAll(SystemCommands.getTools(ToolType.DIE));
      if (Settings.selectedUSB != null) {
         ArrayList<Tool> usbJobs = Settings.selectedUSB.getTools(ToolType.DIE);
         tools.addAll(usbJobs);
      }

      if (this.reverse) {
         Collections.sort(tools, Collections.reverseOrder());
      } else {
         Collections.sort(tools);
      }

      this.listDie.removeAllElements();
      Iterator var3 = tools.iterator();

      while(var3.hasNext()) {
         Tool j = (Tool)var3.next();
         if (j.getType() == ToolType.DIE) {
            this.listDie.addElement(j);
         }
      }

      this.searchListDie.setModel(this.listDie);
   }

   private void updateListDie(String s) {
      ArrayList<Tool> tools = new ArrayList();
      tools.addAll(SystemCommands.getTools(s, ToolType.DIE));
      if (Settings.selectedUSB != null) {
         ArrayList<Tool> usbJobs = Settings.selectedUSB.getTools(s, ToolType.DIE);
         tools.addAll(usbJobs);
      }

      if (this.reverse) {
         Collections.sort(tools, Collections.reverseOrder());
      } else {
         Collections.sort(tools);
      }

      this.listDie.removeAllElements();
      Iterator var4 = tools.iterator();

      while(var4.hasNext()) {
         Tool j = (Tool)var4.next();
         this.listDie.addElement(j);
      }

      this.searchListDie.setModel(this.listDie);
   }

   public static void setPImage(String loc) {
      pImage = new ImageIcon(loc);
      System.out.println(SystemCommands.getWorkingDirectory());
      System.out.println(loc);
      punch.setImage(loc.substring(SystemCommands.getWorkingDirectory().length()));
      punchImage.setIcon(new ImageIcon(pImage.getImage().getScaledInstance(250, 250, 1)));
      punchData.repaint();
   }

   public static void setDImage(String loc) {
      dImage = new ImageIcon(loc);
      die.setImage(loc.substring(SystemCommands.getWorkingDirectory().length()));
      dieImage.setIcon(new ImageIcon(dImage.getImage().getScaledInstance(250, 250, 1)));
      punchData.repaint();
   }
}
