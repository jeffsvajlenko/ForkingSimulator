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
    public int getBlue(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getBlue(pixel,X1);
    }

	public void remove() {
		// do nothing or throw exception
		//throw new UnsupportedOperationException();
	}
}
