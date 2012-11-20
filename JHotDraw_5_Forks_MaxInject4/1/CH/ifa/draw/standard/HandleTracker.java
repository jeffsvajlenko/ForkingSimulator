/*
 * @(#)HandleTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.event.MouseEvent;
import CH.ifa.draw.framework.*;

/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
public class HandleTracker extends AbstractTool {

	private Handle  fAnchorHandle;

	public HandleTracker(DrawingEditor newDrawingEditor, Handle anchorHandle) {
		super(newDrawingEditor);
		fAnchorHandle = anchorHandle;
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
                                                           value.length());
               
             return;
                        }
                    }
                    super.replace(fb, offset, length, string, attrs);
                }

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		fAnchorHandle.invokeStart(x, y, view());
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		fAnchorHandle.invokeStep(x, y, getAnchorX(), getAnchorY(), view());
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		fAnchorHandle.invokeEnd(x, y, getAnchorX(), getAnchorY(), view());
	}

	public void activate() {
		// suppress clearSelection() and tool-activation-notification
		// in superclass by providing an empty implementation
	}
}
