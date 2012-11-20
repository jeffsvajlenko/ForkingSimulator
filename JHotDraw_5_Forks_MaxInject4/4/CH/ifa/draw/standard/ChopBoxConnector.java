/*
 * @(#)ChopBoxConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Geom;

/**
 * A ChopBoxConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 *
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChopBoxConnector extends AbstractConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -1461450322712345462L;

	public ChopBoxConnector() { // only used for Storable implementation
	}

	public ChopBoxConnector(Figure owner) {
		super(owner);
	}

	public Point findStart(ConnectionFigure connection) {
		Figure startFigure = connection.getStartConnector().owner();
		Rectangle r2 = connection.getEndConnector().displayBox();
		Point r2c = null;

		if (connection.pointCount() == 2) {
			r2c = new Point(r2.x + r2.width/2, r2.y + r2.height/2);
		 }
		 else {
			r2c = connection.pointAt(1);
		}

		return chop(startFigure, r2c);
	}

	public Point findEnd(ConnectionFigure connection) {
		Figure endFigure = connection.getEndConnector().owner();
		Rectangle r1 = connection.getStartConnector().displayBox();
		Point r1c = null;

		if (connection.pointCount() == 2) {
			r1c = new Point(r1.x + r1.width/2, r1.y + r1.height/2);
		}
		else {
			r1c = connection.pointAt(connection.pointCount()-2);
		}

		return chop(endFigure, r1c);
	}

	protected Point chop(Figure target, Point from) {
		Rectangle r = target.displayBox();
		return Geom.angleToPoint(r, (Geom.pointToAngle(r, from)));
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
}

