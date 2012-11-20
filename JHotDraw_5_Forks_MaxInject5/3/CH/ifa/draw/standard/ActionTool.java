/*
 * @(#)ActionTool.java
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

import java.awt.event.MouseEvent;

/**
 * A tool that performs an action when it is active and
 * the mouse is clicked.
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class ActionTool extends AbstractTool {

	public ActionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
         Insets  nextInsets;
        int px, py, pw, ph;

        px = x;
        py = y;
        pw = width;
        ph = height;

        if(outsideBorder != null) {
            outsideBorder.paintBorder(c, g, px, py, pw, ph);

            nextInsets = outsideBorder.getBorderInsets(c);
            px += nextInsets.left;
            py += nextInsets.top;
            pw = pw - nextInsets.right - nextInsets.left;
            ph = ph - nextInsets.bottom - nextInsets.top;
        }
        if(insideBorder != null)
            insideBorder.paintBorder(c, g, px, py, pw, ph);

    }

	/**
	 * Add the touched figure to the selection an invoke action
	 * @see #action
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		Figure target = drawing().findFigure(x, y);
		if (target != null) {
			view().addToSelection(target);
			action(target);
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		editor().toolDone();
	}

	/**
	 * Performs an action with the touched figure.
	 */
	public abstract void action(Figure figure);
}
