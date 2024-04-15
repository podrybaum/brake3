package com.automec.objects;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.display.pages.RecallJobPage;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.Mode;
import com.automec.objects.enums.SortMethod;
import com.automec.objects.enums.Units;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Job implements Comparable<Job> {
   private String name;
   private Mode mode;
   private double ramSlowPosition;
   private Units units;
   private ArrayList<Bend> bends;
   private ArrayList<Axis> axisOrder;
   private ArrayList<Double> offsets;
   private int bendNo = 0;
   private ArrayList<String> bendNotes;
   private Location location;
   private String imagePath;
   private LocalDateTime created;
   private LocalDateTime edited;
   private LocalDateTime ran;
   private int parts;
   private String versionNo;
   private boolean retractEnabled;
   private boolean baEnabled;
   private Tool punch;
   private Tool die;
   private double thickness;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$objects$enums$SortMethod;

   public Job(String name, Mode mode, double ramSlowPosition, Units units, ArrayList<Bend> bends, ArrayList<Axis> axes) {
      this.location = Location.MEMORY;
      this.imagePath = "";
      this.parts = 0;
      this.retractEnabled = false;
      this.baEnabled = false;
      this.thickness = 0.0D;
      this.name = name;
      this.mode = mode;
      this.ramSlowPosition = ramSlowPosition;
      this.units = units;
      this.bends = bends;
      this.axisOrder = axes;
      this.offsets = new ArrayList();
      this.setBendNotes(new ArrayList());
      this.created = LocalDateTime.now();
      this.setVersionNo("2.0.14");
   }

   public Job copyJob(String newName) {
      Job ret = new Job(newName, this.mode, this.ramSlowPosition, this.units, new ArrayList(), this.axisOrder);
      ret.initImageFolder();
      if (this.mode == Mode.ANGLE) {
         ret.setPunch(this.getPunch());
         ret.setDie(this.getDie());
         ret.setThickness(this.thickness);
      }

      for(int i = 0; i < this.bends.size(); ++i) {
         Bend t = (Bend)this.bends.get(i);
         ret.getBends().add(new Bend(ret, t.getCycles(), t.getAxisValues()));
         if (!t.getBendImage().equals("")) {
            ((Bend)ret.getBends().get(i)).addBendImage(t.getBendImage());
         }
      }

      return ret;
   }

   public void updateImagePaths() {
      this.initImageFolder();
      System.out.println(this.imagePath);

      for(int i = 0; i < this.bends.size(); ++i) {
         if (!((Bend)this.bends.get(i)).getBendImage().equals("")) {
            ((Bend)this.bends.get(i)).addBendImage(((Bend)this.bends.get(i)).getBendImage());
         }
      }

   }

   public void setEdited(LocalDateTime edited) {
      this.edited = edited;
   }

   public void setCreated(LocalDateTime created) {
      this.created = created;
   }

   public String getName() {
      return this.name;
   }

   public Mode getMode() {
      return this.mode;
   }

   public double getRamSlowPosition() {
      return this.ramSlowPosition;
   }

   public void setRamSlowPosition(double position) {
      this.ramSlowPosition = position;
   }

   public Units getUnits() {
      return this.units;
   }

   public ArrayList<Bend> getBends() {
      return this.bends;
   }

   public ArrayList<Double> getOffsets() {
      return this.offsets;
   }

   public int getBendNo() {
      ++this.bendNo;
      return this.bendNo;
   }

   public void subtractBendNo() {
      --this.bendNo;
   }

   public void setPunch(Tool p) {
      this.punch = p;
   }

   public void setDie(Tool d) {
      this.die = d;
   }

   public Tool getPunch() {
      return this.punch;
   }

   public Tool getDie() {
      return this.die;
   }

   public void setThickness(double t) {
      this.thickness = t;
   }

   public double getThickness() {
      return this.thickness;
   }

   public boolean checkAxes() {
      boolean test = true;

      for(int i = 0; i < this.axisOrder.size(); ++i) {
         if (!((Axis)this.axisOrder.get(i)).equals((Axis)Settings.axes.get(i))) {
            test = false;
         }
      }

      return test;
   }

   public ArrayList<String> getBendNotes() {
      return this.bendNotes;
   }

   public void setBendNotes(ArrayList<String> bendNotes) {
      this.bendNotes = bendNotes;
   }

   public Location getLocation() {
      return this.location;
   }

   public void setLocation(Location location) {
      this.location = location;

      for(int i = 0; i < this.getBends().size(); ++i) {
         ((Bend)this.getBends().get(i)).setLocation(location);
      }

   }

   public void initImageFolder() {
      try {
         if (this.location == Location.LOCAL) {
            (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.getName())).mkdirs();
            this.imagePath = SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.getName();
         } else if (Settings.selectedUSB != null) {
            (new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.getName())).mkdirs();
            this.imagePath = Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Images" + File.separator + this.getName();
         }
      } catch (Exception var2) {
      }

   }

   public String getImagePath() {
      return this.imagePath;
   }

   public void addBendImage(int bendNo, String path) {
      try {
         String ext = path.substring(path.length() - 4);
         Files.copy((new File(path)).toPath(), (new File(this.imagePath + File.separator + "Bend" + bendNo + ext)).toPath(), StandardCopyOption.REPLACE_EXISTING);
         ((Bend)this.bends.get(bendNo)).setBendImage(this.imagePath + File.separator + "Bend" + bendNo + ext);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public int compareTo(Job j) {
      switch($SWITCH_TABLE$com$automec$objects$enums$SortMethod()[RecallJobPage.sorting.ordinal()]) {
      case 1:
         return this.name.compareToIgnoreCase(j.name);
      case 2:
         if (this.created != null && j.created != null) {
            return this.created.compareTo(j.created);
         }
         break;
      case 3:
         if (this.edited != null && j.edited != null) {
            return this.edited.compareTo(j.edited);
         }
         break;
      case 4:
         if (this.ran != null && j.ran != null) {
            return this.ran.compareTo(j.ran);
         }
      }

      return 0;
   }

   public void saveJob(Location loc) {
      this.edited = LocalDateTime.now();
      this.setLocation(loc);
      this.initImageFolder();
      Settings.log.finest("save job memory button pressed");
      Gson gson = new Gson();
      Type jobType = (new TypeToken<Job>() {
      }).getType();
      Settings.log.finer(gson.toJson(this, jobType).toString());
      if (loc == Location.USB) {
         Settings.selectedUSB.writeJob(this.name, gson.toJson(this, jobType));
         Settings.log.fine("Job written to USB database");
      } else {
         SystemCommands.writeJob(this.getName(), gson.toJson(this, jobType));
         Settings.log.fine("Job written to database");
      }

   }

   public String toString() {
      return this.location.equals(Location.USB) ? "USB: " + this.name : this.name;
   }

   public void delete() {
      if (this.location.equals(Location.LOCAL)) {
         SystemCommands.deleteJob(this.name);
      } else {
         Settings.selectedUSB.deleteJob(this.name);
      }

   }

   public ArrayList<Axis> getAxes() {
      return this.axisOrder;
   }

   public void setUnits(Units u) {
      this.units = u;
   }

   public void setParts(int p) {
      this.parts = p;
   }

   public void setParts(Job j) {
      this.parts = j.getParts();
   }

   public int getParts() {
      return this.parts;
   }

   public void addPart() {
      ++this.parts;
   }

   public void resetParts() {
      this.parts = 0;
   }

   public String getVersionNo() {
      return this.versionNo;
   }

   public void setVersionNo(String versionNo) {
      this.versionNo = versionNo;
   }

   public LocalDateTime getRan() {
      return this.ran;
   }

   public void setRan() {
      this.ran = LocalDateTime.now();
   }

   public void setRan(LocalDateTime t) {
      this.ran = t;
   }

   public boolean getRetractEnabled() {
      return this.retractEnabled;
   }

   public void setRetractEnabled(boolean s) {
      this.retractEnabled = s;
   }

   public boolean getBaEnabled() {
      return this.baEnabled;
   }

   public void setBaEnabled(boolean s) {
      this.baEnabled = s;
   }

   public LocalDateTime getEdited() {
      return this.edited;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$objects$enums$SortMethod() {
      int[] var10000 = $SWITCH_TABLE$com$automec$objects$enums$SortMethod;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[SortMethod.values().length];

         try {
            var0[SortMethod.CREATED.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[SortMethod.EDITED.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[SortMethod.NAME.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[SortMethod.RAN.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$objects$enums$SortMethod = var0;
         return var0;
      }
   }
}
