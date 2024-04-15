package com.automec;

import com.automec.objects.Axis;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.ExtAdvPolarity;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.Units;
import java.util.ArrayList;

public class SettingsStorage {
   public int screenBrightness;
   public ArrayList<Axis> axes;
   public AdvanceMode autoAdvanceMode;
   public ExtAdvPolarity extAdvPolarity;
   public AdvancePosition autoAdvancePosition;
   public Units units;
   public boolean screensaver;
   public String version;
   public Mode defaultMode;
   public boolean advancedCalibration;
   public double odometer;
   public double xOdometer;
   public double yOdometer;
   public double rOdometer;
   public double advancedCalibrationPosition;
   public boolean floatingCalibration;

   public SettingsStorage() {
      this.screenBrightness = Settings.screenBrightness;
      this.axes = Settings.axes;
      this.autoAdvanceMode = Settings.autoAdvanceMode;
      this.extAdvPolarity = Settings.extAdvPolarity;
      this.autoAdvancePosition = Settings.autoAdvancePosition;
      this.units = Settings.units;
      this.screensaver = Settings.screensaver;
      this.version = "2.0.14";
      this.defaultMode = Settings.defaultMode;
      this.advancedCalibration = Settings.advancedCalibration;
      this.odometer = Settings.odometer;
      this.xOdometer = Settings.xOdometer;
      this.yOdometer = Settings.yOdometer;
      this.rOdometer = Settings.rOdometer;
      this.advancedCalibrationPosition = Settings.advancedCalibrationPosition;
      this.floatingCalibration = Settings.floatingCalibration;
   }
}
