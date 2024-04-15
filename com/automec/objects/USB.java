package com.automec.objects;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.objects.enums.ToolType;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class USB {
   public String path;
   public boolean hasDatabase = false;
   public boolean hasImages = false;
   public Connection databaseConnector;

   public USB(String path) {
      this.path = path;
      File f = new File(path + File.separator + ".CNC600" + File.separator + "usbconfig.ini");
      if (f.exists()) {
         try {
            Settings.log.info("Conencting to USB: " + path);
            String json = (String)Files.readAllLines(Paths.get(path + File.separator + ".CNC600" + File.separator + "usbconfig.ini")).get(0);
            Gson gson = new Gson();
            USBStorage stored = (USBStorage)gson.fromJson(json, USBStorage.class);
            this.hasDatabase = stored.hasDatabase;
            this.hasImages = stored.hasImages;
         } catch (Exception var7) {
            Settings.log.log(Level.SEVERE, "", var7);
         }
      } else {
         try {
            Settings.log.info("Setting up USB: " + path);
            (new File(path + File.separator + ".CNC600")).mkdirs();
            (new File(path + File.separator + "CNC600" + File.separator + "Updates")).mkdirs();
            (new File(path + File.separator + "CNC600" + File.separator + "Images")).mkdirs();
            File fo = new File(path + File.separator + ".CNC600");
            if (SystemCommands.getOS().contains("windows")) {
               Files.setAttribute(fo.toPath(), "dos:hidden", true);
            } else if (SystemCommands.getOS().equals("Linux")) {
               Runtime.getRuntime().exec("fatattr +h " + path + File.separator + ".CNC600");
            }
         } catch (Exception var6) {
            Settings.log.log(Level.SEVERE, "", var6);
         }
      }

      this.saveUSB();
      this.loadDatabase();
      this.loadJobTable();
      this.loadToolTable();
   }

   public boolean equals(Object o) {
      if (o instanceof USB) {
         return ((USB)o).path.equals(this.path);
      } else {
         return false;
      }
   }

   public void saveUSB() {
      try {
         (new File(this.path + File.separator + ".CNC600")).mkdirs();
         File file = new File(this.path + File.separator + ".CNC600" + File.separator + "usbconfig.ini");
         PrintWriter out = new PrintWriter(file.getPath());
         Gson gson = new Gson();
         out.write(gson.toJson(new USBStorage(this), USBStorage.class));
         Settings.log.info(file.getPath());
         out.close();
      } catch (Exception var4) {
         Settings.log.log(Level.SEVERE, "", var4);
      }

   }

   public void loadDatabase() {
      this.databaseConnector = null;

      try {
         String url = "jdbc:sqlite:" + this.path + File.separator + ".CNC600" + File.separator + "CNC600.db";
         (new File(this.path + File.separator + "CNC600")).mkdirs();
         Settings.log.info("usb sqldatabase location: " + url);

         try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
               DatabaseMetaData meta = conn.getMetaData();
               Settings.log.info("usb Driver: " + meta.getDriverName());
               Settings.log.info("usb Connection to SQLite established");
               this.databaseConnector = conn;
            }
         } catch (SQLException var4) {
            Settings.log.warning(var4.getMessage());
         }
      } catch (Exception var5) {
         Settings.log.log(Level.SEVERE, "usb loaddatabase", var5);
      }

      Settings.log.info("usb sqlite database initialized");
   }

   public void loadJobTable() {
      if (this.databaseConnector == null) {
         this.loadDatabase();
      }

      String sql = "CREATE TABLE IF NOT EXISTS jobs (\n name TEXT PRIMARY KEY, \n jobData BLOB, \n imageData BLOB \n);";

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            Statement stmt = this.databaseConnector.createStatement();

            try {
               stmt.execute(sql);
            } finally {
               if (stmt != null) {
                  stmt.close();
               }

            }
         } catch (Throwable var12) {
            if (var2 == null) {
               var2 = var12;
            } else if (var2 != var12) {
               var2.addSuppressed(var12);
            }

            throw var2;
         }
      } catch (Exception var13) {
         Settings.log.log(Level.SEVERE, "usb loadtable", var13);
      }

   }

   public void findUpdate() {
      File localdir = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Updates");
      localdir.mkdir();
      File usbdir = new File(Settings.selectedUSB.path + File.separator + "CNC600" + File.separator + "Updates");
      if (usbdir.mkdir()) {
         System.out.println("no updates found on disk, folder created instead");
      } else {
         File[] files = usbdir.listFiles();

         for(int i = 0; i < files.length; ++i) {
            if (files[i].isFile()) {
               System.out.println(files[i].getName());
               if (files[i].getName().endsWith(".jar") && files[i].getName().startsWith("CNC600")) {
                  File to = new File(localdir.getPath() + File.separator + files[i].getName());

                  try {
                     if (!to.exists()) {
                        Files.copy(files[i].toPath(), to.toPath());
                     }
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }
               }
            }
         }

      }
   }

   public List<String> listUpgrades() {
      ArrayList<String> ret = new ArrayList();
      File localdir = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "Updates");
      int[] curver = new int[3];
      String[] t = "2.0.14".split("\\.");

      for(int i = 0; i < t.length; ++i) {
         try {
            curver[i] = Integer.parseInt(t[i]);
         } catch (Exception var13) {
            Settings.log.log(Level.WARNING, "Jack put custom software somewhere here, use silly number");
            curver[i] = 99;
         }
      }

      File[] updates = localdir.listFiles();

      for(int i = 0; i < updates.length; ++i) {
         String ver = updates[i].getName().substring(7, updates[i].getName().length() - 4);
         String[] numbs = ver.split("\\.");
         int[] nums = new int[3];

         for(int j = 0; j < numbs.length; ++j) {
            try {
               nums[j] = Integer.parseInt(numbs[j]);
            } catch (Exception var12) {
               Settings.log.log(Level.WARNING, "Jack put custom software somewhere here, use silly number");
               nums[j] = 99;
            }
         }

         if (curver[0] <= nums[0] && curver[1] <= nums[1] && curver[2] < nums[2]) {
            ret.add(ver);
         }
      }

      return ret;
   }

   public void performUpdate(String path) {
      System.out.println(path);
      SystemCommands.backupJobs();
      File from = new File(path);
      File to = new File(SystemCommands.getWorkingDirectory() + File.separator + "CNC600" + File.separator + "CNC600.jar");

      try {
         Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      if (SystemCommands.getOS().equals("Linux")) {
         try {
            Runtime.getRuntime().exec("sudo reboot now");
         } catch (Exception var5) {
            Settings.log.log(Level.SEVERE, "LaunchKeyboard", var5);
         }
      }

   }

   public void writeJob(String name, String json) {
      boolean exists = false;

      try {
         Statement stmt = this.databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM jobs WHERE name='" + name + "'");

         while(rs.next()) {
            if (rs.getString("name").equals(name)) {
               exists = true;
            }
         }
      } catch (Exception var8) {
         Settings.log.log(Level.SEVERE, "usb writeJob", var8);
      }

      String sql;
      PreparedStatement pstmt;
      if (exists) {
         sql = "UPDATE jobs SET jobData = ? WHERE name = ?";

         try {
            pstmt = this.databaseConnector.prepareStatement(sql);
            pstmt.setString(1, json);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
         } catch (Exception var7) {
            Settings.log.log(Level.SEVERE, "usb writeJob", var7);
         }
      } else {
         sql = "INSERT INTO jobs(name,jobData) VALUES(?,?)";

         try {
            pstmt = this.databaseConnector.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, json);
            pstmt.executeUpdate();
         } catch (Exception var6) {
            Settings.log.log(Level.SEVERE, "usb writejob", var6);
         }
      }

      Settings.log.fine("usb job: " + name + "written");
   }

   public ArrayList<Job> getJobs() {
      String sql = "SELECT name, jobData FROM jobs";
      ArrayList jobs = new ArrayList();

      try {
         Statement stmt = this.databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            Gson gson = new Gson();
            Job job = (Job)gson.fromJson(rs.getString("jobData"), Job.class);
            jobs.add(job);
         }
      } catch (Exception var7) {
         Settings.log.log(Level.SEVERE, "usb getjobs", var7);
      }

      return jobs;
   }

   public ArrayList<Job> getJobs(String search) {
      String sql = "SELECT name, jobData FROM jobs WHERE name LIKE ?";
      ArrayList jobs = new ArrayList();

      try {
         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, "%" + search + "%");
         ResultSet rs = pstmt.executeQuery();

         while(rs.next()) {
            Gson gson = new Gson();
            Job job = (Job)gson.fromJson(rs.getString("jobData"), Job.class);
            jobs.add(job);
         }
      } catch (Exception var8) {
         Settings.log.log(Level.SEVERE, "usb getjobs", var8);
      }

      return jobs;
   }

   public Job getJob(String name) {
      String sql = "SELECT name, jobData FROM jobs WHERE name = ?";
      Job job = null;

      try {
         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); job = (Job)gson.fromJson(rs.getString("jobData"), Job.class)) {
            gson = new Gson();
         }
      } catch (Exception var7) {
         Settings.log.log(Level.SEVERE, "usb getjob", var7);
      }

      return job;
   }

   public Job getJob(Job name) {
      String sql = "SELECT name, jobData FROM jobs WHERE name = ?";
      Job job = null;

      try {
         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name.getName());

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); job = (Job)gson.fromJson(rs.getString("jobData"), Job.class)) {
            gson = new Gson();
         }
      } catch (Exception var7) {
         Settings.log.log(Level.SEVERE, "usb getjob", var7);
      }

      return job;
   }

   public boolean jobExists(String name) {
      Job temp = this.getJob(name);
      return temp != null;
   }

   public void deleteJob(String name) {
      String sql = "DELETE FROM jobs WHERE name = ?";

      try {
         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);
         pstmt.executeUpdate();
         Settings.log.fine("job: " + name + " deleted");
      } catch (Exception var4) {
         Settings.log.log(Level.SEVERE, "deletejob", var4);
      }

   }

   public void loadToolTable() {
      if (this.databaseConnector == null) {
         this.loadDatabase();
      }

      String sql = "CREATE TABLE IF NOT EXISTS tools (\n name TEXT PRIMARY KEY, \n type TEXT, \n toolData BLOB \n);";

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            Statement stmt = this.databaseConnector.createStatement();

            try {
               stmt.execute(sql);
            } finally {
               if (stmt != null) {
                  stmt.close();
               }

            }
         } catch (Throwable var12) {
            if (var2 == null) {
               var2 = var12;
            } else if (var2 != var12) {
               var2.addSuppressed(var12);
            }

            throw var2;
         }
      } catch (Exception var13) {
         Settings.log.log(Level.SEVERE, "loadToolTable", var13);
      }

      System.out.println("Tool table init complete");
   }

   public void writeTool(String name, String type, String json) {
      boolean exists = false;

      try {
         Statement stmt = this.databaseConnector.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM tools WHERE name='" + name + "'");

         while(rs.next()) {
            if (rs.getString("name").equals(name)) {
               exists = true;
            }
         }
      } catch (Exception var9) {
         Settings.log.log(Level.SEVERE, "writeTool", var9);
      }

      String sql;
      PreparedStatement pstmt;
      if (exists) {
         sql = "UPDATE tools SET toolData = ?, type = ? WHERE name = ?";

         try {
            pstmt = this.databaseConnector.prepareStatement(sql);
            pstmt.setString(1, json);
            pstmt.setString(2, type);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
         } catch (Exception var8) {
            Settings.log.log(Level.SEVERE, "writeTool", var8);
         }
      } else {
         sql = "INSERT INTO tools(name,type,toolData) VALUES(?,?,?)";

         try {
            pstmt = this.databaseConnector.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setString(3, json);
            pstmt.executeUpdate();
         } catch (Exception var7) {
            Settings.log.log(Level.SEVERE, "writetool", var7);
         }
      }

      Settings.log.fine("tool: " + name + "written");
   }

   public ArrayList<Tool> getTools(ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE type = ?";
      ArrayList tools = new ArrayList();

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement stmt = this.databaseConnector.prepareStatement(sql);
         stmt.setString(1, ttype);
         ResultSet rs = stmt.executeQuery();

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

   public ArrayList<Tool> getTools(String search, ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE name LIKE ? AND type = ?";
      ArrayList tools = new ArrayList();

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, "%" + search + "%");
         pstmt.setString(2, ttype);
         ResultSet rs = pstmt.executeQuery();

         while(rs.next()) {
            Gson gson = new Gson();
            Tool tool = (Tool)gson.fromJson(rs.getString("toolData"), Tool.class);
            tools.add(tool);
         }
      } catch (Exception var10) {
         Settings.log.log(Level.SEVERE, "getTools", var10);
      }

      return tools;
   }

   public Tool getTool(String name, ToolType type) {
      String sql = "SELECT name, toolData FROM tools WHERE name = ? AND type = ?";
      Tool tool = null;

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);
         pstmt.setString(2, ttype);

         Gson gson;
         for(ResultSet rs = pstmt.executeQuery(); rs.next(); tool = (Tool)gson.fromJson(rs.getString("toolData"), Tool.class)) {
            gson = new Gson();
         }
      } catch (Exception var9) {
         Settings.log.log(Level.SEVERE, "getTool", var9);
      }

      return tool;
   }

   public boolean toolExists(String name) {
      Tool tmp = this.getTool(name, ToolType.PUNCH);
      Tool tmp2 = this.getTool(name, ToolType.DIE);
      return tmp != null || tmp2 != null;
   }

   public void deleteTool(String name, ToolType type) {
      String sql = "DELETE FROM tools WHERE name = ? AND type = ?";

      try {
         String ttype = "";
         if (type.equals(ToolType.PUNCH)) {
            ttype = "PUNCH";
         } else {
            ttype = "DIE";
         }

         PreparedStatement pstmt = this.databaseConnector.prepareStatement(sql);
         pstmt.setString(1, name);
         pstmt.setString(2, ttype);
         pstmt.executeUpdate();
         Settings.log.fine("tool: " + name + " deleted");
      } catch (Exception var6) {
         Settings.log.log(Level.SEVERE, "deleteTool", var6);
      }

   }
}
