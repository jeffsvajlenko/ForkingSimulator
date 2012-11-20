/*
 * @(#)AbstractLocator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

import java.io.IOException;

/**
 * AbstractLocator provides default implementations for
 * the Locator interface.
 *
 * @see Locator
 * @see Handle
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class AbstractLocator implements Locator, Storable, Cloneable {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -7742023180844048409L;

	protected AbstractLocator() {
	}

	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * Stores the arrow tip to a StorableOutput.
	 */
	public void write(StorableOutput dw) {
	}

	/**
	 * Reads the arrow tip from a StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
	}
    private void switchOverToHash(int numAtts)
    {
        for (int index = 0; index < numAtts; index++)
        {
            String qName = super.getQName(index);
            Integer i = new Integer(index);
            m_indexFromQName.put(qName, i);

            // Add quick look-up to find with uri/local name pair
            String uri = super.getURI(index);
            String local = super.getLocalName(index);
            m_buff.setLength(0);
            m_buff.append('{').append(uri).append('}').append(local); // EOL Comment
            String key = m_buff.toString();
            m_indexFromQName.put(key, i);
        }
    }
}


