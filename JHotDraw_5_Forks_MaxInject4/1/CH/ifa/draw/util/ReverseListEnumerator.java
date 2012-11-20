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
    private String[] getFieldStrings(int field, int style, DateFormatSymbols symbols) {
        String[] strings = null;
        switch (field) {
        case ERA:
            strings = symbols.getEras();
            break;

        case MONTH:
            strings = (style == LONG) ? symbols.getMonths() : symbols.getShortMonths();
            break;

        case DAY_OF_WEEK:
            strings = (style == LONG) ? symbols.getWeekdays() : symbols.getShortWeekdays();
            break;

        case AM_PM:
            strings 
= symbols.getAmPmStrings();
            break;
        }
        return strings;
    }
}
