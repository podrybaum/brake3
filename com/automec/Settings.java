package com.automec;

import com.automec.display.popups.NotificationPage;
import com.automec.objects.Axis;
import com.automec.objects.PositionStamp;
import com.automec.objects.Tool;
import com.automec.objects.USB;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.AxisLimitSwitch;
import com.automec.objects.enums.ExtAdvPolarity;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import io.dvlopt.linux.i2c.I2CBus;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;

public class Settings {
   public static final String version = "2.0.14";
   public static String commit;
   public static final String branch = "Java";
   public static int screenBrightness = 10;
   public static boolean setupMode = false;
   public static boolean calibrated = false;
   public static ArrayList<Axis> axes = new ArrayList();
   public static I2CBus bus;
   public static String MACREV;
   public static byte errorByte1;
   public static byte errorByte2;
   public static AxisLimitSwitch autoIndexSwitch;
   public static AxisLimitSwitch eStop;
   public static AdvanceMode autoAdvanceMode;
   public static ExtAdvPolarity extAdvPolarity;
   public static AxisLimitSwitch bottomTouchedSwitch;
   public static AdvancePosition autoAdvancePosition;
   public static Units units;
   public static Logger log;
   public static boolean locked;
   public static ArrayList<USB> connectedUSB;
   public static USB selectedUSB;
   public static USB lastUSB;
   public static int xStop;
   public static boolean screensaver;
   public static Mode defaultMode;
   public static boolean advancedCalibration;
   public static double advancedCalibrationPosition;
   public static double odometer;
   public static double xOdometer;
   public static double yOdometer;
   public static double rOdometer;
   public static boolean sxmove;
   public static boolean srmove;
   public static boolean xMotStall;
   public static boolean rMotStall;
   public static boolean macPowerCycled;
   public static boolean bottomTouched;
   public static Tool selectedPunch;
   public static Tool selectedDie;
   public static boolean floatingCalibration;
   public static double calThickness;
   public static Tool calPunch;
   public static Tool calDie;
   public static boolean demoMode;
   public static boolean displayCounts;
   public static JFrame activeFrame;
   public static ArrayDeque<PositionStamp> positionRecording;
   public static int maxRecordingLength;
   public static boolean pauseRecording;

   static {
      autoAdvanceMode = AdvanceMode.INTERNAL;
      extAdvPolarity = ExtAdvPolarity.NC;
      autoAdvancePosition = AdvancePosition.TOS;
      units = Units.INCHES;
      locked = true;
      connectedUSB = new ArrayList();
      selectedUSB = null;
      lastUSB = null;
      xStop = 0;
      screensaver = true;
      defaultMode = Mode.DEPTH;
      advancedCalibration = true;
      advancedCalibrationPosition = 2.0D;
      odometer = 0.0D;
      xOdometer = 0.0D;
      yOdometer = 0.0D;
      rOdometer = 0.0D;
      floatingCalibration = false;
      demoMode = false;
      displayCounts = false;
      positionRecording = new ArrayDeque();
      maxRecordingLength = 5000;
      pauseRecording = false;
   }

   public static void setupLogs() {
      InputStream in = Settings.class.getClassLoader().getResourceAsStream("HEAD");
      BufferedReader r = new BufferedReader(new InputStreamReader(in));

      String s;
      InputStream in2;
      try {
         s = r.readLine();
         if (s.contains("ref:")) {
            in2 = Settings.class.getClassLoader().getResourceAsStream("heads/Java");
            BufferedReader r2 = new BufferedReader(new InputStreamReader(in2));
            commit = r2.readLine().substring(0, 7);
         } else {
            commit = s.substring(0, 7);
         }

         r.close();
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      log = Logger.getLogger("logger");
      s = null;
      in2 = null;

      try {
         (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator)).mkdirs();
         (new File(System.getProperty("java.io.tmpdir") + File.separator + "CNC600" + File.separator + "debug" + File.separator)).mkdirs();
         System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n %6$s%n");
         Formatter format = new SimpleFormatter();
         LocalDate cal = LocalDate.now();
         Handler pHandler = new FileHandler(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + cal.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + "_logP.log", 100000000, 2, true);
         Handler tHandler = new FileHandler(System.getProperty("java.io.tmpdir") + File.separator + "CNC600" + File.separator + "debug" + File.separator + cal.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + "_logT.log", 100000000, 2, true);
         pHandler.setLevel(Level.CONFIG);
         tHandler.setLevel(Level.ALL);
         pHandler.setFormatter(format);
         tHandler.setFormatter(format);
         log.addHandler(pHandler);
         log.addHandler(tHandler);
         log.setLevel(Level.ALL);
         log.config("logger initialized");
      } catch (Exception var6) {
         log.log(Level.SEVERE, "Settings", var6);
      }

   }

   public static void seteStop(AxisLimitSwitch eStop) {
      Settings.eStop = eStop;
   }

   public static void seteStop(int eStop) {
      if (eStop == 0) {
         Settings.eStop = AxisLimitSwitch.ON;
      } else {
         Settings.eStop = AxisLimitSwitch.OFF;
      }

   }

   public static void setMacPowerCycled(boolean powerCycled) {
      macPowerCycled = powerCycled;
   }

   public static void setMacPowerCycled(int powerCycled) {
      if (powerCycled == 0) {
         macPowerCycled = false;
      } else {
         macPowerCycled = true;
      }

   }

   public static void setBottomTouchedSwitch(int sw) {
      if (sw == 0) {
         bottomTouchedSwitch = AxisLimitSwitch.OFF;
      } else {
         bottomTouchedSwitch = AxisLimitSwitch.ON;
      }

   }

   public static AxisLimitSwitch getBottomTouchedSwitch() {
      return bottomTouchedSwitch;
   }

   public static AxisLimitSwitch getAutoIndexSwitch() {
      return autoIndexSwitch;
   }

   public static void setAutoIndexSwitch(AxisLimitSwitch autoIndexSwitch) {
      Settings.autoIndexSwitch = autoIndexSwitch;
   }

   public static void setAutoIndexSwitch(int autoIndexSwitch) {
      if (autoIndexSwitch == 0) {
         setAutoIndexSwitch(AxisLimitSwitch.OFF);
      } else {
         setAutoIndexSwitch(AxisLimitSwitch.ON);
      }

   }

   public static boolean checkPassword(char[] password) {
      char[] pwd = new char[]{'m', 'a', 'g', 'g', 'i', 'e'};
      if (password.length != pwd.length) {
         log.info("incorrect password provided: incorrect length");
         return false;
      } else if (Arrays.equals(password, pwd)) {
         setupMode = true;
         log.info("Correct password entered");
         return true;
      } else {
         log.info("incorrect password provided: wrong password");
         return false;
      }
   }

   public static int getExtAdvPolarity() {
      return extAdvPolarity == ExtAdvPolarity.NO ? 0 : 1;
   }

   public static boolean getExtAdvSwitch() {
      byte indexSwitchValue;
      if (getAutoIndexSwitch() == AxisLimitSwitch.ON) {
         indexSwitchValue = 1;
      } else {
         indexSwitchValue = 0;
      }

      return indexSwitchValue == getExtAdvPolarity();
   }

   public static void loadSettings(String json) {
      Gson gson = new Gson();
      SettingsStorage stored = (SettingsStorage)gson.fromJson(json, SettingsStorage.class);
      if (!stored.version.equals("2.0.14")) {
         new NotificationPage("Settings Version Mismatch", "If you reciently UPDATED, this warning can be ignored");
      }

      screenBrightness = stored.screenBrightness;
      axes = stored.axes;
      extAdvPolarity = stored.extAdvPolarity;
      autoAdvanceMode = stored.autoAdvanceMode;
      autoAdvancePosition = stored.autoAdvancePosition;
      screensaver = stored.screensaver;
      units = stored.units;
      defaultMode = stored.defaultMode;
      advancedCalibration = stored.advancedCalibration;
      odometer = stored.odometer;
      xOdometer = stored.xOdometer;
      yOdometer = stored.yOdometer;
      rOdometer = stored.rOdometer;
      advancedCalibrationPosition = stored.advancedCalibrationPosition;
      floatingCalibration = stored.floatingCalibration;
   }

   public static void updateCalibration(double thickness, Tool punch, Tool die) {
      int adjust = 0;
      int currentPos = ((Axis)axes.get(1)).getPosition();
      int adjust = (int)((double)adjust + (calThickness - thickness) * ((Axis)axes.get(1)).getEncoderCountPerInch());
      adjust = (int)((double)adjust + (calPunch.getHeight() - punch.getHeight()) * ((Axis)axes.get(1)).getEncoderCountPerInch());
      adjust = (int)((double)adjust + (calDie.getHeight() - die.getHeight()) * ((Axis)axes.get(1)).getEncoderCountPerInch());

      try {
         Thread.sleep(250L);
      } catch (Exception var8) {
         log.log(Level.WARNING, "updateCalibration threw an error", var8);
      }

      if (((Axis)axes.get(1)).getPosition() != currentPos) {
         new NotificationPage("WARNING", "Floating calibration procedure failed: Ram moved durring procedure.");
      } else {
         Communications.setEncoderValue((Axis)axes.get(1), currentPos + adjust);

         try {
            Thread.sleep(250L);
         } catch (Exception var7) {
            log.log(Level.WARNING, "updateCalibration threw an error", var7);
         }

         if (((Axis)axes.get(1)).getPosition() != currentPos + adjust) {
            calibrated = false;
            log.log(Level.WARNING, "Floating calibration procedure failed");
            new NotificationPage("WARNING", "Floating calibration procedure failed: Ram position does not match expected position.");
         } else {
            calThickness = thickness;
            calPunch = punch;
            calDie = die;
         }
      }
   }

   public static void printRecording() {
      pauseRecording = true;

      while(!positionRecording.isEmpty()) {
         ((PositionStamp)positionRecording.poll()).print();
      }

      pauseRecording = false;
   }
}
