package com.automec.objects;

import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.Mode;
import java.math.BigDecimal;
import java.util.ArrayList;

public class AxisValues {
   private AxisType axis;
   private String axisName;
   private ArrayList<String> values;
   private ArrayList<BigDecimal> decimals;
   private Mode mode;

   public AxisValues(AxisType axis, String axisName, ArrayList<String> values, Mode mode) {
      this.mode = Mode.DEPTH;
      this.axis = axis;
      this.axisName = axisName;
      this.values = values;
      this.mode = mode;
      this.decimals = new ArrayList();
      if (axis == AxisType.BACKGAUGE) {
         this.decimals.add(0, BigDecimal.valueOf(0.0D));
         this.decimals.add(BigDecimal.valueOf(0.0D));
         this.decimals.add(BigDecimal.valueOf(0.0D));
         this.decimals.add(3, BigDecimal.valueOf(0.0D));
         this.decimals.add(4, BigDecimal.valueOf(0.0D));
      } else if (axis == AxisType.RAM) {
         if (mode.equals(Mode.ANGLE)) {
            this.decimals.add(0, BigDecimal.valueOf(0.0D));
            this.decimals.add(1, BigDecimal.valueOf(0.0D));
            this.decimals.add(2, BigDecimal.valueOf(0.0D));
         } else {
            this.decimals.add(0, BigDecimal.valueOf(0.0D));
            this.decimals.add(1, BigDecimal.valueOf(0.0D));
         }
      } else {
         this.decimals.add(0, BigDecimal.valueOf(0.0D));
      }

   }

   public AxisValues(AxisType axis, String axisName, Mode mode) {
      this.mode = Mode.DEPTH;
      this.axis = axis;
      this.axisName = axisName;
      this.mode = mode;
      this.values = new ArrayList();
      this.decimals = new ArrayList();
      if (axis == AxisType.BACKGAUGE) {
         this.values.add("");
         this.values.add("  ");
         this.values.add("");
         this.values.add("");
         this.values.add("");
         this.decimals.add(0, BigDecimal.valueOf(0.0D));
         this.decimals.add(BigDecimal.valueOf(0.0D));
         this.decimals.add(BigDecimal.valueOf(0.0D));
         this.decimals.add(3, BigDecimal.valueOf(0.0D));
         this.decimals.add(4, BigDecimal.valueOf(0.0D));
      } else if (axis == AxisType.RAM) {
         if (mode.equals(Mode.ANGLE)) {
            this.values.add("");
            this.values.add("0.0");
            this.values.add("");
            this.decimals.add(0, BigDecimal.valueOf(0.0D));
            this.decimals.add(1, BigDecimal.valueOf(0.0D));
            this.decimals.add(2, BigDecimal.valueOf(0.0D));
         } else {
            this.values.add("");
            this.values.add("");
            this.decimals.add(0, BigDecimal.valueOf(0.0D));
            this.decimals.add(1, BigDecimal.valueOf(0.0D));
         }
      } else {
         this.values.add("");
         this.decimals.add(0, BigDecimal.valueOf(0.0D));
      }

   }

   public AxisType getAxisType() {
      return this.axis;
   }

   public ArrayList<String> getValues() {
      return this.values;
   }

   public ArrayList<BigDecimal> getDecimals() {
      return this.decimals;
   }
}
