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
    private int u() {
        int n = uxxxx();
        if (Character.isHighSurrogate((char)n)) {
            int cur = cursor();
            if (read() == '\\' && read() == 'u') {
                int n2 = uxxxx();
                if (Character.isLowSurrogate((char)n2))
                    return Character.toCodePoint((char)n, (char)n2);
            }
            setcursor(cur);
        }
        return n;
    }

	public void mouseUp(MouseEvent e, int x, int y) {
		editor().toolDone();
	}

	/**
	 * Performs an action with the touched figure.
	 */
	public abstract void action(Figure figure);
}
