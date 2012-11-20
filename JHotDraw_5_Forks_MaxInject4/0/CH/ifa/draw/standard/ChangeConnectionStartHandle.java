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
    private void storePostData(HTMLDocument doc, String target, String data) {

        /* POST data is stored into the document property named by constant
         * PostDataProperty from where it is later retrieved by method
         * JEditorPane.getPostData().  If the current document is in a frame,
         * the data is initially put into the toplevel (frameset) document
         * property (named <PostDataProperty>.<Target frame name>).  It is the
         * responsibility of FrameView which updates the target frame
         * to move data from the frameset document property into the frame
         * document property.
         */

        Document propDoc = doc;
        String propName = PostDataProperty;

        if (doc.isFrameDocument()) {
            // find the top-most JEditorPane holding the frameset view.
            FrameView.FrameEditorPane p =
                    (FrameView.FrameEditorPane) getContainer();
            FrameView v = p.getFrameView();
            JEditorPane c = v.getOutermostJEditorPane();
            if (c != null) {
                propDoc = c.getDocument();
                propName += ("." + target);
            }
        }

        propDoc.putProperty(propName, data);
    }
		
		protected Connector replaceConnector(ConnectionFigure connection) {
			Connector tempStartConnector = connection.getStartConnector();
			connection.connectStart(getOldConnector());
			return tempStartConnector;
		}
	}
}
