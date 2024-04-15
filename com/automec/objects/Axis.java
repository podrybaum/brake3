package com.automec.objects;

import com.automec.Settings;
import com.automec.objects.enums.AxisDirection;
import com.automec.objects.enums.AxisLimitSwitch;
import com.automec.objects.enums.AxisRelayState;
import com.automec.objects.enums.AxisSpeed;
import com.automec.objects.enums.AxisType;
import com.automec.objects.enums.XAxisPreset;
import com.automec.objects.enums.YAxisPreset;

public class Axis {
   private String fullName;
   private String shortName;
   private int address;
   private boolean enabled = false;
   private AxisType axisType;
   private int position = 0;
   private AxisDirection direction;
   private AxisSpeed speed;
   private AxisLimitSwitch limitSwitch;
   private int alarm;
   private AxisRelayState slowSpeedSOV;
   private AxisRelayState antiWhipSOV;
   private AxisRelayState bottomStopSOV;
   private AxisRelayState topStopSOV;
   private AxisLimitSwitch bottomLimitSwitch;
   private AxisLimitSwitch topLimitSwitch;
   private double inLimit = 0.0D;
   private double bottomTollerence = 0.0D;
   private double axisLength = 0.0D;
   private double encoderCountPerInch = 0.0D;
   private double slowDistance = 0.0D;
   private double stopDistance = 0.0D;
   private double awDistance = 0.0D;
   private double inFast = 1.0D;
   private double inSlow = 1.0D;
   private double outFast = 1.0D;
   private double defaultOffset = 0.0D;
   private double zeroOffset = 0.0D;
   private boolean zeroAdjust = false;
   private double deadzone = 0.0D;
   private YAxisPreset yPreset;
   private XAxisPreset xPreset;
   private boolean minimumAngleOveride;

   public Axis(String fullName, String shortName, int address, boolean enabled, AxisType type) {
      this.yPreset = YAxisPreset.CAT1;
      this.xPreset = XAxisPreset.G24;
      this.minimumAngleOveride = false;
      this.fullName = fullName;
      this.shortName = shortName;
      this.address = address;
      this.enabled = enabled;
      this.axisType = type;
   }

   public Axis(String fullName, String shortName, int address, boolean enabled) {
      this.yPreset = YAxisPreset.CAT1;
      this.xPreset = XAxisPreset.G24;
      this.minimumAngleOveride = false;
      this.fullName = fullName;
      this.shortName = shortName;
      this.address = address;
      this.enabled = enabled;
      this.axisType = AxisType.OTHER;
   }

   public boolean equals(Axis axis) {
      return this.getAddress() == axis.getAddress() && this.getAxisType() == axis.getAxisType();
   }

   public String getFullName() {
      return this.fullName;
   }

   public String getShortName() {
      return this.shortName;
   }

   public int getAddress() {
      return this.address;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean value) {
      this.enabled = value;
   }

   public void setAxisType(AxisType type) {
      this.axisType = type;
   }

   public AxisType getAxisType() {
      return this.axisType;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public int getPosition() {
      return this.position;
   }

   public double getPositionInches() {
      return (double)this.position / this.encoderCountPerInch * -1.0D + this.axisLength;
   }

   public double getPositionMM() {
      return ((double)this.position / this.encoderCountPerInch * -1.0D + this.axisLength) * 25.4D;
   }

   public AxisDirection getDirection() {
      return this.direction;
   }

   public void setDirection(AxisDirection direction) {
      this.direction = direction;
   }

   public void setDirection(int direction) {
      if (direction == 0) {
         this.direction = AxisDirection.UP;
      } else {
         this.direction = AxisDirection.DOWN;
      }

   }

   public AxisSpeed getSpeed() {
      return this.speed;
   }

   public void setSpeed(AxisSpeed speed) {
      this.speed = speed;
   }

   public void setSpeed(int speed) {
      if (speed == 0) {
         this.speed = AxisSpeed.SLOW;
      } else {
         this.speed = AxisSpeed.NORMAL;
      }

   }

   public AxisLimitSwitch getLimitSwitch() {
      return this.limitSwitch;
   }

   public void setLimitSwitch(AxisLimitSwitch limitSwitch) {
      this.limitSwitch = limitSwitch;
   }

   public void setLimitSwitch(int limitSwitch) {
      if (limitSwitch == 0) {
         this.limitSwitch = AxisLimitSwitch.OFF;
      } else {
         this.limitSwitch = AxisLimitSwitch.ON;
      }

   }

   public int getAlarm() {
      return this.alarm;
   }

   public void setAlarm(int alarm) {
      this.alarm = alarm;
   }

   public AxisRelayState getSlowSpeedSOV() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.slowSpeedSOV;
      }
   }

   public void setSlowSpeedSOV(AxisRelayState slowSpeedSOV) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.slowSpeedSOV = slowSpeedSOV;
      }
   }

   public void setSlowSpeedSOV(int slowSpeedSOV) throws IncorrectAxisException {
      if (slowSpeedSOV == 1) {
         this.setSlowSpeedSOV(AxisRelayState.OFF);
      } else {
         this.setSlowSpeedSOV(AxisRelayState.ON);
      }

   }

   public AxisRelayState getAntiWhipSOV() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.antiWhipSOV;
      }
   }

   public void setAntiWhipSOV(AxisRelayState antiWhipSOV) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.antiWhipSOV = antiWhipSOV;
      }
   }

   public void setAntiWhipSOV(int antiWhipSOV) throws IncorrectAxisException {
      if (antiWhipSOV == 1) {
         this.setAntiWhipSOV(AxisRelayState.OFF);
      } else {
         this.setAntiWhipSOV(AxisRelayState.ON);
      }

   }

   public AxisRelayState getBottomStopSOV() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.bottomStopSOV;
      }
   }

   public void setBottomStopSOV(AxisRelayState bottomStopSOV) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.bottomStopSOV = bottomStopSOV;
      }
   }

   public void setBottomStopSOV(int bottomStopSOV) throws IncorrectAxisException {
      if (bottomStopSOV == 1) {
         this.setBottomStopSOV(AxisRelayState.OFF);
      } else {
         this.setBottomStopSOV(AxisRelayState.ON);
      }

   }

   public AxisRelayState getTopStopSOV() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.topStopSOV;
      }
   }

   public void setTopStopSOV(AxisRelayState topStopSOV) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.topStopSOV = topStopSOV;
      }
   }

   public void setTopStopSOV(int topStopSOV) throws IncorrectAxisException {
      if (topStopSOV == 1) {
         this.setTopStopSOV(AxisRelayState.OFF);
      } else {
         this.setTopStopSOV(AxisRelayState.ON);
      }

   }

   public AxisLimitSwitch getBottomLimitSwitch() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.bottomLimitSwitch;
      }
   }

   public void setBottomLimitSwitch(AxisLimitSwitch bottomLimitSwitch) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.bottomLimitSwitch = bottomLimitSwitch;
      }
   }

   public void setBottomLimitSwitch(int bottomLimitSwitch) throws IncorrectAxisException {
      if (bottomLimitSwitch == 0) {
         this.setBottomLimitSwitch(AxisLimitSwitch.OFF);
      } else {
         this.setBottomLimitSwitch(AxisLimitSwitch.ON);
      }

   }

   public AxisLimitSwitch getTopLimitSwitch() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.topLimitSwitch;
      }
   }

   public void setTopLimitSwitch(AxisLimitSwitch topLimitSwitch) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.topLimitSwitch = topLimitSwitch;
      }
   }

   public void setTopLimitSwitch(int topLimitSwitch) throws IncorrectAxisException {
      if (topLimitSwitch == 0) {
         this.setTopLimitSwitch(AxisLimitSwitch.OFF);
      } else {
         this.setTopLimitSwitch(AxisLimitSwitch.ON);
      }

   }

   public double getAxisLength() {
      return this.axisLength;
   }

   public double getAxisLengthMM() {
      return this.getAxisLength() * 25.4D;
   }

   public void setAxisLength(double axisLength) {
      this.axisLength = axisLength;
   }

   public double getEncoderCountPerInch() {
      return this.encoderCountPerInch;
   }

   public void setEncoderCountPerInch(double encoderCount) {
      this.encoderCountPerInch = encoderCount;
   }

   public double getSlowDistance() {
      return this.slowDistance;
   }

   public double getUpSlowDistance() {
      return this.slowDistance;
   }

   public void setSlowDistance(double slowDistance) {
      this.slowDistance = slowDistance;
   }

   public double getStopDistance() {
      return this.stopDistance;
   }

   public void setStopDistance(double stopDistance) {
      if (stopDistance < 0.2D && stopDistance > -0.2D) {
         this.stopDistance = stopDistance;
      }

   }

   public double getAwDistance() {
      return this.awDistance;
   }

   public void setAwDistance(double awDistance) {
      this.awDistance = awDistance;
   }

   public double getInLimit() {
      return this.inLimit;
   }

   public void setInLimit(double inLimit) {
      this.inLimit = inLimit;
   }

   public double getInFast() throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         return this.inFast;
      }
   }

   public void setInFast(double inFast) throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         this.inFast = inFast;
      }
   }

   public double getInSlow() throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         return this.inSlow;
      }
   }

   public void setInSlow(double inSlow) throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         this.inSlow = inSlow;
      }
   }

   public double getOutFast() throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         return this.outFast;
      }
   }

   public void setOutFast(double outFast) throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         this.outFast = outFast;
      }
   }

   public YAxisPreset getyPreset() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.yPreset;
      }
   }

   public void setyPreset(YAxisPreset yPreset) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.yPreset = yPreset;
      }
   }

   public XAxisPreset getxPreset() throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         return this.xPreset;
      }
   }

   public void setxPreset(XAxisPreset xPreset) throws IncorrectAxisException {
      if (this.axisType != AxisType.BACKGAUGE) {
         throw new IncorrectAxisException(this.axisType, AxisType.BACKGAUGE);
      } else {
         this.xPreset = xPreset;
      }
   }

   public double getBottomTollerence() throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         return this.bottomTollerence;
      }
   }

   public void setBottomTollerence(double bottomTollerence) throws IncorrectAxisException {
      if (this.axisType != AxisType.RAM) {
         throw new IncorrectAxisException(this.axisType, AxisType.RAM);
      } else {
         this.bottomTollerence = bottomTollerence;
      }
   }

   public double getDefaultOffset() {
      return this.defaultOffset;
   }

   public void setDefaultOffset(double defaultOffset) {
      this.defaultOffset = defaultOffset;
   }

   public double getZeroOffset() {
      return this.zeroOffset;
   }

   public void setZeroOffset(double zeroOffset) {
      this.zeroOffset = zeroOffset;
   }

   public boolean getZeroAdjust() {
      return this.zeroAdjust;
   }

   public void setZeroAdjust(boolean zeroAdjust) {
      this.zeroAdjust = zeroAdjust;
   }

   public double getZeroOffsetMax() {
      return Settings.floatingCalibration && Settings.calibrated ? this.axisLength - Settings.calDie.getHeight() + this.zeroOffset - 0.1D : this.axisLength + this.zeroOffset;
   }

   public double getZeroOffsetMin() {
      return Settings.floatingCalibration && Settings.calibrated ? 0.0D - Settings.calDie.getHeight() + this.zeroOffset : 0.0D - this.zeroOffset;
   }

   public double getDeadzone() {
      return this.deadzone;
   }

   public void setDeadzone(double deadzone) {
      this.deadzone = deadzone;
   }

   public boolean getMinimumAngleOverride() {
      return this.minimumAngleOveride;
   }

   public void setMinimumAngleOverride(boolean v) {
      this.minimumAngleOveride = v;
   }
}
