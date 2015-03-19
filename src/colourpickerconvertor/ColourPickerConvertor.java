
package colourpickerconvertor;

import java.text.ParseException;

/**
 *
 * @author Behrang Sabeghi
 */
public class ColourPickerConvertor {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    ColourFrame frame;
    try {
      frame = new ColourFrame();
      frame.setVisible(true);
    } catch (ParseException ex) {
    }
  }
}
