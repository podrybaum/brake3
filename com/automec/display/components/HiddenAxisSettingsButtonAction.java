package com.automec.display.components;

import com.automec.Settings;
import com.automec.display.pages.HiddenAxisSettingsPage;
import com.automec.objects.Axis;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class HiddenAxisSettingsButtonAction implements ActionListener {
   private Axis reference;
   private JFrame oldPage;

   public HiddenAxisSettingsButtonAction(Axis reference, JFrame oldPage) {
      this.reference = reference;
      this.oldPage = oldPage;
   }

   public void actionPerformed(ActionEvent arg0) {
      new HiddenAxisSettingsPage(this.reference);
      Settings.log.finest(this.reference.getShortName() + " axis settings button pressed");
      this.oldPage.dispose();
      Settings.log.finest("axis settings page disposed");
   }
}
