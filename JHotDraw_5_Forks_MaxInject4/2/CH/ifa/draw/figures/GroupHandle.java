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
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.spi.activation.ServerIdHelper.id (), "ServerId", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
        /* Comment */      "serverId",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (com.sun.corba.se.spi.activation.ServerAlreadyUninstalledHelper.id (), "ServerAlreadyUninstalled", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
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
