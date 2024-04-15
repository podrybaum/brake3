package com.automec.objects;

import com.automec.Settings;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionStamp {
   public Date timestamp;
   public int xPos;
   public int yPos;
   public int rPos;

   public PositionStamp(Date ts, int x, int y, int r) {
      this.timestamp = ts;
      this.xPos = x;
      this.yPos = y;
      this.rPos = r;
   }

   public String toString() {
      SimpleDateFormat ft = new SimpleDateFormat("yy.mm.dd hh:mm:ss.SSS");
      return ft.format(this.timestamp) + " x: " + this.xPos + " y: " + this.yPos + " r: " + this.rPos;
   }

   public void print() {
      Settings.log.info(this.toString());
   }
}
