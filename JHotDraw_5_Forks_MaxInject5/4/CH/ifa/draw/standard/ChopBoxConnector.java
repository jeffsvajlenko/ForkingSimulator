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
    private DocumentFragment traverseSameContainer( int how )
    {
        DocumentFragment frag = null;
        if ( how!=DELETE_CONTENTS)
            frag = fDocument.createDocumentFragment();

        // If selection is empty, just return the fragment
        if ( fStartOffset==fEndOffset )
            return frag;

        // Text node needs special case handling
        if ( fStartContainer.getNodeType()==Node.TEXT_NODE )
        {
            // get the substring
            String s = fStartContainer.getNodeValue();
            String sub = s.substring( fStartOffset, fEndOffset );

            // set the original text node to its new value
            if ( how != CLONE_CONTENTS )
            {
                ((TextImpl)fStartContainer).deleteData(fStartOffset,
                     fEndOffset-fStartOffset) ;
                // Nothing is partially selected, so collapse to start point
                collapse( true );
            }
            if ( how==DELETE_CONTENTS)
                return null;
            frag.appendChild( fDocument.createTextNode(sub) );
            return frag;
        }

        // Copy nodes between the start/end offsets.
        Node n = getSelectedNode( fStartContainer, fStartOffset );
        int cnt = fEndOffset - fStartOffset;
        while( cnt > 0 )
        {
            Node sibling = n.getNextSibling();
            Node xferNode = traverseFullySelected( n, how );
            if ( frag!=null )
                frag.appendChild( xferNode );
            --cnt;
            n = sibling;
        }

        // Nothing is partially selected, so collapse to start point
        if ( how != CLONE_CONTENTS )
            collapse( true );
        return frag;
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
}

