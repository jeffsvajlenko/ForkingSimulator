/*
 * @(#)ChangeConnectionEndHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Undoable;
import java.awt.Point;

/**
 * A handle to reconnect the end point of
 * a connection to another figure.
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChangeConnectionEndHandle extends ChangeConnectionHandle {

	/**
	 * Constructs the connection handle.
	 */
	public ChangeConnectionEndHandle(Figure owner) {
		super(owner);
	}

	/**
	 * Gets the end figure of a connection.
	 */
	protected Connector target() {
		return getConnection().getEndConnector();
	}

	/**
	 * Disconnects the end figure.
	 */
	protected void disconnect() {
		getConnection().disconnectEnd();
	}

	/**
	 * Sets the end of the connection.
	 */
	protected void connect(Connector c) {
		getConnection().connectEnd(c);
	}

	/**
	 * Sets the end point of the connection.
	 */
	protected void setPoint(int x, int y) {
		getConnection().endPoint(x, y);
	}

	/**
	 * Returns the end point of the connection.
	 */
	public Point locate() {
		return getConnection().endPoint();
	}
  public static org.omg.DynamicAny.DynUnion narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.omg.DynamicAny.DynUnion)
      return (org.omg.DynamicAny.DynUnion)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.omg.DynamicAny._DynUnionStub stub = new org.omg.DynamicAny._DynUnionStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity(DrawingView newView) {
		return new ChangeConnectionEndHandle.UndoActivity(newView);
	}

	public static class UndoActivity extends ChangeConnectionHandle.UndoActivity {
		public UndoActivity(DrawingView newView) {
			super(newView);
		}

		protected Connector replaceConnector(ConnectionFigure connection) {
			Connector tempEndConnector = connection.getEndConnector();
			connection.connectEnd(getOldConnector());
			return tempEndConnector;
		}
	}
}
