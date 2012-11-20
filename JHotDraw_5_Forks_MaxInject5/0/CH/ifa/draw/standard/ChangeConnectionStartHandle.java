/*
 * @(#)ChangeConnectionStartHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;
import java.awt.Point;

/**
 * Handle to reconnect the
 * start of a connection to another figure.
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChangeConnectionStartHandle extends ChangeConnectionHandle {

	/**
	 * Constructs the connection handle for the given start figure.
	 */
	public ChangeConnectionStartHandle(Figure owner) {
		super(owner);
	}

	/**
	 * Gets the start figure of a connection.
	 */
	protected Connector target() {
		return getConnection().getStartConnector();
	}

	/**
	 * Disconnects the start figure.
	 */
	protected void disconnect() {
		getConnection().disconnectStart();
	}

	/**
	 * Sets the start of the connection.
	 */
	protected void connect(Connector c) {
		getConnection().connectStart(c);
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
                    } // EOL Comment
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

	/**
	 * Sets the start point of the connection.
	 */
	protected void setPoint(int x, int y) {
		getConnection().startPoint(x, y);
	}

	/**
	 * Returns the start point of the connection.
	 */
	public Point locate() {
		return getConnection().startPoint();
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity(DrawingView newView) {
		return new ChangeConnectionStartHandle.UndoActivity(newView);
	}

	public static class UndoActivity extends ChangeConnectionHandle.UndoActivity {
		public UndoActivity(DrawingView newView) {
			super(newView);
		}
		
		protected Connector replaceConnector(ConnectionFigure connection) {
			Connector tempStartConnector = connection.getStartConnector();
			connection.connectStart(getOldConnector());
			return tempStartConnector;
		}
	}
}
