package com.automec.display.components;

import com.automec.Settings;
import com.automec.display.pages.EditJobPage;
import com.automec.display.pages.RunJobPage;
import com.automec.objects.enums.Units;
import java.math.BigDecimal;

public class JTextFieldCustom extends DataInputField {
   private static final long serialVersionUID = -949019937150490637L;
   private int index1;
   private int index2;
   private int bendNo;
   private String text = "";
   public String angleFormat = "";

   public JTextFieldCustom(String text, int index1, int index2, int bendNo, double min, double max) {
      super(text, min, max);
      this.text = text;
      this.index1 = index1;
      this.index2 = index2;
      this.bendNo = bendNo;
      this.angleFormat = "%.1f";
   }

   public int getIndex1() {
      return this.index1;
   }

   public int getIndex2() {
      return this.index2;
   }

   public int getBendNo() {
      return this.bendNo;
   }

   public void setAngle(boolean angle) {
      super.setAngle(angle);
      if (!this.text.equals("")) {
         this.setText(String.format("%.1f", this.number));
      }

   }

   public void setAngleFormat(String format) {
      this.angleFormat = format;
   }

   public String getAngleFormat() {
      return this.angleFormat;
   }

   public void setNumber(String number) {
      Units units;
      if (Settings.activeFrame instanceof EditJobPage) {
         units = EditJobPage.existingPage.getUnits();
      } else if (Settings.activeFrame instanceof RunJobPage) {
         units = RunJobPage.job.getUnits();
      } else {
         units = Settings.units;
      }

      if (number.equals("")) {
         this.setText("");
      } else {
         this.number = Double.valueOf(number);
         if (this.isAngle()) {
            if (this.angleFormat.equals("%d")) {
               int i = (int)Double.valueOf(number);
               this.setText(String.format(this.angleFormat, i));
            } else {
               this.setText(String.format(this.angleFormat, this.number));
            }
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

      }
   }
}
