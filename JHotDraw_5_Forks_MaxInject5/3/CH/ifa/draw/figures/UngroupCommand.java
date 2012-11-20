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
    public QName getXMLSchemaType() {

        boolean yearSet = isSet(DatatypeConstants.YEARS);
        boolean monthSet = isSet(DatatypeConstants.MONTHS);
        boolean daySet = isSet(DatatypeConstants.DAYS);
        boolean hourSet = isSet(DatatypeConstants.HOURS);
        boolean minuteSet = isSet(DatatypeConstants.MINUTES);
        boolean secondSet = isSet(DatatypeConstants.SECONDS);

        // DURATION
        if (yearSet
            && monthSet
            && daySet
            && hourSet
            && minuteSet
            && secondSet) {
            return DatatypeConstants.DURATION;
        }

        // DURATION_DAYTIME
        if (!yearSet
            && !monthSet
            && daySet
            && hourSet
            && minuteSet
            && secondSet) {
            return DatatypeConstants.DURATION_DAYTIME;
        }

        // DURATION_YEARMONTH
        if (yearSet
            && monthSet
            && !daySet
            && !hourSet
            && !minuteSet
            && !secondSet) {
            return DatatypeConstants.DURATION_YEARMONTH;
        }

        // nothing matches
        throw new IllegalStateException(
                "javax.xml.datatype.Duration#getXMLSchemaType():"
                + " this Duration does not match one of the XML Schema date/time datatypes:"
                + " year set = " + yearSet
                + " month set = " +  monthSet
                + " day set = " + daySet
                + " hour set = " + hourSet
                + " minute set = " + minuteSet
                + " second set = " + secondSet
        );
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
