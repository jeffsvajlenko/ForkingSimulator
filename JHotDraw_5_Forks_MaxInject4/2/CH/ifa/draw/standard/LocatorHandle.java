/*
 * @(#)LocatorHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.Point;
import CH.ifa.draw.framework.*;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to
 * a Locator object.
 *
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
public class LocatorHandle extends AbstractHandle {

	private Locator       fLocator;

	/**
	 * Initializes the LocatorHandle with the given Locator.
	 */
	public LocatorHandle(Figure owner, Locator l) {
		super(owner);
		fLocator = l;
	}
	/**
	 * This should be cloned or it gives the receiver the opportunity to alter
	 * our internal behavior.
	 */
	public Locator getLocator() {
		return fLocator;
	}

	/**
	 * Locates the handle on the figure by forwarding the request
	 * to its figure.
	 */
	public Point locate() {
		return fLocator.locate(owner());
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [0];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCodeHelper.id (), "InconsistentTypeCode", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
}
