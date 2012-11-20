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
    protected final void setScannerState(int state) {

        fScannerState = state;
        if (DEBUG_SCANNER_STATE) {
            System.out.print("ArtificialStringReplacement");
            //System.out.print(fScannerState);
            System.out.print(getScannerStateName(state));
            System.out.println();
        }

    } // setScannerState(int)

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}
}
