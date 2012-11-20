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
  public int getLastChild(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int child = _firstch(identity);
    int lastChild = DTM.NULL;

    while (child != DTM.NULL)
    {
      lastChild = child;
      child = _nextsib(child);
    }

    return makeNodeHandle(lastChild)
;
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

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
