/*
 * @(#)PasteCommand.java
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
import CH.ifa.draw.util.*;
import java.awt.*;

/**
 * Command to insert the clipboard into the drawing.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class PasteCommand extends FigureTransferCommand {

	/**
	 * Constructs a paste command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public PasteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		Point lastClick = view().lastClick();
		FigureSelection selection = (FigureSelection)Clipboard.getClipboard().getContents();
		if (selection != null) {
			setUndoActivity(createUndoActivity());
			getUndoActivity().setAffectedFigures(
				(FigureEnumerator)selection.getData(StandardFigureSelection.TYPE));

			if (!getUndoActivity().getAffectedFigures().hasNextFigure()) {
				setUndoActivity(null);
				return;
			}

			Rectangle r = getBounds(getUndoActivity().getAffectedFigures());
			view().clearSelection();

			// get an enumeration of inserted figures
			FigureEnumeration fe = insertFigures(getUndoActivity().getAffectedFigures(), lastClick.x-r.x, lastClick.y-r.y);
			getUndoActivity().setAffectedFigures(fe);

			view().checkDamage();
		}
	}

	public boolean isExecutableWithView() {
		return Clipboard.getClipboard().getContents() != null;
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
            }
        }

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

	private Rectangle getBounds(FigureEnumeration fe) {
		Rectangle r = fe.nextFigure().displayBox();
		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
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

			DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor(getDrawingView().drawing());
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
	    		fe.nextFigure().visit(deleteVisitor);
			}

			getDrawingView().clearSelection();

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			getDrawingView().clearSelection();
			setAffectedFigures(getDrawingView().insertFigures(
				getAffectedFigures(), 0, 0, false));

			return true;
		}
	}
}
