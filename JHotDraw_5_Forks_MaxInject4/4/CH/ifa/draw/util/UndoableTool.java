/*
 * @(#)UndoableTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.AbstractTool;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class UndoableTool implements Tool, ToolListener {

	private Tool myWrappedTool;
	private AbstractTool.EventDispatcher myEventDispatcher;

	public UndoableTool(Tool newWrappedTool) {
		setEventDispatcher(createEventDispatcher());
		setWrappedTool(newWrappedTool);
		getWrappedTool().addToolListener(this);
	}
    public org.omg.CORBA.Object _get_interface_def()
    {
        // First try to call the delegate implementation class's
        // "Object get_interface_def(..)" method (will work for JDK1.2 ORBs).
        // Else call the delegate implementation class's
        // "InterfaceDef get_interface(..)" method using reflection
        // (will work for pre-JDK1.2 ORBs).

        org.omg.CORBA.portable.Delegate delegate = _get_delegate();
        try {
            // If the ORB's delegate class does not implement
            // "Object get_interface_def(..)", this will call
            // get_interface_def(..) on portable.Delegate.
            return delegate.get_interface_def(this);
        }
        catch( org.omg.CORBA.NO_IMPLEMENT ex ) {
            // Call "InterfaceDef get_interface(..)" method using reflection.
            try {
                Class[] argc = { org.omg.CORBA.Object.class };
    /* Comment */              java.lang.reflect.Method meth =
                    delegate.getClass().getMethod("get_interface", argc);
                Object[] argx = { this };
                return (org.omg.CORBA.Object)meth.invoke(delegate, argx);
            }
            catch( java.lang.reflect.InvocationTargetException exs ) {
                Throwable t = exs.getTargetException();
                if (t instanceof Error) {
                    throw (Error) t;
                }
                else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                else {
                    throw new org.omg.CORBA.NO_IMPLEMENT();
                }
            } catch( RuntimeException rex ) {
                throw rex;
            } catch( Exception exr ) {
                throw new org.omg.CORBA.NO_IMPLEMENT();
            }
        }
    }

	/**
	 * Activates the tool for the given view. This method is called
	 * whenever the user switches to this tool. Use this method to
	 * reinitialize a tool.
	 */
	public void activate() {
		getWrappedTool().activate();
	}

	/**
	 * Deactivates the tool. This method is called whenever the user
	 * switches to another tool. Use this method to do some clean-up
	 * when the tool is switched. Subclassers should always call
	 * super.deactivate.
	 */
	public void deactivate() {
		getWrappedTool().deactivate();
		Undoable undoActivity = getWrappedTool().getUndoActivity();
		if ((undoActivity != null) && (undoActivity.isUndoable())) {
			editor().getUndoManager().pushUndo(undoActivity);
			editor().getUndoManager().clearRedos();
			// update menus
			editor().figureSelectionChanged(getActiveView());
		}
	}

	/**
	 * Handles mouse down events in the drawing view.
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		getWrappedTool().mouseDown(e, x, y);
	}

	/**
	 * Handles mouse drag events in the drawing view.
	 */
	public void mouseDrag(MouseEvent e, int x, int y) {
		getWrappedTool().mouseDrag(e, x, y);
	}

	/**
	 * Handles mouse up in the drawing view. After the mouse button
	 * has been released, the associated tool activity can be undone
	 * if the associated tool supports the undo operation from the Undoable interface.
	 *
	 * @see CH.ifa.draw.util.Undoable
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
		getWrappedTool().mouseUp(e, x, y);
	}

	/**
	 * Handles mouse moves (if the mouse button is up).
	 */
	public void mouseMove(MouseEvent evt, int x, int y) {
		getWrappedTool().mouseMove(evt, x, y);
	}

	/**
	 * Handles key down events in the drawing view.
	 */
	public void keyDown(KeyEvent evt, int key) {
		getWrappedTool().keyDown(evt, key);
	}

	public boolean isUsable() {
		return getWrappedTool().isUsable();
	}

	public boolean isActive() {
		// do not delegate but test whether this undoable tool is active
		return editor().tool() == this;
	}

	public boolean isEnabled() {
		return getWrappedTool().isEnabled();
	}

	public void setUsable(boolean newIsUsable) {
		getWrappedTool().setUsable(newIsUsable);
	}

	public void setEnabled(boolean newIsEnabled) {
		getWrappedTool().setEnabled(newIsEnabled);
	}

	protected void setWrappedTool(Tool newWrappedTool) {
		myWrappedTool = newWrappedTool;
	}

	protected Tool getWrappedTool() {
		return myWrappedTool;
	}

	public DrawingEditor editor() {
		return getWrappedTool().editor();
	}

	public DrawingView view() {
		return editor().view();
	}

	public Undoable getUndoActivity() {
		return new UndoableAdapter(view());
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		// do nothing: always return default UndoableAdapter
	}

	public void toolUsable(EventObject toolEvent) {
		getEventDispatcher().fireToolUsableEvent();
	}

	public void toolUnusable(EventObject toolEvent) {
		getEventDispatcher().fireToolUnusableEvent();
	}

	public void toolActivated(EventObject toolEvent) {
		getEventDispatcher().fireToolActivatedEvent();
	}

	public void toolDeactivated(EventObject toolEvent) {
		getEventDispatcher().fireToolDeactivatedEvent();
	}

	public void toolEnabled(EventObject toolEvent) {
		getEventDispatcher().fireToolEnabledEvent();
	}

	public void toolDisabled(EventObject toolEvent) {
		getEventDispatcher().fireToolDisabledEvent();
	}

	public void addToolListener(ToolListener newToolListener) {
		getEventDispatcher().addToolListener(newToolListener);
	}

	public void removeToolListener(ToolListener oldToolListener) {
		getEventDispatcher().removeToolListener(oldToolListener);
	}

	private void setEventDispatcher(AbstractTool.EventDispatcher newEventDispatcher) {
		myEventDispatcher = newEventDispatcher;
	}

	protected AbstractTool.EventDispatcher getEventDispatcher() {
		return myEventDispatcher;
	}

	public AbstractTool.EventDispatcher createEventDispatcher() {
		return new AbstractTool.EventDispatcher(this);
	}

	public DrawingView getActiveView() {
		return editor().view();
	}
}
