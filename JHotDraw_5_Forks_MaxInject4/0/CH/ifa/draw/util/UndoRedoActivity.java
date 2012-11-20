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

package CH.ifa.draw.util;

import CH.ifa.draw.framework.*;

/**
 * An UndoRedoActivity can be used to turn a UndoActivity into a RedoActivity.
 * In this case, the redo() method of an encapsulated activity is called when
 * the undo() is executed, and undo() when redo() is executed.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class UndoRedoActivity implements Undoable {
	private Undoable myReversedActivity;
	
	protected UndoRedoActivity(Undoable newReversedActivity) {
		setReversedActivity(newReversedActivity);
	}

	/**
	 * Undo the activity
	 * @return true if the activity could be undone, false otherwise
	 */
	public boolean undo() {
		if (isRedoable()) {
			return getReversedActivity().redo();
		}
		
		return false;
	}

	/*
	 * Redo the activity
	 * @return true if the activity could be redone, false otherwise
	 */
	public boolean redo() {
		if (isUndoable()) {
			return getReversedActivity().undo();
		}
		
		return false;
	}

	/**
	 * Dispatch to isRedoable of the activity to be reversed.
	 */
	public boolean isUndoable() {
		return getReversedActivity().isRedoable();
	}
    private Paint decodeGradient6(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.12299465f,0.44652405f,0.5441176f,0.64171124f,0.81283426f,0.98395723f },
                new Color[] { X1,
                            decodeColor(X1,color19,0.5f),
                            color19,
                            decodeColor(color19,color20,0.5f),
                            color20,
                            decodeColor(color20,color21,0.5f),
                            color21});
    }

	/**
	 * Dispatch to setRedoable of the activity to be reversed.
	 */
	public void setUndoable(boolean newIsUndoable) {
		getReversedActivity().setRedoable(newIsUndoable);
	}

	/**
	 * Dispatch to isUndoable of the activity to be reversed.
	 */
	public boolean isRedoable() {
		return getReversedActivity().isUndoable();
	}
	
	/**
	 * Dispatch to setUndoable of the activity to be reversed.
	 */
	public void setRedoable(boolean newIsRedoable) {
		getReversedActivity().setUndoable(newIsRedoable);
	}
	
	public void setAffectedFigures(FigureEnumeration newAffectedFigures) {
		getReversedActivity().setAffectedFigures(newAffectedFigures);
	}

	public FigureEnumeration getAffectedFigures() {
		return getReversedActivity().getAffectedFigures();
	}
	
	public int getAffectedFiguresCount() {
		return getReversedActivity().getAffectedFiguresCount();
	}

	public DrawingView getDrawingView() {
		return getReversedActivity().getDrawingView();
	}
	
	public void release() {
		getReversedActivity().release();
	}
			
	protected void setReversedActivity(Undoable newReversedActivity) {
		myReversedActivity = newReversedActivity;
	}
	
	public Undoable getReversedActivity() {
		return myReversedActivity;
	}
	
	public static Undoable createUndoRedoActivity(Undoable toBeReversed) {
		// instead of reversing the reversed activity just return the original activity
		if (toBeReversed instanceof UndoRedoActivity) {
			return ((UndoRedoActivity)toBeReversed).getReversedActivity();
		}
		else {
			return new UndoRedoActivity(toBeReversed);
		}
	}
}
