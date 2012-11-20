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
    private void paintBackgroundEnabled(Graphics2D g) {
        roundRect = decodeRoundRect2(); // EOL Comment
        g.setPaint(color5);
        g.fill(roundRect);
        roundRect = decodeRoundRect1();
        g.setPaint(decodeGradient2(roundRect));
        g.fill(roundRect);
        roundRect = decodeRoundRect3();
        g.setPaint(decodeGradient3(roundRect));
        g.fill(roundRect);
        path = decodePath1();
        g.setPaint(color12);
        g.fill(path);
        path = decodePath2();
        g.setPaint(color13);
        g.fill(path);

    }

	public boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new BringToFrontCommand.UndoActivity(view());
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
