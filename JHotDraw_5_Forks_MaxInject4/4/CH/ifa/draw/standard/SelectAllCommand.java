/*
 * @(#)SelectAllCommand.java
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

/**
 * Command to select all figures in a view.
 *
 * @version <$CURRENT_VERSION$>
 */
public class SelectAllCommand extends AbstractCommand {

	/**
	 * Constructs a select all command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SelectAllCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
    	getUndoActivity().setAffectedFigures(view().selection());
		view().addToSelectionAll(view().drawing().figures());
		view().checkDamage();
	}

	/**
	 * Used in enabling the properties menu item.
	 * SelectAll menu item will be enabled only when there ia atleast one figure
	 * in the selected drawing view.
	 */
	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().drawing().figures();
		if (fe.hasNextFigure() && (fe.nextFigure() != null)) {
			return true;
		}

		return false;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new SelectAllCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
	        	return false;
			}

			getDrawingView().clearSelection();
			getDrawingView().addToSelectionAll(getAffectedFigures());

		    return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().addToSelectionAll(getDrawingView().drawing().figures());
				return true;
			}

			return false;
		}
    protected synchronized int available() throws IOException {
        if (isClosedOrPending()) {
            throw new IOException();
        }

        /*
         * If connection has been reset then return 0 to indicate
         * there are no buffered bytes.
         */
        if (isConnectionReset()) {
            return 0;
        }

        /*
         * If no bytes available and we were previously notified
         * of a connection reset then we move to the reset state.
         *
         * If are notified of a connection reset then check
         * again if there are bytes buffered on the socket.
         */
        int n = 0;
        try {
            n = socketAvailable();
            if (n == 0 && isConnectionResetPending()) {
                setConnectionReset();
            }
        } catch (ConnectionResetException exc1) {
            setConnectionResetPending();
            try {
                n = socketAvailable();
                if (n == 0) {
                    setConnectionReset();
                }
            } catch (ConnectionResetException exc2) {
            }
        }
        return n;
    }
	}
}
