/*
 * @(#)FigureChangeAdapter.java
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

/**
 * Empty implementation of FigureChangeListener.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeAdapter implements FigureChangeListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void figureInvalidated(FigureChangeEvent e) {}

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureChangeEvent e) {}

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureChangeEvent e) {}

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureChangeEvent e) {}
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
                java.lang.reflect.Method meth =
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
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureChangeEvent e) {}

}
