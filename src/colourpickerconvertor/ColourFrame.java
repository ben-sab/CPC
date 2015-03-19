package colourpickerconvertor;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Behrang Sabeghi
 */
public class ColourFrame extends JFrame implements AWTEventListener, PropertyChangeListener, ActionListener {

  private boolean isMouseInside, ownsFocus, isKeyPressed, isPickerActive, ignoreClick;
  private JPanel colourPanel, mainPanel;
  private JFormattedTextField[] rgb, hsb;
  private JFormattedTextField html;
  private JToggleButton colourPicker;
  private Color colour;
  private NumberFormat rgbFormat, hsbFormat;
  private MaskFormatter htmlFormat;

  public ColourFrame() throws ParseException {
    super("Eudemonia Colour Converter");
    isMouseInside = false;
    ownsFocus = false;
    isKeyPressed = false;
    isPickerActive = false;
    ignoreClick = true;
    colour = Color.BLACK;

    rgbFormat = NumberFormat.getIntegerInstance();
    rgbFormat.setMaximumIntegerDigits(3);
    rgbFormat.setMinimumIntegerDigits(1);

    hsbFormat = NumberFormat.getNumberInstance();
    hsbFormat.setMaximumFractionDigits(3);
    hsbFormat.setMaximumIntegerDigits(3);
    hsbFormat.setMinimumIntegerDigits(1);

    htmlFormat = new MaskFormatter("******");
    htmlFormat.setValidCharacters("abcdefABCDEF0123456789");

    html = new JFormattedTextField(htmlFormat);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(300, 305);
    setResizable(false);
    setAlwaysOnTop(true);
    setLayout(new BorderLayout());
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screen = toolkit.getScreenSize();
    setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);
    toolkit.addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

    colourPanel = new JPanel(null);
    colourPanel.setBackground(colour);
    colourPanel.setBounds(20, 20, 100, 100);
    colourPanel.setBorder(new LineBorder(Color.BLACK));
    colourPicker = new JToggleButton("Color Picker");
    colourPicker.setBounds(160, 40, 120, 50);
    colourPicker.setFocusable(false);
    colourPicker.addActionListener(this);
    JPanel topPanel = new JPanel(null);
    topPanel.add(colourPanel);
    topPanel.add(colourPicker);
    topPanel.setPreferredSize(new Dimension(getWidth(), 140));
    topPanel.setBackground(new Color(117, 117, 117));

    getContentPane().add(topPanel, BorderLayout.PAGE_START);

    rgb = new JFormattedTextField[3];
    hsb = new JFormattedTextField[3];
    JLabel[] rgbLabels = new JLabel[3];
    JLabel[] hsbLabels = new JLabel[3];
    JLabel[] hsbLabelsAfter = new JLabel[3];
    mainPanel = new JPanel(null);
    mainPanel.setBackground(new Color(211, 211, 211));

    rgbLabels[0] = new JLabel("R:");
    rgbLabels[1] = new JLabel("G:");
    rgbLabels[2] = new JLabel("B:");
    componentMaker(rgbLabels[0], 0, 0, 20, 20, "");
    componentMaker(rgbLabels[1], 1, 0, 20, 20, "");
    componentMaker(rgbLabels[2], 2, 0, 20, 20, "");

    rgb[0] = new JFormattedTextField(rgbFormat);
    rgb[1] = new JFormattedTextField(rgbFormat);
    rgb[2] = new JFormattedTextField(rgbFormat);
    hsb[0] = new JFormattedTextField(hsbFormat);
    hsb[1] = new JFormattedTextField(hsbFormat);
    hsb[2] = new JFormattedTextField(hsbFormat);

    componentMaker(rgb[0], 0, 1, 70, 20, "R");
    componentMaker(rgb[1], 1, 1, 70, 20, "G");
    componentMaker(rgb[2], 2, 1, 70, 20, "B");

    hsbLabels[0] = new JLabel("H:");
    hsbLabels[1] = new JLabel("S:");
    hsbLabels[2] = new JLabel("L:");
    componentMaker(hsbLabels[0], 0, 2, 20, 20, "");
    componentMaker(hsbLabels[1], 1, 2, 20, 20, "");
    componentMaker(hsbLabels[2], 2, 2, 20, 20, "");

    componentMaker(hsb[0], 0, 3, 70, 20, "H");
    componentMaker(hsb[1], 1, 3, 70, 20, "S");
    componentMaker(hsb[2], 2, 3, 70, 20, "L");

    hsbLabelsAfter[0] = new JLabel("°");
    hsbLabelsAfter[1] = new JLabel("%");
    hsbLabelsAfter[2] = new JLabel("%");
    componentMaker(hsbLabelsAfter[0], 0, 4, 20, 20, "");
    componentMaker(hsbLabelsAfter[1], 1, 4, 20, 20, "");
    componentMaker(hsbLabelsAfter[2], 2, 4, 20, 20, "");

    getContentPane().add(mainPanel, BorderLayout.CENTER);

    JLabel htmlLabel = new JLabel("HTML:   #");
    html.setText("000000");
    html.addPropertyChangeListener(this);
    html.setName("HTML");
    htmlLabel.setBounds(60, 10, 70, 20);
    html.setBounds(130, 10, 100, 20);
    JPanel bottomPanel = new JPanel(null);
    bottomPanel.add(htmlLabel);
    bottomPanel.add(html);
    bottomPanel.setPreferredSize(new Dimension(getWidth(), 40));

    getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

  }

  public final void componentMaker(JComponent comp, int row, int column, int width, int height, String name) {
    int x = 10 + ((column == 0) ? 10 : (column == 1) ? 30 : (column == 2) ? 160 : (column == 3) ? 180 : 250);
    int y = 10 + (row * 30);

    if (comp instanceof JFormattedTextField) {
      JFormattedTextField tmp = ((JFormattedTextField) comp);
      tmp.setText("0");
      tmp.addPropertyChangeListener(this);
      tmp.setName(name);
    }

    comp.setBounds(x, y, width, height);
    mainPanel.add(comp);
  }

  @Override
  public void eventDispatched(AWTEvent event) {
    Point p = MouseInfo.getPointerInfo().getLocation();
    try {
      if (event.getID() == MouseEvent.MOUSE_ENTERED) {
        isMouseInside = true;
      } else if (event.getID() == MouseEvent.MOUSE_EXITED) {
        isMouseInside = false;
      }
      if ((event.getID() == FocusEvent.FOCUS_LOST)) {
        ownsFocus = false;
        if (isKeyPressed) {
//          System.out.println("Left window through keyboard");
        } else if (!isMouseInside && isPickerActive) {
          if (ignoreClick) {
            ignoreClick = false;
          } else {
            colour = new Robot().getPixelColor(p.x, p.y);
            colourPanel.setBackground(colour);
            int r = colour.getRed();
            int g = colour.getGreen();
            int b = colour.getBlue();
            html.setValue(rgb2html(r, g, b));
            colourPicker.doClick();
          }
        }
      } else if (event.getID() == FocusEvent.FOCUS_GAINED) {
        ownsFocus = true;
      } else if (event.getID() == MouseEvent.MOUSE_CLICKED && isPickerActive) {
        if (ignoreClick) {
          ignoreClick = false;
        } else {
          colour = new Robot().getPixelColor(p.x, p.y);
          colourPanel.setBackground(colour);
          int r = colour.getRed();
          int g = colour.getGreen();
          int b = colour.getBlue();
          html.setValue(rgb2html(r, g, b));
          colourPicker.doClick();
        }
      }

      if (event.getID() == KeyEvent.KEY_PRESSED) {
        isKeyPressed = true;
      } else {
        isKeyPressed = false;
      }

    } catch (AWTException ex) {
    }
  }

  private String rgb2html(int r, int g, int b) {
    int r1 = (int) Math.floor(r / 16);
    int g1 = (int) Math.floor(g / 16);
    int b1 = (int) Math.floor(b / 16);

    int r2 = r - r1 * 16;
    int g2 = g - g1 * 16;
    int b2 = b - b1 * 16;

    char r3 = decimal2HexLookup(r1);
    char r4 = decimal2HexLookup(r2);
    char g3 = decimal2HexLookup(g1);
    char g4 = decimal2HexLookup(g2);
    char b3 = decimal2HexLookup(b1);
    char b4 = decimal2HexLookup(b2);

    return (r3 + "" + r4) + (g3 + "" + g4) + (b3 + "" + b4);

  }

  private int[] html2rgb(String html) {
    int[] result = new int[3];
    try {
      html = htmlFormat.valueToString(html);

      if (html.length() == 6) {
        String r = html.substring(0, 2);
        String g = html.substring(2, 4);
        String b = html.substring(4, 6);

        result[0] = hex2DecLookup(r.charAt(0)) * 16 + hex2DecLookup(r.charAt(1));
        result[1] = hex2DecLookup(g.charAt(0)) * 16 + hex2DecLookup(g.charAt(1));
        result[2] = hex2DecLookup(b.charAt(0)) * 16 + hex2DecLookup(b.charAt(1));
      }

    } catch (ParseException ex) {
    }
    return result;
  }

  private int hex2DecLookup(char singleHex) {
    if (singleHex == '0') {
      return 0;
    } else if (singleHex == '1') {
      return 1;
    } else if (singleHex == '2') {
      return 2;
    } else if (singleHex == '3') {
      return 3;
    } else if (singleHex == '4') {
      return 4;
    } else if (singleHex == '5') {
      return 5;
    } else if (singleHex == '6') {
      return 6;
    } else if (singleHex == '7') {
      return 7;
    } else if (singleHex == '8') {
      return 8;
    } else if (singleHex == '9') {
      return 9;
    } else if (singleHex == 'A' || singleHex == 'a') {
      return 10;
    } else if (singleHex == 'B' || singleHex == 'b') {
      return 11;
    } else if (singleHex == 'C' || singleHex == 'c') {
      return 12;
    } else if (singleHex == 'D' || singleHex == 'd') {
      return 13;
    } else if (singleHex == 'E' || singleHex == 'e') {
      return 14;
    } else if (singleHex == 'F' || singleHex == 'f') {
      return 15;
    }
    return -1;
  }

  public static char decimal2HexLookup(int i) {
    if (i >= 0 && i < 16) {
      if (i == 0) {
        return '0';
      } else if (i == 1) {
        return '1';
      } else if (i == 2) {
        return '2';
      } else if (i == 3) {
        return '3';
      } else if (i == 4) {
        return '4';
      } else if (i == 5) {
        return '5';
      } else if (i == 6) {
        return '6';
      } else if (i == 7) {
        return '7';
      } else if (i == 8) {
        return '8';
      } else if (i == 9) {
        return '9';
      } else if (i == 10) {
        return 'A';
      } else if (i == 11) {
        return 'B';
      } else if (i == 12) {
        return 'C';
      } else if (i == 13) {
        return 'D';
      } else if (i == 14) {
        return 'E';
      } else if (i == 15) {
        return 'F';
      }
    }
    throw new IllegalArgumentException("decimal2Hex: Illegal argument.");
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    Object source = evt.getSource();
    if (source instanceof JFormattedTextField) {
      try {
        JFormattedTextField current = (JFormattedTextField) source;
        String name = current.getName();

        int r = (!rgb[0].getText().equals("")) ? Integer.parseInt(rgb[0].getText()) : 0;
        int g = (!rgb[1].getText().equals("")) ? Integer.parseInt(rgb[1].getText()) : 0;
        int b = (!rgb[2].getText().equals("")) ? Integer.parseInt(rgb[2].getText()) : 0;
        double h = (!hsb[0].getText().equals("")) ? Double.parseDouble(hsb[0].getText()) : 0;
        double s = (!hsb[1].getText().equals("")) ? Double.parseDouble(hsb[1].getText()) : 0;
        double l = (!hsb[2].getText().equals("")) ? Double.parseDouble(hsb[2].getText()) : 0;

        switch (name) {
          case "R":
            if (r > 255) {
              r = 255;
              rgb[0].setValue(r);
            }
          case "G":
            if (g > 255) {
              g = 255;
              rgb[1].setValue(g);
            }
          case "B":
            if (b > 255) {
              b = 255;
              rgb[2].setValue(b);
            }
            colour = new Color(r, g, b);
            colourPanel.setBackground(colour);
            float[] hsl = Color.RGBtoHSB(r, g, b, null);
            hsb[0].setText(hsbFormat.format(hsl[0] * 360));
            hsb[1].setText(hsbFormat.format(hsl[1] * 100));
            hsb[2].setText(hsbFormat.format(hsl[2] * 100));
            html.setText(rgb2html(r, g, b));
            break;
          case "H":
            if (h > 360) {
              h = h % 360;
              hsb[0].setValue(h);
            }
          case "S":
            if (s > 100) {
              s = 100;
              hsb[1].setValue(s);
            }
          case "L":
            if (l > 100) {
              l = 100;
              hsb[2].setValue(l);
            }
            colour = Color.getHSBColor((float) h / 360, (float) s / 100, (float) l / 100);
            colourPanel.setBackground(colour);
            r = colour.getRed();
            g = colour.getGreen();
            b = colour.getBlue();
            rgb[0].setText("" + r);
            rgb[1].setText("" + g);
            rgb[2].setText("" + b);
            html.setText(rgb2html(r, g, b));
            break;
          case "HTML":
            String htmlstr = html.getText();
            int[] htmlTOrgb = html2rgb(htmlstr);
            r = htmlTOrgb[0];
            g = htmlTOrgb[1];
            b = htmlTOrgb[2];
            colour = new Color(r, g, b);
            rgb[0].setText("" + r);
            rgb[1].setText("" + g);
            rgb[2].setText("" + b);
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            hsb[0].setText(hsbFormat.format(hsv[0] * 360));
            hsb[1].setText(hsbFormat.format(hsv[1] * 100));
            hsb[2].setText(hsbFormat.format(hsv[2] * 100));
            colourPanel.setBackground(colour);
            break;
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    isPickerActive = !isPickerActive;
    ignoreClick = true;
  }
}
