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
    public static MediaSizeName findMedia(float x, float y, int units) {

        MediaSize match = MediaSize.ISO.A4;

        if (x <= 0.0f || y <= 0.0f || units < 1) {
            throw new IllegalArgumentException("args must be +ve values");
        }

        double ls = x * x + y * y;
        double tmp_ls;
        float []dim;
        float diffx = x;
        float diffy = y;

        for (int i=0; i < sizeVector.size() ; i++) {
            MediaSize mediaSize = (MediaSize)sizeVector.elementAt(i);
            dim = mediaSize.getSize(units);
            if (x == dim[0] && y == dim[1]) {
                match = mediaSize;
                break;
            } else {
                diffx = x - dim[0];
                diffy = y - dim[1];
                tmp_ls = diffx * diffx + diffy * diffy;
                if (tmp_ls < ls) {
                    ls = tmp_ls;
                    match = mediaSize;
                }
            }
        }

        return match.getMediaSizeName();
    }
	}
}
