package com.automec;

import com.automec.objects.Axis;
import java.util.logging.Level;

public class DriveToPosition implements Runnable {
   double xPosition;
   double rPosition;
   double top;
   double slow;
   double metal;
   double aw;
   double bottom;

   public DriveToPosition(double xPosition, double rPosition, double top, double slow, double metal, double aw, double bottom) {
      this.xPosition = xPosition;
      this.rPosition = rPosition;
      this.top = top;
      this.slow = slow;
      this.metal = metal;
      this.aw = aw;
      this.bottom = bottom;
   }

   public void run() {
      if (((Axis)Settings.axes.get(2)).getEnabled() && !(Math.abs(((Axis)Settings.axes.get(2)).getPositionInches() - this.rPosition) <= ((Axis)Settings.axes.get(2)).getDeadzone())) {
         Communications.driveToPosition((Axis)Settings.axes.get(2), this.rPosition);
         double rdz = 0.01D;
         if (((Axis)Settings.axes.get(2)).getDeadzone() > 0.01D) {
            rdz = ((Axis)Settings.axes.get(2)).getDeadzone();
         }

         while(Math.abs(((Axis)Settings.axes.get(2)).getPositionInches() - this.rPosition) > rdz) {
            try {
               Thread.sleep(100L);
            } catch (Exception var5) {
               Settings.log.log(Level.WARNING, "Drive to position thread sleep encountered an error");
            }
         }
      }

      try {
         Thread.sleep(250L);
      } catch (Exception var4) {
         Settings.log.log(Level.WARNING, "Drive to position thread sleep encountered an error 2");
      }

      Communications.xyrCombinedCommand(this.xPosition, ((Axis)Settings.axes.get(2)).getPositionInches(), this.top, this.slow, this.metal, this.aw, this.bottom);
   }
}
