/*
 * @(#)DragTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * DragTracker implements the dragging of the clicked
 * figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
public class DragTracker extends AbstractTool {

	private Figure  fAnchorFigure;
	private int     fLastX, fLastY;      // previous mouse position
	private boolean fMoved = false;

	public DragTracker(DrawingEditor newDrawingEditor, Figure anchor) {
		super(newDrawingEditor);
		fAnchorFigure = anchor;
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		fLastX = x;
		fLastY = y;

		if (e.isShiftDown()) {
		   getActiveView().toggleSelection(fAnchorFigure);
		   fAnchorFigure = null;
		}
		else if (!getActiveView().isFigureSelected(fAnchorFigure)) {
			getActiveView().clearSelection();
			getActiveView().addToSelection(fAnchorFigure);
		}
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(getActiveView().selection());
//		getUndoActivity().setAffectedFigures(view().selectionElements());
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		fMoved = (Math.abs(x - getAnchorX()) > 4) || (Math.abs(y - getAnchorY()) > 4);

		if (fMoved) {
			FigureEnumeration figures = getUndoActivity().getAffectedFigures();
			while (figures.hasNextFigure()) {
				figures.nextFigure().moveBy(x - fLastX, y - fLastY);
			}
		}
		fLastX = x;
		fLastY = y;
	}

	public void activate() {
		// suppress clearSelection() and tool-activation-notification
		// in superclass
	}

	public void deactivate() {
		if (fMoved) {
			((DragTracker.UndoActivity)getUndoActivity()).setBackupPoint(new Point(fLastX, fLastY));
		}
		else {
			setUndoActivity(null);
		}
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new DragTracker.UndoActivity(getActiveView(), new Point(fLastX, fLastY));
	}

	public static class UndoActivity extends UndoableAdapter {
		private Point myOriginalPoint;
		private Point myBackupPoint;

		public UndoActivity(DrawingView newDrawingView, Point newOriginalPoint) {
			super(newDrawingView);
			setOriginalPoint(newOriginalPoint);
			setUndoable(true);
			setRedoable(true);
		}

		/*
		 * Undo the activity
		 * @return true if the activity could be undone, false otherwise
		 */
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			moveAffectedFigures(getBackupPoint(), getOriginalPoint());
			return true;
		}
    private byte[] encodeImpl( Any data, boolean sendTypeCode )
        throws InvalidTypeForEncoding
    {
        if( data == null )
            throw wrapper.nullParam() ;

        // _REVISIT_ Note that InvalidTypeForEncoding is never thrown in
        // the body of this method.  This is due to the fact that CDR*Stream
        // will never throw an exception if the encoding is invalid.  To
        // fix this, the CDROutputStream must know the version of GIOP it
        // is encoding for and it must check to ensure that, for example,
        // wstring cannot be encoded in GIOP 1.0.
        //
        // As part of the GIOP 1.2 work, the CDRInput and OutputStream will
        // be versioned.  This can be handled once this work is complete.

        // Create output stream with default endianness.
        EncapsOutputStream cdrOut = new EncapsOutputStream(
            (com.sun.corba.se.spi.orb.ORB)orb, giopVersion );

        // This is an encapsulation, so put out the endian:
        cdrOut.putEndian();

        // Sometimes encode type code:
        if( sendTypeCode ) {
            cdrOut.write_TypeCode( data.type() );
        }

        // Encode value and return.

        return cdrOut.toByteArray();
    }

		/*
		 * Redo the activity
		 * @return true if the activity could be redone, false otherwise
		 */
		public boolean redo() {
			if (!super.redo()) {
				return false;
			}
			moveAffectedFigures(getOriginalPoint(), getBackupPoint());
			return true;
		}

		public void setBackupPoint(Point newBackupPoint) {
			myBackupPoint = newBackupPoint;
		}

		public Point getBackupPoint() {
			return myBackupPoint;
		}

		public void setOriginalPoint(Point newOriginalPoint) {
			myOriginalPoint = newOriginalPoint;
		}

		public Point getOriginalPoint() {
			return myOriginalPoint;
		}

		public void moveAffectedFigures(Point startPoint, Point endPoint) {
			FigureEnumeration figures = getAffectedFigures();
			while (figures.hasNextFigure()) {
				figures.nextFigure().moveBy(endPoint.x - startPoint.x,
					endPoint.y - startPoint.y);
			}
		}
	}
}
