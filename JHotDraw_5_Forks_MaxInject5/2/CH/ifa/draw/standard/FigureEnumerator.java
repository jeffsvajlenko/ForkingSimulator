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
   protected void engineInitVerify(Key publicKey) throws XMLSignatureException {

      if (!(publicKey instanceof PublicKey)) {
         String supplied = publicKey.getClass().getName();
         String needed = PublicKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initVerify((PublicKey) publicKey);
      } catch (InvalidKeyException ex) {
            // reinstantiate Signature object to work around bug in JDK
            // see: http://bugs.sun.com/view_bug.do?bug_id=4953555
            Signature sig = this._signatureAlgorithm;
            try {
                this._signatureAlgorithm = Signature.getInstance
                    (_signatureAlgorithm.getAlgorithm());
            } catch (Exception e) {
                // this shouldn't occur, but if it does, restore previous
                // Signature
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Signature:" + e);
                }
                this._signatureAlgorithm = sig;
            }
            throw new XMLSignatureException("empty", ex);
      }
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
