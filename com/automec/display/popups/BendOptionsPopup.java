package com.automec.display.popups;

import com.automec.Settings;
import com.automec.display.components.DisplayComponents;
import com.automec.display.components.JCustomButton;
import com.automec.display.pages.EditJobPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.Job;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class BendOptionsPopup {
   private Job job;
   private Bend bend;
   private int bendNo;
   private JFrame bendOptionsFrame;
   private int locX;
   private int locY;
   private static boolean exists = false;
   private Dimension buttonSize = new Dimension(220, 50);

   public BendOptionsPopup(Job job, Bend bend, int bendNo, int locX, int locY) {
      if (!exists) {
         this.job = job;
         this.bend = bend;
         this.bendNo = bendNo;
         this.locX = locX;
         this.locY = locY;
         this.bendOptionsFrame = new JFrame();
         this.initialize();
      }

   }

   public void initialize() {
      exists = true;
      if (this.locY + 300 > 768) {
         int off = this.locY + 300 - 768;
         this.locY -= off;
      }

      EditJobPage.existingPage.highlightBend(this.bendNo);
      this.bendOptionsFrame.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent arg0) {
         }

         public void windowLostFocus(WindowEvent arg0) {
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      this.bendOptionsFrame.setBounds(this.locX, this.locY, 220, 300);
      this.bendOptionsFrame.setDefaultCloseOperation(3);
      this.bendOptionsFrame.setUndecorated(true);
      this.bendOptionsFrame.getContentPane().setLayout(new BoxLayout(this.bendOptionsFrame.getContentPane(), 1));
      this.bendOptionsFrame.setAlwaysOnTop(true);
      JButton insertAbove = new JCustomButton("Insert Bend above");
      JButton insertBelow = new JCustomButton("Insert Bend below");
      JButton deleteBend = new JCustomButton("Delete Bend");
      JButton addImage = new JCustomButton("Add bend image");
      JButton removeImage = new JCustomButton("Remove bend image");
      JButton cancel = new JCustomButton("Cancel");
      insertAbove.setFont(DisplayComponents.bendPanelFont);
      insertBelow.setFont(DisplayComponents.bendPanelFont);
      deleteBend.setFont(DisplayComponents.bendPanelFont);
      addImage.setFont(DisplayComponents.bendPanelFont);
      removeImage.setFont(DisplayComponents.bendPanelFont);
      cancel.setFont(DisplayComponents.bendPanelFont);
      insertAbove.setHorizontalAlignment(2);
      insertBelow.setHorizontalAlignment(2);
      deleteBend.setHorizontalAlignment(2);
      addImage.setHorizontalAlignment(2);
      removeImage.setHorizontalAlignment(2);
      cancel.setHorizontalAlignment(2);
      insertAbove.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      insertBelow.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      deleteBend.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      addImage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      removeImage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      cancel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      insertAbove.setMaximumSize(this.buttonSize);
      insertBelow.setMaximumSize(this.buttonSize);
      deleteBend.setMaximumSize(this.buttonSize);
      addImage.setMaximumSize(this.buttonSize);
      removeImage.setMaximumSize(this.buttonSize);
      cancel.setMaximumSize(this.buttonSize);
      insertAbove.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            BendOptionsPopup.this.job.getBends().add(new Bend(BendOptionsPopup.this.job));

            for(int ix = BendOptionsPopup.this.job.getBends().size() - 1; ix > BendOptionsPopup.this.bendNo - 1; --ix) {
               BendOptionsPopup.this.job.getBends().set(ix, (Bend)BendOptionsPopup.this.job.getBends().get(ix - 1));
            }

            System.out.println(BendOptionsPopup.this.bendNo - 1);
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
            if (BendOptionsPopup.this.bendNo - 2 >= 0) {
               if (BendOptionsPopup.this.job.getMode().equals(Mode.ANGLE)) {
                  y.add("0.0");
                  y.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 2)).getAxisValues().get(1)).getValues().get(2));
               } else {
                  y.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 2)).getAxisValues().get(1)).getValues().get(1));
               }

               r.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 2)).getAxisValues().get(2)).getValues().get(0));
            } else {
               if (BendOptionsPopup.this.job.getMode().equals(Mode.ANGLE)) {
                  y.add("0.0");
                  y.add("");
               } else {
                  y.add("");
               }

               r.add("");
            }

            v.add(new AxisValues(((Axis)Settings.axes.get(0)).getAxisType(), ((Axis)Settings.axes.get(0)).getShortName(), x, BendOptionsPopup.this.job.getMode()));
            v.add(new AxisValues(((Axis)Settings.axes.get(1)).getAxisType(), ((Axis)Settings.axes.get(1)).getShortName(), y, BendOptionsPopup.this.job.getMode()));
            v.add(new AxisValues(((Axis)Settings.axes.get(2)).getAxisType(), ((Axis)Settings.axes.get(2)).getShortName(), r, BendOptionsPopup.this.job.getMode()));
            BendOptionsPopup.this.job.getBends().set(BendOptionsPopup.this.bendNo - 1, new Bend(BendOptionsPopup.this.job, v));

            for(int i = 0; i < BendOptionsPopup.this.job.getBends().size(); ++i) {
               ((Bend)BendOptionsPopup.this.job.getBends().get(i)).setBendNo(i + 1);
            }

            EditJobPage.existingPage.dispose();
            new EditJobPage(BendOptionsPopup.this.job);
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      insertBelow.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
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
            if (BendOptionsPopup.this.job.getMode().equals(Mode.ANGLE)) {
               y.add("0.0");
               y.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 1)).getAxisValues().get(1)).getValues().get(2));
            } else {
               y.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 1)).getAxisValues().get(1)).getValues().get(1));
            }

            r.add((String)((AxisValues)((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo - 1)).getAxisValues().get(2)).getValues().get(0));
            v.add(new AxisValues(((Axis)Settings.axes.get(0)).getAxisType(), ((Axis)Settings.axes.get(0)).getShortName(), x, BendOptionsPopup.this.job.getMode()));
            v.add(new AxisValues(((Axis)Settings.axes.get(1)).getAxisType(), ((Axis)Settings.axes.get(1)).getShortName(), y, BendOptionsPopup.this.job.getMode()));
            v.add(new AxisValues(((Axis)Settings.axes.get(2)).getAxisType(), ((Axis)Settings.axes.get(2)).getShortName(), r, BendOptionsPopup.this.job.getMode()));
            if (BendOptionsPopup.this.job.getBends().size() < BendOptionsPopup.this.bendNo + 1) {
               BendOptionsPopup.this.job.subtractBendNo();
               BendOptionsPopup.this.job.getBends().add(new Bend(BendOptionsPopup.this.job, v));
            } else {
               BendOptionsPopup.this.job.getBends().add(new Bend(BendOptionsPopup.this.job));

               int i;
               for(i = BendOptionsPopup.this.job.getBends().size() - 1; i > BendOptionsPopup.this.bendNo - 1; --i) {
                  BendOptionsPopup.this.job.getBends().set(i, (Bend)BendOptionsPopup.this.job.getBends().get(i - 1));
               }

               BendOptionsPopup.this.job.getBends().set(BendOptionsPopup.this.bendNo, new Bend(BendOptionsPopup.this.job, v));

               for(i = 0; i < BendOptionsPopup.this.job.getBends().size(); ++i) {
                  ((Bend)BendOptionsPopup.this.job.getBends().get(i)).setBendNo(i + 1);
               }
            }

            EditJobPage.existingPage.dispose();
            new EditJobPage(BendOptionsPopup.this.job);
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      deleteBend.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (BendOptionsPopup.this.job != null) {
               for(int i = BendOptionsPopup.this.bendNo; i < BendOptionsPopup.this.job.getBends().size(); ++i) {
                  ((Bend)BendOptionsPopup.this.job.getBends().get(i)).setBendNo(i);
               }

               BendOptionsPopup.this.job.getBends().remove(BendOptionsPopup.this.bendNo - 1);
               BendOptionsPopup.this.job.subtractBendNo();
               ((JFrame)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
               EditJobPage.existingPage.dispose();
               new EditJobPage(BendOptionsPopup.this.job);
               BendOptionsPopup.this.bendOptionsFrame.dispose();
               BendOptionsPopup.exists = false;
            } else {
               Settings.log.log(Level.SEVERE, "Cant delete bend, job null");
               BendOptionsPopup.this.bendOptionsFrame.dispose();
               BendOptionsPopup.exists = false;
            }

         }
      });
      addImage.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new ImageSelectorPage(BendOptionsPopup.this.job, BendOptionsPopup.this.bend);
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      removeImage.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ((Bend)BendOptionsPopup.this.job.getBends().get(BendOptionsPopup.this.bendNo)).setBendImage("");
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            BendOptionsPopup.this.bendOptionsFrame.dispose();
            BendOptionsPopup.exists = false;
         }
      });
      this.bendOptionsFrame.getContentPane().add(insertAbove);
      this.bendOptionsFrame.getContentPane().add(insertBelow);
      this.bendOptionsFrame.getContentPane().add(deleteBend);
      this.bendOptionsFrame.getContentPane().add(addImage);
      this.bendOptionsFrame.getContentPane().add(removeImage);
      this.bendOptionsFrame.getContentPane().add(cancel);
      this.bendOptionsFrame.setVisible(true);
   }

   public void saveJob() {
      if (this.job.getLocation() == Location.LOCAL) {
         this.job.saveJob(Location.LOCAL);
      } else if (Settings.selectedUSB != null) {
         this.job.saveJob(Location.USB);
      }

   }
}
