/*
 * @(#)GroupHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.NullHandle;

/**
 * A Handle for a GroupFigure.
 *
 * @version <$CURRENT_VERSION$>
 */
final class GroupHandle extends NullHandle {

	public GroupHandle(Figure owner, Locator locator) {
		super(owner, locator);
	}
    public byte[] reference_to_id(org.omg.CORBA.Object reference)
        throws WrongAdapter, WrongPolicy
    {
        try {
            lock() ;

            if (debug) {
                ORBUtility.dprint( this, "Calling reference_to_id(reference=" +
                    reference + ") on poa " + this ) ;
            }

            if( state >= STATE_DESTROYING ) {
                throw lifecycleWrapper().adapterDestroyed() ;
            }

            return internalReferenceToId( reference ) ;
        } finally {
            unlock() ;
        }
    }

	/**
	 * Draws the Group handle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
		r.grow(-1, -1);
		g.setColor(Color.white);
		g.drawRect(r.x, r.y, r.width, r.height);
	}
}
