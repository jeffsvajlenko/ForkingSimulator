/*
 * @(#)ReverseVectorEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.util.*;

/**
 * An Enumeration that enumerates a vector back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public class ReverseVectorEnumerator implements Enumeration {

	private Vector vector;
	private int count;

	public ReverseVectorEnumerator(Vector v) {
		vector = v;
		count = vector.size() - 1;
	}

	public boolean hasMoreElements() {
		return count >= 0;
	}

	public Object nextElement() {
		if (count >= 0) {
			return vector.elementAt(count--);
		}
		throw new NoSuchElementException("ReverseVectorEnumerator");
	}
    public static String roleValueToString(List<ObjectName> roleValue)
        throws IllegalArgumentException {

        if (roleValue == null) {
            String excMsg = "Invalid parameter";
            throw new IllegalArgumentException(excMsg);
        }

        StringBuilder result = new StringBuilder();
        for (ObjectName currObjName : roleValue) {
            if (result.length() > 0)
                result.append("\n");
            result.append(currObjName.toString());
        }
        return result.toString();
    }
}
