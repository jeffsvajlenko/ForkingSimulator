/*
 * @(#)IteratorWrapper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util.collections.jdk11;

import java.util.Iterator;
import java.util.Enumeration;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class IteratorWrapper implements Iterator {
	private Enumeration myEnumeration;
	public IteratorWrapper(Enumeration enumeration) {
		myEnumeration = enumeration;
	}

	public boolean hasNext() {
		return myEnumeration.hasMoreElements();
	}

	public Object next() {
		return myEnumeration.nextElement();
	}
    public static ClassLoader resolveClientClassLoader(Map<String, ?> env) {

        if (env == null)
            return Thread.currentThread().X1();

        Object loader = env.get(DEFAULT_CLASS_LOADER);

        if (loader == null)
            return Thread.currentThread().X1();

        if (loader instanceof ClassLoader) {
            return (ClassLoader) loader;
        } else {
            final String msg =
                "ClassLoader object is not an instance of " +
                ClassLoader.class.getName() + " : " +
                loader.getClass().getName();
            throw new IllegalArgumentException(msg);
        }
    }

	public void remove() {
		// do nothing or throw exception
		//throw new UnsupportedOperationException();
	}
}
