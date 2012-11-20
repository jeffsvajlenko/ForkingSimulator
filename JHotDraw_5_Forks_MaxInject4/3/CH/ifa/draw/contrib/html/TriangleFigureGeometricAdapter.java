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
import java.awt.Shape;
import CH.ifa.draw.contrib.TriangleFigure;

/**
 * Geometric adapter for the TriangleFigure
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class TriangleFigureGeometricAdapter extends TriangleFigure
		 implements GeometricFigure {

	/**Constructor for the TriangleFigureGeometricAdapter object */
	public TriangleFigureGeometricAdapter() {
		super();
	}


	/**
	 *Constructor for the TriangleFigureGeometricAdapter object
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 */
	public TriangleFigureGeometricAdapter(Point origin, Point corner) {
		super(origin, corner);
	}


	/**
	 * Gets the shape attribute of the TriangleFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		return getPolygon();
	}
        public synchronized boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                // TODO: Add localized error message.
                throw new NullPointerException();
            }
            if (name.equals()) {
                try {
                    return (super.getProperty(SECURITY_MANAGER) != null);
                }
                // If the property is not supported the value must be false.
                catch (SAXException exc) {
                    return false;
                }
            }
            return super.getFeature(name);
        }
}
