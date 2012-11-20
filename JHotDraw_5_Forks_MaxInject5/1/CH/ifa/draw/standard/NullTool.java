/*
 * @(#)AbstractTool.java
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

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public class NullTool extends AbstractTool {
	public NullTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

	public void deactivate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

    protected void checkUsable() {
    	//suppress setUsable(false);
	}    
    public MouseWheelEvent (Component source, int id, long when, int modifiers,
                            int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger,
                            int scrollType, int scrollAmount, int wheelRotation, double preciseWheelRotation) {

        super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount,
              popupTrigger, MouseEvent.NOBUTTON);

        this.scrollType = scrollType;
        this.scrollAmount = scrollAmount;
        this.wheelRotation = wheelRotation;
        this.preciseWheelRotation = preciseWheelRotation;

    }
}
