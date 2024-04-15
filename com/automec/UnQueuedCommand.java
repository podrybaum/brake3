package com.automec;

import com.automec.display.pages.RunJobPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AxisDirection;
import io.dvlopt.linux.i2c.I2CBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

public class UnQueuedCommand {
   I2CBuffer buffer;
   I2CBuffer response;
   public int priority;

   public UnQueuedCommand(I2CBuffer buffer, int priority) {
      this.buffer = buffer;
      this.priority = priority;
   }

   public void runCommand() {
      try {
         QueuedCommand.lock = true;
         if (this.buffer.get(1) != 255) {
            int i;
            if (this.buffer.get(1) != 83) {
               String t = "Command: " + String.format("%02X", this.buffer.get(1)) + " Sending: ";

               for(i = 0; i < this.buffer.length; ++i) {
                  t = t + String.format("%02x", this.buffer.get(i)) + " ";
               }

               Settings.log.finer(t);
            }

            Communications.bus.write(this.buffer);
            Thread.sleep(1L);
            if (this.buffer.get(1) == 83) {
               this.response = new I2CBuffer(46);
               Thread.sleep(20L);
               Communications.bus.read(this.response);
               if (this.response.get(1) != 21 && this.response.get(0) == 173) {
                  Communications.statusResponse = this.response;
                  Communications.updateStatus();
               } else {
                  Settings.log.finer("MAC sends NAK");
               }
            } else {
               this.response = new I2CBuffer(2);
               Communications.bus.read(this.response);
               if (SystemCommands.isRPi()) {
                  Thread.sleep(15L);
               }

               if (this.buffer.get(1) == 103) {
                  ArrayList<Integer> tmp = new ArrayList();

                  for(i = 0; i < this.response.length; ++i) {
                     tmp.add(this.response.get(i));
                  }
               }

               if (this.response.get(0) == SystemCommands.getTwosComp(this.buffer.get(1)) && this.response.get(1) == 6) {
                  Communications.bus.write((new I2CBuffer(1)).set(0, 6));
                  if (this.buffer.get(1) == 83 || this.buffer.get(1) == 88) {
                     System.out.println("resp: " + this.response.get(0) + ":" + this.response.get(1));
                  }

                  this.response.clear();
                  if (this.buffer.get(1) == 54) {
                     if (this.buffer.get(4) == 7) {
                        RunJobPage.xAxisInitialPosition = ((Axis)Settings.axes.get(0)).getPosition();
                        if (this.buffer.get(2) == 2) {
                           RunJobPage.xAxisTargetDirection = AxisDirection.IN;
                        } else {
                           RunJobPage.xAxisTargetDirection = AxisDirection.OUT;
                        }

                        RunJobPage.xAxisTargetPosition = ByteBuffer.wrap(new byte[]{(byte)this.buffer.get(9), (byte)this.buffer.get(10), (byte)this.buffer.get(11), (byte)this.buffer.get(12)}).getInt();
                        RunJobPage.xAxisMovingStart = System.currentTimeMillis();
                        Listener.start.add(RunJobPage.xAxisMovingStart);
                        RunJobPage.xAxisMoving = true;
                     } else {
                        this.buffer.get(4);
                     }
                  } else {
                     this.buffer.get(1);
                  }

                  Settings.log.finer("Command: " + String.format("%02X", this.buffer.get(1)) + " Final response from MAC: " + String.format("%02x", this.response.get(0)) + ":" + String.format("%02x", this.response.get(1)));
               } else {
                  Settings.log.finer("MAC SENDS NAK: Command: " + String.format("%02X", this.buffer.get(1)) + " " + String.format("%02x", this.response.get(0)) + ":" + String.format("%02x", this.response.get(1)));
               }
            }
         }
      } catch (IOException var8) {
         String t = "IO exception when writing to I2CBuffer: " + var8.getMessage() + " \ncommand: ";

         for(int i = 0; i < this.buffer.length; ++i) {
            t = t + String.format("%02x", this.buffer.get(i)) + " ";
         }

         Settings.log.log(Level.WARNING, t, var8);
      } catch (Exception var9) {
         Settings.log.log(Level.SEVERE, "Listener", var9);
      } finally {
         QueuedCommand.lock = false;
      }

   }
}
