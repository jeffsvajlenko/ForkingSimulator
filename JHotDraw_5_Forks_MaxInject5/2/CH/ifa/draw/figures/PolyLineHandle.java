/*
 * @(#)PolyLineHandle.java
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
import CH.ifa.draw.standard.LocatorHandle;
import CH.ifa.draw.standard.SingleFigureEnumerator;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;
import java.awt.*;

/**
 * A handle for a node on the polyline.
 *
 * @version <$CURRENT_VERSION$>
 */
public class PolyLineHandle extends LocatorHandle {

	private int fIndex;

   /**
	* Constructs a poly line handle.
	* @param owner the owning polyline figure.
	* @param l the locator
	* @param index the index of the node associated with this handle.
	*/
	public PolyLineHandle(PolyLineFigure owner, Locator l, int index) {
		super(owner, l);
		fIndex = index;
	}

	public void invokeStart(int  x, int  y, DrawingView view) {
		setUndoActivity(createUndoActivity(view, fIndex));
		getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(owner()));
		((PolyLineHandle.UndoActivity)getUndoActivity()).setOldPoint(new Point(x,y));
	}

	public void invokeStep(int x, int y, int anchorX, int anchorY, DrawingView view) {
		int fIndex = ((PolyLineHandle.UndoActivity)getUndoActivity()).getPointIndex();
		myOwner().setPointAt(new Point(x, y), fIndex);
	}

	public void invokeEnd(int x, int y, int anchorX, int anchorY, DrawingView view) {
		if ((x == anchorX) && (y == anchorY)) {
			setUndoActivity(null);
		}
	}
	
	private PolyLineFigure myOwner() {
		return (PolyLineFigure)owner();
	}

	/**
	 * Factory method for undo activity. To be overriden by subclasses.
	 */
	protected Undoable createUndoActivity(DrawingView newView, int newPointIndex) {
		return new PolyLineHandle.UndoActivity(newView, newPointIndex);
	}
	
	public static class UndoActivity extends UndoableAdapter {
		private Point myOldPoint;
		private int myPointIndex;

		public UndoActivity(DrawingView newView, int newPointIndex) {
			super(newView);
			setUndoable(true);
			setRedoable(true);
			setPointIndex(newPointIndex);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			return movePointToOldLocation();
		}
	
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			return movePointToOldLocation();
		}
    public Remote toStub (Remote obj)
        throws NoSuchObjectException
    {
        Remote result = null;
        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }

        // If the class is already an IIOP stub then return it.
        if (StubAdapter.isStub( obj )) {
            return obj;
        }

        // If the class is already a JRMP stub then return it.
        if (obj instanceof java.rmi.server.RemoteStub) {
            return obj;
        }

        // Has it been exported to IIOP?
        Tie theTie = Util.getTie(obj);

        if (theTie != null) {
            result = Utility.loadStub(theTie,null,null,true);
        } else {
            if (Utility.loadTie(obj) == null) {
                result = java.rmi.server.RemoteObject.toStub(obj);
            }
        }

        if (result == null) {
            throw new NoSuchObjectException("object not exported");
        }

        return result;
    }

		protected boolean movePointToOldLocation() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasNextFigure()) {
				return false;
			}

			PolyLineFigure figure = (PolyLineFigure)fe.nextFigure();
			Point backupPoint = figure.pointAt(getPointIndex());
			figure.setPointAt(getOldPoint(), getPointIndex());
			setOldPoint(backupPoint);
			return true;
		}

		public void setOldPoint(Point newOldPoint) {
			myOldPoint = newOldPoint;
		}
		
		public Point getOldPoint() {
			return myOldPoint;
		}

		public void setPointIndex(int newPointIndex) {
			myPointIndex = newPointIndex;
		}
		
		public int getPointIndex() {
			return myPointIndex;
		}
	}
}
