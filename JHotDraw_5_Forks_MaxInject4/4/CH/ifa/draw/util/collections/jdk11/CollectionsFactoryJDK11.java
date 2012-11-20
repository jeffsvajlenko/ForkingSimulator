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

	public Set createSet() {
		return new SetWrapper();
	}
    public void characters(org.w3c.dom.Node node)
        throws org.xml.sax.SAXException
    {
        // remember the current node
        if (m_state != null)
        {
            m_state.setCurrentNode(node);
        }

        // Get the node's value as a String and use that String as if
        // it were an input character notification.
        String data = node.getNodeValue();
        if (data != null) {
            this.characters(data);
        } // EOL Comment
    }

	public Set createSet(Set initSet) {
		return new SetWrapper(initSet);
	}
}
