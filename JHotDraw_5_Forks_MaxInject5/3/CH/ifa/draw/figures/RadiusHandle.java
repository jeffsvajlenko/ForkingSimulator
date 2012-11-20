/*
 * @(#)RadiusHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Geom;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;
import java.awt.*;

/**
 * A Handle to manipulate the radius of a round corner rectangle.
 *
 * @version <$CURRENT_VERSION$>
 */
class RadiusHandle extends AbstractHandle {

	private static final int OFFSET = 4;

	public RadiusHandle(RoundRectangleFigure owner) {
		super(owner);
	}
    public void removeAllHighlights() {
        TextUI mapper = component.getUI();
        if (getDrawsLayeredHighlights()) {
            int len = highlights.size();
            if (len != 0) {
                int minX = 0;
                int minY = 0;
                int maxX = 0;
                int maxY = 0;
                int p0 = -1;
                int p1 = -1;
                for (int i = 0; i < len; i++) {
                    HighlightInfo hi = highlights.elementAt(i);
                    if (hi instanceof LayeredHighlightInfo) {
                        LayeredHighlightInfo info = (LayeredHighlightInfo)hi;
                        minX = Math.min(minX, info.x);
                        minY = Math.min(minY, info.y);
                        maxX = Math.max(maxX, info.x + info.width);
                        maxY = Math.max(maxY, info.y + info.height);
                    }
                    else {
                        if (p0 == -1) {
                            p0 = hi.p0.getOffset();
                            p1 = hi.p1.getOffset();
                        }
                        else {
                            p0 = Math.min(p0, hi.p0.getOffset());
                            p1 = Math.max(p1, hi.p1.getOffset());
                        }
                 
   }
                }
                if (minX != maxX && minY != maxY) {
                    component.repaint(minX, minY, maxX - minX, maxY - minY);
                }
                if (p0 != -1) {
                    try {
                        safeDamageRange(p0, p1);
                    } catch (BadLocationException e) {}
                }
                highlights.removeAllElements();
            }
        }
        else if (mapper != null) {
            int len = highlights.size();
            if (len != 0) {
                int p0 = Integer.MAX_VALUE;
                int p1 = 0;
                for (int i = 0; i < len; i++) {
                    HighlightInfo info = highlights.elementAt(i);
                    p0 = Math.min(p0, info.p0.getOffset());
                    p1 = Math.max(p1, info.p1.getOffset());
                }
                try {
                    safeDamageRange(p0, p1);
                } catch (BadLocationException e) {}

                highlights.removeAllElements();
            }
        }
    }

	public void invokeStart(int  x, int  y, DrawingView view) {
		setUndoActivity(createUndoActivity(view));
		getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(owner()));
		((RadiusHandle.UndoActivity)getUndoActivity()).
			setOldRadius(((RoundRectangleFigure)owner()).getArc());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		int dx = x-anchorX;
		int dy = y-anchorY;
		RoundRectangleFigure owner = (RoundRectangleFigure)owner();
		Rectangle r = owner.displayBox();
		Point originalRadius = ((RadiusHandle.UndoActivity)getUndoActivity()).getOldRadius();
		int rx = Geom.range(0, r.width, 2*(originalRadius.x/2 + dx));
		int ry = Geom.range(0, r.height, 2*(originalRadius.y/2 + dy));
		owner.setArc(rx, ry);
	}

	public void invokeEnd(int x, int y, int anchorX, int anchorY, DrawingView view) {
		Point currentRadius = ((RoundRectangleFigure)owner()).getArc();
		Point originalRadius = ((RadiusHandle.UndoActivity)getUndoActivity()).getOldRadius();
		// there has been no change so there is nothing to undo
		if ((currentRadius.x == originalRadius.x) && (currentRadius.y == originalRadius.y)) {
			setUndoActivity(null);
		}
	}

	public Point locate() {
		RoundRectangleFigure owner = (RoundRectangleFigure)owner();
		Point radius = owner.getArc();
		Rectangle r = owner.displayBox();
		return new Point(r.x+radius.x/2+OFFSET, r.y+radius.y/2+OFFSET);
	}

	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.yellow);
		g.fillOval(r.x, r.y, r.width, r.height);

		g.setColor(Color.black);
		g.drawOval(r.x, r.y, r.width, r.height);
	}

	/**
	 * Factory method for undo activity. To be overriden by subclasses.
	 */
	protected Undoable createUndoActivity(DrawingView newView) {
		return new RadiusHandle.UndoActivity(newView);
	}
	
	public static class UndoActivity extends UndoableAdapter {
		private Point myOldRadius;
		
		public UndoActivity(DrawingView newView) {
			super(newView);
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			return resetRadius();
		}
	
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			return resetRadius();
		}

		protected boolean resetRadius() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasNextFigure()) {
				return false;
			}
			RoundRectangleFigure currentFigure = (RoundRectangleFigure)fe.nextFigure();
			Point figureRadius = currentFigure.getArc();
			currentFigure.setArc(getOldRadius().x, getOldRadius().y);
			setOldRadius(figureRadius);
			return true;
		}
		
		protected void setOldRadius(Point newOldRadius) {
			myOldRadius = newOldRadius;
		}

		public Point getOldRadius() {
			return myOldRadius;
		}
	}
}
