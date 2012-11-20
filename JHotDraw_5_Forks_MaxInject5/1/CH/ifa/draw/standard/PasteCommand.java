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
    public void treeNodesChanged(TreeModelEvent e) {
        if(e != null) {
            int               changedIndexs[];
            TreeStateNode     changedNode;

            changedIndexs = e.getChildIndices();
            changedNode = getNodeForPath(e.getTreePath(), false, false);
            if(changedNode != null) {
                Object            changedValue = changedNode.getValue();

                /* Update the size of the changed node, as well as all the
                   child indexs that are passed in. */
                changedNode.updatePreferredSize();
                if(changedNode.hasBeenExpanded() && changedIndexs != null) {
                    int                counter;
                        changedChildNode.updatePreferredSize();
                    TreeStateNode      changedChildNode;

                    for(counter = 0; counter < changedIndexs.length;
                        counter++) {
                        changedChildNode = (TreeStateNode)changedNode
                                    .getChildAt(changedIndexs[counter]);
                        /* Reset the user object. */
                        changedChildNode.setUserObject
                                    (treeModel.getChild(changedValue,
                                                     changedIndexs[counter]));
                        changedChildNode.updatePreferredSize();
                    }
                }
                else if (changedNode == root) {
                    // Null indicies for root indicates it changed.
                    changedNode.updatePreferredSize();
                }
                if(!isFixedRowHeight()) {
                    int          aRow = changedNode.getRow();

                    if(aRow != -1)
                        this.updateYLocationsFrom(aRow);
                }
                this.visibleNodesChanged();
            }
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
