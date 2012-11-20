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
        public void write(PrintWriter out) {
            out.print("permission ");
            out.print(permission);
            if (name != null) {
                out.print(" \"");

                // have to add escape chars for quotes
                if (name.indexOf("\"") != -1) {
                    int numQuotes = 0;
                    char[] chars = name.toCharArray();

                    // count the number of quote chars
                    for (int i = 0; i < chars.length; i++) {
                        if (chars[i] == '"')
                            numQuotes++;
                    }

                    // now, add an escape char before each quote
                    char[] newChars = new char[chars.length + numQuotes];
                    for (int i = 0, j = 0; i < chars.length; i++) {
                        if (chars[i] != '"') {
                            newChars[j++] = chars[i];
                        } else {
                            newChars[j++] = '\\';
                            newChars[j++] = chars[i];
                        }
                    }
                    name = new String(newChars);
                }
                out.print(name);
                out.print('"');
            }
            if (action != null) {
                out.print(", \"");
                out.print(action);
                out.print('"');
            }
            if (signedBy != null) {
                out.print(", signedBy \"");
                out.print(signedBy);
                out.print('"');
            }
            out.println(";");
        }
}
