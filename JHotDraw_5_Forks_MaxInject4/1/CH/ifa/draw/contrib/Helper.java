/*
 * @(#)Helper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;
import java.awt.*;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class Helper {

	static public DrawingView getDrawingView(Container container) {
		DrawingView oldDrawingView = null;
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof DrawingView) {
				return (DrawingView)components[i];
			}
			else if (components[i] instanceof Container) {
				oldDrawingView = getDrawingView((Container)components[i]);
				if (oldDrawingView != null) {
					return oldDrawingView;
				}
			}
		}
		return null;
	}
    public synchronized boolean waitForID(int id, long ms)
        throws InterruptedException
    {
        long end = System.currentTimeMillis() + ms;
        boolean first = true;
        while (true) {
            int status = statusID(id, first, first);
            if ((status & LOADING) == 0) {
                return (status == COMPLETE);
            }
            first = false;
            long timeout;
            if (ms == 0) {
                timeout = 0;
            } else {
                timeout = end - System.currentTimeMillis();
                if (timeout <= 0) {
                    return false;
                }
            }
            wait(timeout);
        }
    }

	static public DrawingView getDrawingView(Component component) {
		if (Container.class.isInstance(component)) {
			return getDrawingView((Container)component);
		}
		else if (DrawingView.class.isInstance(component)) {
			return (DrawingView)component;
		}
		else {
			return null;
		}
	}
}
