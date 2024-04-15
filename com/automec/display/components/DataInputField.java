package com.automec.display.components;

import com.automec.Settings;
import com.automec.display.pages.EditJobPage;
import com.automec.display.pages.RunJobPage;
import com.automec.objects.enums.Units;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javax.swing.JTextField;

public class DataInputField extends JTextField {
   private static final long serialVersionUID = 3890501051046929420L;
   private double min;
   private double max;
   public double number;
   protected boolean flipped = false;
   private String format = "";
   private boolean angle = false;
   MathContext mc;

   public DataInputField(double number, double min, double max) {
      super("");
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      this.number = number;
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (units == Units.INCHES) {
         this.setText(String.format("%.3f", number));
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         this.setText(String.format("%.2f", BigDecimal.valueOf(number).multiply(mm, this.mc)));
      }

      this.min = min;
      this.max = max;
   }

   public DataInputField(double number, double min, double max, boolean flipped) {
      super("");
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      this.number = number;
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (units == Units.INCHES) {
         this.setText(String.format("%.3f", number));
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         if (flipped) {
            this.setText(String.format("%.2f", BigDecimal.valueOf(number).divide(mm, this.mc)));
         } else {
            this.setText(String.format("%.2f", BigDecimal.valueOf(number).multiply(mm, this.mc)));
         }
      }

      this.min = min;
      this.max = max;
      this.flipped = flipped;
   }

   public DataInputField(String text, double min, double max) {
      super("");
      this.mc = new MathContext(10, RoundingMode.HALF_EVEN);
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (text.equals("")) {
         this.number = 0.0D;
      } else {
         this.number = Double.parseDouble(text);
      }

      if (this.isAngle()) {
         this.setText(String.format("%.1f", this.number));
      } else if (units == Units.INCHES) {
         this.setText(String.format("%.3f", this.number));
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         if (this.flipped) {
            this.setText(String.format("%.2f", BigDecimal.valueOf(this.number).divide(mm, this.mc)));
         } else {
            this.setText(String.format("%.2f", BigDecimal.valueOf(this.number).multiply(mm, this.mc)));
         }
      }

      if (text.equals("")) {
         this.setText("");
      }

      this.min = min;
      this.max = max;
   }

   public void setAngle(boolean angle) {
      this.angle = angle;
   }

   public boolean getAngle() {
      return this.isAngle();
   }

   public void setMin(double min) {
      this.min = min;
   }

   public void setMax(double max) {
      this.max = max;
   }

   public void setMinMax(double min, double max) {
      this.setMin(min);
      this.setMax(max);
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public String getFormat() {
      return this.format;
   }

   public void setNumber(double number) {
      this.number = number;
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (this.isAngle()) {
         this.setText(String.format("%.1f", number));
      } else if (units == Units.INCHES) {
         this.setText(String.format("%.3f", number));
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         if (this.flipped) {
            this.setText(String.format("%.2f", BigDecimal.valueOf(number).divide(mm, this.mc)));
         } else {
            this.setText(String.format("%.2f", BigDecimal.valueOf(number).multiply(mm, this.mc)));
         }
      }

      System.out.println(this.getText() + " " + number);
   }

   public double getMin() {
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (this.isAngle()) {
         return this.min;
      } else if (units == Units.INCHES) {
         return this.min;
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         return this.flipped ? BigDecimal.valueOf(this.min).divide(mm, this.mc).doubleValue() : BigDecimal.valueOf(this.min).multiply(mm, this.mc).doubleValue();
      }
   }

   public double getMax() {
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (this.isAngle()) {
         return this.max;
      } else if (units == Units.INCHES) {
         return this.max;
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         return this.flipped ? BigDecimal.valueOf(this.max).divide(mm, this.mc).doubleValue() : BigDecimal.valueOf(this.max).multiply(mm, this.mc).doubleValue();
      }
   }

   public boolean isAngle() {
      return this.angle;
   }
}
