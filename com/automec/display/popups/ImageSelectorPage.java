package com.automec.display.popups;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.Location;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ImageSelectorPage {
   private JFrame imageSelectorFrame = new JFrame();
   private Job job;
   private Bend bend;
   private JList<Icon> imageList;
   private DefaultListModel<Icon> list;
   private ArrayList<String> extensions = new ArrayList(Arrays.asList(".jpg", ".png", ".gif"));
   private ArrayList<String> imagePaths = new ArrayList();

   public ImageSelectorPage(Job job, Bend bend) {
      this.job = job;
      this.bend = bend;
      this.initialize();
   }

   public void initialize() {
      this.imageSelectorFrame.setDefaultCloseOperation(3);
      this.imageSelectorFrame.setUndecorated(true);
      this.imageSelectorFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.imageSelectorFrame.setAlwaysOnTop(false);
      this.list = new DefaultListModel();
      this.imageList = new JList(this.list);
      this.imageList.setLayoutOrientation(2);
      this.imageList.setVisibleRowCount(-1);
      JScrollPane scroll = new JScrollPane();
      scroll.setMaximumSize(new Dimension(900, 568));
      scroll.setViewportView(this.imageList);
      scroll.setHorizontalScrollBarPolicy(31);
      this.imageSelectorFrame.add(scroll, "Center");
      this.updateList();
      GridBagLayout bpgbl = new GridBagLayout();
      bpgbl.columnWidths = new int[2];
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(bpgbl);
      final JButton selectButton = new JCustomButton("Select");
      selectButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      JButton cancelButton = new JCustomButton("Cancel");
      cancelButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      selectButton.setEnabled(false);
      selectButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).contains(SystemCommands.getWorkingDirectory())) {
               ImageSelectorPage.this.bend.addBendImage((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex()));
            } else {
               System.out.println((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex()));

               try {
                  if (ImageSelectorPage.this.job.getLocation().equals(Location.LOCAL)) {
                     Files.copy((new File((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex()))).toPath(), (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + ((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).substring(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).lastIndexOf(File.separator) + 1))).toPath());
                     ImageSelectorPage.this.bend.addBendImage(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + ((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).substring(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).lastIndexOf(File.separator) + 1));
                  } else if (ImageSelectorPage.this.bend.getLocation().equals(Location.USB)) {
                     if (!(new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + ImageSelectorPage.this.job.getName())).exists()) {
                        (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + ImageSelectorPage.this.job.getName())).mkdirs();
                     }

                     Files.copy((new File((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex()))).toPath(), (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + ImageSelectorPage.this.job.getName() + File.separator + ((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).substring(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).lastIndexOf(File.separator) + 1))).toPath());
                     ImageSelectorPage.this.bend.addBendImage(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + ((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).substring(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).lastIndexOf(File.separator) + 1));
                  }
               } catch (Exception var3) {
                  Settings.log.log(Level.SEVERE, "error in add image to bend from usb", var3);
               }
            }

            ImageSelectorPage.this.imageSelectorFrame.dispose();
         }
      });
      cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ImageSelectorPage.this.imageSelectorFrame.dispose();
         }
      });
      buttonPanel.add(selectButton, DisplayComponents.GenerateConstraints(0, 0));
      buttonPanel.add(cancelButton, DisplayComponents.GenerateConstraints(1, 0));
      this.imageSelectorFrame.add(buttonPanel, "South");
      boolean runonce = true;
      this.imageList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               if (ImageSelectorPage.this.imageList.getSelectedIndex() == -1) {
                  selectButton.setEnabled(false);
               } else {
                  System.out.println(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).substring(((String)ImageSelectorPage.this.imagePaths.get(ImageSelectorPage.this.imageList.getSelectedIndex())).lastIndexOf(File.separator) + 1));
                  selectButton.setEnabled(true);
               }
            }

         }
      });
      this.imageSelectorFrame.setSize(930, 630);
      this.imageSelectorFrame.setLocation((1024 - this.imageSelectorFrame.getWidth()) / 2, (768 - this.imageSelectorFrame.getHeight()) / 2);
      this.imageSelectorFrame.setVisible(true);
      Settings.log.finest("image popup page initialized");
   }

   public void updateList() {
      File[] f = (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images")).listFiles();
      this.list.removeAllElements();
      if (Settings.selectedUSB != null) {
         this.list.clear();
         File[] f2 = (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images")).listFiles();

         for(int i = 0; i < f2.length; ++i) {
            String ext = f2[i].getPath().substring(f2[i].getPath().length() - 4);
            if (this.extensions.contains(ext)) {
               ImageIcon t = new ImageIcon(f2[i].getPath());
               this.list.addElement(new ImageIcon(t.getImage().getScaledInstance(300, 300, 1)));
               this.imagePaths.add(f2[i].getPath());
            }
         }
      } else {
         for(int i = 0; i < f.length; ++i) {
            String ext = f[i].getPath().substring(f[i].getPath().length() - 4);
            if (this.extensions.contains(ext)) {
               ImageIcon t = new ImageIcon(f[i].getPath());
               this.list.addElement(new ImageIcon(t.getImage().getScaledInstance(300, 300, 1)));
               this.imagePaths.add(f[i].getPath());
            }
         }
      }

   }

   public void saveJob() {
      if (this.job.getLocation() == Location.LOCAL) {
         this.job.saveJob(Location.LOCAL);
      } else if (Settings.selectedUSB != null) {
         this.job.saveJob(Location.USB);
      }

   }
}
