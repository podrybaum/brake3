package com.automec;

import com.automec.display.components.DataInputField;
import com.automec.display.popups.NotificationPage;
import com.automec.objects.Job;
import com.automec.objects.Tool;
import com.automec.objects.USB;
import com.automec.objects.enums.ToolType;
import com.google.gson.Gson;
import io.dvlopt.linux.i2c.I2CBuffer;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFileChooser;

public class SystemCommands {
   private static String screen = "";
   private static String workingDirectory = "";
   private static Connection databaseConnector;
   private static String url;

   public static ArrayList<String> getUSBs() {
      ArrayList<String> ret = new ArrayList();
      File[] f;
      if (getOS().equals("Linux")) {
         f = (new File("/media")).listFiles();
      } else {
         f = File.listRoots();
      }

      for(int i = 0; i < f.length; ++i) {
         ret.add(f[i].getPath());
      }

      return ret;
   }

   public static void scanUSBDrives() {
      File[] f;
      if (getOS().equals("Linux")) {
         f = (new File("/media")).listFiles();
      } else {
         f = File.listRoots();
      }

      Settings.connectedUSB.clear();

      for(int i = 0; i < f.length; ++i) {
         System.out.println("Checking: " + f[i].getAbsolutePath() + File.separator + ".CNC600" + File.separator + "usbconfig.ini");
         if ((new File(f[i].getAbsolutePath() + File.separator + ".CNC600" + File.separator + "usbconfig.ini")).exists()) {
            Settings.log.fine("pre-configured USB detected: " + f[i].getAbsolutePath());
            Settings.connectedUSB.add(new USB(f[i].getAbsolutePath()));
         } else {
            Settings.log.fine("unconfigured USB detected: " + f[i].getAbsolutePath());
         }
      }

      if (!Settings.connectedUSB.contains(Settings.selectedUSB) && Settings.selectedUSB != null) {
         System.out.println("Selected usb disconnected");
         new NotificationPage("USB Disconnected", "The USB you had selected has been disconnected", 5000);
         Settings.lastUSB = Settings.selectedUSB;
         Settings.selectedUSB = null;
      }

      if (Settings.connectedUSB.contains(Settings.lastUSB) && Settings.selectedUSB == null) {
         System.out.println("Previous usb reconnected");
         new NotificationPage("USB Connected", "Previous USB reconnected", 5000);
         Settings.selectedUSB = Settings.lastUSB;
      }

   }

   public static boolean validInput(DataInputField input) {
      double min = input.getMin();
      double max = input.getMax();

      try {
         double value = Double.parseDouble(input.getText());
         if (value >= min && value <= max) {
            return true;
         }
      } catch (NumberFormatException var7) {
         Settings.log.warning("non number entered into data input field");
      }

      return false;
   }

   public static void openFileExplorer() {
      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("xdg-open .");
         } catch (Exception var2) {
            Settings.log.log(Level.SEVERE, "openFileExplorer", var2);
         }
      } else if (getOS().contains("Windows")) {
         try {
            JFileChooser ch = new JFileChooser();
            ch.setFileSelectionMode(1);
            ch.setAcceptAllFileFilterUsed(false);
            if (ch.showOpenDialog((Component)null) == 0) {
               System.out.println(ch.getSelectedFile().getAbsolutePath());
            }
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "openFileExplorer", var1);
         }
      }

   }

   public static int getTwosComp(int byt) {
      int tmp = byt * -1;
      return tmp & 255;
   }

   public static void setClock() {
      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("hwclock --hctosys");
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "setClock", var1);
         }
      }

   }

   public static void launchKeyboard() {
      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("onboard -size=1024x200");
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "LaunchKeyboard", var1);
         }
      }

   }

   public static List<String> listDowngrades() {
      ArrayList<String> ret = new ArrayList();
      File localdir = new File(getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Updates");
      int[] curver = new int[3];
      String[] t = "2.0.14".split("\\.");

      for(int i = 0; i < t.length; ++i) {
         curver[i] = Integer.parseInt(t[i]);
      }

      File[] updates = localdir.listFiles();

      for(int i = 0; i < updates.length; ++i) {
         String ver = updates[i].getName().substring(7, updates[i].getName().length() - 4);
         String[] numbs = ver.split("\\.");
         int[] nums = new int[3];

         for(int j = 0; j < numbs.length; ++j) {
            try {
               nums[j] = Integer.parseInt(numbs[j]);
            } catch (Exception var11) {
            }
         }

         if (curver[0] > nums[0] || curver[1] > nums[1] || curver[2] > nums[2]) {
            ret.add(ver);
         }
      }

      return ret;
   }

   public static void performDowngrade(String path) {
      System.out.println(path);
      backupJobs();
      File from = new File(path);
      File to = new File(getWorkingDirectory() + File.separator + "CNC600" + File.separator + "CNC600.jar");

      try {
         Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("sudo reboot now");
         } catch (Exception var4) {
            Settings.log.log(Level.SEVERE, "LaunchKeyboard", var4);
         }
      }

   }

   public static void launchKeyboard(int x, int y) {
      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("onboard -x " + x + " -y " + y + "-s 1024x200");
         } catch (Exception var3) {
            Settings.log.log(Level.SEVERE, "LaunchKeyboard", var3);
         }
      }

   }

   public static int checkChecksum(I2CBuffer buf) {
      short sum = 0;

      for(int i = 0; i < buf.length - 2; ++i) {
         sum += Short.valueOf((short)buf.get(i));
      }

      short flip = (short)(sum * -1);
      return flip;
   }

   public static int getChecksum(I2CBuffer buf) {
      int sum = 0;

      int flip;
      for(flip = 0; flip < buf.length; ++flip) {
         sum += buf.get(flip);
      }

      flip = sum * -1;
      return flip;
   }

   public static int getChecksum(int[] buf) {
      int sum = 0;

      int flip;
      for(flip = 0; flip < buf.length; ++flip) {
         sum += buf[flip];
      }

      flip = sum * -1;
      return flip;
   }

   public static int getChecksum(ArrayList<Integer> buf) {
      int sum = 0;

      int flip;
      for(flip = 0; flip < buf.size(); ++flip) {
         sum += (Integer)buf.get(flip);
      }

      flip = sum * -1;
      return flip;
   }

   public static String getWorkingDirectory() {
      if (workingDirectory != "") {
         return workingDirectory;
      } else {
         workingDirectory = System.getProperty("user.home");
         return workingDirectory;
      }
   }

   public static void changeBrightness(double brightness) {
      if (screen.isEmpty()) {
         getScreen();
      }

      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("xrandr --output " + screen + " --brightness " + brightness);
         } catch (Exception var3) {
            Settings.log.log(Level.SEVERE, "Brightness", var3);
         }
      }

   }

   public static String getOS() {
      return System.getProperty("os.name");
   }

   public static boolean isRPi() {
      if (getOS().equals("Linux")) {
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

   public static void calibrateScreen() {
      if (getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("gCal 4");
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "calibrate screen", var1);
         }
      }

   }

   public static String getSettingsFile() {
      getWorkingDirectory();
      String json = "";

      try {
         (new File(workingDirectory + File.separator + "CNC600")).mkdirs();
         File file = new File(workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini");
         if (file.exists()) {
            json = (String)Files.readAllLines(Paths.get(workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini")).get(0);
         } else {
            Settings.log.log(Level.SEVERE, "no settings found at: " + workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini");
         }
      } catch (Exception var2) {
         Settings.log.log(Level.SEVERE, "getSettingsFile", var2);
      }

      return json;
   }

   public static void makeImageDirectories() {
      (new File(workingDirectory + File.separator + "CNC600" + File.separator + "Images")).mkdirs();
      (new File(workingDirectory + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Punch")).mkdirs();
      (new File(workingDirectory + File.separator + "CNC600" + File.separator + "ToolImages" + File.separator + "Die")).mkdirs();
   }

   public static void writeSettingsFile() {
      getWorkingDirectory();

      try {
         (new File(workingDirectory + File.separator + "CNC600")).mkdirs();
         File file = new File(workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini");
         if (file.exists()) {
            Files.deleteIfExists((new File(workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini.bak")).toPath());
            Files.copy(file.toPath(), (new File(workingDirectory + File.separator + "CNC600" + File.separator + "Settings.ini.bak")).toPath());
         }

         PrintWriter out = new PrintWriter(file.getPath());
         Gson gson = new Gson();
         out.write(gson.toJson(new SettingsStorage(), SettingsStorage.class));
         Settings.log.info(file.getPath());
         out.close();
      } catch (Exception var3) {
         Settings.log.log(Level.SEVERE, "writesettingsfile", var3);
      }

   }

   public static void loadDatabase() {
      getWorkingDirectory();
      databaseConnector = null;

      try {
         url = "jdbc:sqlite:" + workingDirectory + File.separator + "CNC600" + File.separator + "CNC600.db";
         (new File(workingDirectory + File.separator + "CNC600")).mkdirs();
         Settings.log.info("sqldatabase location: " + url);
         Connection conn = DriverManager.getConnection(url);
         if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            Settings.log.info("Driver: " + meta.getDriverName());
            Settings.log.info("Connection to SQLite established");
            databaseConnector = conn;
         }
      } catch (Exception var2) {
         Settings.log.log(Level.SEVERE, "loaddatabase", var2);
      }

      Settings.log.info("sqlite database initialized");
   }

   public static void loadJobTable() {
      if (workingDirectory.equals("")) {
         getWorkingDirectory();
      }

      if (databaseConnector == null) {
         loadDatabase();
      }

      String sql = "CREATE TABLE IF NOT EXISTS jobs (\n name TEXT PRIMARY KEY, \n jobData BLOB, \n imageData BLOB \n);";

      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            Statement stmt = databaseConnector.createStatement();

            try {
               stmt.execute(sql);
            } finally {
               if (stmt != null) {
                  stmt.close();
               }

            }
         } catch (Throwable var11) {
            if (var1 == null) {
               var1 = var11;
            } else if (var1 != var11) {
               var1.addSuppressed(var11);
            }

            throw var1;
         }
      } catch (Exception var12) {
         Settings.log.log(Level.SEVERE, "loadJobTable", var12);
      }

   }

   public static void loadToolTable() {
      if (workingDirectory.equals("")) {
         getWorkingDirectory();
      }

      if (databaseConnector == null) {
         loadDatabase();
      }

      String sql = "CREATE TABLE IF NOT EXISTS tools (\n name TEXT PRIMARY KEY, \n type TEXT, \n toolData BLOB \n);";

      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            Statement stmt = databaseConnector.createStatement();

            try {
               stmt.execute(sql);
            } finally {
               if (stmt != null) {
                  stmt.close();
               }

            }
         } catch (Throwable var11) {
            if (var1 == null) {
               var1 = var11;
            } else if (var1 != var11) {
               var1.addSuppressed(var11);
            }

            throw var1;
         }
      } catch (Exception var12) {
         Settings.log.log(Level.SEVERE, "loadToolTable", var12);
      }

   }

   public static void writeJob(String name, String json) {
      boolean exists = false;

      try {
         Statement stmt = databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM jobs WHERE name='" + name + "'");

         while(rs.next()) {
            if (rs.getString("name").equals(name)) {
               exists = true;
            }
         }
      } catch (Exception var7) {
         Settings.log.log(Level.SEVERE, "writeJob", var7);
      }

      String sql;
      PreparedStatement pstmt;
      if (exists) {
         sql = "UPDATE jobs SET jobData = ? WHERE name = ?";

         try {
            pstmt = databaseConnector.prepareStatement(sql);
            pstmt.setString(1, json);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
         } catch (Exception var6) {
            Settings.log.log(Level.SEVERE, "writeJob", var6);
         }
      } else {
         sql = "INSERT INTO jobs(name,jobData) VALUES(?,?)";

         try {
            pstmt = databaseConnector.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, json);
            pstmt.executeUpdate();
         } catch (Exception var5) {
            Settings.log.log(Level.SEVERE, "writejob", var5);
         }
      }

      Settings.log.fine("job: " + name + "written");
   }

   public static void writeTool(String name, String type, String json) {
      boolean exists = false;

      try {
         Statement stmt = databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM tools WHERE name='" + name + "'");

         while(rs.next()) {
            if (rs.getString("name").equals(name)) {
               exists = true;
            }
         }
      } catch (Exception var8) {
         Settings.log.log(Level.SEVERE, "writeTool", var8);
      }

      String sql;
      PreparedStatement pstmt;
      if (exists) {
         sql = "UPDATE tools SET toolData = ?, type = ? WHERE name = ?";

         try {
            pstmt = databaseConnector.prepareStatement(sql);
            pstmt.setString(1, json);
            pstmt.setString(2, type);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
         } catch (Exception var7) {
            Settings.log.log(Level.SEVERE, "writeTool", var7);
         }
      } else {
         sql = "INSERT INTO tools(name,type,toolData) VALUES(?,?,?)";

         try {
            pstmt = databaseConnector.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setString(3, json);
            pstmt.executeUpdate();
         } catch (Exception var6) {
            Settings.log.log(Level.SEVERE, "writetool", var6);
         }
      }

      Settings.log.fine("tool: " + name + "written");
   }

   public static ArrayList<Job> getJobs() {
      String sql = "SELECT name, jobData FROM jobs";
      ArrayList jobs = new ArrayList();

      try {
         Statement stmt = databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            Gson gson = new Gson();
            Job job = (Job)gson.fromJson(rs.getString("jobData"), Job.class);
            jobs.add(job);
         }
      } catch (Exception var6) {
         Settings.log.log(Level.SEVERE, "getjobs", var6);
      }

      return jobs;
   }

   public static ArrayList<Tool> getTools(ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE type = ?";
      ArrayList tools = new ArrayList();

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement stmt = databaseConnector.prepareStatement(sql);
         stmt.setString(1, ttype);
         ResultSet rs = stmt.executeQuery();

         while(rs.next()) {
            Gson gson = new Gson();
            Tool tool = (Tool)gson.fromJson(rs.getString("toolData"), Tool.class);
            tools.add(tool);
         }
      } catch (Exception var8) {
         Settings.log.log(Level.SEVERE, "getTools", var8);
      }

      return tools;
   }

   public static ArrayList<Job> getJobs(String search) {
      String sql = "SELECT name, jobData FROM jobs WHERE name LIKE ?";
      ArrayList jobs = new ArrayList();

      try {
         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, "%" + search + "%");
         ResultSet rs = pstmt.executeQuery();

         while(rs.next()) {
            Gson gson = new Gson();
            Job job = (Job)gson.fromJson(rs.getString("jobData"), Job.class);
            jobs.add(job);
         }
      } catch (Exception var7) {
         Settings.log.log(Level.SEVERE, "getjobs", var7);
      }

      return jobs;
   }

   public static ArrayList<Tool> getTools(String search, ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE name LIKE ? AND type = ?";
      ArrayList tools = new ArrayList();

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, "%" + search + "%" + ttype);
         pstmt.setString(2, ttype);
         ResultSet rs = pstmt.executeQuery();

         while(rs.next()) {
            Gson gson = new Gson();
            Tool tool = (Tool)gson.fromJson(rs.getString("toolData"), Tool.class);
            tools.add(tool);
         }
      } catch (Exception var9) {
         Settings.log.log(Level.SEVERE, "getTools", var9);
      }

      return tools;
   }

   public static Job getJob(String name) {
      String sql = "SELECT name, jobData FROM jobs WHERE name = ?";
      Job job = null;

      try {
         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); job = (Job)gson.fromJson(rs.getString("jobData"), Job.class)) {
            gson = new Gson();
         }
      } catch (Exception var6) {
         Settings.log.log(Level.SEVERE, "getjob", var6);
      }

      return job;
   }

   public static Job getJob(Job name) {
      String sql = "SELECT name, jobData FROM jobs WHERE name = ?";
      Job job = null;

      try {
         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name.getName());

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); job = (Job)gson.fromJson(rs.getString("jobData"), Job.class)) {
            gson = new Gson();
         }
      } catch (Exception var6) {
         Settings.log.log(Level.SEVERE, "getjob", var6);
      }

      return job;
   }

   public static Tool getTool(String name, ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE name = ? AND type = ?";
      Tool tool = null;

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name + ttype);
         pstmt.setString(2, ttype);

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); tool = (Tool)gson.fromJson(rs.getString("toolData"), Tool.class)) {
            gson = new Gson();
         }
      } catch (Exception var8) {
         Settings.log.log(Level.SEVERE, "getTool", var8);
      }

      return tool;
   }

   public static boolean jobExists(String name) {
      Job temp = getJob(name);
      return temp != null;
   }

   public static boolean toolExists(String name) {
      Tool tmp = getTool(name, ToolType.PUNCH);
      Tool tmp2 = getTool(name, ToolType.DIE);
      return tmp != null || tmp2 != null;
   }

   public static void deleteJob(String name) {
      String sql = "DELETE FROM jobs WHERE name = ?";

      try {
         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);
         pstmt.executeUpdate();
         Settings.log.fine("job: " + name + " deleted");
      } catch (Exception var3) {
         Settings.log.log(Level.SEVERE, "deletejob", var3);
      }

   }

   public static void deleteJob(Job name) {
      name.delete();
   }

   public static void deleteTool(String name, ToolType type) {
      String sql = "DELETE FROM tools WHERE name = ? AND type = ?";

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name + ttype);
         pstmt.setString(2, ttype);
         pstmt.executeUpdate();
         Settings.log.fine("tool: " + name + " deleted");
      } catch (Exception var5) {
         Settings.log.log(Level.SEVERE, "deleteTool", var5);
      }

   }

   public static void closeDatabase() {
      try {
         databaseConnector.close();
      } catch (Exception var1) {
         Settings.log.log(Level.SEVERE, "close database", var1);
      }

   }

   public static void enableScreensaver() {
      if (getOS().equals("Linux")) {
         try {
            Listener.screenSaverStopper.stop();
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "getscreen", var1);
         }
      }

   }

   public static void disableScreensaver() {
      if (getOS().equals("Linux")) {
         try {
            Listener.screenSaverStopper.start();
         } catch (Exception var1) {
            Settings.log.log(Level.SEVERE, "getscreen", var1);
         }
      }

   }

   private static void getScreen() {
      if (getOS().equals("Linux")) {
         try {
            String output = "";
            Process p = Runtime.getRuntime().exec("xrandr -q");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = null;

            while((s = stdInput.readLine()) != null) {
               if (s.contains(" connected")) {
                  output = s;
               }
            }

            String[] words = output.split(" ");
            screen = words[0];
         } catch (Exception var5) {
            Settings.log.log(Level.SEVERE, "getscreen", var5);
         }
      }

   }

   public static void dumpLogs() {
      if (Settings.selectedUSB != null) {
         LocalDate cal = LocalDate.now();
         String debugFolder = Settings.selectedUSB.path + File.separator + ".CNC600" + File.separator + cal.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
         File usbPerm = new File(debugFolder + File.separator + "perm");
         File usbTmp = new File(debugFolder + File.separator + "tmp");
         if (!usbPerm.exists()) {
            usbPerm.mkdirs();
         }

         if (!usbTmp.exists()) {
            usbTmp.mkdirs();
         }

         File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "CNC600" + File.separator + "debug");
         File perm = new File(getWorkingDirectory() + File.separator + "CNC600");

         try {
            copyFolder(tmp, usbTmp);
            copyFolder(perm, usbPerm);
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

   }

   public static void backupJobs() {
      if (Settings.selectedUSB != null && getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("tar -czf " + Settings.selectedUSB.path + File.separator + "backup.tar.gz " + getWorkingDirectory() + File.separator + "CNC600" + File.separator + "CNC600.db " + getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Settings.ini");
         } catch (Exception var1) {
         }
      }

   }

   private static void copyFolder(File src, File dest) {
      if (src != null && dest != null) {
         if (src.isDirectory()) {
            if (dest.exists()) {
               if (!dest.isDirectory()) {
                  return;
               }
            } else {
               dest.mkdir();
            }

            if (src.listFiles() != null && src.listFiles().length != 0) {
               File[] var5;
               int var4 = (var5 = src.listFiles()).length;

               for(int var3 = 0; var3 < var4; ++var3) {
                  File file = var5[var3];
                  File fileDest = new File(dest, file.getName());
                  if (file.isDirectory()) {
                     copyFolder(file, fileDest);
                  } else if (!fileDest.exists()) {
                     try {
                        Files.copy(file.toPath(), fileDest.toPath());
                     } catch (IOException var8) {
                     }
                  }
               }

            }
         }
      }
   }
}
