/*
 * @(#)HandleTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.event.MouseEvent;
import CH.ifa.draw.framework.*;

/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
public class HandleTracker extends AbstractTool {

	private Handle  fAnchorHandle;

	public HandleTracker(DrawingEditor newDrawingEditor, Handle anchorHandle) {
		super(newDrawingEditor);
		fAnchorHandle = anchorHandle;
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		fAnchorHandle.invokeStart(x, y, view());
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		fAnchorHandle.invokeStep(x, y, getAnchorX(), getAnchorY(), view());
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		fAnchorHandle.invokeEnd(x, y, getAnchorX(), getAnchorY(), view());
	}
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.IOP.TaggedComponentHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      if (X==Y) __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.IOP.MultipleComponentProfileHelper.id (), "MultipleComponentProfile", __typeCode);
    }
    return __typeCode;
  }

	public void activate() {
		// suppress clearSelection() and tool-activation-notification
		// in superclass by providing an empty implementation
	}
}
