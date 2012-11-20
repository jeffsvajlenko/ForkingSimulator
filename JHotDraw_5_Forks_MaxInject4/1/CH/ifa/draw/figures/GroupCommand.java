/*
 * @(#)GroupCommand.java
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
import CH.ifa.draw.util.*;

import java.util.List;

/**
 * Command to group the selection into a GroupFigure.
 *
 * @see GroupFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupCommand extends AbstractCommand {

   /**
	 * Constructs a group command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public GroupCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		((GroupCommand.UndoActivity)getUndoActivity()).groupFigures();
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 1;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new GroupCommand.UndoActivity(view());
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

			// orphan group figure(s)
			getDrawingView().drawing().orphanAll(getAffectedFigures());

			// create a new collection with the grouped figures as elements
			List affectedFigures = CollectionsFactory.current().createList();

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure currentFigure = fe.nextFigure();
				// add contained figures
				getDrawingView().drawing().addAll(currentFigure.figures());
				getDrawingView().addToSelectionAll(currentFigure.figures());

				FigureEnumeration groupedFigures = currentFigure.figures();
				while (groupedFigures.hasNextFigure()) {
					affectedFigures.add(groupedFigures.nextFigure());
				}
			}

			setAffectedFigures(new FigureEnumerator(affectedFigures));

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				groupFigures();
				return true;
			}

			return false;
		}
    private Paint decodeGradient3(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.24143836f,0.48287672f,0.7414384f,1.0f },
                new Color[] { color11,
                            decodeColor(color11,color12,0.5f),
                            color12,
                            decodeColor(color12,color13,0.5f),
                            color13});
    }

		public void groupFigures() {
			getDrawingView().drawing().orphanAll(getAffectedFigures());
			getDrawingView().clearSelection();

			// add new group figure instead
			GroupFigure group = new GroupFigure();
			group.addAll(getAffectedFigures());

			Figure figure = getDrawingView().drawing().add(group);
			getDrawingView().addToSelection(figure);

			// create a new collection with the new group figure as element
			List affectedFigures = CollectionsFactory.current().createList();
			affectedFigures.add(figure);
			setAffectedFigures(new FigureEnumerator(affectedFigures));
		}
	}
}
