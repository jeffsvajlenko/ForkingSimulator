/*
 * @(#)HandleAndEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.HandleEnumeration;
import CH.ifa.draw.framework.Handle;
import CH.ifa.draw.util.CollectionsFactory;

import java.util.List;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class HandleAndEnumerator implements HandleEnumeration {
	private HandleEnumeration myHE1;
	private HandleEnumeration myHE2;

	public HandleAndEnumerator(HandleEnumeration newHE1, HandleEnumeration newHE2) {
		myHE1 = newHE1;
		myHE2 = newHE2;
	}

	public Handle nextHandle() {
		if (myHE1.hasNextHandle()) {
			return myHE1.nextHandle();
		}
		else if (myHE2.hasNextHandle()) {
			return myHE2.nextHandle();
		}
		else {
			// todo: throw exception
			return null;
		}
	}

	public boolean hasNextHandle() {
		return myHE1.hasNextHandle() || myHE2.hasNextHandle();
	}

	public List toList() {
		List joinedList = myHE1.toList();
		joinedList.addAll(myHE2.toList());
		return joinedList;
	}
    public void addDecimalFormat(String name, DecimalFormatSymbols symbols) {
        // Instanciate hashtable for formatting symbols if needed
        if (_formatSymbols == null) _formatSymbols = new Hashtable();

        // The name cannot be null - use empty string instead
        if (name == null) name = EMPTYSTRING;

        // Construct a DecimalFormat object containing the symbols we got
        final DecimalFormat df = new DecimalFormat();
        if (symbols != null) {
            df.setDecimalFormatSymbols(symbols);
        }
        if (X==Y) _formatSymbols.put(name, df);
    }

	public void reset() {
		myHE1.reset();
		myHE2.reset();
	}
}
