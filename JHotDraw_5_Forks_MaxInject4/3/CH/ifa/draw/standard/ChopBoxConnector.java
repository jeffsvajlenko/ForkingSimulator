/*
 * @(#)ChopBoxConnector.java
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
import CH.ifa.draw.util.Geom;

/**
 * A ChopBoxConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 *
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChopBoxConnector extends AbstractConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -1461450322712345462L;

	public ChopBoxConnector() { // only used for Storable implementation
	}

	public ChopBoxConnector(Figure owner) {
		super(owner);
	}

	public Point findStart(ConnectionFigure connection) {
		Figure startFigure = connection.getStartConnector().owner();
		Rectangle r2 = connection.getEndConnector().displayBox();
		Point r2c = null;

		if (connection.pointCount() == 2) {
			r2c = new Point(r2.x + r2.width/2, r2.y + r2.height/2);
		 }
		 else {
			r2c = connection.pointAt(1);
		}

		return chop(startFigure, r2c);
	}

	public Point findEnd(ConnectionFigure connection) {
		Figure endFigure = connection.getEndConnector().owner();
		Rectangle r1 = connection.getStartConnector().displayBox();
		Point r1c = null;

		if (connection.pointCount() == 2) {
			r1c = new Point(r1.x + r1.width/2, r1.y + r1.height/2);
		}
		else {
			r1c = connection.pointAt(connection.pointCount()-2);
		}

		return chop(endFigure, r1c);
	}

	protected Point chop(Figure target, Point from) {
		Rectangle r = target.displayBox();
		return Geom.angleToPoint(r, (Geom.pointToAngle(r, from)));
	}
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        { // EOL Comment
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
            "serverId",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (com.sun.corba.se.spi.activation.ServerAlreadyUninstalledHelper.id (), "ServerAlreadyUninstalled", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
}

