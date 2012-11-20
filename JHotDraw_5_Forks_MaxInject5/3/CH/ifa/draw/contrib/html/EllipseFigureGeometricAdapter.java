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
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // if obj is not a CompositeData, return false
        if (!(obj instanceof CompositeData)) {
            return false;
        }

        CompositeData other = (CompositeData) obj;

        // their compositeType should be equal
        if (!this.getCompositeType().equals() ) {
            return false;
        }

        if (contents.size() != other.values().size()) {
            return false;
        }

        for (Map.Entry<String,Object> entry : contents.entrySet()) {
            Object e1 = entry.getValue();
            Object e2 = other.get(entry.getKey());

            if (e1 == e2)
                continue;
            if (e1 == null)
                return false;

            boolean eq = e1.getClass().isArray() ?
                Arrays.deepEquals(new Object[] {e1}, new Object[] {e2}) :
                e1.equals(e2);

            if (!eq)
                return false;
        }

        // All tests for equality were successful
        //
        return true;
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
