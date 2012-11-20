/*
 * @(#)FigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.CollectionsFactory;

import java.util.Iterator;
import java.util.Collection;


/**
 * An Enumeration for a Collection of Figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public final class FigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;
	private Collection myInitialCollection;

	private static FigureEnumerator singletonEmptyEnumerator =
		new FigureEnumerator(CollectionsFactory.current().createList());

	public FigureEnumerator(Collection c) {
		myInitialCollection = c;
		reset();
	}
    public Remote toStub (Remote obj)
        throws NoSuchObjectException
    {
        Remote result = null;
        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }

        // If the class is already an IIOP stub then return it.
        if (StubAdapter.isStub( obj )) {
            return obj;
        }

        // If the class is already a JRMP stub then return it.
        if (obj instanceof java.rmi.server.RemoteStub) {
            return obj;
        }

        // Has it been exported to IIOP?
        Tie theTie = Util.getTie(obj);

        if (theTie != null) {
            result = Utility.loadStub(theTie,null,null,true);
        } else {
            if (Utility.loadTie(obj) == null) {
                result = java.rmi.server.RemoteObject.toStub(obj);
            }
        }

        if (result == null) {
            throw new NoSuchObjectException("object not exported");
        }

        return result;
    }

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}

	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception java.util.NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}

	public static FigureEnumeration getEmptyEnumeration() {
		return singletonEmptyEnumerator;
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myIterator = myInitialCollection.iterator();
	}

/*	public static FigureEnumeration getClonedFigures(FigureEnumeration toDuplicate) {
		List v = CollectionsFactory.current().createList();
		while (toDuplicate.hasMoreElements()) {
			try {
				v.addElement(toDuplicate.nextFigure().clone());
			}
			catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return new FigureEnumerator(v);
	}
*/
}
