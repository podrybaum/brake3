package com.automec.display.components;

import com.automec.Settings;
import com.automec.display.popups.CalculatorPage;
import com.automec.display.popups.KeyboardPage;
import com.automec.display.popups.PasswordPromptPage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class DisplayComponents {
   public static Font displayValueLarge = new Font("Tahoma", 1, 90);
   public static Font displayValueMedium = new Font("Tahoma", 1, 55);
   public static Font pageTitleFont = new Font("Tahoma", 1, 50);
   public static Font pageTitleFontSmall = new Font("Tahoma", 0, 40);
   public static Font pageHeaderFont = new Font("Tahoma", 1, 30);
   public static Font pageTextFont = new Font("Tahoma", 0, 18);
   public static Font buttonFont = new Font("Tahoma", 0, 18);
   public static Font bendPanelFont = new Font("Tahoma", 0, 24);
   public static Font editJobPageText = new Font("Tahoma", 1, 16);
   public static Font editJobPageValue = new Font("Tahoma", 0, 24);
   public static Dimension bottomButtonSize = new Dimension(100, 100);
   public static Color Active = new Color(104, 162, 221);
   public static Color Inactive = new Color(150, 150, 150);
   public static Color Background = new Color(238, 238, 238);
   public static Dimension minimumButtonSize = new Dimension(80, 80);

   public static JButton GenerateCalcButton(String buttonText) {
      JButton ret = GenerateButton(buttonText);
      ret.setBorder((Border)null);
      return ret;
   }

   public static JButton GenerateCalcButton(String buttonText, ActionListener action) {
      JButton ret = GenerateButton(buttonText, action);
      ret.setBorder((Border)null);
      ret.setFocusable(false);
      return ret;
   }

   public static JButton GenerateButton(String buttonText, String icon, String pressedIcon, ActionListener action) {
      return BuildButton(buttonText, icon, pressedIcon, action);
   }

   public static JButton GenerateButton(String buttonText, ActionListener action) {
      return BuildButton(buttonText, (String)null, (String)null, action);
   }

   public static JButton GenerateButton(String buttonText, String icon, String pressedIcon) {
      return BuildButton(buttonText, icon, pressedIcon, (ActionListener)null);
   }

   public static JButton GenerateButton(String buttonText, String icon, ActionListener action) {
      return BuildButton(buttonText, icon, icon, action);
   }

   public static JButton GenerateButton(String buttonText) {
      return BuildButton(buttonText, (String)null, (String)null, (ActionListener)null);
   }

   private static JButton BuildButton(String buttonText, String icon, String pressedIcon, ActionListener action) {
      JButton button = new JCustomButton(buttonText);
      button.setFont(buttonFont);
      button.setVerticalTextPosition(3);
      button.setHorizontalTextPosition(0);
      if (icon != null && pressedIcon != null) {
         button.setIcon(new ImageIcon(DisplayComponents.class.getClassLoader().getResource(icon)));
         button.setPressedIcon(new ImageIcon(DisplayComponents.class.getClassLoader().getResource(pressedIcon)));
      }

      if (action != null) {
         button.addActionListener(action);
      }

      return button;
   }

   public static GridBagConstraints GenerateConstraints(int x, int y) {
      return BuildConstraints(x, y, 1.0D, 0.0D, 1, 1, 18, new Insets(0, 0, 5, 5), 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY) {
      return BuildConstraints(x, y, weightX, weightY, 1, 1, 18, new Insets(0, 0, 5, 5), 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY, Insets insets) {
      return BuildConstraints(x, y, weightX, weightY, 1, 1, 18, insets, 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, int gridwidth, int gridheight) {
      return BuildConstraints(x, y, 1.0D, 0.0D, gridwidth, gridheight, 18, new Insets(0, 0, 5, 5), 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, int gridwidth, int gridheight, double weightX, double weightY, Insets insets) {
      return BuildConstraints(x, y, weightX, weightY, gridwidth, gridheight, 18, insets, 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY, int gridwidth, int gridheight) {
      return BuildConstraints(x, y, weightX, weightY, gridwidth, gridheight, 18, new Insets(0, 0, 5, 5), 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY, int gridwidth, int gridheight, int anchor) {
      return BuildConstraints(x, y, weightX, weightY, gridwidth, gridheight, anchor, new Insets(0, 0, 5, 5), 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY, int gridwidth, int gridheight, int anchor, Insets insets) {
      return BuildConstraints(x, y, weightX, weightY, gridwidth, gridheight, anchor, insets, 1);
   }

   public static GridBagConstraints GenerateConstraints(int x, int y, double weightX, double weightY, int gridwidth, int gridheight, int anchor, Insets insets, int fill) {
      return BuildConstraints(x, y, weightX, weightY, gridwidth, gridheight, anchor, insets, fill);
   }

   private static GridBagConstraints BuildConstraints(int x, int y, double weightX, double weightY, int gridwidth, int gridheight, int anchor, Insets insets, int fill) {
      GridBagConstraints gBC = new GridBagConstraints();
      gBC.anchor = anchor;
      gBC.gridx = x;
      gBC.gridy = y;
      gBC.weightx = weightX;
      gBC.weighty = weightY;
      gBC.gridheight = gridheight;
      gBC.gridwidth = gridwidth;
      gBC.insets = insets;
      gBC.fill = fill;
      return gBC;
   }

   public static MouseListener CalculatorPopupSettings() {
      return new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            if (Settings.locked) {
               new PasswordPromptPage();
            } else {
               if (!(arg0.getSource() instanceof JTextField)) {
                  boolean var10000 = arg0.getSource() instanceof JLabel;
               }

               new CalculatorPage(arg0.getComponent());
            }

         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      };
   }

   public static MouseListener CalculatorPopup() {
      return new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            if (!(arg0.getSource() instanceof JTextField)) {
               boolean var10000 = arg0.getSource() instanceof JLabel;
            }

            new CalculatorPage(arg0.getComponent());
         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      };
   }

   public static MouseListener KeyboardPopup() {
      return new MouseListener() {
         public void mouseClicked(MouseEvent arg0) {
            if (arg0.getSource() instanceof JTextField) {
               new KeyboardPage((JTextField)arg0.getSource());
            } else if (arg0.getSource() instanceof JTextArea) {
               new KeyboardPage((JTextArea)arg0.getSource());
            }

         }

         public void mouseEntered(MouseEvent arg0) {
         }

         public void mouseExited(MouseEvent arg0) {
         }

         public void mousePressed(MouseEvent arg0) {
         }

         public void mouseReleased(MouseEvent arg0) {
         }
      };
   }
}
