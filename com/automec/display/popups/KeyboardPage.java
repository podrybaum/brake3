package com.automec.display.popups;

import com.automec.display.components.DisplayComponents;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class KeyboardPage {
   private JFrame keyboardFrame;
   private static boolean exists = false;
   private static final String backspace = "<html>←</html>";
   private static final String leftArrow = "<html>◄</html>";
   private static final String rightArrow = "<html>►</html>";
   private static final Font keyFont = new Font("Tahoma", 0, 18);
   private static final Font keyFontBold = new Font("Tahoma", 1, 18);
   private boolean capsLock = false;
   private boolean caps = false;
   private ArrayList<ArrayList<KeyboardPage.KeyboardButton>> keys;

   public KeyboardPage(JTextComponent source) {
      if (!exists) {
         this.keyboardFrame = new JFrame("Keyboard");
         this.initialize(source);
      }

   }

   private void initialize(final JTextComponent source) {
      exists = true;
      if (source.getLocationOnScreen().getY() < 384.0D) {
         this.keyboardFrame.setBounds(12, 368, 1000, 401);
      } else {
         this.keyboardFrame.setBounds(12, 0, 1000, 401);
      }

      this.keyboardFrame.setDefaultCloseOperation(3);
      this.keyboardFrame.setUndecorated(true);
      this.keyboardFrame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.keyboardFrame.setAlwaysOnTop(true);
      this.keyboardFrame.setFocusable(true);
      KeyboardPage.FrameDragListener fd = new KeyboardPage.FrameDragListener(this.keyboardFrame);
      this.keyboardFrame.addMouseListener(fd);
      this.keyboardFrame.addMouseMotionListener(fd);
      this.keyboardFrame.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent arg0) {
         }

         public void focusLost(FocusEvent arg0) {
            if (arg0.getOppositeComponent() != null) {
               KeyboardPage.this.keyboardFrame.dispose();
               KeyboardPage.exists = false;
            }

         }
      });
      String[] row1 = new String[]{"CLEAR", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "<html>←</html>"};
      String[] row2 = new String[]{"TAB", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "-"};
      String[] row3 = new String[]{"CAPS", "a", "s", "d", "f", "g", "h", "j", "k", "l", "ENTER", ""};
      String[] row4 = new String[]{"SHIFT", "z", "x", "c", "v", "b", "n", "m", ".", "<html>◄</html>", "<html>►</html>", "CLOSE"};
      String[][] rows = new String[][]{row1, row2, row3, row4};
      String[] capsRow1 = new String[]{"CLEAR", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "<html>←</html>"};
      String[] capsRow2 = new String[]{"TAB", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "_"};
      String[] capsRow3 = new String[]{"CAPS", "A", "S", "D", "F", "G", "H", "J", "K", "L", "ENTER", ""};
      String[] capsRow4 = new String[]{"SHIFT", "Z", "X", "C", "V", "B", "N", "M", ",", "<html>◄</html>", "<html>►</html>", "CLOSE"};
      String[][] capsRows = new String[][]{capsRow1, capsRow2, capsRow3, capsRow4};
      GridBagLayout gbl = new GridBagLayout();
      gbl.rowWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      gbl.columnWeights = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Double.MIN_VALUE};
      JPanel center = new JPanel();
      center.setLayout(gbl);
      this.keyboardFrame.getContentPane().add(center, "North");
      this.keys = new ArrayList();

      int j;
      for(j = 0; j < 5; ++j) {
         this.keys.add(new ArrayList());
      }

      for(j = 0; j < 4; ++j) {
         for(int i = 0; i < 12; ++i) {
            ((ArrayList)this.keys.get(j)).add(new KeyboardPage.KeyboardButton(rows[j][i], capsRows[j][i]));
            KeyboardPage.KeyboardButton k = (KeyboardPage.KeyboardButton)((ArrayList)this.keys.get(j)).get(i);
            String var18;
            switch((var18 = k.getText()).hashCode()) {
            case -533782777:
               if (var18.equals("<html>←</html>")) {
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        JTextComponent t = source;

                        try {
                           if (t.getCaretPosition() != 0) {
                              t.getDocument().remove(t.getCaretPosition() - 1, 1);
                           }
                        } catch (BadLocationException var4) {
                           var4.printStackTrace();
                        }

                        if (KeyboardPage.this.capsLock) {
                           KeyboardPage.this.caps = true;
                           KeyboardPage.this.setCaps();
                        } else {
                           KeyboardPage.this.caps = false;
                           KeyboardPage.this.setCaps();
                        }

                     }
                  });
                  continue;
               }
               break;
            case 0:
               if (var18.equals("")) {
                  continue;
               }
               break;
            case 45:
               if (var18.equals("-")) {
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        KeyboardPage.KeyboardButton k = (KeyboardPage.KeyboardButton)e.getSource();
                        JTextComponent t = source;

                        try {
                           t.getDocument().insertString(t.getCaretPosition(), k.getText(), (AttributeSet)null);
                        } catch (BadLocationException var5) {
                           var5.printStackTrace();
                        }

                        if (KeyboardPage.this.capsLock) {
                           KeyboardPage.this.caps = true;
                           KeyboardPage.this.setCaps();
                        } else {
                           KeyboardPage.this.caps = false;
                           KeyboardPage.this.setCaps();
                        }

                     }
                  });
                  continue;
               }
               break;
            case 82805:
               if (var18.equals("TAB")) {
                  k.setFont(keyFontBold);
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        try {
                           source.getDocument().insertString(source.getCaretPosition(), "    ", (AttributeSet)null);
                        } catch (BadLocationException var3) {
                           var3.printStackTrace();
                        }

                     }
                  });
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  continue;
               }
               break;
            case 2061025:
               if (var18.equals("CAPS")) {
                  k.setFont(keyFontBold);
                  k.setPreferredSize(new Dimension(100, 80));
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        KeyboardPage.this.capsLock();
                     }
                  });
                  continue;
               }
               break;
            case 64208429:
               if (var18.equals("CLEAR")) {
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        try {
                           source.getDocument().remove(0, source.getDocument().getLength());
                        } catch (BadLocationException var3) {
                           var3.printStackTrace();
                        }

                     }
                  });
                  k.setFont(keyFontBold);
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  continue;
               }
               break;
            case 64218584:
               if (var18.equals("CLOSE")) {
                  k.setFont(keyFontBold);
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        KeyboardPage.this.keyboardFrame.dispose();
                        KeyboardPage.exists = false;
                     }
                  });
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  continue;
               }
               break;
            case 66129592:
               if (var18.equals("ENTER")) {
                  k.setFont(keyFontBold);
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 2, 1, 1));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        ActionEvent ev = new ActionEvent(source, 1001, source.getText());
                        if (source instanceof JTextArea) {
                           ((JTextArea)source).insert("\n", source.getCaretPosition());
                        } else {
                           ActionListener[] var6;
                           int var5 = (var6 = ((JTextField)source).getActionListeners()).length;

                           for(int var4 = 0; var4 < var5; ++var4) {
                              ActionListener a = var6[var4];
                              a.actionPerformed(ev);
                           }

                           KeyboardPage.exists = false;
                           KeyboardPage.this.keyboardFrame.dispose();
                        }

                     }
                  });
                  continue;
               }
               break;
            case 78869602:
               if (var18.equals("SHIFT")) {
                  k.setFont(keyFontBold);
                  center.add(k, new KeyboardPage.GBLTemp(i, j, 1, 1, 1));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        KeyboardPage.this.shift();
                     }
                  });
                  continue;
               }
               break;
            case 1876162461:
               if (var18.equals("<html>►</html>")) {
                  k.setPreferredSize(new Dimension(80, 80));
                  center.add(k, new KeyboardPage.GBLTemp(i, j));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        JTextComponent t = source;

                        try {
                           if (t.getCaretPosition() != t.getDocument().getLength()) {
                              t.setCaretPosition(t.getCaretPosition() + 1);
                           }
                        } catch (Exception var4) {
                           var4.printStackTrace();
                        }

                        if (KeyboardPage.this.capsLock) {
                           KeyboardPage.this.caps = true;
                           KeyboardPage.this.setCaps();
                        } else {
                           KeyboardPage.this.caps = false;
                           KeyboardPage.this.setCaps();
                        }

                     }
                  });
                  continue;
               }
               break;
            case 2124396627:
               if (var18.equals("<html>◄</html>")) {
                  k.setPreferredSize(new Dimension(80, 80));
                  center.add(k, new KeyboardPage.GBLTemp(i, j));
                  k.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        JTextComponent t = source;

                        try {
                           if (t.getCaretPosition() != 0) {
                              t.setCaretPosition(t.getCaretPosition() - 1);
                           }
                        } catch (Exception var4) {
                           var4.printStackTrace();
                        }

                        if (KeyboardPage.this.capsLock) {
                           KeyboardPage.this.caps = true;
                           KeyboardPage.this.setCaps();
                        } else {
                           KeyboardPage.this.caps = false;
                           KeyboardPage.this.setCaps();
                        }

                     }
                  });
                  continue;
               }
            }

            k.setPreferredSize(new Dimension(80, 80));
            center.add(k, new KeyboardPage.GBLTemp(i, j));
            k.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  KeyboardPage.KeyboardButton k = (KeyboardPage.KeyboardButton)e.getSource();
                  JTextComponent t = source;

                  try {
                     t.getDocument().insertString(t.getCaretPosition(), k.getText(), (AttributeSet)null);
                  } catch (BadLocationException var5) {
                     var5.printStackTrace();
                  }

                  if (KeyboardPage.this.capsLock) {
                     KeyboardPage.this.caps = true;
                     KeyboardPage.this.setCaps();
                  } else {
                     KeyboardPage.this.caps = false;
                     KeyboardPage.this.setCaps();
                  }

               }
            });
         }
      }

      KeyboardPage.KeyboardButton sp = new KeyboardPage.KeyboardButton("SPACE", "SPACE");
      sp.setPreferredSize(new Dimension(80, 80));
      center.add(sp, new KeyboardPage.GBLTemp(1, 4, 9, 1, 1));
      sp.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JTextComponent t = source;

            try {
               t.getDocument().insertString(t.getCaretPosition(), " ", (AttributeSet)null);
            } catch (BadLocationException var4) {
               var4.printStackTrace();
            }

         }
      });
      this.keyboardFrame.setVisible(true);
      source.setCaret(new KeyboardPage.CustomCaret());
      if (source instanceof JTextComponent && source.getDocument().getLength() != 0) {
         System.out.println(source.getDocument().getLength());
         source.setCaretPosition(source.getDocument().getLength());
      }

   }

   public void capsLock() {
      if (this.capsLock) {
         this.capsLock = false;
         this.caps = false;
      } else {
         this.capsLock = true;
         this.caps = true;
      }

      this.setCaps();
   }

   public void shift() {
      if (this.caps) {
         this.caps = false;
      } else {
         this.caps = true;
      }

      this.setCaps();
   }

   public void setCaps() {
      for(int j = 0; j < 4; ++j) {
         for(int i = 0; i < 12; ++i) {
            KeyboardPage.KeyboardButton k = (KeyboardPage.KeyboardButton)((ArrayList)this.keys.get(j)).get(i);
            k.setCaps(this.caps);
         }
      }

   }

   class CustomCaret extends DefaultCaret {
      private static final long serialVersionUID = 1L;

      public void focusGained(FocusEvent e) {
         super.focusGained(e);
      }

      public void focusLost(FocusEvent e) {
      }
   }

   static class FrameDragListener extends MouseAdapter {
      private final JFrame frame;
      private Point mouseDownCompCoords = null;

      public FrameDragListener(JFrame frame) {
         this.frame = frame;
      }

      public void mouseReleased(MouseEvent e) {
         this.mouseDownCompCoords = null;
      }

      public void mousePressed(MouseEvent e) {
         this.mouseDownCompCoords = e.getPoint();
      }

      public void mouseDragged(MouseEvent e) {
         Point currCoords = e.getLocationOnScreen();
         this.frame.setLocation(currCoords.x - this.mouseDownCompCoords.x, currCoords.y - this.mouseDownCompCoords.y);
      }
   }

   class GBLTemp extends GridBagConstraints {
      private static final long serialVersionUID = 1L;

      public GBLTemp(int x, int y) {
         this.gridx = x;
         this.gridy = y;
      }

      public GBLTemp(int x, int y, int w, int h) {
         this.gridx = x;
         this.gridy = y;
         this.gridwidth = w;
         this.gridheight = h;
      }

      public GBLTemp(int x, int y, int w, int h, int fill) {
         this.gridx = x;
         this.gridy = y;
         this.gridwidth = w;
         this.gridheight = h;
         this.fill = fill;
      }
   }

   class KeyboardButton extends JButton {
      private static final long serialVersionUID = 1L;
      private String ch;
      private String caps;
      private String active;

      public KeyboardButton(String ch, String caps) {
         super(ch);
         this.setFont(KeyboardPage.keyFont);
         this.active = ch;
         this.ch = ch;
         this.caps = caps;
         this.setContentAreaFilled(false);
         this.setFocusPainted(false);
         this.setFocusable(false);
      }

      public String getChar() {
         return this.active;
      }

      public void modChar() {
         if (this.active.equals(this.ch)) {
            this.active = this.caps;
         } else {
            this.active = this.ch;
         }

         this.setText(this.active);
      }

      public void setCaps(boolean cap) {
         if (cap) {
            this.active = this.caps;
            this.setText(this.active);
         } else {
            this.active = this.ch;
            this.setText(this.active);
         }

      }

      protected void paintComponent(Graphics g) {
         if (this.isEnabled()) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setPaint(new GradientPaint(new Point(0, 0), Color.WHITE, new Point(0, this.getHeight()), DisplayComponents.Active));
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.dispose();
            super.paintComponent(g);
         } else {
            super.paintComponent(g);
         }

      }
   }
}
