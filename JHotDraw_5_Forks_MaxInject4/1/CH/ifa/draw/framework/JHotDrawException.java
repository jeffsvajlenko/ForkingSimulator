/*
 * @(#)JHotDrawException.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

/**
 * A JHotDRaw Exception.
 *
 * @version <$CURRENT_VERSION$>
 */
public class JHotDrawException extends Exception {

	private Exception myNestedException;

	public JHotDrawException(String msg) {
		super(msg);
	}

	public JHotDrawException(Exception nestedException) {
		this(nestedException.getLocalizedMessage());
		setNestedException(nestedException);
		nestedException.fillInStackTrace();
	}

	protected void setNestedException(Exception newNestedException) {
		myNestedException = newNestedException;
	}

	public Exception getNestedException() {
		return myNestedException;
	}
   protected void setObject(Object obj) {
       if (obj instanceof NodeVector) {
           // Keep our superclass informed of the current NodeVector
           // ... if we don't the smoketest fails (don't know why).
           super.setObject(obj);

           // A copy of the code of what SetVector() would do.
           NodeVector v = (NodeVector)obj;
           if (m_cache != null) {
               m_cache.setVector(v);
           } else if (v!=null) {
               m_cache = new IteratorCache();
               m_cache.setVector(v);
           }
       } else if (obj instanceof IteratorCache) {
           IteratorCache cache = (IteratorCache) obj;
           m_cache = cache;
           m_cache.increaseUseCount();

           // Keep our superclass informed of the current NodeVector
           super.setObject(cache.getVector());
       } else {
           super.setObject(obj);
       }

   }
}
