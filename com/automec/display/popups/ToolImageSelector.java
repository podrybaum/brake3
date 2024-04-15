package com.automec.display.popups;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import com.automec.display.pages.ToolLibraryPage;
import com.automec.objects.Tool;
import com.automec.objects.enums.ToolType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

public class ToolImageSelector {
   private JFrame imageSelectorFrame = new JFrame();
   private Tool tool;
   private JList<Icon> imageList;
   private DefaultListModel<Icon> list;
   private ArrayList<String> extensions = new ArrayList(Arrays.asList(".jpg", ".png", ".gif"));
   private ArrayList<String> imagePaths = new ArrayList();

   public ToolImageSelector(Tool tool) {
      this.tool = tool;
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
            ToolImageSelector.this.tool.setImage((String)ToolImageSelector.this.imagePaths.get(ToolImageSelector.this.imageList.getSelectedIndex()));
            if (ToolImageSelector.this.tool.getType().equals(ToolType.PUNCH)) {
               ToolLibraryPage.setPImage(ToolImageSelector.this.tool.getImage());
            } else {
               ToolLibraryPage.setDImage(ToolImageSelector.this.tool.getImage());
            }

            ToolImageSelector.this.imageSelectorFrame.dispose();
         }
      });
      cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ToolImageSelector.this.imageSelectorFrame.dispose();
         }
      });
      buttonPanel.add(selectButton, DisplayComponents.GenerateConstraints(0, 0));
      buttonPanel.add(cancelButton, DisplayComponents.GenerateConstraints(1, 0));
      this.imageSelectorFrame.add(buttonPanel, "South");
      this.imageList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               if (ToolImageSelector.this.imageList.getSelectedIndex() == -1) {
                  selectButton.setEnabled(false);
               } else {
                  selectButton.setEnabled(true);
               }
            }

         }
      });
      this.imageSelectorFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null) {
               ToolImageSelector.this.imageSelectorFrame.dispose();
               Settings.log.finest("Tool image selector page closed");
            }

         }
      });
      this.imageSelectorFrame.setSize(930, 630);
      this.imageSelectorFrame.setLocation((1024 - this.imageSelectorFrame.getWidth()) / 2, (768 - this.imageSelectorFrame.getHeight()) / 2);
      this.imageSelectorFrame.setVisible(true);
      Settings.log.finest("image popup page initialized");
   }

   public void updateList() {
      String ty = "";
      if (this.tool.getType().equals(ToolType.PUNCH)) {
         ty = "Punch";
      } else {
         ty = "Die";
      }

      File[] f = (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + ty)).listFiles();
      this.list.removeAllElements();

      for(int i = 0; i < f.length; ++i) {
         String ext = f[i].getPath().substring(f[i].getPath().length() - 4);
         if (this.extensions.contains(ext)) {
            ImageIcon t = new ImageIcon(f[i].getPath());
            this.list.addElement(new ImageIcon(t.getImage().getScaledInstance(300, 300, 1)));
            this.imagePaths.add(f[i].getPath());
         }
      }

      if (Settings.selectedUSB != null) {
         File[] f2 = (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + ty)).listFiles();

         for(int i = 0; i < f2.length; ++i) {
            String ext = f2[i].getPath().substring(f2[i].getPath().length() - 4);
            if (this.extensions.contains(ext)) {
               ImageIcon t = new ImageIcon(f2[i].getPath());
               this.list.addElement(new ImageIcon(t.getImage().getScaledInstance(300, 300, 1)));
               this.imagePaths.add(f2[i].getPath());
            }
         }
      }

   }
}
