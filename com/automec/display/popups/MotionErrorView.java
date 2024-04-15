package com.automec.display.popups;

import com.automec.Recording;
import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.components.DisplayComponents;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MotionErrorView {
   private JFrame notificationFrame;
   private Recording rec;
   private JPanel center;
   private int start = 0;
   private int end = 0;

   public MotionErrorView(String title) {
      this.notificationFrame = new JFrame(title);
      this.initialize(title, (JPanel)null, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MotionErrorView.this.notificationFrame.dispose();
            Settings.log.finest("notification page disposed");
         }
      });
   }

   public MotionErrorView(String title, JPanel panel, ActionListener action) {
      this.notificationFrame = new JFrame(title);
      this.initialize(title, panel, action);
   }

   private void initialize(String title, JPanel panel, ActionListener action) {
      this.notificationFrame.setBounds(100, 84, 600, 600);
      this.notificationFrame.setDefaultCloseOperation(3);
      this.notificationFrame.setUndecorated(true);
      this.notificationFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      JLabel titleLabel = new JLabel(title);
      titleLabel.setHorizontalAlignment(0);
      titleLabel.setFont(DisplayComponents.pageTitleFont);
      this.notificationFrame.getContentPane().add(titleLabel, "North");
      titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      File tmp = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrorgraph.log");

      try {
         this.rec = (Recording)(new Gson()).fromJson((String)Files.readAllLines(tmp.toPath()).get(0), Recording.class);
      } catch (Exception var15) {
         var15.printStackTrace();
         this.rec = new Recording();
      }

      this.end = this.rec.getStop().size() - 1;
      this.center = new JPanel();
      System.out.println((new Gson()).toJson(this.rec));
      XYSeriesCollection data = this.getDataset(this.getStartIndex((Long)this.rec.getStart().get(this.rec.getStart().size() - 1)), this.getStopIndex((Long)this.rec.getStop().get(this.rec.getStop().size() - 1)));
      JFreeChart chart = ChartFactory.createScatterPlot("pos vs expected", "x-axis", "y-axis", data);
      ChartPanel center1 = new ChartPanel(chart);
      center1.setSize(new Dimension(300, 300));
      this.notificationFrame.getContentPane().add(this.center, "Center");
      this.center.add(center1);
      JPanel bottom = new JPanel();
      JButton startplus = new JButton("Start++");
      JButton startminus = new JButton("Start--");
      JButton endplus = new JButton("end++");
      JButton endminus = new JButton("end--");
      startplus.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MotionErrorView var10000 = MotionErrorView.this;
            var10000.start = var10000.start + 1;
            if (MotionErrorView.this.start >= MotionErrorView.this.rec.getStart().size()) {
               MotionErrorView.this.start = MotionErrorView.this.rec.getStart().size() - 1;
            }

            MotionErrorView.this.refreshChart(MotionErrorView.this.getDataset(MotionErrorView.this.getStartIndex((Long)MotionErrorView.this.rec.getStart().get(MotionErrorView.this.start)), MotionErrorView.this.getStopIndex((Long)MotionErrorView.this.rec.getStop().get(MotionErrorView.this.end))));
         }
      });
      startminus.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MotionErrorView var10000 = MotionErrorView.this;
            var10000.start = var10000.start - 1;
            if (MotionErrorView.this.start < 0) {
               MotionErrorView.this.start = 0;
            }

            MotionErrorView.this.refreshChart(MotionErrorView.this.getDataset(MotionErrorView.this.getStartIndex((Long)MotionErrorView.this.rec.getStart().get(MotionErrorView.this.start)), MotionErrorView.this.getStopIndex((Long)MotionErrorView.this.rec.getStop().get(MotionErrorView.this.end))));
         }
      });
      endplus.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MotionErrorView var10000 = MotionErrorView.this;
            var10000.end = var10000.end + 1;
            if (MotionErrorView.this.end >= MotionErrorView.this.rec.getStop().size()) {
               MotionErrorView.this.end = MotionErrorView.this.rec.getStop().size() - 1;
            }

            MotionErrorView.this.refreshChart(MotionErrorView.this.getDataset(MotionErrorView.this.getStartIndex((Long)MotionErrorView.this.rec.getStart().get(MotionErrorView.this.start)), MotionErrorView.this.getStopIndex((Long)MotionErrorView.this.rec.getStop().get(MotionErrorView.this.end))));
         }
      });
      endminus.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            MotionErrorView var10000 = MotionErrorView.this;
            var10000.end = var10000.end - 1;
            if (MotionErrorView.this.end < 0) {
               MotionErrorView.this.end = 0;
            }

            MotionErrorView.this.refreshChart(MotionErrorView.this.getDataset(MotionErrorView.this.getStartIndex((Long)MotionErrorView.this.rec.getStart().get(MotionErrorView.this.start)), MotionErrorView.this.getStopIndex((Long)MotionErrorView.this.rec.getStop().get(MotionErrorView.this.end))));
         }
      });
      JButton closeButton = new JButton("Close");
      closeButton.setFont(DisplayComponents.buttonFont);
      bottom.add(startplus);
      bottom.add(startminus);
      bottom.add(endplus);
      bottom.add(endminus);
      bottom.add(closeButton);
      this.notificationFrame.getContentPane().add(bottom, "South");
      closeButton.addActionListener(action);
      closeButton.setPreferredSize(DisplayComponents.minimumButtonSize);
      closeButton.setBackground(Color.GRAY);
      this.notificationFrame.setVisible(true);
      Settings.log.finest("notification initialized");
   }

   public void dispose() {
      this.notificationFrame.dispose();
   }

   public void refreshChart(XYSeriesCollection data) {
      this.center.removeAll();
      this.center.revalidate();
      JFreeChart chart = ChartFactory.createScatterPlot("pos vs expected", "x-axis", "y-axis", data);
      ChartPanel panel1 = new ChartPanel(chart);
      panel1.setSize(new Dimension(300, 300));
      this.center.add(panel1, "Center");
   }

   public XYSeriesCollection getDataset(int start, int end) {
      XYSeriesCollection data = new XYSeriesCollection();
      XYSeries position = new XYSeries("position");
      XYSeries position1 = new XYSeries("poserror");
      XYSeries position2 = new XYSeries("negerror");
      XYSeries expectedPos = new XYSeries("Expected Position");
      File tmp = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrorgraph.log");

      Recording rec;
      try {
         rec = (Recording)(new Gson()).fromJson((String)Files.readAllLines(tmp.toPath()).get(0), Recording.class);
      } catch (Exception var11) {
         var11.printStackTrace();
         rec = new Recording();
      }

      for(int i = start; i < end + 1; ++i) {
         position.add((double)(Long)rec.getTimes().get(i), (double)(Integer)rec.getPositions().get(i));
         position1.add((double)(Long)rec.getTimes().get(i), (double)((Integer)rec.getExpected().get(i) + 4000));
         position2.add((double)(Long)rec.getTimes().get(i), (double)((Integer)rec.getExpected().get(i) - 4000));
         expectedPos.add((double)(Long)rec.getTimes().get(i), (double)(Integer)rec.getExpected().get(i));
      }

      data.addSeries(position);
      data.addSeries(expectedPos);
      data.addSeries(position1);
      data.addSeries(position2);
      return data;
   }

   public int getStartIndex(long timestamp) {
      if (this.rec.getTimes().contains(timestamp)) {
         return this.rec.getTimes().indexOf(timestamp);
      } else {
         for(int j = 0; j < 100; ++j) {
            if (this.rec.getTimes().contains(timestamp + (long)j)) {
               return this.rec.getTimes().indexOf(timestamp + (long)j);
            }

            if (this.rec.getTimes().contains(timestamp - (long)j)) {
               return this.rec.getTimes().indexOf(timestamp - (long)j);
            }
         }

         return 0;
      }
   }

   public int getStopIndex(long timestamp) {
      if (this.rec.getTimes().contains(timestamp)) {
         return this.rec.getTimes().indexOf(timestamp);
      } else {
         for(int j = 0; j < 100; ++j) {
            if (this.rec.getTimes().contains(timestamp + (long)j)) {
               return this.rec.getTimes().indexOf(timestamp + (long)j);
            }

            if (this.rec.getTimes().contains(timestamp - (long)j)) {
               return this.rec.getTimes().indexOf(timestamp - (long)j);
            }
         }

         return 0;
      }
   }
}
