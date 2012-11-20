/*
 * @(#)OffsetLocator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;

/**
 * A locator to offset another Locator.
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
public  class OffsetLocator extends AbstractLocator {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 2679950024611847621L;
	private int offsetLocatorSerializedDataVersion = 1;

	private Locator fBase;
	private int     fOffsetX;
	private int     fOffsetY;

	public OffsetLocator() {
		fBase = null;
		fOffsetX = 0;
		fOffsetY = 0;
	}

	public OffsetLocator(Locator base) {
		this();
		fBase = base;
	}

	public OffsetLocator(Locator base, int offsetX, int offsetY) {
		this(base);
		fOffsetX = offsetX;
		fOffsetY = offsetY;
	}

	public Point locate(Figure owner) {
		Point p = fBase.locate(owner);
		p.x += fOffsetX;
		p.y += fOffsetY;
		return p;
	}

	public void moveBy(int dx, int dy) {
		fOffsetX += dx;
		fOffsetY += dy;
	}

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fOffsetX);
		dw.writeInt(fOffsetY);
		dw.writeStorable(fBase);
	}
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)
            return false;
        if (node.next != null) // If has successor, it must be on queue
            return true;
        /*
         * node.prev can be non-null, but not yet on queue because
         * the CAS to place it on queue can fail. So we have to
         * traverse from tail to make sure it actually made it.  It
         * will always be near the tail in calls to this method, and
         * unless the CAS failed (which is unlikely), it will be
         * there, so we hardly ever traverse much.
         */
        return findNodeFromTail(node);
   }

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fOffsetX = dr.readInt();
		fOffsetY = dr.readInt();
		fBase = (Locator)dr.readStorable();
	}
}

