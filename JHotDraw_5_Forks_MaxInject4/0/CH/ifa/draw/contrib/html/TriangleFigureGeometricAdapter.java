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
    private void paintBackgroundMouseOverAndSelected(Graphics2D g) {
        roundRect = decodeRoundRect5();
        g.setPaint(color30);
        g.fill(roundRect);
        roundRect = decodeRoundRect2();
        g.setPaint(decodeGradient12(roundRect));
        g.fill(roundRect);
        roundRect = decodeRoundRect3();
        g.setPaint(decodeGradient10(roundRect));
        g.fill(roundRect);

    } // EOL Comment
}
