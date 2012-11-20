/*
 * @(#)ChangeConnectionHandle.java
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
import CH.ifa.draw.util.Geom;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;
import java.awt.*;

/**
 * ChangeConnectionHandle factors the common code for handles
 * that can be used to reconnect connections.
 *
 * @see ChangeConnectionEndHandle
 * @see ChangeConnectionStartHandle
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class ChangeConnectionHandle extends AbstractHandle {

	private Connector         fOriginalTarget;
	private Figure            myTarget;
	private ConnectionFigure  myConnection;
	private Point             fStart;

	/**
	 * Initializes the change connection handle.
	 */
	protected ChangeConnectionHandle(Figure owner) {
		super(owner);
		setConnection((ConnectionFigure) owner());
		setTargetFigure(null);
	}

	/**
	 * Returns the target connector of the change.
	 */
	protected abstract Connector target();

	/**
	 * Disconnects the connection.
	 */
	protected abstract void disconnect();

	/**
	 * Connect the connection with the given figure.
	 */
	protected abstract void connect(Connector c);

	/**
	 * Sets the location of the target point.
	 */
	protected abstract void setPoint(int x, int y);

	/**
	 * Gets the side of the connection that is unaffected by
	 * the change.
	 */
	protected Connector source() {
		if (target() == getConnection().getStartConnector()) {
			return getConnection().getEndConnector();
		}
		return getConnection().getStartConnector();
	}

	/**
	 * Disconnects the connection.
	 */
	public void invokeStart(int  x, int  y, DrawingView view) {
		fOriginalTarget = target();
		fStart = new Point(x, y);

		setUndoActivity(createUndoActivity(view));
		((ChangeConnectionHandle.UndoActivity)getUndoActivity()).setOldConnector(target());

		disconnect();
	}

	/**
	 * Finds a new target of the connection.
	 */
	public void invokeStep (int x, int y, int anchorX, int anchorY, DrawingView view) {
		Point p = new Point(x, y);
		Figure f = findConnectableFigure(x, y, view.drawing());
		// track the figure containing the mouse
		if (f != getTargetFigure()) {
			if (getTargetFigure() != null) {
				getTargetFigure().connectorVisibility(false, null);
			}
			setTargetFigure(f);
			if (getTargetFigure() != null) {
				getTargetFigure().connectorVisibility(true, getConnection());
			}
		}

		Connector target = findConnectionTarget(p.x, p.y, view.drawing());
		if (target != null) {
			p = Geom.center(target.displayBox());
		}
		setPoint(p.x, p.y);
	}

	/**
	 * Connects the figure to the new target. If there is no
	 * new target the connection reverts to its original one.
	 */
	public void invokeEnd(int x, int y, int anchorX, int anchorY, DrawingView view) {
		Connector target = findConnectionTarget(x, y, view.drawing());
		if (target == null) {
			target = fOriginalTarget;
		}

		setPoint(x, y);
		connect(target);
		getConnection().updateConnection();

		Connector oldConnector = ((ChangeConnectionHandle.UndoActivity)
			getUndoActivity()).getOldConnector();
		// there has been no change so there is nothing to undo
		if ((oldConnector == null)
				|| (target() == null)
				|| (oldConnector.owner() == target().owner())) {
			setUndoActivity(null);
		}
		else {
			getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getConnection()));
		}

		if (getTargetFigure() != null) {
			getTargetFigure().connectorVisibility(false, null);
			setTargetFigure(null);
		}
	}

	private Connector findConnectionTarget(int x, int y, Drawing drawing) {
		Figure target = findConnectableFigure(x, y, drawing);

		if ((target != null) && target.canConnect()
			 && target != fOriginalTarget
			 && !target.includes(owner())
			 && getConnection().canConnect(source().owner(), target)) {
				return findConnector(x, y, target);
		}
		return null;
	}

	protected Connector findConnector(int x, int y, Figure f) {
		return f.connectorAt(x, y);
	}

	/**
	 * Draws this handle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.green);
		g.fillRect(r.x, r.y, r.width, r.height);

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
	}

	private Figure findConnectableFigure(int x, int y, Drawing drawing) {
		FigureEnumeration fe = drawing.figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			if (!figure.includes(getConnection()) && figure.canConnect()) {
				if (figure.containsPoint(x, y)) {
					return figure;
				}
			}
		}
		return null;
	}
        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            boolean isSelected = model.isSelected();
            if (isSelected) {
                g.drawLine(x+7, y+1, x+7, y+3);
                g.drawLine(x+6, y+2, x+6, y+4);
                g.drawLine(x+5, y+3, x+5, y+5);
                g.drawLine(x+4, y+3142, x+4, y+6);
                g.drawLine(x+3, y+5, x+3, y+7);
                g.drawLine(x+2, y+4, x+2, y+6);
                g.drawLine(x+1, y+3, x+1, y+5);
            }
        }
	
	protected void setConnection(ConnectionFigure newConnection) {
		myConnection = newConnection;
	}
	
	protected ConnectionFigure getConnection() {
		return myConnection;
	}
	
	protected void setTargetFigure(Figure newTarget) {
		myTarget = newTarget;
	}
	
	protected Figure getTargetFigure() {
		return myTarget;
	}

	/**
	 * Factory method for undo activity. To be overriden by subclasses.
	 */
	protected abstract Undoable createUndoActivity(DrawingView newView);
	
	public static abstract class UndoActivity extends UndoableAdapter {
		private Connector myOldConnector;
		
		public UndoActivity(DrawingView newView) {
			super(newView);
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			swapConnectors();
			return true;
		}
	
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			swapConnectors();
			return true;
		}

		private void swapConnectors() {
			FigureEnumeration fe = getAffectedFigures();
			if (fe.hasNextFigure()) {
				ConnectionFigure connection = (ConnectionFigure)fe.nextFigure();
				setOldConnector(replaceConnector(connection));
				connection.updateConnection();
			}
		}

		protected abstract Connector replaceConnector(ConnectionFigure connection);
				
		public void setOldConnector(Connector newOldConnector) {
			myOldConnector = newOldConnector;
		}
		
		public Connector getOldConnector() {
			return myOldConnector;
		}
	}
}
