package com.automec.objects;

import com.automec.objects.enums.AxisType;

public class IncorrectAxisException extends Exception {
   private static final long serialVersionUID = 6275823643238534296L;
   private AxisType current;
   private AxisType expected;

   public IncorrectAxisException(AxisType current, AxisType expected) {
      this.current = current;
      this.expected = expected;
   }

   public AxisType getCurrent() {
      return this.current;
   }

   public AxisType getExpected() {
      return this.expected;
   }
}
