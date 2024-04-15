package com.automec.display.pages;

import com.automec.Settings;
import com.automec.SystemCommands;
import com.automec.objects.Axis;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SettingsButtonAction implements ActionListener {
   private SettingsPage reference;
   private Axis axis;

   public SettingsButtonAction(SettingsPage reference, Axis axis) {
      this.reference = reference;
      this.axis = axis;
   }

   public void actionPerformed(ActionEvent arg0) {
      Settings.log.finest("axis settings button pressed");
      new AxisSettingsPage(this.axis);
      Settings.log.finest("settings page disposed");
      SystemCommands.writeSettingsFile();
      this.reference.getFrame().dispose();
   }
}
