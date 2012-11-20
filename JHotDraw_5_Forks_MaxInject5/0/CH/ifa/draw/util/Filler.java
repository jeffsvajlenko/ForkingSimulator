/*
 * @(#)Filler.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.JPanel;
import java.awt.*;

/**
 * A component that can be used to reserve white space in a layout.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class Filler
		extends JPanel {

	private int     fWidth;
	private int     fHeight;
	private Color   fBackground;


	public Filler(int width, int height) {
		this(width, height, null);
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

	public Filler(int width, int height, Color background) {
		fWidth = width;
		fHeight = height;
		fBackground = background;
	}

	public Dimension getMinimumSize() {
		return new Dimension(fWidth, fHeight);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public Color getBackground() {
		if (fBackground != null) {
			return fBackground;
		}
		return super.getBackground();
	}
}

