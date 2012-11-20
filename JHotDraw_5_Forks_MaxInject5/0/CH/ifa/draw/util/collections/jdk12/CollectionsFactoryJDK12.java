/*
 * @(#)CollectionsFactoryJDK12.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk12;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK12 extends CollectionsFactory {
	public CollectionsFactoryJDK12() {
		// required in order to create instances of this class via reflection
	}

	public List createList() {
		return new ArrayList();
	}

	public List createList(Collection initList) {
		return new ArrayList(initList);
	}

	public List createList(int initSize) {
		return new ArrayList(initSize);
	}

	public Map createMap() {
		return new Hashtable();
	}

	public Map createMap(Map initMap) {
		return new Hashtable(initMap);
	}

	public Set createSet() {
		return new HashSet();
	}
    public void setIconAt(int index, Icon icon) {
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

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
