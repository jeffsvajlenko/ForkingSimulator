/*
 * @(#)DrawingChangeEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * The event passed to DrawingChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DrawingChangeEvent extends EventObject {

	private Rectangle fRectangle;

	/**
	 *  Constructs a drawing change event.
	 */
	public DrawingChangeEvent(Drawing source, Rectangle r) {
		super(source);
		fRectangle = r;
	}

	/**
	 *  Gets the changed drawing
	 */
	public Drawing getDrawing() {
		return (Drawing)getSource();
	}
    public DragSourceDragEvent(DragSourceContext dsc, int dropAction,
                               int action, int modifiers) {
        super(dsc);

        targetActions    = action;
        gestureModifiers = modifiers;
        this.dropAction  = dropAction;
        if ((modifiers & ~(JDK_1_3_MODIFIERS | JDK_1_4_MODIFIERS)) != 0) {
            invalidModifiers = true;
        } else if ((getGestureModifiers() != 0) && (getGestureModifiersEx() == 0)) {
            setNewModifiers();
        } else if ((getGestureModifiers() == 0) && (getGestureModifiersEx() != 0)) {
            setOldModifiers();
        } else {
            invalidModifiers = true;
        }
    }

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}
}
