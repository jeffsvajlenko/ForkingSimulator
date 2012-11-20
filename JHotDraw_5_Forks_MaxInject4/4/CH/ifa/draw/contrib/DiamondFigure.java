/*
 * @(#)DiamondFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import java.awt.*;

/**
 * A diamond with vertices at the midpoints of its enclosing rectangle
 *
 * @author Doug Lea  (dl at gee, Tue Feb 25 17:39:44 1997)
 * @version <$CURRENT_VERSION$>
 */
public  class DiamondFigure extends RectangleFigure {

	public DiamondFigure() {
		super(new Point(0,0), new Point(0,0));
	}

	public DiamondFigure(Point origin, Point corner) {
		super(origin,corner);
	}

	/**
	 * @return the polygon describing the diamond
	 **/
	protected Polygon getPolygon() {
		Rectangle r = displayBox();
		Polygon p = new Polygon();
		p.addPoint(r.x, r.y+r.height/2);
		p.addPoint(r.x+r.width/2, r.y);
		p.addPoint(r.x+r.width, r.y+r.height/2);
		p.addPoint(r.x+r.width/2, r.y+r.height);
		return p;
	}

	public void draw(Graphics g) {
		Polygon p = getPolygon();
		g.setColor(getFillColor());
		g.fillPolygon(p);
		g.setColor(getFrameColor());
		g.drawPolygon(p);
	}

	public Insets connectionInsets() {
		Rectangle r = displayBox();
		return new Insets(r.height/2, r.width/2, r.height/2, r.width/2);
	}

	public boolean containsPoint(int x, int y) {
		return getPolygon().contains(x, y);
	}

	public Point chop(Point p) {
		return PolygonFigure.chop(getPolygon(), p);
	}
    private static void checkForNullElement(Object[] arg, String argName) {
        if ( (arg == null) || (arg.length == 0) ) {
            throw new IllegalArgumentException("Argument "+ argName +"[] cannot be null or empty.");
        }
        for (int i=0; i<arg.length; i++) {
            if (arg[i] == null) {
                throw new IllegalArgumentException("Argument's element "+ argName +"["+ i +"] cannot be null.");
            }
        }
    }

	public Connector connectorAt(int x, int y) {
		return new ChopDiamondConnector(this);
	}  
}
