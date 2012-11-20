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
    public void setScannerVersion(short version) {

        if(version == Constants.XML_VERSION_1_0) {
            if(fXML10EntityScanner == null) {
                fXML10EntityScanner = new XMLEntityScanner();
            }
            fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML10EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        } else {
            if(fXML11EntityScanner == null) {
                fXML11EntityScanner = new XML11EntityScanner();
            }
            fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
            fEntityScanner = fXML11EntityScanner;
            fEntityScanner.setCurrentEntity(fCurrentEntity);
        }

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
