package com.automec.display.components;

import com.automec.objects.enums.Units;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javax.swing.JLabel;

public class DataDisplayLabel extends JLabel {
   private static final long serialVersionUID = 5749205740566981383L;
   private double number;

   public DataDisplayLabel(double number, Units units, boolean angle) {
      super("");
      this.setNumber(number, units, angle);
   }

   public DataDisplayLabel(String number, Units units, boolean angle) {
      super("");
      if (number.equals("")) {
         this.setText("");
      } else {
         this.number = Double.valueOf(number);
         this.setNumber(this.number, units, angle);
      }
   }

   public void setNumber(double number, Units units, boolean angle) {
      if (angle) {
         this.setText(String.format("%.1f°", number));
      } else if (units == Units.INCHES) {
         this.setText(String.format("%.3f", number));
      } else {
         BigDecimal mm = BigDecimal.valueOf(25.4D);
         MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);
         this.setText(String.format("%.2f", BigDecimal.valueOf(number).multiply(mm, mc)));
      }

   }
}
