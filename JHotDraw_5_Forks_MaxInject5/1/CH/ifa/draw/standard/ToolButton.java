/*
 * @(#)ToolButton.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;
import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

/**
 * A PaletteButton that is associated with a tool.
 *
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */
public class ToolButton extends PaletteButton implements ToolListener {

	private Tool            myTool;
	private PaletteIcon     myIcon;

	public ToolButton(PaletteListener listener, String iconName, String name, Tool tool) {
		super(listener);
		tool.addToolListener(this);
		setEnabled(tool.isUsable());

		// use a Mediatracker to ensure that all the images are initially loaded
		Iconkit kit = Iconkit.instance();
		if (kit == null) {
			throw new JHotDrawRuntimeException("Iconkit instance isn't set");
		}

		Image im[] = new Image[3];
		im[0] = kit.loadImageResource(iconName+"1.gif");
		im[1] = kit.loadImageResource(iconName+"2.gif");
		im[2] = kit.loadImageResource(iconName+"3.gif");

		MediaTracker tracker = new MediaTracker(this);
		for (int i = 0; i < 3; i++) {
			tracker.addImage(im[i], i);
		}
		try {
			tracker.waitForAll();
		}
		catch (Exception e) {
			// ignore exception
		}

		setPaletteIcon(new PaletteIcon(new Dimension(24,24), im[0], im[1], im[2]));
		setTool(tool);
		setName(name);

		// avoid null pointer exception if image could not be loaded
		if (im[0] != null) {
			setIcon(new ImageIcon(im[0]));
		}
		if (im[1] != null) {
			setPressedIcon(new ImageIcon(im[1]));
		}
		if (im[2] != null) {
			setSelectedIcon(new ImageIcon(im[2]));
		}
		setToolTipText(name);
	}

	public Tool tool() {
		return myTool;
	}

	public String name() {
		return getName();
	}

	public Object attributeValue() {
		return tool();
	}

	public Dimension getMinimumSize() {
		return new Dimension(getPaletteIcon().getWidth(), getPaletteIcon().getHeight());
	}

	public Dimension getPreferredSize() {
		return new Dimension(getPaletteIcon().getWidth(), getPaletteIcon().getHeight());
	}

	public Dimension getMaximumSize() {
		return new Dimension(getPaletteIcon().getWidth(), getPaletteIcon().getHeight());
	}

//  Not necessary anymore in JFC due to the support of Icons in JButton
/*
	public void paintBackground(Graphics g) { }

	public void paintNormal(Graphics g) {
		if (fIcon.normal() != null)
			g.drawImage(fIcon.normal(), 0, 0, this);
	}

	public void paintPressed(Graphics g) {
		if (fIcon.pressed() != null)
			g.drawImage(fIcon.pressed(), 0, 0, this);
	}
*/
	public void paintSelected(Graphics g) {
		if (getPaletteIcon().selected() != null) {
			g.drawImage(getPaletteIcon().selected(), 0, 0, this);
		}
	}

	public void paint(Graphics g) {
		// selecting does not work as expected with JFC1.1
		// see JavaBug: 4228035, 4233965
		if (isSelected()) {
			paintSelected(g);
		}
		else {
			super.paint(g);
		}
	}
  public static boolean isWellFormedIPv4Address(String address) {

      int addrLength = address.length();
      char testChar;
      int numDots = 0;
      int numDigits = 0;

      // make sure that 1) we see only digits and dot separators, 2) that
      // any dot separator is preceded and followed by a digit and
      // 3) that we find 3 dots
      //
      // RFC 2732 amended RFC 2396 by replacing the definition
      // of IPv4address with the one defined by RFC 2373. - mrglavas
      //
      // IPv4address = 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT
      //
      // One to three digits must be in each segment.
      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if ((i > 0 && !isDigit(address.charAt(i-1))) ||
              (i+1 < addrLength && !isDigit(address.charAt(i+1)))) {
            return false;
          }
          numDigits = 0;
          if (++numDots > 3) {
            return false;
          }
        }
        else if (!isDigit(testChar)) {
          return false;
        }
        // Check that that there are no more than three digits
        // in this segment.
        else if (++numDigits > 3) {
          return false;
        }
        // Check that this segment is not greater than 255.
        else if (numDigits == 3) {
          char first = address.charAt(i-2);
          char second = address.charAt(i-1);
          if (!(first < '2' ||
               (first == '2' &&
               (second < '5' ||
               (second == '5' && testChar <= '5'))))) {
            return false;
          }
        }
      }
      return (numDots == 3);
  }

	public void toolUsable(EventObject toolEvent) {
		setEnabled(true);
	}

	public void toolUnusable(EventObject toolEvent) {
		setEnabled(false);
		setSelected(false);
	}

	public void toolActivated(EventObject toolEvent) {
	}

	public void toolDeactivated(EventObject toolEvent) {
	}

	public void toolEnabled(EventObject toolEvent) {
		setEnabled(true);
	}

	public void toolDisabled(EventObject toolEvent) {
		setEnabled(false);
	}

	protected PaletteIcon getPaletteIcon() {
		return myIcon;
	}

	private void setPaletteIcon(PaletteIcon myIcon) {
		this.myIcon = myIcon;
	}

	private void setTool(Tool myTool) {
		this.myTool = myTool;
	}
}
