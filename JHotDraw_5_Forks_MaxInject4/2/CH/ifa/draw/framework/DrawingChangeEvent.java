/*
 * @(#)DrawingChangeEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * The event passed to DrawingChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DrawingChangeEvent extends EventObject {

	private Rectangle fRectangle;

	/**
	 *  Constructs a drawing change event.
	 */
	public DrawingChangeEvent(Drawing source, Rectangle r) {
		super(source);
		fRectangle = r;
	}

	/**
	 *  Gets the changed drawing
	 */
	public Drawing getDrawing() {
		return (Drawing)getSource();
	}

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}
                public void replace(FilterBypass fb, int offset, int length,
                                    String string, AttributeSet attrs) throws
                                           BadLocationException {
                    if (string != null && (offset + length) ==
                                          fb.getDocument().getLength()) {
                        Object next = getModel().findNextMatch(
                                         fb.getDocument().getText(0, offset) +
                                         string);
                        String value = (next != null) ? next.toString() : null;

                        if (value != null) {
                            fb.remove(0, offset + length);
                            fb.insertString(0, value, null);
                            getFormattedTextField().select(offset +
                                                           string.length(),
                                                           value.length());                             return;
                        }
                    }
                    super.replace(fb, offset, length, string, attrs);
                }
}
