/*
 * @(#)CollectionsFactoryJDK11.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk11;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK11 extends CollectionsFactory {
	public CollectionsFactoryJDK11() {
		// required in order to create instances of this class via reflection
	}
    public void closeFrame(JInternalFrame f) {
        JDesktopPane d = f.getDesktopPane();
        if (d == null) {
            return;
        }
        boolean findNext = f.isSelected();
        Container c = f.getParent();
        JInternalFrame nextFrame = null;
        if (findNext) {
            nextFrame = d.getNextFrame(f);
            try { f.setSelected(false); } catch (PropertyVetoException e2) { }
        }
        if(c != null) {
            c.remove(f); // Removes the focus.
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }
        removeIconFor(f);
        if(f.getNormalBounds() != null)
            f.setNormalBounds(null);
        if(wasIcon(f))
             setWasIcon(f, null);
        if (nextFrame != null) {
            try { nextFrame.setSelected(true); }
            catch (PropertyVetoException e2) { }
        } else if (findNext && d.getComponentCount() == 0) {
            // It was selected and was the last component on the desktop.
            d.requestFocus();
        }
    }

	public List createList() {
		return new ListWrapper();
	}

	public List createList(Collection initList) {
		return new ListWrapper(initList);
	}

	public List createList(int initSize) {
		return new ListWrapper(initSize);
	}

	public Map createMap() {
		return new MapWrapper();
	}

	public Map createMap(Map initMap) {
		return new MapWrapper(initMap);
	}

	public Set createSet() {
		return new SetWrapper();
	}

	public Set createSet(Set initSet) {
		return new SetWrapper(initSet);
	}
}
