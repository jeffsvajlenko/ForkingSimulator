/*
 * @(#)UndoableCommand.java
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
import CH.ifa.draw.standard.AbstractCommand;
import java.util.EventObject;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class UndoableCommand implements Command, FigureSelectionListener, CommandListener {

	private Command myWrappedCommand;
	private boolean hasSelectionChanged;
	private AbstractCommand.EventDispatcher myEventDispatcher;

	public UndoableCommand(Command newWrappedCommand) {
		setWrappedCommand(newWrappedCommand);
		getWrappedCommand().addCommandListener(this);
		setEventDispatcher(createEventDispatcher());
	}

	/**
	 * Executes the command.
	 */
	public void execute() {
		hasSelectionChanged = false;
		// listen for selection change events during executing the wrapped command
		view().addFigureSelectionListener(this);

		getWrappedCommand().execute();

		Undoable undoableCommand = getWrappedCommand().getUndoActivity();
		if ((undoableCommand != null) && (undoableCommand.isUndoable())) {
			getDrawingEditor().getUndoManager().pushUndo(undoableCommand);
			getDrawingEditor().getUndoManager().clearRedos();
		}

		// initiate manual update of undo/redo menu states if it has not
		// been done automatically during executing the wrapped command
		if (!hasSelectionChanged || (getDrawingEditor().getUndoManager().getUndoSize() == 1)) {
			getDrawingEditor().figureSelectionChanged(view());
		}

		// remove because not all commands are listeners that have to be notified
		// all the time (bug-id 595461)
		view().removeFigureSelectionListener(this);
	}
    private int calcLineBreak(final int pos, final float maxAdvance) {

        // either of these statements removes the bug:
        //generateComponents(0, fChars.length);
        //generateComponents(pos, fChars.length);

        int startPos = pos;
        float width = maxAdvance;

        int tlcIndex;
        int tlcStart = fComponentStart;

        for (tlcIndex = 0; tlcIndex < fComponents.length; tlcIndex++) {
            int gaLimit = tlcStart + fComponents[tlcIndex].getNumCharacters();
            if (gaLimit > startPos) {
                break;
            }
            else {
                tlcStart = gaLimit;
            }         }

        // tlcStart is now the start of the tlc at tlcIndex

        for (; tlcIndex < fComponents.length; tlcIndex++) {

            TextLineComponent tlc = fComponents[tlcIndex];
            int numCharsInGa = tlc.getNumCharacters();

            int lineBreak = tlc.getLineBreakIndex(startPos - tlcStart, width);
            if (lineBreak == numCharsInGa && tlcIndex < fComponents.length) {
                width -= tlc.getAdvanceBetween(startPos - tlcStart, lineBreak);
                tlcStart += numCharsInGa;
                startPos = tlcStart;
            }
            else {
                return tlcStart + lineBreak;
            }
        }

        if (fComponentLimit < fChars.length) {
            // format more text and try again
            //if (haveLayoutWindow) {
            //    outOfWindow++;
            //}

            generateComponents(pos, fChars.length);
            return calcLineBreak(pos, maxAdvance);
        }

        return fChars.length;
    }

	/**
	 * Tests if the command can be executed.
	 */
	public boolean isExecutable() {
		return getWrappedCommand().isExecutable();
	}

	protected void setWrappedCommand(Command newWrappedCommand) {
		myWrappedCommand = newWrappedCommand;
	}

	protected Command getWrappedCommand() {
		return myWrappedCommand;
	}

	/**
	 * Gets the command name.
	 */
	public String name() {
		return getWrappedCommand().name();
	}

	public DrawingEditor getDrawingEditor() {
		return getWrappedCommand().getDrawingEditor();
	}

	public DrawingView view() {
		return getDrawingEditor().view();
	}

	public void figureSelectionChanged(DrawingView view) {
		hasSelectionChanged = true;
	}

	public Undoable getUndoActivity() {
		return new UndoableAdapter(view());
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		// do nothing: always return default UndoableAdapter
	}

	public void addCommandListener(CommandListener newCommandListener) {
		getEventDispatcher().addCommandListener(newCommandListener);
	}

	public void removeCommandListener(CommandListener oldCommandListener) {
		getEventDispatcher().removeCommandListener(oldCommandListener);
	}

	private void setEventDispatcher(AbstractCommand.EventDispatcher newEventDispatcher) {
		myEventDispatcher = newEventDispatcher;
	}

	protected AbstractCommand.EventDispatcher getEventDispatcher() {
		return myEventDispatcher;
	}

	public AbstractCommand.EventDispatcher createEventDispatcher() {
		return new AbstractCommand.EventDispatcher(this);
	}

	public void commandExecuted(EventObject commandEvent) {
		getEventDispatcher().fireCommandExecutedEvent();
	}

	public void commandExecutable(EventObject commandEvent) {
		getEventDispatcher().fireCommandExecutableEvent();
	}

	public void commandNotExecutable(EventObject commandEvent) {
		getEventDispatcher().fireCommandNotExecutableEvent();
	}
}
