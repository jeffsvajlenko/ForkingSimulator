/*
 * @(#)CompositeFigureCreationTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.DrawingView;

import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class CompositeFigureCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;

	public CompositeFigureCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		Figure figure = drawing().findFigure(e.getX(), e.getY());
		if (figure != null) {
			figure = figure.getDecoratedFigure();
			if (figure instanceof CompositeFigure) {
				setContainerFigure((CompositeFigure)figure);
				setCreatedFigure(createFigure());
				setAddedFigure((getContainerFigure().add(getCreatedFigure())));
				getAddedFigure().displayBox(new Point(x, y), new Point(x, y));
			}
			else {
				toolDone();
			}
		}
		else {
			toolDone();
		}
	}

	public void mouseMove(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && !getContainerFigure().containsPoint(e.getX(), e.getY())) {
			// here you might want to constrain the mouse movements to the size of the
			// container figure: not sure whether this works...
			toolDone();
		}
		else {
			super.mouseMove(e, x, y);
		}
	}

	protected void setContainerFigure(CompositeFigure newContainerFigure) {
		myContainerFigure = newContainerFigure;
	}

	public CompositeFigure getContainerFigure() {
		return myContainerFigure;
	}

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
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
}
