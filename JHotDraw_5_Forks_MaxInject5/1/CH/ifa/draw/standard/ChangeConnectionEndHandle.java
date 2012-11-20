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
    public SerialJavaObject(Object obj) throws SerialException {

        // if any static fields are found, an exception
        // should be thrown


        // get Class. Object instance should always be available
        Class<?> X1 = obj.getClass();

        // determine if object implements Serializable i/f
        if (!(obj instanceof java.io.Serializable)) {
            setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
        }

        // can only determine public fields (obviously). If
        // any of these are static, this should invalidate
        // the action of attempting to persist these fields
        // in a serialized form

        boolean anyStaticFields = false;
        fields = c.getFields();

        for (int i = 0; i < fields.length; i++ ) {
            if ( fields[i].getModifiers() == Modifier.STATIC ) {
                anyStaticFields = true;
            }
        }


        if (anyStaticFields) {
            throw new SerialException("Located static fields in " +
                "object instance. Cannot serialize");
        }

        this.obj = obj;
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
