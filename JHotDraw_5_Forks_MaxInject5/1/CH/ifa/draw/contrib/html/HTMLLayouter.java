/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.awt.Insets;
import java.awt.Point;

import java.awt.Rectangle;
import java.io.IOException;
import CH.ifa.draw.contrib.Layoutable;
import CH.ifa.draw.contrib.Layouter;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * HTMLLayouter implements the logic for laying out figures based on an
 * HTML template.
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class HTMLLayouter implements Layouter {

	/**Constructor for the HTMLLayouter object */
	public HTMLLayouter() { }


	/**
	 * Constructor which associates a HTMLLayouter with
	 * a certain Layoutable.
	 *
	 * @param newLayoutable  Layoutable to be laid out
	 */
	public HTMLLayouter(Layoutable newLayoutable) {
		this();
//		setLayoutable(newLayoutable);
	}


	/**
	 * Description of the Method
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 * @return        Description of the Return Value
	 */
	public Rectangle calculateLayout(Point origin, Point corner) {
		/**
		 * @todo:   Implement this CH.ifa.draw.contrib.Layouter method
		 */
		throw new UnsupportedOperationException("Method calculateLayout() not yet implemented.");
	}


	/**
	 * Description of the Method
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 * @return        Description of the Return Value
	 */
	public Rectangle layout(Point origin, Point corner) {
		/**
		 * @todo:   Implement this CH.ifa.draw.contrib.Layouter method
		 */
		throw new UnsupportedOperationException("Method layout() not yet implemented.");
	}


	/**
	 * Sets the insets attribute of the HTMLLayouter object
	 *
	 * @param newInsets  The new insets value
	 */
	public void setInsets(Insets newInsets) {
		/**
		 * @todo:   Implement this CH.ifa.draw.contrib.Layouter method
		 */
		throw new UnsupportedOperationException("Method setInsets() not yet implemented.");
	}


	/**
	 * Gets the insets attribute of the HTMLLayouter object
	 *
	 * @return   The insets value
	 */
	public Insets getInsets() {
		/**
		 * @todo:   Implement this CH.ifa.draw.contrib.Layouter method
		 */
		throw new UnsupportedOperationException("Method getInsets() not yet implemented.");
	}


	/**
	 * Description of the Method
	 *
	 * @param dw  Description of the Parameter
	 */
	public void write(StorableOutput dw) {
		/**
		 * @todo:   Implement this CH.ifa.draw.util.Storable method
		 */
		throw new UnsupportedOperationException("Method write() not yet implemented.");
	}


	/**
	 * Description of the Method
	 *
	 * @param dr               Description of the Parameter
	 * @exception IOException  Description of the Exception
	 */
	public void read(StorableInput dr)
		throws IOException {
		/**
		 * @todo:   Implement this CH.ifa.draw.util.Storable method
		 */
		throw new UnsupportedOperationException("Method read() not yet implemented.");
	}


	/**
	 * Create a new instance of this type and sets the layoutable
	 *
	 * @param newLayoutable  Description of the Parameter
	 * @return               Description of the Return Value
	 */
	public Layouter create(Layoutable newLayoutable) {
		return new HTMLLayouter(newLayoutable);
	}
    private boolean typeAheadAssertions(Component target, AWTEvent e) {

        // Clear any pending events here as well as in the FOCUS_GAINED
        // handler. We need this call here in case a marker was removed in
        // response to a call to dequeueKeyEvents.
        pumpApprovedKeyEvents();

        switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
            case KeyEvent.KEY_PRESSED:
            case KeyEvent.KEY_RELEASED: {
                KeyEvent ke = (KeyEvent)e;
                synchronized (this) {
                    if (e.isPosted && typeAheadMarkers.size() != 0) {
                        TypeAheadMarker marker = (TypeAheadMarker)
                            typeAheadMarkers.getFirst();
                        // Fixed 5064013: may appears that the events have the same time
                        // if (ke.getWhen() >= marker.after) {
                        // The fix is rolled out.

                        if (ke.getWhen() > marker.after) {
                            focusLog.finer("Storing event {0} because of marker {1}", ke, marker);
                            enqueuedKeyEvents.addLast(ke);
                            return true;
                        }
                    }
                }

                // KeyEvent was posted before focus change request
                return preDispatchKeyEvent(ke);
            }

            case FocusEvent.FOCUS_GAINED:
                focusLog.finest("Markers before FOCUS_GAINED on {0}", target);
                dumpMarkers();
                // Search the marker list for the first marker tied to
                // the Component which just gained focus. Then remove
                // that marker, any markers which immediately follow
                // and are tied to the same component, and all markers
                // that preceed it. This handles the case where
                // multiple focus requests were made for the same
                // Component in a row and when we lost some of the
                // earlier requests. Since FOCUS_GAINED events will
                // not be generated for these additional requests, we
                // need to clear those markers too.
                synchronized (this) {
                    boolean found = false;
                    if (hasMarker(target)) {
                        for (Iterator iter = typeAheadMarkers.iterator();
                             iter.hasNext(); )
                        {
                            if (((TypeAheadMarker)iter.next()).untilFocused ==
                                target)
                            {
                
                found = true;
                            } else if (found) {
                                break;
                            }
                            iter.remove();
                        }
                    } else {
                        // Exception condition - event without marker
                        focusLog.finer("Event without marker {0}", e);
                    }
                }
                focusLog.finest("Markers after FOCUS_GAINED");
                dumpMarkers();

                redispatchEvent(target, e);

                // Now, dispatch any pending KeyEvents which have been
                // released because of the FOCUS_GAINED event so that we don't
                // have to wait for another event to be posted to the queue.
                pumpApprovedKeyEvents();
                return true;

            default:
                redispatchEvent(target, e);
                return true;
        }
    }
}
