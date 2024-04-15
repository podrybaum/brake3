package com.automec.objects;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.objects.enums.Location;
import com.automec.objects.enums.ToolType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Tool implements Comparable<Tool> {
   private String name;
   private ToolType type;
   private double height;
   private double punchRadius;
   private double dieWidth;
   private double dieRadius;
   private Location location;
   private String sqlKey;
   private String image;

   public Tool(String name, ToolType type) {
      this.name = name;
      this.type = type;
      String ttype = "";
      if (this.type.equals(ToolType.PUNCH)) {
         ttype = "PUNCH";
      } else {
         ttype = "DIE";
      }

      this.sqlKey = name + ttype;
   }

   public ToolType getType() {
      return this.type;
   }

   public void setHeight(double height) {
      this.height = height;
   }

   public void setPunchRadius(double radius) {
      if (this.type == ToolType.PUNCH) {
         this.punchRadius = radius;
      }

   }

   public void setDieWidth(double width) {
      if (this.type == ToolType.DIE) {
         this.dieWidth = width;
      }

   }

   public void setDieRadius(double radius) {
      if (this.type == ToolType.DIE) {
         this.dieRadius = radius;
      }

   }

   public double getHeight() {
      return this.height;
   }

   public double getPunchRadius() {
      return this.type == ToolType.PUNCH ? this.punchRadius : 0.0D;
   }

   public double getDieWidth() {
      return this.type == ToolType.DIE ? this.dieWidth : 0.0D;
   }

   public double getDieRadius() {
      return this.type == ToolType.DIE ? this.dieRadius : 0.0D;
   }

   private void setLocation(Location loc) {
      this.location = loc;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.location.equals(Location.USB) ? "USB: " + this.name : this.name;
   }

   public void delete() {
      if (this.location.equals(Location.LOCAL)) {
         SystemCommands.deleteTool(this.name, this.type);
      } else {
         Settings.selectedUSB.deleteTool(this.name, this.type);
      }

   }

   public void save(Location loc) {
      this.setLocation(loc);
      Type toolType = (new TypeToken<Tool>() {
      }).getType();
      String type = "";
      if (this.type.equals(ToolType.PUNCH)) {
         type = "PUNCH";
      } else {
         type = "DIE";
      }

      if (this.location == Location.USB) {
         Settings.selectedUSB.writeTool(this.sqlKey, type, (new Gson()).toJson(this, toolType).toString());
      } else {
         SystemCommands.writeTool(this.sqlKey, type, (new Gson()).toJson(this, toolType).toString());
      }

   }

   public int compareTo(Tool t) {
      return this.name.compareToIgnoreCase(t.name);
   }

   public String getImage() {
      return this.image;
   }

   public void setImage(String image) {
      this.image = image;
   }
}
