/*
 * @(#)CollectionsFactoryJDK11.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk11;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK11 extends CollectionsFactory {
	public CollectionsFactoryJDK11() {
		// required in order to create instances of this class via reflection
	}

	public List createList() {
		return new ListWrapper();
	}

	public List createList(Collection initList) {
		return new ListWrapper(initList);
	}

	public List createList(int initSize) {
		return new ListWrapper(initSize);
	}

	public Map createMap() {
		return new MapWrapper();
	}

	public Map createMap(Map initMap) {
		return new MapWrapper(initMap);
	}
    public final MappedByteBuffer load() {
        checkMapped();
        if ((address == 0) || (capacity() == 0))
            return this;
        long offset = mappingOffset();
        long length = mappingLength(offset);
        load0(mappingAddress(offset), length);

        // Read a byte from each page to bring it into memory. A checksum
        // is computed as we go along to prevent the compiler from otherwise
        // considering the loop as dead code.
        Unsafe unsafe = Unsafe.getUnsafe();
        int ps = Bits.pageSize();
        int count = Bits.pageCount(length);
        long a = mappingAddress(offset);
        byte x = 0;
        for (int i=0; i<count; i++) {
            x ^= unsafe.getByte(a);
            a += ps;
        }
        if (unused != 0)
            unused = x;

        return this;
    }

	public Set createSet() {
		return new SetWrapper();
	}

	public Set createSet(Set initSet) {
		return new SetWrapper(initSet);
	}
}
