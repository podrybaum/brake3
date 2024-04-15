package com.automec;

import com.automec.display.pages.RunJobPage;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.Axis;
import com.automec.objects.enums.AxisDirection;
import io.dvlopt.linux.i2c.I2CBuffer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class QueuedCommand {
   I2CBuffer buffer;
   I2CBuffer response;
   public static boolean lock = false;
   public int priority;

   public QueuedCommand(I2CBuffer buffer, int priority) {
      this.buffer = buffer;
      this.priority = priority;
      Listener.commandQueue.add(this);
   }

   public void runCommand() {
      try {
         lock = true;
         int i;
         if (this.buffer.get(1) != 83) {
            String t = "Command: " + String.format("%02X", this.buffer.get(1)) + " Sending: ";
            i = 0;

            while(true) {
               if (i >= this.buffer.length) {
                  Settings.log.finer(t);
                  break;
               }

               t = t + String.format("%02x", this.buffer.get(i)) + " ";
               ++i;
            }
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
               Thread.sleep(20L);
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

         Listener.ioErrorCount = 0;
      } catch (IOException var8) {
         String t = "IO exception when writing to I2CBuffer: " + var8.getMessage() + " \ncommand: ";

         for(int i = 0; i < this.buffer.length; ++i) {
            t = t + String.format("%02x", this.buffer.get(i)) + " ";
         }

         Settings.log.log(Level.WARNING, t, var8);
         ++Listener.ioErrorCount;
         if (Listener.ioErrorCount > 50) {
            new NotificationPage("I2C Error", "There is a communications problem between the MAC and GUI", new ActionListener() {
               public void actionPerformed(ActionEvent arg0) {
                  ((JFrame)SwingUtilities.getRoot((Component)arg0.getSource())).dispose();
               }
            });
         }

         new QueuedCommand(this.buffer, this.priority);
      } catch (Exception var9) {
         Settings.log.log(Level.SEVERE, "Listener", var9);
      } finally {
         lock = false;
      }

   }
}
