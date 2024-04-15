package com.automec.display.components;

import com.automec.Settings;
import com.automec.display.pages.EditJobPage;
import com.automec.display.pages.RunJobPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.Units;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AxisPanel {
   private JPanel axisPanel;
   private JLabel axisValueLabel;

   public AxisPanel(Axis axis) {
      Font font;
      if (Settings.axes.size() > 4) {
         font = DisplayComponents.displayValueMedium;
      } else {
         font = DisplayComponents.displayValueLarge;
      }

      this.axisPanel = new JPanel();
      this.axisPanel.setPreferredSize(new Dimension(550, 165));
      this.axisPanel.setMinimumSize(new Dimension(550, 165));
      this.axisPanel.setMaximumSize(new Dimension(550, 165));
      GridBagLayout gbl_panel = new GridBagLayout();
      gbl_panel.columnWidths = new int[2];
      gbl_panel.rowHeights = new int[2];
      gbl_panel.columnWeights = new double[]{1.0D, 5.0D};
      gbl_panel.rowWeights = new double[]{1.0D, 5.0D};
      this.axisPanel.setLayout(gbl_panel);
      JLabel axisLabel = new JLabel(axis.getShortName());
      axisLabel.setFont(font);
      GridBagConstraints axisLabelGBC = new GridBagConstraints();
      axisLabelGBC.gridx = 0;
      axisLabelGBC.gridy = 0;
      axisLabelGBC.fill = 1;
      this.axisPanel.add(axisLabel, axisLabelGBC);
      this.axisValueLabel = new JLabel("0.000");
      this.axisValueLabel.setFont(font);
      GridBagConstraints axisValueLabelGBC = new GridBagConstraints();
      axisValueLabelGBC.gridx = 1;
      axisValueLabelGBC.gridy = 0;
      axisValueLabelGBC.fill = 1;
      this.axisValueLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.axisPanel.add(this.axisValueLabel, axisValueLabelGBC);
      this.axisValueLabel.setOpaque(true);
      this.axisPanel.setBorder(new EmptyBorder(0, 0, 0, 30));
   }

   public void setText(double number) {
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.displayUnits;
      } else {
         units = Settings.units;
      }

      if (units.equals(Units.INCHES)) {
         this.axisValueLabel.setText(String.format("%.3f", number));
      } else {
         BigDecimal v = BigDecimal.valueOf(number);
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
         this.axisValueLabel.setText(String.format("%.2f", v.multiply(mm, mc).doubleValue()));
      }

   }

   public void setText(double number, boolean angle) {
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.displayUnits;
      } else {
         units = Settings.units;
      }

      if (angle) {
         this.axisValueLabel.setText(String.format("%.1f°", number));
      } else if (units.equals(Units.INCHES)) {
         this.axisValueLabel.setText(String.format("%.3f", number));
      } else {
         BigDecimal v = BigDecimal.valueOf(number);
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
         this.axisValueLabel.setText(String.format("%.2f", v.multiply(mm, mc).doubleValue()));
      }

   }

   public void setTextAngle(double number) {
      this.axisValueLabel.setText(String.format("%.1f°", number));
   }

   public JPanel getAxisPanel() {
      return this.axisPanel;
   }

   public JLabel getAxisValueLabel() {
      return this.axisValueLabel;
   }
}
