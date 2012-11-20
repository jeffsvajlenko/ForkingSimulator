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
    public final FileChannel getChannel() {
        synchronized (this) {
            if (channel == null) {
                channel = FileChannelImpl.open(fd, true, rw, this); 
                /*
                 * FileDescriptor could be shared by FileInputStream or
                 * FileOutputStream.
                 * Ensure that FD is GC'ed only when all the streams/channels
                 * are done using it.
                 * Increment fd's use count. Invoking the channel's close()
                 * method will result in decrementing the use count set for
                 * the channel.
                 */
                fd.incrementAndGetUseCount();
            }
            return channel;
        }
    }

	public void remove() {
		// do nothing or throw exception
		//throw new UnsupportedOperationException();
	}
}
