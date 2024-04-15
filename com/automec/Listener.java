package com.automec;

import com.automec.display.components.DisplayComponents;
import com.automec.display.pages.HiddenSettingsPage;
import com.automec.display.pages.HomePage;
import com.automec.display.pages.RunJobPage;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.Axis;
import com.automec.objects.AxisValues;
import com.automec.objects.Bend;
import com.automec.objects.enums.AdvanceMode;
import com.automec.objects.enums.AdvancePosition;
import com.automec.objects.enums.AutoMode;
import com.automec.objects.enums.AxisDirection;
import com.google.gson.Gson;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.Timer;

public class Listener {
   public static boolean retract = false;
   public static String retractType = "";
   public static int topPosition = Integer.MAX_VALUE;
   public static int oldTopPosition;
   public static int gaugeDelayTime;
   public static boolean delayGR;
   public static int usbCount;
   public static long wait;
   public static boolean xTurnAround;
   public static Long xTurnAroundTime;
   public static boolean watched;
   public static double xAxisAccelerationAdj;
   public static ArrayList<Long> start;
   public static ArrayList<Long> stop;
   public static boolean retractDelay;
   public static boolean resetDelay;
   public static UnQueuedCommand sendStatus;
   private static boolean retracting;
   public static int ioErrorCount;
   public static Timer screenSaverStopper;
   static long execTime;
   static int execCnt;
   public static double retPos;
   public static Comparator<QueuedCommand> commandComparator;
   public static PriorityQueue<QueuedCommand> commandQueue;
   public static Thread usbDetector;
   public static Thread getStatus;
   public static Thread sendCommands;
   public static Thread advance;
   public static Thread calibrateWatcher;
   public static Thread motionErrorX;

   static {
      oldTopPosition = topPosition;
      gaugeDelayTime = 0;
      delayGR = false;
      usbCount = 0;
      wait = 100L;
      xTurnAround = false;
      watched = false;
      xAxisAccelerationAdj = 1.0D;
      start = new ArrayList();
      stop = new ArrayList();
      retractDelay = false;
      resetDelay = false;
      retracting = false;
      ioErrorCount = 0;
      execTime = 0L;
      execCnt = 0;
      commandComparator = new Comparator<QueuedCommand>() {
         public int compare(QueuedCommand q1, QueuedCommand q2) {
            try {
               int var10000 = q1.priority;
               var10000 = q2.priority;
            } catch (Exception var6) {
               Settings.log.log(Level.SEVERE, "compare threw an error", var6);
            } finally {
               ;
            }

            return 0;
         }
      };
      commandQueue = new PriorityQueue(commandComparator);
      usbDetector = new Thread(new Runnable() {
         public void run() {
            while(true) {
               try {
                  File[] f;
                  if (SystemCommands.getOS().equals("Linux")) {
                     f = (new File("/media")).listFiles();
                     if (f.length != Listener.usbCount) {
                        SystemCommands.scanUSBDrives();
                        Listener.usbCount = f.length;
                     }
                  } else {
                     f = File.listRoots();
                     if (f.length != Listener.usbCount) {
                        SystemCommands.scanUSBDrives();
                        Listener.usbCount = f.length;
                     }
                  }

                  Thread.sleep(500L);
               } catch (Exception var2) {
                  Settings.log.severe("usbDetector thread has encountered an exception");
                  Settings.log.log(Level.SEVERE, "", var2);
               }
            }
         }
      });
      getStatus = new Thread(new Runnable() {
         public void run() {
            while(true) {
               if (!Listener.retractDelay) {
                  Communications.sendStatus();
               }

               try {
                  if (SystemCommands.isRPi()) {
                     Thread.sleep(70L);
                  } else {
                     Thread.sleep(40L);
                  }
               } catch (Exception var2) {
                  Settings.log.severe("Send status thread has encountered an exception");
                  Settings.log.log(Level.SEVERE, "", var2);
               }
            }
         }
      });
      sendCommands = new Thread(new Runnable() {
         public void run() {
            int count = 0;

            while(true) {
               int i;
               if (!Listener.commandQueue.isEmpty()) {
                  ++count;

                  try {
                     i = 2500;

                     while(QueuedCommand.lock) {
                        Thread.sleep(0L, 100000);
                        --i;
                        if (i <= 0) {
                           Settings.log.log(Level.WARNING, "Timeout waiting for QueuedCommand to finish");
                           break;
                        }
                     }

                     if (Listener.retractDelay) {
                        Listener.sendStatus.runCommand();
                     } else if (!Listener.resetDelay) {
                        ((QueuedCommand)Listener.commandQueue.poll()).runCommand();
                     }

                     if (count % 25 == 0 && Listener.commandQueue.size() > 25 && !HiddenSettingsPage.ignoreQueue) {
                        Settings.log.warning("Command queue has grown large: " + Listener.commandQueue.size());
                     }

                     if (Listener.commandQueue.size() > 100) {
                        Listener.commandQueue.clear();
                     }

                     if (count > 100) {
                        count = 0;
                     }
                  } catch (Exception var3) {
                     Settings.log.severe("Send Commands Thread has encountered an error");
                     Settings.log.log(Level.SEVERE, "", var3);
                  }
               } else {
                  Listener.sendStatus.runCommand();
               }

               try {
                  i = 2500;

                  while(QueuedCommand.lock) {
                     Thread.sleep(0L, 100000);
                     --i;
                     if (i <= 0) {
                        Settings.log.log(Level.WARNING, "Timeout waiting for QueuedCommand to finish");
                        break;
                     }
                  }
               } catch (Exception var4) {
                  Settings.log.severe("Send Commands Thread has encountered an error");
                  Settings.log.log(Level.SEVERE, "", var4);
               }
            }
         }
      });
      advance = new Thread(new Runnable() {
         public void run() {
            Settings.log.info("Starting advance thread");

            while(true) {
               while(true) {
                  try {
                     if (RunJobPage.autoMode == AutoMode.ON && !RunJobPage.firstAdvance) {
                        Settings.log.finest("Entering advance routine for bend: " + RunJobPage.currentBend);
                        if (Settings.autoAdvanceMode == AdvanceMode.EXTERNAL) {
                           Settings.log.finest("Advance mode = EXT");
                           if (Settings.autoAdvancePosition == AdvancePosition.TOS) {
                              Settings.log.finest("Advance Position = TOS");
                              if (Listener.retract) {
                                 Settings.log.finest("Retract = true");
                                 if (!Listener.retractType.equals("U")) {
                                    new NotificationPage("Warning", "External Auto Advance with Pinch Point Retract not possible, please disable retract or change to U retract");
                                 } else {
                                    Settings.log.finest("Retract type = U");

                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (!Settings.getExtAdvSwitch()) {
                                          label218:
                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (Settings.getExtAdvSwitch()) {
                                                Settings.log.finest("Starting advance procedure for TOP EXT U RET");
                                                this.retract();
                                                this.delayR();
                                                this.advance();

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label218;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(Settings.getExtAdvSwitch());

                                                Settings.log.finest("TOP EXT U RET procedure complete");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting TOP EXT U RET loop");
                                          break;
                                       }
                                    }
                                 }
                              } else {
                                 Settings.log.finest("Retract = false");

                                 while(RunJobPage.autoMode == AutoMode.ON) {
                                    Thread.sleep(1L);
                                    if (!Settings.getExtAdvSwitch()) {
                                       label237:
                                       while(RunJobPage.autoMode == AutoMode.ON) {
                                          Thread.sleep(1L);
                                          if (Settings.getExtAdvSwitch()) {
                                             Settings.log.finest("Starting advance procedure for TOP EXT NO RET");
                                             this.advance();

                                             do {
                                                if (RunJobPage.autoMode != AutoMode.ON) {
                                                   break label237;
                                                }

                                                Thread.sleep(1L);
                                             } while(Settings.getExtAdvSwitch());

                                             Settings.log.finest("TOP EXT NO RET procedure complete");
                                             break;
                                          }
                                       }

                                       Settings.log.finest("Exiting TOP EXT NO RET loop");
                                       break;
                                    }
                                 }
                              }
                           } else {
                              Settings.log.finest("Advance Position = PP");
                              if (Listener.retract) {
                                 Settings.log.finest("Retract = true");
                                 if (Listener.retractType.equals("U")) {
                                    Settings.log.finest("Retract type = U");

                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (Settings.getExtAdvSwitch()) {
                                          Settings.log.finest("Starting advance procedure for PP EXT U RET");

                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (!Settings.getExtAdvSwitch()) {
                                                this.retract();
                                                this.delayR();
                                                this.advance();
                                                Settings.log.finest("PP EXT U RET procedure complete");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting PP EXT U RET loop");
                                          break;
                                       }
                                    }
                                 } else {
                                    Settings.log.finest("Retract type = PP");

                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (Settings.getExtAdvSwitch()) {
                                          Settings.log.finest("Starting advance procedure for PP EXT PP RET");
                                          this.retract();
                                          this.delayG();

                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (!Settings.getExtAdvSwitch()) {
                                                this.delay();
                                                this.advance();
                                                Settings.log.finest("PP EXT PP RET procedure complete");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting PP EXT PP RET loop");
                                          break;
                                       }
                                    }
                                 }
                              } else {
                                 Settings.log.finest("Retract = false");

                                 while(RunJobPage.autoMode == AutoMode.ON) {
                                    Thread.sleep(1L);
                                    if (Settings.getExtAdvSwitch()) {
                                       Settings.log.finest("Starting advance procedure for PP EXT NO RET");

                                       while(RunJobPage.autoMode == AutoMode.ON) {
                                          Thread.sleep(1L);
                                          if (!Settings.getExtAdvSwitch()) {
                                             this.advance();
                                             Settings.log.finest("PP EXT NO RET procedure complete");
                                             break;
                                          }
                                       }

                                       Settings.log.finest("Exiting PP EXT NO RET loop");
                                       break;
                                    }
                                 }
                              }
                           }
                        } else {
                           Settings.log.finest("Advance type = Internal");
                           Settings.bottomTouched = false;
                           if (Settings.autoAdvancePosition == AdvancePosition.TOS) {
                              Settings.log.finest("Advance Position = TOS");
                              if (Listener.retract) {
                                 Settings.log.finest("Retract = true");
                                 if (Listener.retractType.equals("U")) {
                                    Settings.log.finest("Retract type = U");

                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (((Axis)Settings.axes.get(1)).getPosition() < Listener.topPosition) {
                                          label316:
                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (((Axis)Settings.axes.get(1)).getPosition() < 0) {
                                                Settings.log.finest("Waiting for bottom to advance");

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label316;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(!Settings.bottomTouched);

                                                Settings.log.finest("Starting advance Procedure for TOP INT U RET");

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label316;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() <= 0);

                                                this.retract();
                                                this.delayG();

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label316;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() <= Listener.topPosition);

                                                Listener.oldTopPosition = Listener.topPosition;
                                                this.delay();
                                                this.advance();
                                                Settings.log.finest("TOP INT U RET procedure %95 complete, waiting for gauge to go below top");

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label316;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() >= Listener.oldTopPosition);

                                                Settings.bottomTouched = false;
                                                Settings.log.finest("TOP INT U RET procedure complete, below OLD top");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting TOP INT U RET loop");
                                          break;
                                       }
                                    }
                                 } else {
                                    Settings.log.finest("Retract type = PP");

                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (((Axis)Settings.axes.get(1)).getPosition() < Listener.topPosition) {
                                          label345:
                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (((Axis)Settings.axes.get(1)).getPosition() < 0) {
                                                Settings.log.finest("Starting advance procedure for TOP INT PP RET");
                                                this.retract();
                                                this.delayG();
                                                Settings.log.finest("Waiting for bottom to advance");

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label345;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(!Settings.bottomTouched);

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label345;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() <= Listener.topPosition);

                                                Listener.oldTopPosition = Listener.topPosition;
                                                this.delay();
                                                this.advance();
                                                Settings.log.finest("TOP INT PP RET advance procedure %95 complete, waiting for ram to go below top");

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label345;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() >= Listener.oldTopPosition);

                                                Settings.bottomTouched = false;
                                                Settings.log.finest("TOP INT PP RET advance procedure complete, ram below OLD top");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting TOP INT PP RET loop");
                                          break;
                                       }
                                    }
                                 }
                              } else {
                                 Settings.log.finest("Retract = false");

                                 label372:
                                 while(RunJobPage.autoMode == AutoMode.ON) {
                                    Thread.sleep(1L);
                                    if (((Axis)Settings.axes.get(1)).getPosition() < Listener.topPosition) {
                                       Settings.log.finest("Waiting for bottom to advance");

                                       do {
                                          if (RunJobPage.autoMode != AutoMode.ON) {
                                             break label372;
                                          }

                                          Thread.sleep(1L);
                                       } while(!Settings.bottomTouched);

                                       label364:
                                       while(RunJobPage.autoMode == AutoMode.ON) {
                                          Thread.sleep(1L);
                                          if (((Axis)Settings.axes.get(1)).getPosition() > Listener.topPosition) {
                                             Settings.log.finest("Starting advance procedure for TOP INT NO RET");
                                             Listener.oldTopPosition = Listener.topPosition;
                                             this.advance();
                                             Settings.log.finest("TOP INT NO RET procedure %95 complete, waiting for ram to go below top");

                                             do {
                                                if (RunJobPage.autoMode != AutoMode.ON) {
                                                   break label364;
                                                }

                                                Thread.sleep(1L);
                                             } while(((Axis)Settings.axes.get(1)).getPosition() >= Listener.oldTopPosition);

                                             Settings.bottomTouched = false;
                                             Settings.log.finest("TOP INT NO RET procedure complete, left OLD top");
                                             break;
                                          }
                                       }

                                       Settings.log.finest("Exiting TOP INT NO RET loop");
                                       break;
                                    }
                                 }
                              }
                           } else {
                              Settings.log.finest("Advance Position = PP");
                              if (Listener.retract) {
                                 Settings.log.finest("retract = true");
                                 if (Listener.retractType.equals("U")) {
                                    Settings.log.finest("retract type = U");

                                    label396:
                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (((Axis)Settings.axes.get(1)).getPosition() < 0) {
                                          Settings.log.finest("Waiting for bottom to advance");

                                          do {
                                             if (RunJobPage.autoMode != AutoMode.ON) {
                                                break label396;
                                             }

                                             Thread.sleep(1L);
                                          } while(!Settings.bottomTouched);

                                          Settings.log.finest("Starting advance procedure for PP INT U RET");

                                          label387:
                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (((Axis)Settings.axes.get(1)).getPosition() > 0) {
                                                this.retract();
                                                this.delayR();
                                                Listener.oldTopPosition = Listener.topPosition;
                                                this.advance();
                                                Settings.bottomTouched = false;
                                                Settings.log.finest("Waiting for top: " + Listener.oldTopPosition);

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label387;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() <= Listener.oldTopPosition);

                                                Settings.log.finest("PP INT U RET procedure complete");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting PP INT U RET loop");
                                          break;
                                       }
                                    }
                                 } else {
                                    Settings.log.finest("Retract type  = PP");

                                    label419:
                                    while(RunJobPage.autoMode == AutoMode.ON) {
                                       Thread.sleep(1L);
                                       if (((Axis)Settings.axes.get(1)).getPosition() < 0) {
                                          Settings.log.finest("Starting advance procedure for PP INT PP RET");
                                          this.retract();
                                          this.delayG();
                                          Settings.log.finest("Waiting for bottom to advance");

                                          do {
                                             if (RunJobPage.autoMode != AutoMode.ON) {
                                                break label419;
                                             }

                                             Thread.sleep(1L);
                                          } while(!Settings.bottomTouched);

                                          label411:
                                          while(RunJobPage.autoMode == AutoMode.ON) {
                                             Thread.sleep(1L);
                                             if (((Axis)Settings.axes.get(1)).getPosition() > 0) {
                                                this.delay();
                                                Listener.oldTopPosition = Listener.topPosition;
                                                this.advance();
                                                Settings.log.finest("Waiting for top: " + Listener.oldTopPosition);

                                                do {
                                                   if (RunJobPage.autoMode != AutoMode.ON) {
                                                      break label411;
                                                   }

                                                   Thread.sleep(1L);
                                                } while(((Axis)Settings.axes.get(1)).getPosition() <= Listener.oldTopPosition);

                                                Settings.bottomTouched = false;
                                                Settings.log.finest("PP INT PP RET procedure complete");
                                                break;
                                             }
                                          }

                                          Settings.log.finest("Exiting PP INT PP RET loop");
                                          break;
                                       }
                                    }
                                 }
                              } else {
                                 Settings.log.finest("retract = false");

                                 label437:
                                 while(RunJobPage.autoMode == AutoMode.ON) {
                                    Thread.sleep(1L);
                                    if (((Axis)Settings.axes.get(1)).getPosition() < 0) {
                                       Settings.log.finest("Waiting for bottom to advance");

                                       do {
                                          if (RunJobPage.autoMode != AutoMode.ON) {
                                             break label437;
                                          }

                                          Thread.sleep(1L);
                                       } while(!Settings.bottomTouched);

                                       while(RunJobPage.autoMode == AutoMode.ON) {
                                          Thread.sleep(1L);
                                          if (((Axis)Settings.axes.get(1)).getPosition() > 0) {
                                             Settings.log.finest("Starting advance procedure for PP INT NO RET");
                                             this.advance();
                                             Settings.bottomTouched = false;
                                             Settings.log.finest("PP INT NO RET procedure complete");
                                             break;
                                          }
                                       }

                                       Settings.log.finest("Exiting PP INT NO RET loop");
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     Thread.sleep(2L);
                  } catch (Exception var2) {
                     Settings.log.log(Level.SEVERE, "Error in Advance Thread", var2);
                  }
               }
            }
         }

         void retract() {
            try {
               if (((Axis)Settings.axes.get(0)).getEnabled()) {
                  Listener.retPos = Double.parseDouble((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(0)) + Double.parseDouble((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(3)) + (Double)RunJobPage.job.getOffsets().get(0);
                  Communications.driveToPosition(((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getAxisType(), Listener.retPos);
                  Listener.gaugeDelayTime = (int)(100.0D + (Double.parseDouble((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(3)) - Double.parseDouble((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(0)) + Double.parseDouble((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(3)) + (Double)RunJobPage.job.getOffsets().get(0)) / ((Axis)Settings.axes.get(0)).getEncoderCountPerInch() / ((Axis)Settings.axes.get(0)).getOutFast() / 0.001D + ((Axis)Settings.axes.get(0)).getSlowDistance() * 2.0D / ((Axis)Settings.axes.get(0)).getInSlow() / 0.001D);
               }

               Thread.sleep(100L);
            } catch (Exception var2) {
               Settings.log.log(Level.WARNING, "retract function has encountered a critical error", var2);
            }

         }

         void delayR() {
            Listener.delayGR = true;
            this.delayG();

            while(Listener.delayGR) {
               try {
                  Thread.sleep(1L);
               } catch (Exception var2) {
                  Settings.log.log(Level.WARNING, "delayR function encountered critical error", var2);
               }
            }

            this.delay();
         }

         void advance() {
            while(Listener.retractDelay) {
               try {
                  Thread.sleep(1L);
               } catch (Exception var2) {
                  Settings.log.severe(var2.toString());
               }
            }

            RunJobPage.autoAdvance();
         }

         void delayG() {
            RunJobPage.retractDistanceValueLabel.setBackground(DisplayComponents.Active);
            (new Timer(Listener.gaugeDelayTime, new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  RunJobPage.retractDistanceValueLabel.setBackground(DisplayComponents.Background);
                  Listener.delayGR = false;
                  ((Timer)e.getSource()).stop();
               }
            })).start();
         }

         void delay() {
            Listener.retractDelay = true;
            RunJobPage.retractDelayValueLabel.setBackground(DisplayComponents.Active);
            (new Timer((int)(Double.valueOf((String)((AxisValues)((Bend)RunJobPage.job.getBends().get(RunJobPage.currentBend)).getAxisValues().get(0)).getValues().get(2)) * 1000.0D), new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Listener.retractDelay = false;
                  RunJobPage.retractDelayValueLabel.setBackground(DisplayComponents.Background);
                  ((Timer)e.getSource()).stop();
               }
            })).start();
         }
      });
      calibrateWatcher = new Thread(new Runnable() {
         public void run() {
            HashMap<Long, Integer> posMapX = new HashMap();
            SystemCommands.getWorkingDirectory();
            long start = System.currentTimeMillis();
            Settings.log.info("calibrate watcher thread started");
            int last = -1;
            int timeout = 0;

            while(true) {
               while(true) {
                  try {
                     for(; HomePage.recordX; Thread.sleep(1L)) {
                        int pos = ((Axis)Settings.axes.get(0)).getPosition();
                        if (pos != last) {
                           timeout = 0;
                           posMapX.put(System.currentTimeMillis() - start, pos);
                           last = pos;
                        } else {
                           ++timeout;
                           if (timeout > 20000) {
                              HomePage.recordX = false;
                              System.out.println("recordx while loop failure");
                              break;
                           }
                        }
                     }

                     if (Settings.calibrated && !Listener.watched) {
                        Settings.log.info("starting calibration routine");
                        if (((Axis)Settings.axes.get(0)).getEnabled()) {
                           Gson json = new Gson();
                           (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600")).mkdirs();
                           File file = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "GaugeData.txt");
                           PrintWriter out = new PrintWriter(file.getPath());
                           Settings.log.fine("Created file object at: " + SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "GaugeData.txt");
                           out.write(json.toJson(posMapX, posMapX.getClass()));
                           Map<Integer, Double> t2 = new TreeMap();
                           Iterator var11 = posMapX.keySet().iterator();

                           while(var11.hasNext()) {
                              Long a = (Long)var11.next();
                              t2.put(Integer.valueOf(a.toString()), Double.valueOf(((Integer)posMapX.get(a)).toString()));
                           }

                           Map<Integer, Double> tmap = new TreeMap(t2);
                           ArrayList<Integer> time = new ArrayList();
                           ArrayList<Double> position = new ArrayList();
                           position.addAll(tmap.values());
                           Settings.log.fine("finding points of interest");
                           Iterator var14 = tmap.keySet().iterator();

                           while(var14.hasNext()) {
                              Integer ax = (Integer)var14.next();
                              time.add(ax);
                           }

                           int min1 = Listener.findMin(position.size() - 1, position);
                           int max1 = Listener.getGap(min1, time);
                           int outf1 = min1 - (min1 - max1) / 4;
                           int stop1 = Listener.getRGap(min1, time);
                           int start1 = stop1 + 1;
                           int end1 = Listener.findRMax(min1, position);
                           int inf1 = end1 - (end1 - start1) / 4;
                           int inf11 = start1 + (end1 - start1) / 4;
                           double infast1 = ((Double)position.get(inf11) - (Double)position.get(inf1)) / (double)((Integer)time.get(inf11) - (Integer)time.get(inf1));
                           double inslow1 = ((Double)position.get(stop1) - (Double)position.get(min1)) / (double)((Integer)time.get(stop1) - (Integer)time.get(min1));
                           double outfast1 = ((Double)position.get(min1) - (Double)position.get(outf1)) / (double)((Integer)time.get(min1) - (Integer)time.get(outf1));
                           Settings.log.fine("min1: " + min1 + " max1: " + max1 + " stop1: " + stop1 + " start1: " + start1 + " end1: " + end1);
                           Settings.log.fine("outf1: " + outf1 + " inf1: " + inf1);
                           Settings.log.fine("min1: " + position.get(min1) + " max1: " + time.get(max1) + " stop1: " + time.get(stop1) + " start1: " + time.get(start1) + " end1: " + position.get(end1));
                           Settings.log.fine("in fast1: " + infast1 + " inslow1: " + inslow1 + " outfast1: " + outfast1);
                           int min2 = Listener.findMin(max1, position);
                           int max2 = Listener.getGap(min2, time);
                           int outf2 = min2 - (min2 - max2) / 4;
                           int stop2 = Listener.getRGap(min2, time);
                           int start2 = stop2 + 1;
                           int end2 = max1 - 1;
                           int inf2 = end2 - (end2 - start2) / 4;
                           int inf22 = start2 + (end2 - start2) / 4;
                           double infast2 = ((Double)position.get(inf22) - (Double)position.get(inf2)) / (double)((Integer)time.get(inf22) - (Integer)time.get(inf2));
                           double inslow2 = ((Double)position.get(stop2) - (Double)position.get(min2)) / (double)((Integer)time.get(stop2) - (Integer)time.get(min2));
                           double outfast2 = ((Double)position.get(min2) - (Double)position.get(outf2)) / (double)((Integer)time.get(min2) - (Integer)time.get(outf2));
                           Settings.log.fine("min2: " + min2 + " max2: " + max2 + " stop2: " + stop2 + " start2: " + start2 + " end2: " + end2);
                           Settings.log.fine("outf2: " + outf2 + " inf2: " + inf2);
                           Settings.log.fine("min2: " + position.get(min2) + " max2: " + time.get(max2) + " stop2: " + time.get(stop2) + " start2: " + time.get(start2) + " end2: " + position.get(end2));
                           Settings.log.fine("in fast2: " + infast2 + " inslow2: " + inslow2 + " outfast2: " + outfast2);
                           int min3 = Listener.findMin(max2, position);
                           int max3 = Listener.getGap(min3, time);
                           int outf3 = min3 - (min3 - max3) / 4;
                           int stop3 = Listener.getRGap(min3, time);
                           int start3 = stop3 + 1;
                           int end3 = max2 - 1;
                           int inf3 = end3 - (end3 - start3) / 4;
                           int inf33 = start3 + (end3 - start3) / 4;
                           double infast3 = ((Double)position.get(inf33) - (Double)position.get(inf3)) / (double)((Integer)time.get(inf33) - (Integer)time.get(inf3));
                           double inslow3 = ((Double)position.get(stop3) - (Double)position.get(min3)) / (double)((Integer)time.get(stop3) - (Integer)time.get(min3));
                           double outfast3 = ((Double)position.get(min3) - (Double)position.get(outf3)) / (double)((Integer)time.get(min3) - (Integer)time.get(outf3));
                           Settings.log.fine("min3: " + min3 + " max3: " + max3 + " stop3: " + stop3 + " start3: " + start3 + " end3: " + end3);
                           Settings.log.fine("outf3: " + outf3 + " inf3: " + inf3);
                           Settings.log.fine("min3: " + position.get(min3) + " max3: " + time.get(max3) + " stop3: " + time.get(stop3) + " start3: " + time.get(start3) + " end3: " + position.get(end3));
                           Settings.log.fine("in fast3: " + infast3 + " inslow3: " + inslow3 + " outfast3: " + outfast3);
                           NotificationPage.bar.setValue(95);
                           double avginfast = (infast1 + infast2 + infast3) / 3.0D;
                           double avginslow = (inslow1 + inslow2 + inslow3) / 3.0D;
                           double avgoutfast = (outfast1 + outfast2 + outfast3) / 3.0D;
                           Settings.log.fine("In Fast: " + avginfast + " In Slow: " + avginslow + " Out Fast: " + avgoutfast);
                           out.close();
                           SystemCommands.writeSettingsFile();
                        }

                        Settings.log.info("Calibration routine completed");
                        NotificationPage.bar.setValue(99);
                        HomePage.calibrationLabel.setText("Calibrated");
                        if (NotificationPage.containsKey("Running Calibration")) {
                           NotificationPage.removePage("Running Calibration");
                        }

                        Listener.watched = true;
                     }

                     Thread.sleep(1L);
                  } catch (Exception var61) {
                     Settings.log.log(Level.SEVERE, var61.getMessage(), var61);
                  }
               }
            }
         }
      });
      motionErrorX = new Thread(new Runnable() {
         public void run() {
            try {
               int lastPos = 0;
               int stopCount = 0;
               int oopc = 0;
               Recording rec = new Recording();

               while(true) {
                  if (RunJobPage.xAxisMoving) {
                     BufferedWriter out = new BufferedWriter(new FileWriter(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrordebug.log", true));
                     BufferedWriter out2 = new BufferedWriter(new FileWriter(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrorgraph.log", false));
                     long now = System.currentTimeMillis();
                     int pos = ((Axis)Settings.axes.get(0)).getPosition();
                     long dt = now - RunJobPage.xAxisMovingStart;
                     double cpi = ((Axis)Settings.axes.get(0)).getEncoderCountPerInch();
                     if (!HomePage.recordX) {
                        int targetDistance;
                        boolean var10000;
                        if (RunJobPage.xAxisTargetDirection == AxisDirection.IN) {
                           int expectedPos = RunJobPage.xAxisInitialPosition + (int)dt * (int)(((Axis)Settings.axes.get(0)).getInFast() * cpi / 1000.0D) - (int)(Listener.xAxisAccelerationAdj * cpi);
                           if (expectedPos < RunJobPage.xAxisInitialPosition) {
                              expectedPos = RunJobPage.xAxisInitialPosition;
                           } else if (expectedPos > RunJobPage.xAxisTargetPosition) {
                              expectedPos = RunJobPage.xAxisTargetPosition;
                           }

                           int dp = expectedPos - pos;
                           targetDistance = RunJobPage.xAxisTargetPosition - pos;
                           if (!HomePage.recordX) {
                              if (Math.abs(lastPos - pos) < 10) {
                                 ++stopCount;
                              } else {
                                 stopCount = 0;
                              }
                           }

                           if ((double)Math.abs(dp) > ((Axis)Settings.axes.get(0)).getEncoderCountPerInch()) {
                              ++oopc;
                           } else {
                              oopc = 0;
                           }

                           if (oopc > 10 || stopCount > 10) {
                              var10000 = HomePage.recordX;
                              oopc = 0;
                              stopCount = 0;
                              Listener.stop.add(System.currentTimeMillis());
                              if (!HomePage.recordX) {
                                 var10000 = Listener.retractDelay;
                                 Thread.sleep(50L);
                                 RunJobPage.xAxisInitialPosition = -1;
                                 RunJobPage.xAxisMovingStart = null;
                                 RunJobPage.xAxisTargetDirection = null;
                                 RunJobPage.xAxisTargetPosition = -1;
                                 RunJobPage.xAxisMoving = false;
                              }
                           }

                           out.write(now + "," + pos + "," + expectedPos + "," + (pos - expectedPos) + "," + RunJobPage.xAxisTargetPosition + ";");
                           rec.addPoint(now, pos, expectedPos);
                           if (Math.abs(targetDistance) < 50) {
                              Settings.log.info("X axis achieved position: " + pos);
                              Listener.stop.add(System.currentTimeMillis());
                              Listener.xTurnAround = false;
                              Listener.xTurnAroundTime = null;
                              RunJobPage.xAxisInitialPosition = -1;
                              RunJobPage.xAxisMovingStart = null;
                              RunJobPage.xAxisTargetDirection = null;
                              RunJobPage.xAxisTargetPosition = -1;
                              RunJobPage.xAxisMoving = false;
                              oopc = 0;
                              stopCount = 0;
                           }
                        } else {
                           double maxTime = 100.0D + (double)(RunJobPage.xAxisInitialPosition - RunJobPage.xAxisTargetPosition) / cpi / ((Axis)Settings.axes.get(0)).getOutFast() / 0.001D + ((Axis)Settings.axes.get(0)).getSlowDistance() * 2.0D / ((Axis)Settings.axes.get(0)).getInSlow() / 0.001D;
                           targetDistance = RunJobPage.xAxisTargetPosition - (int)(((Axis)Settings.axes.get(0)).getEncoderCountPerInch() * ((Axis)Settings.axes.get(0)).getSlowDistance());
                           if (!Listener.xTurnAround && Math.abs(pos - targetDistance) < (int)(((Axis)Settings.axes.get(0)).getEncoderCountPerInch() * ((Axis)Settings.axes.get(0)).getSlowDistance() / 2.0D)) {
                              Listener.xTurnAroundTime = System.currentTimeMillis();
                              Listener.xTurnAround = true;
                           }

                           int expectedPosx;
                           int dpx;
                           if (Listener.xTurnAround) {
                              expectedPosx = targetDistance + (int)((double)(now - Listener.xTurnAroundTime) * 0.001D * ((Axis)Settings.axes.get(0)).getInSlow() * cpi);
                              dpx = expectedPosx - pos;
                           } else {
                              expectedPosx = RunJobPage.xAxisInitialPosition - (int)((double)dt * 0.001D * ((Axis)Settings.axes.get(0)).getOutFast() * cpi - Listener.xAxisAccelerationAdj * cpi);
                              if (expectedPosx > RunJobPage.xAxisInitialPosition) {
                                 expectedPosx = RunJobPage.xAxisInitialPosition;
                              } else if (expectedPosx < RunJobPage.xAxisTargetPosition) {
                                 expectedPosx = RunJobPage.xAxisTargetPosition;
                              }

                              dpx = expectedPosx - pos;
                           }

                           if (!HomePage.recordX) {
                              if (Math.abs(lastPos - pos) < 10) {
                                 ++stopCount;
                              } else {
                                 stopCount = 0;
                              }
                           }

                           if ((double)Math.abs(dpx) > ((Axis)Settings.axes.get(0)).getEncoderCountPerInch()) {
                              ++oopc;
                           } else {
                              oopc = 0;
                           }

                           if (oopc > 10 || stopCount > 10) {
                              var10000 = HomePage.recordX;
                              oopc = 0;
                              stopCount = 0;
                              Listener.stop.add(System.currentTimeMillis());
                              if (!HomePage.recordX) {
                                 var10000 = Listener.retractDelay;
                                 Thread.sleep(50L);
                                 RunJobPage.xAxisInitialPosition = -1;
                                 RunJobPage.xAxisMovingStart = null;
                                 RunJobPage.xAxisTargetDirection = null;
                                 RunJobPage.xAxisTargetPosition = -1;
                                 RunJobPage.xAxisMoving = false;
                              }
                           }

                           out.write(now + "," + pos + "," + expectedPosx + "," + (pos - expectedPosx) + "," + RunJobPage.xAxisTargetPosition + ";");
                           rec.addPoint(now, pos, expectedPosx);
                           if (Listener.xTurnAround && Math.abs(pos - RunJobPage.xAxisTargetPosition) < 50) {
                              if (Listener.retracting) {
                                 RunJobPage.retractDistanceValueLabel.setBackground(DisplayComponents.Background);
                                 Listener.retracting = false;
                              }

                              Listener.xTurnAround = false;
                              Listener.xTurnAroundTime = null;
                              Listener.stop.add(System.currentTimeMillis());
                              Settings.log.info("X axis achieved position: " + pos);
                              RunJobPage.xAxisInitialPosition = -1;
                              RunJobPage.xAxisMovingStart = null;
                              RunJobPage.xAxisTargetDirection = null;
                              RunJobPage.xAxisTargetPosition = -1;
                              RunJobPage.xAxisMoving = false;
                              oopc = 0;
                              stopCount = 0;
                           }

                           if (!Listener.xTurnAround && Math.abs(pos - RunJobPage.xAxisTargetPosition) < 50 && (double)dt > maxTime) {
                              if (Listener.retracting) {
                                 RunJobPage.retractDistanceValueLabel.setBackground(DisplayComponents.Background);
                                 Listener.retracting = false;
                              }

                              Settings.log.warning("gauge achieved position, motion error missed turnaround");
                              Listener.stop.add(System.currentTimeMillis());
                              Listener.xTurnAround = false;
                              Listener.xTurnAroundTime = null;
                              Settings.log.info("X axis achieved position: " + pos);
                              RunJobPage.xAxisInitialPosition = -1;
                              RunJobPage.xAxisMovingStart = null;
                              RunJobPage.xAxisTargetDirection = null;
                              RunJobPage.xAxisTargetPosition = -1;
                              RunJobPage.xAxisMoving = false;
                              oopc = 0;
                              stopCount = 0;
                           }
                        }
                     }

                     out.close();
                     rec.updateStartStop(Listener.start, Listener.stop);
                     out2.write((new Gson()).toJson(rec));
                     out2.close();
                     if ((new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrordebug.log")).length() > 500000000L) {
                        (new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "debug" + File.separator + "motionerrordebug.log")).delete();
                     }
                  }

                  Thread.sleep(30L);
               }
            } catch (Exception var19) {
               Settings.log.log(Level.SEVERE, var19.getMessage(), var19);
            }
         }
      });
   }

   public static int findMin(int start, ArrayList<Double> pos) {
      int index = 0;
      int count = 0;
      double min = 9.9999999E7D;

      for(int i = start; i >= 1; --i) {
         if ((Double)pos.get(i) < min) {
            min = (Double)pos.get(i);
            index = i;
            count = 0;
         } else {
            ++count;
            if (count == 10) {
               break;
            }
         }
      }

      return index;
   }

   public static int getGap(int start, ArrayList<Integer> time) {
      int index = 0;
      int diff = 0;
      int count = 0;

      for(int i = start; i >= 1; --i) {
         if ((Integer)time.get(i) - (Integer)time.get(i - 1) > diff) {
            diff = (Integer)time.get(i) - (Integer)time.get(i - 1);
            index = i;
            count = 0;
         } else {
            ++count;
            if (count == 100) {
               break;
            }
         }
      }

      return index;
   }

   public static int findRMax(int start, ArrayList<Double> pos) {
      int index = 0;
      int count = 0;
      double max = -9.9999999E7D;

      for(int i = start; i <= pos.size() - 1; ++i) {
         if ((Double)pos.get(i) > max) {
            max = (Double)pos.get(i);
            index = i;
            count = 0;
         } else {
            ++count;
            if (count == 10) {
               break;
            }
         }
      }

      return index;
   }

   public static int getRGap(int start, ArrayList<Integer> time) {
      int index = 0;
      int diff = 0;
      int count = 0;

      for(int i = start; i <= time.size() - 2; ++i) {
         if ((Integer)time.get(i + 1) - (Integer)time.get(i) > diff) {
            diff = (Integer)time.get(i + 1) - (Integer)time.get(i);
            index = i;
            count = 0;
         } else {
            ++count;
            if (count == 5 && diff > 1000) {
               break;
            }
         }
      }

      return index;
   }
}
