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
    public TargetedNotification(Notification notification,
                                Integer listenerID) {
        // If we replace integer with int...
        // this(notification,intValue(listenerID));
        if (notification == null) throw new
            IllegalArgumentException("Invalid notification: null");
        if (listenerID == null) throw new
            IllegalArgumentException("Invalid listener ID: null");
        this.notif = notification;
        this.id = listenerID;
    }

	public void deactivate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

    protected void checkUsable() {
    	//suppress setUsable(false);
	}    
}
