package com.automec.display.pages;

import com.automec.Listener;
import com.automec.Settings;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.Axis;

class waitForMove extends Thread {
   double xTar;
   double rTar;
   int count = 0;
   static int attempt = 0;
   boolean br = false;
   int bend = -1;

   public waitForMove(double x, double r, int bend) {
      if (x < 0.0D) {
         this.xTar = 0.0D;
      } else if (x > ((Axis)Settings.axes.get(0)).getAxisLength() - 0.1D) {
         this.xTar = ((Axis)Settings.axes.get(0)).getAxisLength() - 0.1D;
      } else {
         this.xTar = x;
      }

      if (r < 0.0D) {
         this.rTar = 0.0D;
      } else if (r > ((Axis)Settings.axes.get(2)).getAxisLength() - 0.1D) {
         this.rTar = ((Axis)Settings.axes.get(2)).getAxisLength() - 0.1D;
      } else {
         this.rTar = r;
      }

      this.bend = bend;
   }

   public void run() {
      Settings.log.fine("Starting waitForMove: bend " + this.bend);
      double xdz = 0.01D;
      double rdz = 0.01D;
      if (((Axis)Settings.axes.get(0)).getDeadzone() > 0.01D) {
         xdz = ((Axis)Settings.axes.get(0)).getDeadzone();
      }

      if (((Axis)Settings.axes.get(2)).getDeadzone() > 0.01D) {
         rdz = ((Axis)Settings.axes.get(2)).getDeadzone();
      }

      while(Math.abs(this.xTar - ((Axis)Settings.axes.get(0)).getPositionInches()) > xdz && ((Axis)Settings.axes.get(0)).getEnabled() || Math.abs(this.rTar - ((Axis)Settings.axes.get(2)).getPositionInches()) > rdz && ((Axis)Settings.axes.get(2)).getEnabled()) {
         try {
            Thread.sleep(100L);
         } catch (InterruptedException var6) {
            var6.printStackTrace();
         }

         if (!Settings.sxmove && !Settings.srmove && !Listener.delayGR && !Listener.retractDelay) {
            ++this.count;
         }

         if (this.count > 50) {
            if (attempt > 2) {
               new NotificationPage("Notice", "Gauge never achieved position, did it move?");
               Settings.log.info("X: " + this.xTar + " : " + ((Axis)Settings.axes.get(0)).getPositionInches());
               Settings.log.info("R: " + this.rTar + " : " + ((Axis)Settings.axes.get(2)).getPositionInches());
               Settings.printRecording();
               this.br = true;
               break;
            }

            ++attempt;
            this.count = 0;
         }

         if (this.isInterrupted()) {
            this.br = true;
            break;
         }
      }

      if (this.br) {
         RunJobPage.disableAuto();
         Settings.log.fine("Leaving waitForMove: unsuccessful, bend " + this.bend);
         attempt = 0;
      } else {
         Settings.log.fine("Leaving waitForMove: successful, bend " + this.bend);
         RunJobPage.advanceButton.setEnabled(true);
         attempt = 0;
      }
   }
}
