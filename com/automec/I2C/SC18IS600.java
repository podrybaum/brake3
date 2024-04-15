package com.automec.I2C;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import java.io.IOException;

public class SC18IS600 {
   private static final byte SC18IS600_MAXBYTES = 96;
   private static final byte SC18IS600_WRITENBYTES = 0;
   private static final byte SC18IS600_READNBYTES = 1;
   private static final byte SC18IS600_READAFTERWRITE = 2;
   private static final byte SC18IS600_WRITEAFTERWRITE = 3;
   private static final byte SC18IS600_READBUFFER = 6;
   private static final byte SC18IS600_SPICONFIG = 24;
   private static final byte SC18IS600_WRITETOREGISTER = 32;
   private static final byte SC18IS600_READFROMREGISTER = 33;
   private static final byte SC18IS600_POWERDOWNMODE = 48;
   private static final byte IOConfig = 0;
   private static final byte IOState = 1;
   private static final byte I2CClock = 2;
   private static final byte I2CTO = 3;
   private static final byte I2CStat = 4;
   private static final byte I2CAdr = 5;
   private static final byte QBIDOut = 0;
   private static final byte Input = 1;
   private static final byte PushPullOut = 2;
   private static final byte OpenDrainOut = 3;
   private static final byte IO3_1_bp = 7;
   private static final byte IO3_0_bp = 6;
   private static final byte IO2_1_bp = 5;
   private static final byte IO2_0_bp = 4;
   private static final byte IO1_1_bp = 3;
   private static final byte IO1_0_bp = 2;
   private static final byte IO0_1_bp = 1;
   private static final byte IO0_0_bp = 0;
   private static final byte IO5_bp = 5;
   private static final byte IO4_bp = 4;
   private static final byte GPIO3_bp = 3;
   private static final byte GPIO2_bp = 2;
   private static final byte GPIO1_bp = 1;
   private static final byte GPIO0_bp = 0;
   private static final byte I2CClk_MIN = 5;
   private static final byte I2CClk_MAX = -1;
   private SpiDevice spi = null;
   private static final int SPEED = 125000;
   private GpioPinDigitalOutput pin = null;
   private int slave = 0;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$automec$I2C$SC18IS600$I2CSTATUS;

   public SC18IS600() throws IOException {
      GpioController gpio = GpioFactory.getInstance();
      this.pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Reset", PinState.HIGH);
      this.spi = SpiFactory.getInstance(SpiChannel.CS0, 125000, SpiMode.MODE_3);
      this.readFromRegister((byte)4);
   }

   private byte[] writeToSlave(byte size, byte address, byte[] data) throws IOException {
      if (size > 96) {
         throw new IOException("Maximum buffer size exceded");
      } else if (address > 127) {
         throw new IOException("Invalid address");
      } else if (size != data.length) {
         throw new IOException("Size does not match data buffer length");
      } else {
         byte command = 0;
         byte f_address = (byte)(address << 1);
         byte[] send = new byte[size + 3];
         send[0] = command;
         send[1] = size;
         send[2] = f_address;
         System.arraycopy(data, 0, send, 3, size);
         return this.spi.write(send);
      }
   }

   private void readFromSlave(byte size, byte address) throws IOException {
      if (size > 96) {
         throw new IOException("Maximum buffer size exceded");
      } else if (address > 127) {
         throw new IOException("Invalid address");
      } else {
         byte command = 1;
         byte f_address = (byte)(address << 1);
         byte[] send = new byte[]{command, size, f_address};
         this.spi.write(send);
      }
   }

   private byte[] readAfterWrite(byte writeSize, byte readSize, byte writeAddress, byte readAddress, byte[] data) throws IOException {
      if (writeSize > 96) {
         throw new IOException("Maximum buffer size exceded");
      } else if (writeAddress > 127) {
         throw new IOException("Invalid write address");
      } else if (readAddress > 127) {
         throw new IOException("Invalid read address");
      } else if (writeSize != data.length) {
         throw new IOException("Size does not match data buffer length");
      } else {
         byte command = 2;
         byte f_writeAddress = (byte)(writeAddress << 1);
         byte f_readAddress = (byte)(readAddress << 1);
         byte[] send = new byte[writeSize + 5];
         send[0] = command;
         send[1] = writeSize;
         send[2] = readSize;
         send[3] = f_writeAddress;
         System.arraycopy(data, 0, send, 4, writeSize);
         send[send.length - 1] = f_readAddress;
         return this.spi.write(data);
      }
   }

   private byte[] readBuffer(byte size) throws IOException {
      if (size > 96) {
         throw new IOException("Maximum buffer size exceded");
      } else {
         byte[] send = new byte[size + 1];
         send[0] = 6;
         return this.spi.write(send);
      }
   }

   private byte[] writeAfterWrite(byte writeSize1, byte writeSize2, byte writeAddress1, byte writeAddress2, byte[] data1, byte[] data2) throws IOException {
      if (writeSize1 > 96) {
         throw new IOException("Maximum buffer size exceded");
      } else if (writeAddress1 > 127) {
         throw new IOException("Invalid write address");
      } else if (writeAddress2 > 127) {
         throw new IOException("Invalid read address");
      } else if (writeSize1 != data1.length) {
         throw new IOException("Size does not match data buffer length");
      } else {
         byte command = 3;
         byte f_writeAddress1 = (byte)(writeAddress1 << 1);
         byte f_writeAddress2 = (byte)(writeAddress2 << 1);
         byte[] send = new byte[writeSize1 + 5];
         send[0] = command;
         send[1] = writeSize1;
         send[2] = writeSize2;
         send[3] = f_writeAddress1;
         System.arraycopy(data1, 0, send, 4, writeSize1);
         send[4 + writeSize1] = f_writeAddress2;
         System.arraycopy(data2, 0, send, 4 + writeSize1 + 1, writeSize2);
         return this.spi.write(data1);
      }
   }

   private void configureSPI(SC18IS600.SPICONFIG conf) throws IOException {
      byte[] send = new byte[]{24, 0};
      if (conf.equals(SC18IS600.SPICONFIG.LSBFIRST)) {
         send[1] = -127;
      } else {
         if (!conf.equals(SC18IS600.SPICONFIG.MSBFIRST)) {
            throw new IOException("Invalid configuration");
         }

         send[1] = 66;
      }

      this.spi.write(send);
   }

   private void writeToRegister(byte register, byte data) throws IOException {
      byte[] send = new byte[]{32, register, data};
      this.spi.write(send);
   }

   private byte[] readFromRegister(byte register) throws IOException {
      byte[] send = new byte[]{33, register, -1};
      return this.spi.write(send);
   }

   private void powerDownMode() throws IOException {
      byte[] send = new byte[]{48, 90, -91};
      this.spi.write(send);
   }

   private void configureI2CClk(int speed) throws IOException {
      byte out = (byte)(7372800 / (4 * speed));
      if (Byte.toUnsignedInt(out) < 5) {
         out = 5;
      }

      this.writeToRegister((byte)2, out);
   }

   private void configureI2CTO(boolean enable, long nanos) throws IOException {
   }

   private SC18IS600.I2CSTATUS getI2CStatus() throws IOException {
      byte[] rx = new byte[3];
      rx = this.readFromRegister((byte)4);
      switch(Byte.toUnsignedInt(rx[2])) {
      case 240:
         return SC18IS600.I2CSTATUS.TRANS_SUCCESS;
      case 241:
         return SC18IS600.I2CSTATUS.ADDR_NAK;
      case 242:
         return SC18IS600.I2CSTATUS.BYTE_NAK;
      case 243:
         return SC18IS600.I2CSTATUS.BUSY;
      case 244:
      case 245:
      case 246:
      case 247:
      default:
         throw new IOException("Unhandled I2C Status");
      case 248:
         return SC18IS600.I2CSTATUS.TIMEOUT;
      case 249:
         return SC18IS600.I2CSTATUS.BAD_CNT;
      }
   }

   private boolean isReadReady() throws IOException {
      return !this.getI2CStatus().equals(SC18IS600.I2CSTATUS.BUSY);
   }

   public void setSlave(int slave) {
      this.slave = slave;
   }

   public int write(byte[] buf) throws IOException, InterruptedException {
      this.writeToSlave((byte)buf.length, (byte)this.slave, buf);
      Thread.sleep(0L, 20000);

      while(!this.isReadReady()) {
         Thread.sleep(0L, 5000);
      }

      switch($SWITCH_TABLE$com$automec$I2C$SC18IS600$I2CSTATUS()[this.getI2CStatus().ordinal()]) {
      case 1:
         return 0;
      default:
         return 1;
      }
   }

   public byte[] read(int size) throws IOException, InterruptedException {
      this.readFromSlave((byte)size, (byte)this.slave);
      Thread.sleep(0L, 20000);

      while(!this.isReadReady()) {
         Thread.sleep(0L, 5000);
      }

      byte[] read = this.readBuffer((byte)size);
      byte[] ret = new byte[size];
      System.arraycopy(read, 1, ret, 0, size);
      return ret;
   }

   public void close() {
      System.out.println("Closing does nothing yet!");
   }

   public void setI2CSpeed(int speed) throws IOException {
      this.configureI2CClk(speed);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$automec$I2C$SC18IS600$I2CSTATUS() {
      int[] var10000 = $SWITCH_TABLE$com$automec$I2C$SC18IS600$I2CSTATUS;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[SC18IS600.I2CSTATUS.values().length];

         try {
            var0[SC18IS600.I2CSTATUS.ADDR_NAK.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[SC18IS600.I2CSTATUS.BAD_CNT.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[SC18IS600.I2CSTATUS.BUSY.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[SC18IS600.I2CSTATUS.BYTE_NAK.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[SC18IS600.I2CSTATUS.TIMEOUT.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[SC18IS600.I2CSTATUS.TRANS_SUCCESS.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$automec$I2C$SC18IS600$I2CSTATUS = var0;
         return var0;
      }
   }

   public static enum I2CSTATUS {
      TRANS_SUCCESS,
      ADDR_NAK,
      BYTE_NAK,
      BUSY,
      TIMEOUT,
      BAD_CNT;
   }

   public static enum SPICONFIG {
      LSBFIRST,
      MSBFIRST;
   }
}
