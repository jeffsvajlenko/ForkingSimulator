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
    private static boolean hasStaticInitializer(Class cl) {
        if (hasStaticInitializerMethod == null) {
            Class classWithThisMethod = null;

            try {
                try {
                    // When using rip-int with Merlin or when this is a Merlin
                    // workspace, the method we want is in sun.misc.ClassReflector
                    // and absent from java.io.ObjectStreamClass.
                    //
                    // When compiling rip-int with JDK 1.3.x, we have to get it
                    // from java.io.ObjectStreamClass.
                    classWithThisMethod = Class.forName("sun.misc.ClassReflector");
                } catch (ClassNotFoundException cnfe) {
                    // Do nothing.  This is either not a Merlin workspace,
                    // or rip-int is being compiled with something other than
                    // Merlin, probably JDK 1.3.  Fall back on java.io.ObjectStreaClass.
                }
                if (classWithThisMethod == null)
                    classWithThisMethod = java.io.ObjectStreamClass.class;

                hasStaticInitializerMethod =
                    classWithThisMethod.getDeclaredMethod("hasStaticInitializer",
                                                          new Class[] { Class.class });
            } catch (NoSuchMethodException ex) {
            }

            if (hasStaticInitializerMethod == null) {
                // XXX I18N, logging needed
                throw new InternalError("Can't find hasStaticInitializer method on "
                                        + classWithThisMethod.getName());
            }
        }

        try {
            Boolean retval = (Boolean)
                hasStaticInitializerMethod.invoke(null, new Object[] { cl });
            return retval.booleanValue();
        } catch (Exception ex) {
            // XXX I18N, logging needed
            InternalError ie = new InternalError( "Error invoking hasStaticInitializer" ) ;
            ie.initCause( ex ) ;
            throw ie ;
        }
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
