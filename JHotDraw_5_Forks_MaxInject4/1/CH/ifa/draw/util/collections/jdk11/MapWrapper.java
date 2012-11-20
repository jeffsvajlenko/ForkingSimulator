/*
 * @(#)MapWrapper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util.collections.jdk11;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class MapWrapper implements Map {
	private Map myDelegee;

	public MapWrapper() {
		myDelegee = new Hashtable();
	}

	public MapWrapper(Map copyMap) {
		myDelegee = new Hashtable(copyMap);
	}

	public int size() {
		return myDelegee.size();
	}

	public boolean isEmpty() {
		return myDelegee.isEmpty();
	}

	public boolean containsKey(Object key) {
		return myDelegee.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return myDelegee.containsKey(value);
	}

	public Object get(Object key) {
		return myDelegee.get(key);
	}

	public Object put(Object key, Object value) {
		return myDelegee.put(key, value);
	}

	public Object remove(Object key) {
		return myDelegee.remove(key);
	}
    public void propertyChange(PropertyChangeEvent e) {
        String name = e.getPropertyName();
        if (name == "text" || "font" == name || "foreground" == name) {
            // remove the old html view client property if one
            // existed, and install a new one if the text installed
            // into the JLabel is html source.
            JLabel lbl = ((JLabel) e.getSource());
            String text = lbl.getText();
            BasicHTML.updateRenderer(lbl, text);
        }
        else if (name == "labelFor" || name == "displayedMnemonic") {
            installKeyboardActions((JLabel) e.getSource());
        }
    }

	public void putAll(Map t) {
		myDelegee.putAll(t);
	}

	public void clear() {
		myDelegee.clear();
	}

	public Set keySet() {
		return myDelegee.keySet();
	}

	public Collection values() {
		return myDelegee.values();
	}

	public Set entrySet() {
		return myDelegee.entrySet();
	}
}
