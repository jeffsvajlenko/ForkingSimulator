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
    private boolean areSame(XSWildcardDecl wildcard) {
        if (fType == wildcard.fType) {
            // ##any, true
            if (fType == NSCONSTRAINT_ANY)
                return true;

            // ##other, only check the negated value
            // * when we support not(list), we need to check in the same way
            //   as for NSCONSTRAINT_LIST.
            if (fType == NSCONSTRAINT_NOT)
                return fNamespaceList[0] == wildcard.fNamespaceList[0];

            // ## list, must have the same length,
            // and each item in one list must appear in the other one
            // (we are assuming that there are no duplicate items in a list)
            if (fNamespaceList.length == wildcard.fNamespaceList.length) {
                for (int i=0; i<fNamespaceList.length; i++) {
                    if (!elementInSet(fNamespaceList[i], wildcard.fNamespaceList))
                        return false;
                }
                return true;
            }
        }

        return false;
    } // areSame

	public Connector connectorAt(int x, int y) {
		return new ChopDiamondConnector(this);
	}  
}
