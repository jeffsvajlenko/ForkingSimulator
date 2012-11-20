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
        private void paintMe(Component c, Graphics g) {

            int right = folderIcon16Size.width - 1;
            int bottom = folderIcon16Size.height - 1;

            // Draw tab top
            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
            g.drawLine( right - 5, 3, right, 3 );
            g.drawLine( right - 6, 4, right, 4 );

            // Draw folder front
            g.setColor( MetalLookAndFeel.getPrimaryControl() );
            g.fillRect( 2, 7, 13, 8 );

            // Draw tab bottom
            g.setColor( MetalLookAndFeel.getPrimaryControlShadow() );
            g.drawLine( right - 6, 5, right - 1, 5 );

            // Draw outline
            g.setColor( MetalLookAndFeel.getPrimaryControlInfo() );
            g.drawLine( 0, 6, 0, bottom );            // left side
            g.drawLine( 1, 5, right - 7, 5 );         // first part of top
            g.drawLine( right - 6, 6, right - 1, 6 ); // second part of top
            g.drawLine( right, 5, right, bottom );    // right side
            g.drawLine( 0, bottom, right, bottom );   // bottom

            // Draw highlight
            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
            g.drawLine( 1, 6, 1, bottom - 1 );
            g.drawLine( 1, 6, right - 7, 6 );
            g.drawLine( right - 6, 7, right - 1, 7 );

        }
	}
}
