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

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;

import CH.ifa.draw.contrib.DiamondFigure;

/**
 * Geometric adapter for the DiamondFigure
 *
 * @author    Eduardo Francos - InContext
 * @created   6 mai 2002
 * @version   1.0
 */

public class DiamondFigureGeometricAdapter extends DiamondFigure
		 implements GeometricFigure {

	/**Constructor for the DiamondFigureGeometricAdapter object */
	public DiamondFigureGeometricAdapter() {
		super();
	}


	/**
	 *Constructor for the DiamondFigureGeometricAdapter object
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 */
	public DiamondFigureGeometricAdapter(Point origin, Point corner) {
		super(origin, corner);
	}


	/**
	 * Gets the shape attribute of the DiamondFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		return getPolygon();
	}
    public int createDeferredAttribute(String attrName, String attrURI,
                                       String attrValue, boolean specified) {

        // create node
        int nodeIndex = createNode(NodeImpl.ATTRIBUTE_NODE);
        int chunk = nodeIndex >> CHUNK_SHIFT;
        int index = nodeIndex & CHUNK_MASK;
        setChunkValue(fNodeName, attrName, chunk, index);
        setChunkValue(fNodeURI, attrURI, chunk, index);
        setChunkValue(fNodeValue, attrValue, chunk, index);
        int extra = specified ? SPECIFIED : 0;
        setChunkIndex(fNodeExtra, extra, chunk, index);

        // return node index
        return nodeIndex;

    } // createDeferredAttribute(String,String,String,boolean):int

}
