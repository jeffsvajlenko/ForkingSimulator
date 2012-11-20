/*
 * @(#)DuplicateCommand.java
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

/**
 * Duplicate the selection and select the duplicates.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DuplicateCommand extends FigureTransferCommand {

   /**
	* Constructs a duplicate command.
	* @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	*/
	public DuplicateCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		FigureSelection selection = view().getFigureSelection();

		// create duplicate figure(s)
		FigureEnumeration figures = (FigureEnumeration)selection.getData(StandardFigureSelection.TYPE);
		getUndoActivity().setAffectedFigures(figures);

		view().clearSelection();
		getUndoActivity().setAffectedFigures(
			insertFigures(getUndoActivity().getAffectedFigures(), 10, 10));
		view().checkDamage();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
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

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
