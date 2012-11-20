/*
 * @(#)UngroupCommand.java
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
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;

/**
 * Command to ungroup the selected figures.
 *
 * @see GroupCommand
 *
 * @version <$CURRENT_VERSION$>
 */
public  class UngroupCommand extends AbstractCommand {

	/**
	 * Constructs a group command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public UngroupCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		// selection of group figures
		getUndoActivity().setAffectedFigures(view().selection());
		view().clearSelection();

		((UngroupCommand.UndoActivity)getUndoActivity()).ungroupFigures();
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().selection();
		while (fe.hasNextFigure()) {
			Figure currentFigure = fe.nextFigure();
			currentFigure = currentFigure.getDecoratedFigure();

			if (!(currentFigure instanceof GroupFigure)) {
				return false;
			}
		}

		return view().selectionCount() > 0;

	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new UngroupCommand.UndoActivity(view());
	}
    private void getUsernamePassword(boolean getPasswdFromSharedState)
        throws LoginException {

        if (getPasswdFromSharedState) {
            // use the password saved by the first module in the stack
            username = (String)sharedState.get(USERNAME_KEY);
            password = (char[])sharedState.get(PASSWORD_KEY);
            return;
        }

        // prompt for a username and password
        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler available " +
                "to acquire authentication information from the user");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(rb.getString("username."));
        callbacks[1] = new PasswordCallback(rb.getString("password."), false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                                password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());

        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                        " not available to acquire authentication information" +
                        " from the user");
        }
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

			FigureEnumeration groupFigures = getAffectedFigures();
			while (groupFigures.hasNextFigure()) {
				Figure groupFigure = groupFigures.nextFigure();
				// orphan individual figures from the group
				getDrawingView().drawing().orphanAll(groupFigure.figures());

				Figure figure = getDrawingView().drawing().add(groupFigure);
				getDrawingView().addToSelection(figure);
			}

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().drawing().orphanAll(getAffectedFigures());
				getDrawingView().clearSelection();
				ungroupFigures();
				return true;
			}
			return false;
		}

		protected void ungroupFigures() {
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure selected = fe.nextFigure();
				Figure group = getDrawingView().drawing().orphan(selected);

				getDrawingView().drawing().addAll(group.figures());
				getDrawingView().addToSelectionAll(group.figures());
			}
		}
	}
}
