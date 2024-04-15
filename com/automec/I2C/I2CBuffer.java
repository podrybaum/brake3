package com.automec.I2C;

public class I2CBuffer {
   private int size;
   private byte[] data;
   public int length;

   public I2CBuffer(int size) {
      this.size = size;
      this.length = size;
      this.data = new byte[size];
   }

   public I2CBuffer set(int i, byte b) {
      this.data[i] = b;
      return this;
   }

   public I2CBuffer set(int i, int b) {
      return this.set(i, (byte)b);
   }

   public int getSize() {
      return this.size;
   }

   public byte get(int i) {
      return this.data[i];
   }

   public byte[] getData() {
      return this.data;
   }

   public void clear() {
      this.data = new byte[this.size];
   }

   public String toString() {
      String r = "";

      for(int i = 0; i < this.data.length; ++i) {
         r = r + String.format("%02X", this.data[i] & 255) + " ";
      }

      return r;
   }

   public io.dvlopt.linux.i2c.I2CBuffer getNativeBuffer() {
      io.dvlopt.linux.i2c.I2CBuffer t = new io.dvlopt.linux.i2c.I2CBuffer(this.getSize());

      for(int i = 0; i < this.getSize(); ++i) {
         t.set(i, this.get(i));
      }

      return t;
   }
}
