/*
 * @(#)NullHandle.java
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
import CH.ifa.draw.framework.*;

/**
 * A handle that doesn't change the owned figure. Its only purpose is
 * to show feedback that a figure is selected.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b>NullObject</b><br>
 * NullObject enables to treat handles that don't do
 * anything in the same way as other handles.
 *
 * @version <$CURRENT_VERSION$>
 */
public class NullHandle extends LocatorHandle {

	/**
	 * The handle's locator.
	 */
	protected Locator fLocator;

	public NullHandle(Figure owner, Locator locator) {
		super(owner, locator);
	}

	/**
	 * Draws the NullHandle. NullHandles are drawn as a
	 * red framed rectangle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
	}
    public void processFragment(ByteBuffer byteBuffer, FragmentMessage msg)
    {
        ByteBufferWithInfo bbwi =
            new ByteBufferWithInfo(orb, byteBuffer, msg.getHeaderLength());

        synchronized (fragmentQueue) {
            if (debug)
            {
                // print address of ByteBuffer being queued
                int bbAddress = System.identityHashCode(byteBuffer);
                StringBuffer sb = new StringBuffer(80);
                sb.append("processFragment() - queueing ByteBuffer id (");
                sb.append(bbAddress).append(") to fragment queue.");
                String strMsg = sb.toString();
                dprint(strMsg);
            }
            fragmentQueue.enqueue(bbwi);
            endOfStream = !msg.moreFragmentsToFollow();
            fragmentQueue.notify();
        }
    }
}
