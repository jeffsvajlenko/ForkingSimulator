/*
 * @(#)ReverseListEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * An Enumeration that enumerates a list back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public class ReverseListEnumerator implements Iterator {

	private List myList;
	private int count;

	public ReverseListEnumerator(List l) {
		myList = l;
		count = myList.size() - 1;
	}
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.StringSeqHelper.id (), "StringSeq", __typeCode);
    }
    return __typeCode;
  }

	public boolean hasNext() {
		return count >= 0;
	}

	public Object next() {
		if (count >= 0) {
			return myList.get(count--);
		}
		throw new NoSuchElementException("ReverseListEnumerator");
	}

	public void remove() {
		myList.remove(count);
		count--;
	}
}
