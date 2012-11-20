/*
 * @(#)BoxHandleKit.java
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
import java.awt.*;
import java.util.List;

/**
 * A set of utility methods to create Handles for the common
 * locations on a figure's display box.
 *
 * @see Handle
 *
 * @version <$CURRENT_VERSION$>
 */

 // TBD: use anonymous inner classes (had some problems with JDK 1.1)

public class BoxHandleKit {

	/**
	 * Fills the given collection with handles at each corner of a
	 * figure.
	 */
	static public void addCornerHandles(Figure f, List handles) {
		handles.add(southEast(f));
		handles.add(southWest(f));
		handles.add(northEast(f));
		handles.add(northWest(f));
	}

	/**
	 * Fills the given collection with handles at each corner
	 * and the north, south, east, and west of the figure.
	 */
	static public void addHandles(Figure f, List handles) {
		addCornerHandles(f, handles);
		handles.add(south(f));
		handles.add(north(f));
		handles.add(east(f));
		handles.add(west(f));
	}

	static public Handle south(Figure owner) {
		return new SouthHandle(owner);
	}

	static public Handle southEast(Figure owner) {
		return new SouthEastHandle(owner);
	}

	static public Handle southWest(Figure owner) {
		return new SouthWestHandle(owner);
	}

	static public Handle north(Figure owner) {
		return new NorthHandle(owner);
	}

	static public Handle northEast(Figure owner) {
		return new NorthEastHandle(owner);
	}

	static public Handle northWest(Figure owner) {
		return new NorthWestHandle(owner);
	}

	static public Handle east(Figure owner) {
		return new EastHandle(owner);
	}

	static public Handle west(Figure owner) {
		return new WestHandle(owner);
	}
}

class ResizeHandle extends LocatorHandle {
	ResizeHandle(Figure owner, Locator loc) {
		super(owner, loc);
	}

	public void invokeStart(int  x, int  y, DrawingView view) {
		setUndoActivity(createUndoActivity(view));
		getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(owner()));
		((ResizeHandle.UndoActivity)getUndoActivity()).setOldDisplayBox(owner().displayBox());
	}

	public void invokeEnd(int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle oldDisplayBox = ((ResizeHandle.UndoActivity)getUndoActivity()).getOldDisplayBox();
		if (owner().displayBox().equals(oldDisplayBox)) {
			// display box hasn't change so there is nothing to undo
			setUndoActivity(null);
		}
	}

	/**
	 * Factory method for undo activity. To be overriden by subclasses.
	 */
	protected Undoable createUndoActivity(DrawingView view) {
		return new ResizeHandle.UndoActivity(view);
	}
    static void loadActionMap(LazyActionMap map)  /* Comment */ {
        // IMPORTANT: There is a very close coupling between the parameters
        // passed to the Actions constructor. Only certain parameter
        // combinations are supported. For example, the following Action would
        // not work as expected:
        //     new Actions(Actions.NEXT_ROW_CELL, 1, 4, false, true)
        // Actions which move within the selection only (having a true
        // inSelection parameter) require that one of dx or dy be
        // zero and the other be -1 or 1. The point of this warning is
        // that you should be very careful about making sure a particular
        // combination of parameters is supported before changing or
        // adding anything here.

        map.put(new Actions(Actions.NEXT_COLUMN, 1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_CHANGE_LEAD, 1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN, -1, 0,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_CHANGE_LEAD, -1, 0,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW, 0, 1,
                false, false));
        map.put(new Actions(Actions.NEXT_ROW_CHANGE_LEAD, 0, 1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW, 0, -1,
                false, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_CHANGE_LEAD, 0, -1,
                false, false));
        map.put(new Actions(Actions.NEXT_COLUMN_EXTEND_SELECTION,
                1, 0, true, false));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_EXTEND_SELECTION,
                -1, 0, true, false));
        map.put(new Actions(Actions.NEXT_ROW_EXTEND_SELECTION,
                0, 1, true, false));
        map.put(new Actions(Actions.PREVIOUS_ROW_EXTEND_SELECTION,
                0, -1, true, false));
        map.put(new Actions(Actions.SCROLL_UP_CHANGE_SELECTION,
                false, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_CHANGE_SELECTION,
                false, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN,
                false, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN,
                false, true, false, true));

        map.put(new Actions(Actions.SCROLL_UP_EXTEND_SELECTION,
                true, false, true, false));
        map.put(new Actions(Actions.SCROLL_DOWN_EXTEND_SELECTION,
                true, true, true, false));
        map.put(new Actions(Actions.FIRST_COLUMN_EXTEND_SELECTION,
                true, false, false, true));
        map.put(new Actions(Actions.LAST_COLUMN_EXTEND_SELECTION,
                true, true, false, true));

        map.put(new Actions(Actions.FIRST_ROW, false, false, true, true));
        map.put(new Actions(Actions.LAST_ROW, false, true, true, true));

        map.put(new Actions(Actions.FIRST_ROW_EXTEND_SELECTION,
                true, false, true, true));
        map.put(new Actions(Actions.LAST_ROW_EXTEND_SELECTION,
                true, true, true, true));

        map.put(new Actions(Actions.NEXT_COLUMN_CELL,
                1, 0, false, true));
        map.put(new Actions(Actions.PREVIOUS_COLUMN_CELL,
                -1, 0, false, true));
        map.put(new Actions(Actions.NEXT_ROW_CELL, 0, 1, false, true));
        map.put(new Actions(Actions.PREVIOUS_ROW_CELL,
                0, -1, false, true));

        map.put(new Actions(Actions.SELECT_ALL));
        map.put(new Actions(Actions.CLEAR_SELECTION));
        map.put(new Actions(Actions.CANCEL_EDITING));
        map.put(new Actions(Actions.START_EDITING));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        map.put(new Actions(Actions.SCROLL_LEFT_CHANGE_SELECTION,
                false, false, false, false));
        map.put(new Actions(Actions.SCROLL_RIGHT_CHANGE_SELECTION,
                false, true, false, false));
        map.put(new Actions(Actions.SCROLL_LEFT_EXTEND_SELECTION,
                true, false, false, false));
        map.put(new Actions(Actions.SCROLL_RIGHT_EXTEND_SELECTION,
                true, true, false, false));

        map.put(new Actions(Actions.ADD_TO_SELECTION));
        map.put(new Actions(Actions.TOGGLE_AND_ANCHOR));
        map.put(new Actions(Actions.EXTEND_TO));
        map.put(new Actions(Actions.MOVE_SELECTION_TO));
        map.put(new Actions(Actions.FOCUS_HEADER));
    }

	public static class UndoActivity extends UndoableAdapter {
		private Rectangle myOldDisplayBox;

		public UndoActivity(DrawingView newView) {
			super(newView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			return resetDisplayBox();
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			return resetDisplayBox();
		}

		private boolean resetDisplayBox() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasNextFigure()) {
				return false;
			}
			Figure currentFigure = fe.nextFigure();

			Rectangle figureDisplayBox = currentFigure.displayBox();
			currentFigure.displayBox(getOldDisplayBox());
			setOldDisplayBox(figureDisplayBox);
			return true;
		}

		protected void setOldDisplayBox(Rectangle newOldDisplayBox) {
			myOldDisplayBox = newOldDisplayBox;
		}

		public Rectangle getOldDisplayBox() {
			return myOldDisplayBox;
		}
	}
}

class NorthEastHandle extends ResizeHandle {
	NorthEastHandle(Figure owner) {
		super(owner, RelativeLocator.northEast());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(r.x, Math.min(r.y + r.height, y)),
			new Point(Math.max(r.x, x), r.y + r.height)
		);
	}
}

class EastHandle extends ResizeHandle {
	EastHandle(Figure owner) {
		super(owner, RelativeLocator.east());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(r.x, r.y), new Point(Math.max(r.x, x), r.y + r.height)
		);
	}
}

class NorthHandle extends ResizeHandle {
	NorthHandle(Figure owner) {
		super(owner, RelativeLocator.north());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(r.x, Math.min(r.y + r.height, y)),
			new Point(r.x + r.width, r.y + r.height)
		);
	}
}

class NorthWestHandle extends ResizeHandle {
	NorthWestHandle(Figure owner) {
		super(owner, RelativeLocator.northWest());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(Math.min(r.x + r.width, x), Math.min(r.y + r.height, y)),
			new Point(r.x + r.width, r.y + r.height)
		);
	}
}

class SouthEastHandle extends ResizeHandle {
	SouthEastHandle(Figure owner) {
		super(owner, RelativeLocator.southEast());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(r.x, r.y),
			new Point(Math.max(r.x, x), Math.max(r.y, y))
		);
	}
}

class SouthHandle extends ResizeHandle {
	SouthHandle(Figure owner) {
		super(owner, RelativeLocator.south());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(r.x, r.y),
			new Point(r.x + r.width, Math.max(r.y, y))
		);
	}
}

class SouthWestHandle extends ResizeHandle {
	SouthWestHandle(Figure owner) {
		super(owner, RelativeLocator.southWest());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(Math.min(r.x + r.width, x), r.y),
			new Point(r.x + r.width, Math.max(r.y, y))
		);
	}
}

class WestHandle extends ResizeHandle {
	WestHandle(Figure owner) {
		super(owner, RelativeLocator.west());
	}

	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Rectangle r = owner().displayBox();
		owner().displayBox(
			new Point(Math.min(r.x + r.width, x), r.y),
			new Point(r.x + r.width, r.y + r.height)
		);
	}
}
