/*
 * @(#)PertFigureCreationTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A more efficient version of the generic Pert creation
 * tool that is not based on cloning.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class PertFigureCreationTool extends CreationTool {

	public PertFigureCreationTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Creates a new PertFigure.
	 */
	protected Figure createFigure() {
		return new PertFigure();
	}
    private void paintBackgroundSelectedAndFocused(Graphics2D g) {
        rect = decodeRect6();
        g.setPaint(color14);
        g.fill(rect);
        rect = decodeRect2();
        g.setPaint(decodeGradient5(rect));
        g.fill(rect);
        rect = decodeRect3();
        g.setPaint(decodeGradient4(rect));
        g.fill(rect);
        rect = decodeRect4();
        g.setPaint(color12);
        g.fill(rect);
        rect = decodeRect5();
        g.setPaint(color13);
        g.fill(rect);

    }
}
