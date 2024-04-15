package com.automec;

import com.automec.display.popups.NotificationPage;
import com.automec.display.popups.Popups;
import com.automec.objects.Axis;
import com.automec.objects.PositionStamp;
import com.automec.objects.enums.AxisLimitSwitch;
import com.automec.objects.enums.AxisType;
import com.google.gson.Gson;
import io.dvlopt.linux.i2c.I2CBuffer;
import io.dvlopt.linux.i2c.I2CBus;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

public class Communications {
   public static I2CBus bus;
   public static boolean printNextStatus = false;
   public static I2CBuffer ack;
   public static boolean goingUp = false;
   public static I2CBuffer statusResponse;
   public static int xUnplugged = 0;
   public static int yUnplugged = 0;
   public static int rUnplugged = 0;
   public static int sxmove = 0;
   public static int srmove = 0;
   public static int xMotStall = 0;
   public static int rMotStall = 0;
   public static int xPower = 0;
   public static int rPower = 0;
   public static int macPower = 0;
   public static int autoIndex = 0;
   public static int bottomCount = 0;
   private static int rpiloop = 0;
   private static int eStopCount = 0;
   private static long lastTime = 0L;
   private static boolean skipped = false;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType;

   public static void sendCommand(int command, int priority) {
      int[] tmp = new int[0];
      sendCommand(command, tmp, priority);
   }

   static void sendCommand(int command, int[] parameters, int priority) {
      boolean correct = false;
      ArrayList<Integer> msgData = new ArrayList();
      int temp;
      switch(command) {
      case 15:
         msgData.add(4);
         msgData.add(15);
         correct = true;
         break;
      case 53:
         if (parameters.length == 7) {
            msgData.add(10);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            correct = true;
         }
         break;
      case 54:
         if (parameters.length == 11) {
            msgData.add(15);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            msgData.add(parameters[7]);
            msgData.add(parameters[8]);
            msgData.add(parameters[9]);
            msgData.add(parameters[10]);
            correct = true;
         }
         break;
      case 55:
         if (parameters.length == 43) {
            msgData.add(47);
            msgData.add(command);

            for(temp = 0; temp < parameters.length; ++temp) {
               msgData.add(parameters[temp]);
            }

            correct = true;
         }
         break;
      case 83:
         if (parameters.length == 0) {
            msgData.add(4);
            msgData.add(command);
            correct = true;
         }
         break;
      case 87:
         if (parameters.length == 0) {
            msgData.add(4);
            msgData.add(command);
            correct = true;
         }
         break;
      case 88:
         if (parameters.length == 8) {
            msgData.add(12);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            msgData.add(parameters[7]);
            correct = true;
         }
         break;
      case 89:
         if (parameters.length == 8) {
            msgData.add(12);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            msgData.add(parameters[7]);
            correct = true;
         }
         break;
      case 90:
         if (parameters.length == 7) {
            msgData.add(10);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            correct = true;
         }
         break;
      case 96:
         if (parameters.length == 0) {
            msgData.add(4);
            msgData.add(96);
            correct = true;
         }
         break;
      case 97:
         if (parameters.length == 4) {
            msgData.add(6);
            msgData.add(97);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            correct = true;
         }
         break;
      case 98:
         if (parameters.length == 7) {
            msgData.add(10);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            correct = true;
         }
         break;
      case 103:
         if (parameters.length == 5) {
            msgData.add(9);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            correct = true;
         }
         break;
      case 165:
         System.out.println("Download software not implemented yet");
         break;
      case 167:
         if (parameters.length == 7) {
            msgData.add(10);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            correct = true;
         }
         break;
      case 195:
         if (parameters.length == 20) {
            msgData.add(24);
            msgData.add(command);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            msgData.add(parameters[4]);
            msgData.add(parameters[5]);
            msgData.add(parameters[6]);
            msgData.add(parameters[7]);
            msgData.add(parameters[8]);
            msgData.add(parameters[9]);
            msgData.add(parameters[10]);
            msgData.add(parameters[11]);
            msgData.add(parameters[12]);
            msgData.add(parameters[13]);
            msgData.add(parameters[14]);
            msgData.add(parameters[15]);
            msgData.add(parameters[16]);
            msgData.add(parameters[17]);
            msgData.add(parameters[18]);
            msgData.add(parameters[19]);
            correct = true;
         }
         break;
      case 255:
         if (parameters.length == 4) {
            msgData.add(6);
            msgData.add(255);
            msgData.add(parameters[0]);
            msgData.add(parameters[1]);
            msgData.add(parameters[2]);
            msgData.add(parameters[3]);
            correct = true;
         }
         break;
      default:
         Settings.log.log(Level.WARNING, "Command not Recognised");
      }

      if (correct && SystemCommands.getOS().equals("Linux")) {
         temp = SystemCommands.getChecksum(msgData);
         I2CBuffer buffer = new I2CBuffer((Integer)msgData.get(0));

         for(int i = 0; i < msgData.size(); ++i) {
            buffer.set(i, (Integer)msgData.get(i));
         }

         buffer.set((Integer)msgData.get(0) - 2, (temp & '\uff00') >>> 8);
         buffer.set((Integer)msgData.get(0) - 1, temp & 255);
         new QueuedCommand(buffer, priority);
      } else {
         Settings.log.warning("Somting went wong, prolly perameters");
      }

   }

   public static void restartI2C() {
      try {
         bus.close();
         Thread.sleep(5000L);
         initI2C();
      } catch (Exception var1) {
      }

   }

   public static void initI2C() {
      if (SystemCommands.getOS().equals("Linux")) {
         try {
            String output = "";
            Process p = Runtime.getRuntime().exec("i2cdetect -l");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = null;

            while((s = stdInput.readLine()) != null) {
               if (s.contains("i2c-mcp2221")) {
                  output = s;
               }
            }

            if (output.equals("")) {
               new NotificationPage("WARNING", "I2C driver or device not found");
               throw new Exception("No I2C Devices Detected");
            }

            String[] words = output.split("\t");
            String i2c = words[0];
            bus = new I2CBus("/dev/" + i2c);
            bus.selectSlave(85);
            ack = (new I2CBuffer(1)).set(0, 6);
            Listener.sendStatus = new UnQueuedCommand((new I2CBuffer(4)).set(0, 4).set(1, 83).set(2, 255).set(3, 169), 4);

            try {
               Listener.sendCommands.start();
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         } catch (Exception var8) {
            Settings.log.log(Level.SEVERE, "Communications", var8);
         }
      } else {
         Settings.log.warning("I2C not supported on windows yet, sorry!");
      }

   }

   public static void sendStatus() {
      sendCommand(83, 2);
   }

   public static void updateStatus() {
      try {
         if (printNextStatus) {
            ArrayList<Integer> tmp = new ArrayList();
            int i = 0;

            while(true) {
               if (i >= statusResponse.length) {
                  System.out.println((new Gson()).toJson(tmp));
                  printNextStatus = false;
                  break;
               }

               tmp.add(statusResponse.get(i));
               ++i;
            }
         }

         if (SystemCommands.isRPi()) {
            ++rpiloop;
            long now = System.nanoTime();
            long t = now - lastTime;
            if (t < 60000000L || rpiloop == 25) {
               System.out.println("Loop time: " + t);
               rpiloop = 0;
            }

            lastTime = now;
         }

         if (statusResponse.get(40) != 2 || statusResponse.get(41) < 32) {
            Settings.log.fine("Bad Packet, wrong mac version");
            return;
         }

         ((Axis)Settings.axes.get(2)).setPosition(ByteBuffer.wrap(new byte[]{(byte)statusResponse.get(1), (byte)statusResponse.get(2), (byte)statusResponse.get(3), (byte)statusResponse.get(4)}).getInt());
         ((Axis)Settings.axes.get(2)).setDirection(statusResponse.get(5));
         ((Axis)Settings.axes.get(2)).setSpeed(statusResponse.get(6));
         ((Axis)Settings.axes.get(2)).setLimitSwitch(statusResponse.get(7));
         ((Axis)Settings.axes.get(0)).setPosition(ByteBuffer.wrap(new byte[]{(byte)statusResponse.get(8), (byte)statusResponse.get(9), (byte)statusResponse.get(10), (byte)statusResponse.get(11)}).getInt());
         ((Axis)Settings.axes.get(0)).setDirection(statusResponse.get(12));
         ((Axis)Settings.axes.get(0)).setSpeed(statusResponse.get(13));
         ((Axis)Settings.axes.get(0)).setLimitSwitch(statusResponse.get(14));
         ((Axis)Settings.axes.get(0)).setAlarm(statusResponse.get(15));
         int yPos = ByteBuffer.wrap(new byte[]{(byte)statusResponse.get(16), (byte)statusResponse.get(17), (byte)statusResponse.get(18), (byte)statusResponse.get(19)}).getInt();
         if (skipped) {
            ((Axis)Settings.axes.get(1)).setPosition(yPos);
            skipped = false;
         } else if (yPos == -1) {
            Settings.log.info("Y axis -1 read, ignoring");
            skipped = true;
         } else {
            ((Axis)Settings.axes.get(1)).setPosition(yPos);
            skipped = false;
         }

         ((Axis)Settings.axes.get(1)).setSlowSpeedSOV(statusResponse.get(20));
         ((Axis)Settings.axes.get(1)).setAntiWhipSOV(statusResponse.get(21));
         ((Axis)Settings.axes.get(1)).setBottomStopSOV(statusResponse.get(23));
         if (statusResponse.get(23) == 0) {
            ++bottomCount;
            Settings.bottomTouched = true;
         } else {
            bottomCount = 0;
         }

         ((Axis)Settings.axes.get(1)).setTopStopSOV(statusResponse.get(22));
         ((Axis)Settings.axes.get(1)).setBottomLimitSwitch(statusResponse.get(24));
         ((Axis)Settings.axes.get(1)).setTopLimitSwitch(statusResponse.get(25));
         if (Settings.getBottomTouchedSwitch() == AxisLimitSwitch.ON && statusResponse.get(35) == 0) {
            goingUp = true;
         } else {
            goingUp = false;
         }

         if (Settings.getAutoIndexSwitch() == AxisLimitSwitch.ON) {
            if (statusResponse.get(26) == 0) {
               ++autoIndex;
               if (autoIndex > 4) {
                  Settings.setAutoIndexSwitch(0);
                  autoIndex = 0;
               }
            } else {
               autoIndex = 0;
            }
         } else if (statusResponse.get(26) == 1) {
            ++autoIndex;
            if (autoIndex > 4) {
               Settings.setAutoIndexSwitch(1);
               autoIndex = 0;
            }
         } else {
            autoIndex = 0;
         }

         Settings.setBottomTouchedSwitch(statusResponse.get(35));
         Settings.MACREV = statusResponse.get(40) + "." + statusResponse.get(41);
         Settings.errorByte1 = (byte)statusResponse.get(42);
         Settings.seteStop(Settings.errorByte1 & 2);
         Settings.setMacPowerCycled(Settings.errorByte1 & 64);
         if ((Settings.errorByte1 & 64) == 64) {
            Popups.powerCycled();
            Settings.log.warning("mac power cycle error");
         } else {
            macPower = 0;
         }

         if ((Settings.errorByte1 & 2) == 2) {
            ++eStopCount;
            if (eStopCount > 2) {
               Popups.eStop();
               Settings.log.warning("e-stop pressed");
            }
         } else {
            eStopCount = 0;
         }

         if ((Settings.errorByte1 & 1) == 1 && ((Axis)Settings.axes.get(2)).getEnabled()) {
            ++rUnplugged;
            if (rUnplugged > 2) {
               Popups.rUnplugged();
               Settings.log.warning("R axis unplugged");
            }
         } else {
            rUnplugged = 0;
            Popups.rPluggedIn();
         }

         Settings.errorByte2 = (byte)statusResponse.get(43);
         if ((Settings.errorByte2 & 64) == 64 && ((Axis)Settings.axes.get(0)).getEnabled()) {
            ++xUnplugged;
            if (xUnplugged > 2) {
               Popups.xUnplugged();
               Settings.log.warning("X axis unplugged");
            }
         } else {
            xUnplugged = 0;
            Popups.xPluggedIn();
         }

         if ((Settings.errorByte2 & 16) != 16 && (Settings.errorByte1 & 8) != 8) {
            rMotStall = 0;
            Popups.rNotStalled();
            Settings.rMotStall = false;
         } else {
            ++rMotStall;
            if (rMotStall > 2) {
               Popups.rStalled();
               Settings.rMotStall = true;
            }
         }

         if ((Settings.errorByte2 & 32) != 32 && (Settings.errorByte1 & 4) != 4) {
            xMotStall = 0;
            Popups.xNotStalled();
            Settings.xMotStall = false;
         } else {
            ++xMotStall;
            if (xMotStall > 2) {
               Popups.xStalled();
               Settings.xMotStall = true;
            }
         }

         if (Settings.sxmove) {
            if ((Settings.errorByte1 & 16) == 16) {
               sxmove = 0;
            } else {
               ++sxmove;
            }

            if (sxmove > 2) {
               sxmove = 0;
               Settings.sxmove = false;
            }
         } else {
            if ((Settings.errorByte1 & 16) == 16) {
               ++sxmove;
            } else {
               sxmove = 0;
            }

            if (sxmove > 2) {
               sxmove = 0;
               Settings.sxmove = true;
            }
         }

         if (Settings.srmove) {
            if ((Settings.errorByte1 & 32) == 32) {
               srmove = 0;
            } else {
               ++srmove;
            }

            if (srmove > 2) {
               srmove = 0;
               Settings.srmove = false;
            }
         } else {
            if ((Settings.errorByte1 & 32) == 32) {
               ++srmove;
            } else {
               srmove = 0;
            }

            if (srmove > 2) {
               srmove = 0;
               Settings.srmove = true;
            }
         }

         if ((Settings.errorByte2 & 128) == 128 && ((Axis)Settings.axes.get(1)).getEnabled()) {
            ++yUnplugged;
            if (yUnplugged > 2) {
               Popups.yUnplugged();
               Settings.log.warning("Y axis unplugged");
            }
         } else {
            yUnplugged = 0;
            Popups.yPluggedIn();
         }

         if (!Settings.pauseRecording) {
            if (Settings.positionRecording.size() > Settings.maxRecordingLength) {
               Settings.positionRecording.poll();
            }

            Settings.positionRecording.add(new PositionStamp(new Date(), ((Axis)Settings.axes.get(0)).getPosition(), ((Axis)Settings.axes.get(1)).getPosition(), ((Axis)Settings.axes.get(2)).getPosition()));
         }
      } catch (Exception var4) {
         Settings.log.log(Level.SEVERE, "Communications", var4);
      }

   }

   public static void reset() {
      sendCommand(87, 1);
      if (SystemCommands.isRPi()) {
         try {
            Settings.log.log(Level.FINE, "pausing command stack so MAC can reset");
            Listener.resetDelay = true;
            Thread.sleep(100L);
            Listener.commandQueue.clear();
            Listener.resetDelay = false;
         } catch (Exception var1) {
            var1.printStackTrace();
         }
      }

   }

   public static void setEncoderValue(Axis axis, int encoderValue) {
      ArrayList<Integer> tmp = new ArrayList();
      int encoder = -1;
      switch($SWITCH_TABLE$com$automec$objects$enums$AxisType()[axis.getAxisType().ordinal()]) {
      case 1:
         encoder = 12;
         break;
      case 2:
         encoder = 13;
         break;
      case 3:
         encoder = 14;
      }

      tmp.add(Integer.valueOf(encoder));
      tmp.add((encoderValue & -16777216) >>> 24);
      tmp.add((encoderValue & 16711680) >>> 16);
      tmp.add((encoderValue & '\uff00') >>> 8);
      tmp.add(encoderValue & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      System.out.println((new Gson()).toJson(tmp));
      sendCommand(103, ret, 1);
   }

   public static void setYControlParam(Axis axis, double top, double slow, double metal, double aw, double bottom) {
      ArrayList<Integer> tmp = new ArrayList();
      double count = axis.getEncoderCountPerInch();
      int tPos = (int)(top * count);
      int sPos = (int)(slow * count);
      int mPos = (int)(metal * count);
      int aWPos = (int)(aw * count);
      int bPos = (int)(bottom * count);
      tmp.add((tPos & -16777216) >>> 24);
      tmp.add((tPos & 16711680) >>> 16);
      tmp.add((tPos & '\uff00') >>> 8);
      tmp.add(tPos & 255);
      tmp.add((sPos & -16777216) >>> 24);
      tmp.add((sPos & 16711680) >>> 16);
      tmp.add((sPos & '\uff00') >>> 8);
      tmp.add(sPos & 255);
      tmp.add((mPos & -16777216) >>> 24);
      tmp.add((mPos & 16711680) >>> 16);
      tmp.add((mPos & '\uff00') >>> 8);
      tmp.add(mPos & 255);
      tmp.add((aWPos & -16777216) >>> 24);
      tmp.add((aWPos & 16711680) >>> 16);
      tmp.add((aWPos & '\uff00') >>> 8);
      tmp.add(aWPos & 255);
      tmp.add((bPos & -16777216) >>> 24);
      tmp.add((bPos & 16711680) >>> 16);
      tmp.add((bPos & '\uff00') >>> 8);
      tmp.add(bPos & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(195, ret, 1);
   }

   /** @deprecated */
   public static void calibrateSystem() {
      reset();
      Iterator var1 = Settings.axes.iterator();

      while(true) {
         while(var1.hasNext()) {
            Axis axis = (Axis)var1.next();
            ArrayList<Integer> tmp = new ArrayList();
            int pos = false;
            int slow = false;
            int stop = false;
            int[] ret;
            int i;
            int pos;
            int slow;
            int stop;
            switch($SWITCH_TABLE$com$automec$objects$enums$AxisType()[axis.getAxisType().ordinal()]) {
            case 1:
               pos = (int)((axis.getAxisLength() - 0.1D) * 0.25D * axis.getEncoderCountPerInch()) + (int)(0.25D * axis.getEncoderCountPerInch());
               slow = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
               stop = pos - (int)(axis.getStopDistance() * axis.getEncoderCountPerInch());
               tmp.add((slow & -16777216) >>> 24);
               tmp.add((slow & 16711680) >>> 16);
               tmp.add((slow & '\uff00') >>> 8);
               tmp.add(slow & 255);
               tmp.add((stop & -16777216) >>> 24);
               tmp.add((stop & 16711680) >>> 16);
               tmp.add((stop & '\uff00') >>> 8);
               tmp.add(stop & 255);
               ret = new int[tmp.size()];

               for(i = 0; i < tmp.size(); ++i) {
                  ret[i] = (Integer)tmp.get(i);
               }

               sendCommand(88, ret, 1);
            case 2:
            default:
               break;
            case 3:
               pos = (int)((axis.getAxisLength() - 0.1D) * 0.25D * axis.getEncoderCountPerInch()) + (int)(0.25D * axis.getEncoderCountPerInch());
               slow = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
               stop = pos - (int)(axis.getStopDistance() * axis.getEncoderCountPerInch());
               tmp.add((slow & -16777216) >>> 24);
               tmp.add((slow & 16711680) >>> 16);
               tmp.add((slow & '\uff00') >>> 8);
               tmp.add(slow & 255);
               tmp.add((stop & -16777216) >>> 24);
               tmp.add((stop & 16711680) >>> 16);
               tmp.add((stop & '\uff00') >>> 8);
               tmp.add(stop & 255);
               ret = new int[tmp.size()];

               for(i = 0; i < tmp.size(); ++i) {
                  ret[i] = (Integer)tmp.get(i);
               }

               sendCommand(89, ret, 1);
            }
         }

         return;
      }
   }

   public static void initializeYAxis() {
      setYControlParam((Axis)Settings.axes.get(1), 2147483.647D, 2147483.647D, 0.0D, -2147483.647D, -2147483.647D);
   }

   public static void calibrateXAxis() {
      Axis axis = (Axis)Settings.axes.get(0);
      ArrayList<Integer> tmp = new ArrayList();
      int pos = false;
      int slow = false;
      int stop = false;
      int pos = (int)((axis.getAxisLength() - 0.1D) * 0.25D * axis.getEncoderCountPerInch()) + (int)(0.1D * axis.getEncoderCountPerInch());
      int slow = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
      tmp.add((slow & -16777216) >>> 24);
      tmp.add((slow & 16711680) >>> 16);
      tmp.add((slow & '\uff00') >>> 8);
      tmp.add(slow & 255);
      tmp.add((pos & -16777216) >>> 24);
      tmp.add((pos & 16711680) >>> 16);
      tmp.add((pos & '\uff00') >>> 8);
      tmp.add(pos & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(88, ret, 1);
   }

   public static void calibrateRAxis() {
      Axis axis = (Axis)Settings.axes.get(2);
      ArrayList<Integer> tmp = new ArrayList();
      int pos = false;
      int slow = false;
      int stop = false;
      int pos = (int)((axis.getAxisLength() - 0.1D) * 0.25D * axis.getEncoderCountPerInch()) + (int)(0.1D * axis.getEncoderCountPerInch());
      int slow = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
      int stop = pos - (int)(axis.getStopDistance() * axis.getEncoderCountPerInch());
      tmp.add((slow & -16777216) >>> 24);
      tmp.add((slow & 16711680) >>> 16);
      tmp.add((slow & '\uff00') >>> 8);
      tmp.add(slow & 255);
      tmp.add((stop & -16777216) >>> 24);
      tmp.add((stop & 16711680) >>> 16);
      tmp.add((stop & '\uff00') >>> 8);
      tmp.add(stop & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(89, ret, 1);
   }

   public static void driveToPosition(AxisType axis, double position) {
      Iterator var4 = Settings.axes.iterator();

      while(var4.hasNext()) {
         Axis a = (Axis)var4.next();
         if (a.getAxisType() == axis) {
            driveToPosition(a, position);
            return;
         }
      }

      Settings.log.warning("Could not find axis of that type, canceling");
   }

   public static void driveToPosition(Axis axis, double position) {
      double currentPosition = axis.getPositionInches();
      int cur = axis.getPosition();
      double targetPosition = axis.getAxisLength() - position;
      if (position < axis.getInLimit()) {
         targetPosition = axis.getAxisLength() - axis.getInLimit();
      }

      if (position > axis.getAxisLength() - 0.1D) {
         targetPosition = 0.1D;
      }

      if (axis.equals((Axis)Settings.axes.get(0))) {
         Settings.xOdometer += Math.abs(currentPosition - targetPosition);
      } else if (axis.equals((Axis)Settings.axes.get(2))) {
         Settings.rOdometer += Math.abs(currentPosition - targetPosition);
      }

      int command = true;
      int outputDeviceCode = true;
      int encoder = true;
      int slowPosition = true;
      int stopPosition = true;
      int speed = 1;
      ArrayList<Integer> tmp = new ArrayList();
      byte command;
      byte outputDeviceCode;
      byte encoder;
      if (axis.getAxisType() == AxisType.BACKGAUGE) {
         encoder = 7;
         command = 54;
         if (currentPosition - position > 0.0D) {
            outputDeviceCode = 2;
         } else {
            outputDeviceCode = 3;
         }
      } else {
         if (axis.getAxisType() != AxisType.OTHER) {
            Settings.log.warning("I can't do that dave");
            return;
         }

         encoder = 6;
         command = 54;
         if (currentPosition - position > 0.0D) {
            outputDeviceCode = 0;
         } else {
            outputDeviceCode = 1;
         }
      }

      int pos = (int)(targetPosition * axis.getEncoderCountPerInch());
      int slowPosition = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
      int stopPosition = pos - (int)(axis.getStopDistance() * axis.getEncoderCountPerInch());
      if (outputDeviceCode == 2) {
         if (slowPosition < cur) {
            outputDeviceCode = 3;
         }
      } else if (outputDeviceCode == 0 && slowPosition < cur) {
         outputDeviceCode = 1;
      }

      System.out.println("axis: " + axis.getShortName() + "current: " + currentPosition * axis.getEncoderCountPerInch() + " slowP: " + slowPosition + " stopP: " + stopPosition + " pos: " + pos);
      if (slowPosition < 0) {
         slowPosition = 0;
      }

      if (stopPosition < 0) {
         stopPosition = 0;
      }

      tmp.add(Integer.valueOf(outputDeviceCode));
      tmp.add(Integer.valueOf(speed));
      tmp.add(Integer.valueOf(encoder));
      tmp.add((slowPosition & -16777216) >>> 24);
      tmp.add((slowPosition & 16711680) >>> 16);
      tmp.add((slowPosition & '\uff00') >>> 8);
      tmp.add(slowPosition & 255);
      tmp.add((stopPosition & -16777216) >>> 24);
      tmp.add((stopPosition & 16711680) >>> 16);
      tmp.add((stopPosition & '\uff00') >>> 8);
      tmp.add(stopPosition & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(command, ret, 1);
   }

   public static void driveToPositionRaw(Axis axis, double position) {
      double currentPosition = axis.getPositionInches();
      double targetPosition = axis.getAxisLength() - position;
      if (position < axis.getInLimit()) {
         targetPosition = axis.getAxisLength() - axis.getInLimit();
      }

      if (position > axis.getAxisLength() - 0.1D) {
         targetPosition = 0.0D;
      }

      if (axis.equals((Axis)Settings.axes.get(0))) {
         Settings.xOdometer += Math.abs(currentPosition - targetPosition);
      } else if (axis.equals((Axis)Settings.axes.get(2))) {
         Settings.rOdometer += Math.abs(currentPosition - targetPosition);
      }

      int command = true;
      int outputDeviceCode = true;
      int encoder = true;
      int slowPosition = true;
      int stopPosition = true;
      int speed = 1;
      ArrayList<Integer> tmp = new ArrayList();
      byte command;
      byte outputDeviceCode;
      byte encoder;
      if (axis.getAxisType() == AxisType.BACKGAUGE) {
         encoder = 7;
         command = 54;
         if (currentPosition - position > 0.0D) {
            outputDeviceCode = 2;
         } else {
            outputDeviceCode = 3;
         }
      } else {
         if (axis.getAxisType() != AxisType.OTHER) {
            Settings.log.warning("I can't do that dave");
            return;
         }

         encoder = 6;
         command = 54;
         if (currentPosition - position > 0.0D) {
            outputDeviceCode = 0;
         } else {
            outputDeviceCode = 1;
         }
      }

      int pos = (int)(targetPosition * axis.getEncoderCountPerInch());
      int slowPosition = pos - (int)(axis.getSlowDistance() * axis.getEncoderCountPerInch());
      int stopPosition = pos;
      if (slowPosition < 0) {
         slowPosition = 0;
      }

      if (pos < 0) {
         stopPosition = 0;
      }

      tmp.add(Integer.valueOf(outputDeviceCode));
      tmp.add(Integer.valueOf(speed));
      tmp.add(Integer.valueOf(encoder));
      tmp.add((slowPosition & -16777216) >>> 24);
      tmp.add((slowPosition & 16711680) >>> 16);
      tmp.add((slowPosition & '\uff00') >>> 8);
      tmp.add(slowPosition & 255);
      tmp.add((stopPosition & -16777216) >>> 24);
      tmp.add((stopPosition & 16711680) >>> 16);
      tmp.add((stopPosition & '\uff00') >>> 8);
      tmp.add(stopPosition & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(command, ret, 1);
   }

   public static void xyrCombinedCommand(double xPosition, double rPosition, double top, double slow, double metal, double aw, double bottom) {
      ArrayList<Integer> tmp = new ArrayList();
      double xTarget = ((Axis)Settings.axes.get(0)).getAxisLength() - xPosition;
      double rTarget = ((Axis)Settings.axes.get(2)).getAxisLength() - rPosition;
      boolean xDeadzone = false;
      boolean rDeadzone = false;
      double xCurrent;
      if (Listener.retractDelay) {
         xCurrent = Listener.retPos;
      } else {
         xCurrent = ((Axis)Settings.axes.get(0)).getPositionInches();
      }

      double rCurrent = ((Axis)Settings.axes.get(2)).getPositionInches();
      if (Math.abs(xCurrent - xPosition) <= ((Axis)Settings.axes.get(0)).getDeadzone()) {
         xDeadzone = true;
      }

      if (Math.abs(rCurrent - rPosition) <= ((Axis)Settings.axes.get(2)).getDeadzone()) {
         rDeadzone = true;
      }

      Settings.xOdometer += Math.abs(xCurrent - xTarget);
      Settings.yOdometer += Math.abs(top - bottom);
      Settings.rOdometer += Math.abs(rCurrent - rTarget);
      Settings.log.finest("X travel: " + Math.abs(xCurrent - xTarget));
      int xCur;
      if (Listener.retractDelay) {
         xCur = (int)((((Axis)Settings.axes.get(0)).getAxisLength() - Listener.retPos) * ((Axis)Settings.axes.get(0)).getEncoderCountPerInch());
      } else {
         xCur = ((Axis)Settings.axes.get(0)).getPosition();
      }

      int rCur = ((Axis)Settings.axes.get(2)).getPosition();
      if (xPosition < ((Axis)Settings.axes.get(0)).getInLimit()) {
         xTarget = ((Axis)Settings.axes.get(0)).getAxisLength() - ((Axis)Settings.axes.get(0)).getInLimit();
      }

      if (rPosition < ((Axis)Settings.axes.get(2)).getInLimit()) {
         rTarget = ((Axis)Settings.axes.get(2)).getAxisLength() - ((Axis)Settings.axes.get(2)).getInLimit();
      }

      if (xPosition > ((Axis)Settings.axes.get(0)).getAxisLength() - 0.1D) {
         xTarget = 0.1D;
      }

      if (rPosition > ((Axis)Settings.axes.get(2)).getAxisLength() - 0.1D) {
         rTarget = 0.1D;
      }

      int status = setStatus(!xDeadzone, !rDeadzone);
      double yCount = ((Axis)Settings.axes.get(1)).getEncoderCountPerInch();
      byte xOut;
      if (xCurrent - xPosition > 0.0D) {
         xOut = 2;
      } else {
         xOut = 3;
      }

      byte rOut;
      if (rCurrent - rPosition > 0.0D) {
         rOut = 0;
      } else {
         rOut = 1;
      }

      int xPos = (int)(xTarget * ((Axis)Settings.axes.get(0)).getEncoderCountPerInch());
      int rPos = (int)(rTarget * ((Axis)Settings.axes.get(2)).getEncoderCountPerInch());
      int tPos = (int)(top * yCount);
      int sPos = (int)(slow * yCount);
      int mPos = (int)(metal * yCount);
      int aWPos = (int)(aw * yCount);
      int bPos = (int)(bottom * yCount);
      int xSlow = xPos - (int)(((Axis)Settings.axes.get(0)).getSlowDistance() * ((Axis)Settings.axes.get(0)).getEncoderCountPerInch());
      int xStop = xPos - (int)(((Axis)Settings.axes.get(0)).getStopDistance() * ((Axis)Settings.axes.get(0)).getEncoderCountPerInch());
      int rSlow = rPos - (int)(((Axis)Settings.axes.get(2)).getSlowDistance() * ((Axis)Settings.axes.get(2)).getEncoderCountPerInch());
      int rStop = rPos - (int)(((Axis)Settings.axes.get(2)).getStopDistance() * ((Axis)Settings.axes.get(2)).getEncoderCountPerInch());
      if (xOut == 2 && xSlow < xCur) {
         xOut = 3;
         if (Math.abs(xSlow - xCur) < 40) {
            xSlow -= 50;
         }
      }

      if (rOut == 0 && rSlow < rCur) {
         rOut = 1;
      }

      if (xSlow < 0) {
         xSlow = 0;
      }

      if (xStop < 0) {
         xStop = 0;
      }

      if (rSlow < 0) {
         rSlow = 0;
      }

      if (rStop < 0) {
         rStop = 0;
      }

      if (!((Axis)Settings.axes.get(0)).getEnabled()) {
         xSlow = 0;
         xStop = 0;
      }

      if (!((Axis)Settings.axes.get(2)).getEnabled()) {
         rSlow = 0;
         rStop = 0;
      }

      tmp.add(status & 255);
      if (((Axis)Settings.axes.get(0)).getEnabled() && !xDeadzone) {
         tmp.add(xOut & 255);
         tmp.add(1);
         tmp.add(7);
      } else {
         tmp.add(0);
         tmp.add(0);
         tmp.add(0);
      }

      tmp.add((xSlow & -16777216) >>> 24);
      tmp.add((xSlow & 16711680) >>> 16);
      tmp.add((xSlow & '\uff00') >>> 8);
      tmp.add(xSlow & 255);
      tmp.add((xStop & -16777216) >>> 24);
      tmp.add((xStop & 16711680) >>> 16);
      tmp.add((xStop & '\uff00') >>> 8);
      tmp.add(xStop & 255);
      if (((Axis)Settings.axes.get(2)).getEnabled() && !rDeadzone) {
         tmp.add(rOut & 255);
         tmp.add(1);
         tmp.add(6);
      } else {
         tmp.add(0);
         tmp.add(0);
         tmp.add(0);
      }

      tmp.add((rSlow & -16777216) >>> 24);
      tmp.add((rSlow & 16711680) >>> 16);
      tmp.add((rSlow & '\uff00') >>> 8);
      tmp.add(rSlow & 255);
      tmp.add((rStop & -16777216) >>> 24);
      tmp.add((rStop & 16711680) >>> 16);
      tmp.add((rStop & '\uff00') >>> 8);
      tmp.add(rStop & 255);
      tmp.add((tPos & -16777216) >>> 24);
      tmp.add((tPos & 16711680) >>> 16);
      tmp.add((tPos & '\uff00') >>> 8);
      tmp.add(tPos & 255);
      tmp.add((sPos & -16777216) >>> 24);
      tmp.add((sPos & 16711680) >>> 16);
      tmp.add((sPos & '\uff00') >>> 8);
      tmp.add(sPos & 255);
      tmp.add((mPos & -16777216) >>> 24);
      tmp.add((mPos & 16711680) >>> 16);
      tmp.add((mPos & '\uff00') >>> 8);
      tmp.add(mPos & 255);
      tmp.add((aWPos & -16777216) >>> 24);
      tmp.add((aWPos & 16711680) >>> 16);
      tmp.add((aWPos & '\uff00') >>> 8);
      tmp.add(aWPos & 255);
      tmp.add((bPos & -16777216) >>> 24);
      tmp.add((bPos & 16711680) >>> 16);
      tmp.add((bPos & '\uff00') >>> 8);
      tmp.add(bPos & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(55, ret, 1);
   }

   private static int setStatus(boolean x, boolean r) {
      int ret = 0;
      if (((Axis)Settings.axes.get(0)).getEnabled() && x) {
         ++ret;
      }

      if (((Axis)Settings.axes.get(1)).getEnabled()) {
         ret += 4;
      }

      if (((Axis)Settings.axes.get(2)).getEnabled() && r) {
         ret += 2;
      }

      return ret;
   }

   public void setTopReference(double pos) {
      ArrayList<Integer> tmp = new ArrayList();
      int yPos = (int)(pos * ((Axis)Settings.axes.get(1)).getEncoderCountPerInch());
      tmp.add((yPos & -16777216) >>> 24);
      tmp.add((yPos & 16711680) >>> 16);
      tmp.add((yPos & '\uff00') >>> 8);
      tmp.add(yPos & 255);
      int[] ret = new int[tmp.size()];

      for(int i = 0; i < tmp.size(); ++i) {
         ret[i] = (Integer)tmp.get(i);
      }

      sendCommand(97, ret, 1);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$objects$enums$AxisType() {
      int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$AxisType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[AxisType.values().length];

         try {
            var0[AxisType.BACKGAUGE.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[AxisType.OTHER.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[AxisType.RAM.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$objects$enums$AxisType = var0;
         return var0;
      }
   }
}
