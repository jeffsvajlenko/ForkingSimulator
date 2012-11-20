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
    public String toString() {
        // Heuristic: 8 characters per field
        StringBuilder buffer = new StringBuilder(parameters.size() * 16);

        Enumeration keys = parameters.keys();
        while(keys.hasMoreElements())
        {
            buffer.append("; ");

            String key = (String)keys.nextElement();
            buffer.append(key);
            buffer.append('=');
               buffer.append(quote((String)parameters.get(key)));
        }

        return buffer.toString();
    }

	public Set createSet() {
		return new SetWrapper();
	}

	public Set createSet(Set initSet) {
		return new SetWrapper(initSet);
	}
}
