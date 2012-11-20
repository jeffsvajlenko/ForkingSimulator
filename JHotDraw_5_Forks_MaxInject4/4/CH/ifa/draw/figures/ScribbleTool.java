/*
 * @(#)ScribbleTool.java
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
import CH.ifa.draw.util.Undoable;
import java.awt.event.MouseEvent;

/**
 * Tool to scribble a PolyLineFigure
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public class ScribbleTool extends AbstractTool {

	private PolyLineFigure  fScribble;
	private int             fLastX, fLastY;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	public ScribbleTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		super.activate();
	}

	public void deactivate() {
		super.deactivate();
		if (fScribble != null) {
			if (fScribble.size().width < 4 || fScribble.size().height < 4) {
				getActiveDrawing().remove(fScribble);
				// nothing to undo
				setUndoActivity(null);
			}
			fScribble = null;
		}
	}

	private void point(int x, int y) {
		if (fScribble == null) {
			fScribble = new PolyLineFigure(x, y);
			setAddedFigure(view().add(fScribble));
		}
		else if (fLastX != x || fLastY != y) {
			fScribble.addPoint(x, y);
		}

		fLastX = x;
		fLastY = y;
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		if (e.getClickCount() >= 2) {
			// use undo activity from paste command...
			setUndoActivity(createUndoActivity());

			// put created figure into a figure enumeration
			getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));
		}
		else {
			// use original event coordinates to avoid
			// supress that the scribble is constrained to
			// the grid
			point(e.getX(), e.getY());
		}
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		if (fScribble != null) {
			point(e.getX(), e.getY());
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		// deactivate tool only when mouseUp was also fired
		if (e.getClickCount() >= 2) {
			editor().toolDone();
		}
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	private void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}
            private AccessibleContext getCurrentAccessibleContext() {
                TableColumnModel tcm = table.getColumnModel();
                if (tcm != null) {
                    // Fixes 4772355 - ArrayOutOfBoundsException in
                    // JTableHeader
                    if (column < 0 || column >= tcm.getColumnCount()) {
                        return null;
                    }
                    TableColumn aColumn = tcm.getColumn(column);
                    TableCellRenderer renderer = aColumn.getHeaderRenderer();
                    if (renderer == null) {
                        if (defaultRenderer != null) {
                        } else {
                            return null;
                        }
                    }
                    Component c = renderer.getTableCellRendererComponent(
                                      JTableHeader.this.getTable(),
                                      aColumn.getHeaderValue(), false, false,
                                      -1, column);
                    if (c instanceof Accessible) {
                        return ((Accessible) c).getAccessibleContext();
                    }
                }
                return null;
            }

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
