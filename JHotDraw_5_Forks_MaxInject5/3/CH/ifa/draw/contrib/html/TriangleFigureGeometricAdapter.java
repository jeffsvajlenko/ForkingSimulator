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
    private Paint decodeGradient7(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h= (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.024f,0.06f,0.276f,0.6f,0.65f,0.7f,0.856f,0.96f,0.98f,1.0f },
                new Color[] { (Color)componentColors[0],
                            decodeColor((Color)componentColors[0],(Color)componentColors[1],0.5f),
                            (Color)componentColors[1],
                            decodeColor((Color)componentColors[1],(Color)componentColors[2],0.5f),
                            (Color)componentColors[2],
                            decodeColor((Color)componentColors[2],(Color)componentColors[2],0.5f),
                            (Color)componentColors[2],
                            decodeColor((Color)componentColors[2],(Color)componentColors[3],0.5f),
                            (Color)componentColors[3],
                            decodeColor((Color)componentColors[3],(Color)componentColors[3],0.5f),
                            (Color)componentColors[3]});
    }


	/**
	 * Gets the shape attribute of the TriangleFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		return getPolygon();
	}
}
