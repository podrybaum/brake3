package com.microchip.mcp2221;

public class HidFeatures {
   static {
      if (System.getProperty("sun.arch.data.model").equals("32")) {
         try {
            System.loadLibrary("libmcp2221_jni_x86");
            System.out.println("Windows x86 loaded");
         } catch (Exception var2) {
            System.out.println("The specified library does not exist");
         }
      } else if (System.getProperty("sun.arch.data.model").equals("64")) {
         try {
            System.loadLibrary("libmcp2221_jni_x64");
            System.out.println("Windows x64 loaded");
         } catch (Exception var1) {
            System.out.println("The specified library does not exist");
         }
      }

   }

   public native int Mcp2221_LoadDll();

   public native String Mcp2221_GetLibraryVersion();

   public native int Mcp2221_GetConnectedDevices(int var1, int var2);

   public native long Mcp2221_OpenByIndex(int var1, int var2, int var3);

   public native long Mcp2221_OpenBySN(int var1, int var2, String var3);

   public native int Mcp2221_Close(long var1);

   public native int Mcp2221_CloseAll();

   public native int Mcp2221_Reset(long var1);

   public native int Mcp2221_SetSpeed(long var1, int var3);

   public native int Mcp2221_SetAdvancedCommParams(long var1, short var3, short var4);

   public native int Mcp2221_I2cCancelCurrentTransfer(long var1);

   public native int Mcp2221_I2cRead(long var1, int var3, byte var4, boolean var5, byte[] var6);

   public native int Mcp2221_I2cWrite(long var1, int var3, byte var4, boolean var5, byte[] var6);

   public native int Mcp2221_SmbusWriteByte(long var1, byte var3, boolean var4, boolean var5, byte var6, byte var7);

   public native int Mcp2221_SmbusReadByte(long var1, byte var3, boolean var4, boolean var5, byte var6, byte[] var7);

   public native int Mcp2221_SmbusWriteWord(long var1, byte var3, boolean var4, boolean var5, byte var6, byte[] var7);

   public native int Mcp2221_SmbusReadWord(long var1, byte var3, boolean var4, boolean var5, byte var6, byte[] var7);

   public native int Mcp2221_SmbusBlockWrite(long var1, byte var3, boolean var4, boolean var5, byte var6, short var7, byte[] var8);

   public native int Mcp2221_SmbusBlockRead(long var1, byte var3, boolean var4, boolean var5, byte var6, short var7, byte[] var8);

   public native String Mcp2221_GetManufacturerDescriptor(long var1);

   public native int Mcp2221_SetManufacturerDescriptor(long var1, String var3);

   public native String Mcp2221_GetProductDescriptor(long var1);

   public native int Mcp2221_SetProductDescriptor(long var1, String var3);

   public native String Mcp2221_GetSerialNumberDescriptor(long var1);

   public native int Mcp2221_SetSerialNumberDescriptor(long var1, String var3);

   public native String Mcp2221_GetFactorySerialNumber(long var1);

   public native int Mcp2221_GetVidPid(long var1, int[] var3, int[] var4);

   public native int Mcp2221_SetVidPid(long var1, int var3, int var4);

   public native int Mcp2221_GetInitialPinValues(long var1, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7);

   public native int Mcp2221_SetInitialPinValues(long var1, byte var3, byte var4, byte var5, byte var6, byte var7);

   public native int Mcp2221_GetUsbPowerAttributes(long var1, short[] var3, int[] var4);

   public native int Mcp2221_SetUsbPowerAttributes(long var1, short var3, int var4);

   public native int Mcp2221_GetSerialNumberEnumerationEnable(long var1, byte[] var3);

   public native int Mcp2221_SetSerialNumberEnumerationEnable(long var1, byte var3);

   public native int Mcp2221_GetSecuritySetting(long var1, byte[] var3);

   public native int Mcp2221_SetSecuritySetting(long var1, byte var3, String var4, String var5);

   public native int Mcp2221_SetPermanentLock(long var1);

   public native int Mcp2221_SendPassword(long var1, String var3);

   public native int Mcp2221_GetInterruptEdgeSetting(long var1, byte var3, byte[] var4);

   public native int Mcp2221_SetInterruptEdgeSetting(long var1, byte var3, byte var4);

   public native int Mcp2221_ClearInterruptPinFlag(long var1);

   public native int Mcp2221_GetInterruptPinFlag(long var1, byte[] var3);

   public native String Mcp2221_GetHardwareRevision(long var1);

   public native String Mcp2221_GetFirmwareRevision(long var1);

   public native int Mcp2221_GetClockSettings(long var1, byte var3, byte[] var4, byte[] var5);

   public native int Mcp2221_SetClockSettings(long var1, byte var3, byte var4, byte var5);

   public native int Mcp2221_GetGpioSettings(long var1, byte var3, byte[] var4, byte[] var5, byte[] var6);

   public native int Mcp2221_SetGpioSettings(long var1, byte var3, byte[] var4, byte[] var5, byte[] var6);

   public native int Mcp2221_GetGpioValues(long var1, byte[] var3);

   public native int Mcp2221_SetGpioValues(long var1, byte[] var3);

   public native int Mcp2221_GetGpioDirection(long var1, byte[] var3);

   public native int Mcp2221_SetGpioDirection(long var1, byte[] var3);

   public native int Mcp2221_GetDacVref(long var1, byte var3, byte[] var4);

   public native int Mcp2221_SetDacVref(long var1, byte var3, byte var4);

   public native int Mcp2221_GetDacValue(long var1, byte var3, byte[] var4);

   public native int Mcp2221_SetDacValue(long var1, byte var3, byte var4);

   public native int Mcp2221_GetAdcVref(long var1, byte var3, byte[] var4);

   public native int Mcp2221_SetAdcVref(long var1, byte var3, byte var4);

   public native int Mcp2221_GetAdcData(long var1, int[] var3);

   public native int Mcp2221_GetLastError();
}
