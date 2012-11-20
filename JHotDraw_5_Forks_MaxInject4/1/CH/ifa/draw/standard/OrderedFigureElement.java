/*
 * @(#)OrderFigureElement.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.Figure;

/**
 * @author WMG (INIT Copyright (C) 2000 All rights reserved)
 * @version <$CURRENT_VERSION$>
 */
class OrderedFigureElement implements Comparable {

	//_________________________________________________________VARIABLES

	private Figure  _theFigure;
	private int     _nZ;

	//______________________________________________________CONSTRUCTORS

	public OrderedFigureElement(Figure aFigure, int nZ) {
		_theFigure = aFigure;
		_nZ = nZ;
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
            "serverId",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (com.sun.corba.se.spi.activation.ServerAlreadyInstalledHelper.id (), "ServerAlreadyInstalled", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

	//____________________________________________________PUBLIC METHODS

	public Figure getFigure() {
		return _theFigure;
	}

	public int getZValue() {
		return _nZ;
	}

	public int compareTo(Object o) {
		OrderedFigureElement ofe = (OrderedFigureElement) o;
		if (_nZ == ofe.getZValue()) {
			return 0;
		}

		if (_nZ > ofe.getZValue()) {
			return 1;
		}

		return -1;
	}

	//_______________________________________________________________END

} //end of class OrderedFigureElement
