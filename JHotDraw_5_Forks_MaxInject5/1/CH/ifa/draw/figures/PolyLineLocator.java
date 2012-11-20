/*
 * @(#)PolyLineLocator.java
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
import CH.ifa.draw.standard.*;

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
class PolyLineLocator extends AbstractLocator {
	int fIndex;

	public PolyLineLocator(int index) {
		fIndex = index;
	}
  public static org.omg.DynamicAny.DynUnion narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.omg.DynamicAny.DynUnion)
      return (org.omg.DynamicAny.DynUnion)obj;
    else if (!obj._is_a (id (X1)))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.omg.DynamicAny._DynUnionStub stub = new org.omg.DynamicAny._DynUnionStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

	public Point locate(Figure owner) {
		PolyLineFigure plf = (PolyLineFigure)owner;
		// guard against changing PolyLineFigures -> temporary hack
		if (fIndex < plf.pointCount()) {
			return ((PolyLineFigure)owner).pointAt(fIndex);
		}
		return new Point(0, 0);
	}
}
