package com.automec.display.popups;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.Location;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class ImagePopupPage {
   private JFrame imagePopupFrame = new JFrame();
   private Job job;
   private int bendNo;
   public static JTextArea bendNotes;

   public ImagePopupPage(Job job, int bendNo) {
      this.job = job;
      this.bendNo = bendNo;
      this.initialize();
   }

   public void initialize() {
      this.imagePopupFrame.setDefaultCloseOperation(3);
      this.imagePopupFrame.setUndecorated(true);
      this.imagePopupFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.imagePopupFrame.setAlwaysOnTop(false);
      Timer timerLBCal = new Timer(1000, new ImagePopupPage.Focus());
      timerLBCal.start();
      bendNotes = new JTextArea();
      bendNotes.setPreferredSize(new Dimension(80, 100));
      this.imagePopupFrame.getContentPane().add(bendNotes, "South");
      bendNotes.setText((String)this.job.getBendNotes().get(this.bendNo));
      bendNotes.addMouseListener(DisplayComponents.KeyboardPopup());
      JLabel image = new JLabel();
      ImageIcon imageIcon = new ImageIcon(this.getBendImage());
      System.out.println("img: " + this.getBendImage());
      image.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(900, 568, 1)));
      this.imagePopupFrame.getContentPane().add(image, "Center");
      image.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            try {
               ImagePopupPage.this.job.getBendNotes().set(ImagePopupPage.this.bendNo, ImagePopupPage.bendNotes.getText());
            } catch (IndexOutOfBoundsException var3) {
               ImagePopupPage.this.job.getBendNotes().add(ImagePopupPage.this.bendNo, ImagePopupPage.bendNotes.getText());
            }

            ImagePopupPage.this.imagePopupFrame.dispose();
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
      this.imagePopupFrame.setSize(900, 668);
      this.imagePopupFrame.setLocation((1024 - this.imagePopupFrame.getWidth()) / 2, (768 - this.imagePopupFrame.getHeight()) / 2);
      this.imagePopupFrame.setVisible(true);
      Settings.log.finest("image popup page initialized");
   }

   public String getBendImage() {
      System.out.println(((Bend)this.job.getBends().get(this.bendNo)).getBendImage());
      String path = ((Bend)this.job.getBends().get(this.bendNo)).getBendImage();
      if (File.separatorChar == '\\') {
         path = path.replace('/', File.separatorChar);
      } else {
         path = path.replace('\\', File.separatorChar);
      }

      if (this.job.getLocation().equals(Location.LOCAL)) {
         if ((new File(SystemCommands.getWorkingDirectory() + File.separator + path)).exists()) {
            return SystemCommands.getWorkingDirectory() + File.separator + path;
         }
      } else if (this.job.getLocation().equals(Location.USB) && (new File(Settings.selectedUSB.path + File.separator + path)).exists()) {
         return Settings.selectedUSB.path + File.separator + path;
      }

      return "";
   }

   class Focus implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         ImagePopupPage.bendNotes.requestFocusInWindow();
      }
   }
}
