package com.automec;

import java.util.ArrayList;

public class Recording {
   private ArrayList<Long> time = new ArrayList();
   private ArrayList<Integer> position = new ArrayList();
   private ArrayList<Integer> expectedPos = new ArrayList();
   private ArrayList<Long> startTime = new ArrayList();
   private ArrayList<Long> stopTime = new ArrayList();

   public ArrayList<Long> getTimes() {
      return this.time;
   }

   public ArrayList<Integer> getPositions() {
      return this.position;
   }

   public ArrayList<Integer> getExpected() {
      return this.expectedPos;
   }

   public void updateStartStop(ArrayList<Long> start, ArrayList<Long> stop) {
      this.startTime = start;
      this.stopTime = stop;
   }

   public ArrayList<Long> getStart() {
      return this.startTime;
   }

   public ArrayList<Long> getStop() {
      return this.stopTime;
   }

   public void addPoint(long time, int position, int expected) {
      this.time.add(time);
      this.position.add(position);
      this.expectedPos.add(expected);
   }
}
