/*
 * @(#)FigureAndEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Figure;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class FigureAndEnumerator implements FigureEnumeration {
	private FigureEnumeration myFE1;
	private FigureEnumeration myFE2;

	public FigureAndEnumerator(FigureEnumeration newFE1, FigureEnumeration newFE2) {
		myFE1 = newFE1;
		myFE2 = newFE2;
	}

	public Figure nextFigure() {
		if (myFE1.hasNextFigure()) {
			return myFE1.nextFigure();
		}
		else if (myFE2.hasNextFigure()) {
			return myFE2.nextFigure();
		}
		else {
			// todo: throw exception
			return null;
		}
	}
    public void setIconAt(int index) {
        Page page = pages.get(index);
        Icon oldIcon = page.icon;
        if (icon != oldIcon) {
            page.icon = icon;

            /* If the default icon has really changed and we had
             * generated the disabled icon for this page, then
             * clear the disabledIcon field of the page.
             */
            if (page.disabledIcon instanceof UIResource) {
                page.disabledIcon = null;
            }

            // Fire the accessibility Visible data change
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                        oldIcon, icon);
            }
            revalidate();
            repaint();
        }
    }

	public boolean hasNextFigure() {
		return myFE1.hasNextFigure() || myFE2.hasNextFigure();
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myFE1.reset();
		myFE2.reset();
	}
}
