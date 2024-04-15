package com.automec.display.pages;

import com.automec.Communications;
import com.automec.Settings;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.Axis;
import java.util.logging.Level;

class calibrateRoutine extends Thread {
   int counts;
   double outPosition;
   double inPosition;

   public calibrateRoutine(int counts, double outPosition, double inPosition) {
      this.counts = counts;
      this.outPosition = outPosition;
      this.inPosition = inPosition;
      if (outPosition > ((Axis)Settings.axes.get(0)).getAxisLength() - 1.0D) {
         outPosition = ((Axis)Settings.axes.get(0)).getAxisLength() - 1.0D;
      }

      if (inPosition < ((Axis)Settings.axes.get(0)).getInLimit()) {
         inPosition = ((Axis)Settings.axes.get(0)).getInLimit();
      }

   }

   public void run() {
      try {
         HomePage.recordX = true;
         Settings.xStop = 0;
         int load = 90 / this.counts;
         int pass = load / 3;
         Thread.sleep(1000L);

         while(Settings.sxmove) {
            if (Settings.xMotStall) {
               return;
            }

            Thread.sleep(500L);
         }

         Thread.sleep(1000L);
         if (Settings.xMotStall) {
            this.interrupt();
         }

         Communications.driveToPositionRaw((Axis)Settings.axes.get(0), this.inPosition);

         for(int i = 0; i < this.counts; ++i) {
            NotificationPage.bar.setValue(i * load);
            Thread.sleep(1000L);

            while(Settings.sxmove && !Settings.xMotStall) {
               Thread.sleep(500L);
            }

            Settings.xStop += ((Axis)Settings.axes.get(0)).getPosition();
            Thread.sleep(1000L);
            if (Settings.xMotStall) {
               break;
            }

            Communications.driveToPositionRaw((Axis)Settings.axes.get(0), this.outPosition);
            NotificationPage.bar.setValue(i * load + pass);
            Thread.sleep(1000L);

            while(Settings.sxmove && !Settings.xMotStall) {
               Thread.sleep(500L);
            }

            Thread.sleep(1000L);
            if (Settings.xMotStall) {
               break;
            }

            Communications.driveToPositionRaw((Axis)Settings.axes.get(0), this.inPosition);
            NotificationPage.bar.setValue(i * load + pass * 2);
            Thread.sleep(1000L);

            while(Settings.sxmove) {
               Thread.sleep(500L);
            }
         }

         Settings.xStop /= this.counts;
         Settings.xStop -= (int)(((Axis)Settings.axes.get(0)).getEncoderCountPerInch() * (((Axis)Settings.axes.get(0)).getAxisLength() - this.inPosition));
         ((Axis)Settings.axes.get(0)).setStopDistance((double)Settings.xStop / ((Axis)Settings.axes.get(0)).getEncoderCountPerInch());
         HomePage.recordX = false;
         Thread.sleep(500L);
         Settings.calibrated = true;
         HomePage.calibrateButton.setEnabled(true);
         HomePage.calibrationLabel.setText("System Calibrated: calculating");
      } catch (Exception var4) {
         Settings.log.log(Level.SEVERE, var4.getMessage(), var4);
      }

   }
}
