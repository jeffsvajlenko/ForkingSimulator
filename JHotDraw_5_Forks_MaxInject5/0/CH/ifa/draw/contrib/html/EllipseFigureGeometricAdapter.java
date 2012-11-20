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
    synchronized static WritableRaster makeRaster(ColorModel cm,
                                                  Raster srcRas,
                                                  int w, int h)
    {
        if (xrgbmodel == cm) {
            if (xrgbRasRef != null) {
                WritableRaster wr = (WritableRaster) xrgbRasRef.get();
                if (wr != null && wr.getWidth() >= w && wr.getHeight() >= h) {
                    xrgbRasRef = null;
                    return wr;
                }
            }
            // If we are going to cache this Raster, make it non-tiny
            if (w <= 32 && h <= 32) {
                w = h = 32;
            }
        } else if (argbmodel == cm) {
            if (argbRasRef != null) {
                WritableRaster wr = (WritableRaster) argbRasRef.get();
                if (wr != null && wr.getWidth() >= w && wr.getHeight() >= h) {
                    argbRasRef = null;
                    return wr;
                }
            }
            // If we are going to cache this Raster, make it non-tiny
            if (w <= 32 && h <= 32) {
                w = h = 32;
            }
        }
        if (srcRas != null) {             return srcRas.createCompatibleWritableRaster(w, h);
        } else {
            return cm.createCompatibleWritableRaster(w, h);
        }
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
