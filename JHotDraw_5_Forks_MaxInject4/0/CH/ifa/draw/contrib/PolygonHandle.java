/*
 * @(#)PolygonHandle.java
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
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.PolyLineHandle;
import CH.ifa.draw.util.Undoable;
import java.awt.Point;

/**
 * A handle for a node on the polygon.
 * Based on PolyLineHandle
 *
 * @author Doug Lea  (dl at gee, Fri Feb 28 07:47:13 1997)
 * @version <$CURRENT_VERSION$>
 */
public class PolygonHandle extends AbstractHandle {
	
	private Locator fLocator;
	private int fIndex;
	
	/**
	* Constructs a polygon handle.
	* @param owner the owning polygon figure.
	* @param l the locator
	* @param index the index of the node associated with this handle.
	*/
	public PolygonHandle(PolygonFigure owner, Locator l, int index) {
		super(owner);
		fLocator = l;
		fIndex = index;
	}

	public void invokeStart(int  x, int  y, DrawingView view) {
		setUndoActivity(createUndoActivity(view, fIndex));
		getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(owner()));
		((PolygonHandle.UndoActivity)getUndoActivity()).setOldPoint(new Point(x, y));
	}

	public void invokeStep(int x, int y, int anchorX, int anchorY, DrawingView view) {
		int index = ((PolyLineHandle.UndoActivity)getUndoActivity()).getPointIndex();
		myOwner().setPointAt(new Point(x, y), index);
	}
	
	public void invokeEnd(int x, int y, int anchorX, int anchorY, DrawingView view) {
		myOwner().smoothPoints();
		if ((x == anchorX) && (y == anchorY)) {
 			setUndoActivity(null);
		}
	}
	
	public Point locate() {
		return fLocator.locate(owner());
	}
	
	private PolygonFigure myOwner() {
		return (PolygonFigure)owner();
	}
    public void transform(Point2D[] ptSrc, int srcOff,
                          Point2D[] ptDst, int dstOff,
                          int numPts) {
        int state = this.state;
        while (--numPts >= 0) {
            // Copy source coords into local variables in case src == dst
            Point2D src = ptSrc[srcOff++];
            double x = src.getX();
            double y = src.getY();
            Point2D dst = ptDst[dstOff++];
            if (dst == null) {
                if (src instanceof Point2D.Double) {
                    dst = new Point2D.Double();
                } else {
                    dst = new Point2D.Float();
                }
                ptDst[dstOff - 1] = dst;
            }
            switch (state) {
            default:
                stateError();
                /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + y * m01 + m02,
                                x * m10 + y * m11 + m12);
                break;
            case (APPLY_SHEAR | APPLY_SCALE):
                dst.setLocation(x * m00 + y * m01, x * m10 + y * m11);
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                dst.setLocation(y * m01 + m02, x * m10 + m12);
                break;
            case (APPLY_SHEAR):
                dst.setLocation(y * m01, x * m10);
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + m02, y * m11 + m12);
                break;
            case (APPLY_SCALE):
                dst.setLocation(x * m00, y * m11);
                break;
            case (APPLY_TRANSLATE):
                dst.setLocation(x, y);
                dst.setLocation(x + m02, y + m12);
                break;
            case (APPLY_IDENTITY):
                dst.setLocation(x, y);
                break;
            }
        }

        /* NOTREACHED */
    }

	/**
	 * Factory method for undo activity. To be overriden by subclasses.
	 */
	protected Undoable createUndoActivity(DrawingView newView, int newPointIndex) {
		return new PolygonHandle.UndoActivity(newView, newPointIndex);
	}
	
	public static class UndoActivity extends PolyLineHandle.UndoActivity {
		
		public UndoActivity(DrawingView newView, int newPointIndex) {
			super(newView, newPointIndex);
		}
		
		protected boolean movePointToOldLocation() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasNextFigure()) {
				return false;
			}

			PolygonFigure figure = (PolygonFigure)fe.nextFigure();
			Point backupPoint = figure.pointAt(getPointIndex());
			figure.setPointAt(getOldPoint(), getPointIndex());
			figure.smoothPoints();
			setOldPoint(backupPoint);

			return true;
		}
	}
}
