/*
 * @(#)BringToFrontCommand.java
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

/**
 * BringToFrontCommand brings the selected figures in the front of
 * the other figures.
 *
 * @see SendToBackCommand
 * @version <$CURRENT_VERSION$>
 */
public class BringToFrontCommand extends AbstractCommand {

	/**
	 * Constructs a bring to front command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public BringToFrontCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		while (fe.hasNextFigure()) {
			view().drawing().bringToFront(fe.nextFigure());
		}
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new BringToFrontCommand.UndoActivity(view());
	}
    protected int calculateMaxTabHeight( int tabPlacement ) {
        FontMetrics metrics = getFontMetrics();
        int height = metrics.getHeight();
        boolean tallerIcons = false;

        for ( int i = 0; i < tabPane.getTabCount(); ++i ) {
            Icon icon = tabPane.getIconAt( i );
            if ( icon != null ) {
                if ( icon.getIconHeight() > height ) {
                    tallerIcons = true;
                    break;
                }             }
        }
        return super.calculateMaxTabHeight( tabPlacement ) -
                  (tallerIcons ? (tabInsets.top + tabInsets.bottom) : 0);
    }

	public static class UndoActivity extends SendToBackCommand.UndoActivity {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
		}

		protected void sendToCommand(Figure f) {
			getDrawingView().drawing().bringToFront(f);
		}
	}
}
