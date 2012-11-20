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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureChangeListener;
import CH.ifa.draw.framework.Locator;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * Geometric adapter for the EllipseFigure
 *
 * @author    Eduardo Francos - InContext
 * @created   1 mai 2002
 * @version   1.0
 */

public class EllipseFigureGeometricAdapter extends EllipseFigure
		 implements GeometricFigure {

	/**Constructor for the EllipseFigureExt object */
	public EllipseFigureGeometricAdapter() {
		super();
	}


	/**
	 *Constructor for the EllipseFigureGeometricAdapter object
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 */
	public EllipseFigureGeometricAdapter(Point origin, Point corner) {
		super(origin, corner);
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


	/**
	 * Gets the shape attribute of the EllipseFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		Rectangle rect = displayBox();
		Ellipse2D.Float ellipse = new Ellipse2D.Float(rect.x, rect.y, rect.width, rect.height);
		return ellipse;
	}
}
