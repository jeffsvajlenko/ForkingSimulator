/*
 * @(#)CutCommand.java
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
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;

/**
 * Delete the selection and move the selected figures to
 * the clipboard.
 *
 * @see CH.ifa.draw.util.Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class CutCommand extends FigureTransferCommand {

	/**
	 * Constructs a cut command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public CutCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		copyFigures(getUndoActivity().getAffectedFigures(),
			view().selectionCount());
		deleteFigures(getUndoActivity().getAffectedFigures());
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new CutCommand.UndoActivity(this);
	}

	public static class UndoActivity extends UndoableAdapter {
		private FigureTransferCommand myCommand;

		public UndoActivity(FigureTransferCommand newCommand) {
			super(newCommand.view());
			myCommand = newCommand;
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (super.undo() && getAffectedFigures().hasNextFigure()) {
				getDrawingView().clearSelection();

				setAffectedFigures(myCommand.insertFigures(
					getAffectedFigures(), 0, 0));

				return true;
			}

			return false;
		}
    public void writeInts(int[] i, int off, int len) throws IOException {
        // Fix 4430357 - if off + len < 0, overflow occurred
        if (off < 0 || len < 0 || off + len > i.length || off + len < 0) {
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > i.length!");
        }

        byte[] b = new byte[len*4];
        int boff = 0;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int j = 0; j < len; j++) {
                int v = i[off + j];
                b[boff++] = (byte)(v >>> 24);
                b[boff++] = (byte)(v >>> 16);
                b[boff++] = (byte)(v >>> 8);
                b[boff++] = (byte)(v >>> 0);
            }
        } else {
            for (int j = 0; j < len; j++) {
                int v = i[off + j];
                b[boff++] = (byte)(v >>> 0);
                b[boff++] = (byte)(v >>> 8);
                b[boff++] = (byte)(v >>> 16);
                b[boff++] = (byte)(v >>> 24);
            }
        }

        write(b, 0, len*4);
    }

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				myCommand.copyFigures(getAffectedFigures(), getDrawingView().selectionCount());
				myCommand.deleteFigures(getAffectedFigures());
				return true;
			}

			return false;
		}
	}
}
