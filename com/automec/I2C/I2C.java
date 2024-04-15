package com.automec.I2C;

import com.microchip.mcp2221.HidFeatures;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;
import io.dvlopt.linux.i2c.I2CBus;
import io.dvlopt.linux.i2c.I2CTransaction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class I2C {
   private static Driver driver;
   private I2CBus lBus;
   private HidFeatures wBus;
   private final int wVid = 1240;
   private final int wPid = 221;
   private byte wSlaveAddress;
   private long wHandle;
   private static int retries = 0;
   private SC18IS600 pBus;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$I2C$Driver;

   public I2C() {
      if (System.getProperty("os.name").contains("Windows")) {
         driver = Driver.WINDOWS;
      } else if (isRPi()) {
         driver = Driver.RASPBIAN;
      } else {
         driver = Driver.LINUX;
      }

      try {
         System.out.println(driver);
         this.initializeI2C(driver);
      } catch (Exception var2) {
         System.out.println("I2C initialization Exception");
         var2.printStackTrace();
      }

   }

   private void initializeI2C(Driver d) throws Exception {
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[d.ordinal()]) {
      case 1:
         this.wBus = new HidFeatures();
         int e = this.wBus.Mcp2221_LoadDll();
         if (e != 0) {
            throw new Exception("Cant load MCP2221 Native DLL: " + e);
         }

         int count = this.wBus.Mcp2221_GetConnectedDevices(1240, 221);
         if (count == 0) {
            throw new Exception("No MCP2221 connected");
         }

         this.wHandle = this.wBus.Mcp2221_OpenByIndex(1240, 221, 0);
         if (this.wHandle < 0L) {
            throw new Exception("Bad Handle");
         }

         this.wBus.Mcp2221_SetAdvancedCommParams(this.wHandle, (short)1, (short)2);
         int x = this.wBus.Mcp2221_SetSpeed(this.wHandle, 500000);
         if (x != 0) {
            this.wBus.Mcp2221_I2cCancelCurrentTransfer(this.wHandle);
            System.out.println("Speed err: " + x);
            x = this.wBus.Mcp2221_SetSpeed(this.wHandle, 500000);
         }
         break;
      case 2:
         PlatformManager.setPlatform(Platform.RASPBERRYPI);
         this.pBus = new SC18IS600();
         break;
      case 3:
         String output = "";
         Process p = Runtime.getRuntime().exec("i2cdetect -l");
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String s = null;

         while(true) {
            do {
               if ((s = stdInput.readLine()) == null) {
                  String[] words = output.split("\t");
                  String i2c = words[0];
                  this.lBus = new I2CBus("/dev/" + i2c);
                  return;
               }
            } while(!s.contains("i2c-mcp2221") && !s.contains("bcm2835"));

            output = s;
         }
      default:
         throw new Exception("System not Supported");
      }

   }

   public void selectSlave(int s) throws Exception {
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[driver.ordinal()]) {
      case 1:
         this.wSlaveAddress = (byte)s;
         break;
      case 2:
         this.pBus.setSlave(s);
         break;
      case 3:
         this.lBus.selectSlave(s);
      }

   }

   public int write(I2CBuffer b) throws Exception {
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[driver.ordinal()]) {
      case 1:
         int r = this.wBus.Mcp2221_I2cWrite(this.wHandle, b.getSize(), this.wSlaveAddress, true, b.getData());
         if (r != 0) {
            if (r != -407) {
               System.out.println("I2C write returns error: " + r);
            }

            return r;
         }
         break;
      case 2:
         return this.pBus.write(b.getData());
      case 3:
         this.lBus.write(b.getNativeBuffer());
      }

      return 0;
   }

   public int transaction(I2CTransaction t) {
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[driver.ordinal()]) {
      case 3:
         try {
            this.lBus.doTransaction(t);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      default:
         return 0;
      }
   }

   public void read(I2CBuffer b) throws Exception {
      int i;
      byte[] rd;
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[driver.ordinal()]) {
      case 1:
         rd = new byte[b.getSize()];
         this.wBus.Mcp2221_I2cRead(this.wHandle, b.getSize(), this.wSlaveAddress, true, rd);

         for(int i = 0; i < b.getSize(); ++i) {
            b.set(i, rd[i]);
         }

         retries = 0;
         break;
      case 2:
         rd = this.pBus.read(b.getSize());

         for(i = 0; i < b.getSize(); ++i) {
            b.set(i, rd[i]);
         }

         return;
      case 3:
         io.dvlopt.linux.i2c.I2CBuffer t = new io.dvlopt.linux.i2c.I2CBuffer(b.getSize());
         this.lBus.read(t);

         for(i = 0; i < t.length; ++i) {
            b.set(i, t.get(i));
         }
      }

   }

   public void close() throws Exception {
      switch($SWITCH_TABLE$com$automec$I2C$Driver()[driver.ordinal()]) {
      case 1:
         this.wBus.Mcp2221_Close(this.wHandle);
         break;
      case 2:
         this.pBus.close();
         break;
      case 3:
         this.lBus.close();
      }

   }

   private static boolean isRPi() {
      if (System.getProperty("os.name").equals("Linux")) {
         File file = new File("/etc", "os-release");

         try {
            Throwable var1 = null;
            Object var2 = null;

            try {
               FileInputStream fis = new FileInputStream(file);

               label374: {
                  try {
                     BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                     String string;
                     try {
                        while((string = br.readLine()) != null) {
                           if (string.toLowerCase().contains("raspbian") && string.toLowerCase().contains("name")) {
                              break label374;
                           }
                        }
                     } finally {
                        if (br != null) {
                           br.close();
                        }

                     }
                  } catch (Throwable var19) {
                     if (var1 == null) {
                        var1 = var19;
                     } else if (var1 != var19) {
                        var1.addSuppressed(var19);
                     }

                     if (fis != null) {
                        fis.close();
                     }

                     throw var1;
                  }

                  if (fis != null) {
                     fis.close();
                  }

                  return false;
               }

               if (fis != null) {
                  fis.close();
               }

               return true;
            } catch (Throwable var20) {
               if (var1 == null) {
                  var1 = var20;
               } else if (var1 != var20) {
                  var1.addSuppressed(var20);
               }

               throw var1;
            }
         } catch (Exception var21) {
            var21.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$I2C$Driver() {
      int[] var10000 = $SWITCH_TABLE$com$automec$I2C$Driver;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Driver.values().length];

         try {
            var0[Driver.LINUX.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Driver.RASPBIAN.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Driver.WINDOWS.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$I2C$Driver = var0;
         return var0;
      }
   }
}
