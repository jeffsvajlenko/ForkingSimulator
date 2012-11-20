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
    private Paint decodeGradient4(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.1f,0.49999997f,0.9f },
                new Color[] { (Color)componentColors[0],
                            decodeColor((Color)componentColors[0],(Color)componentColors[1],0.5f),
                            (Color)componentColors[1]});
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
