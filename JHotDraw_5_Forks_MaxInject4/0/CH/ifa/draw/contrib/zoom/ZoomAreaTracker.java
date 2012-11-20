/*
 * @(#)ZoomAreaTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.framework.DrawingEditor;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class ZoomAreaTracker extends AreaTracker {

	public ZoomAreaTracker(DrawingEditor editor) {
		super(editor);
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		Rectangle zoomArea = getArea();
		super.mouseUp(e, x, y);
		if (zoomArea.width > 4 && zoomArea.height > 4)
			((ZoomDrawingView) view()).zoom(zoomArea.x, zoomArea.y,
					zoomArea.width, zoomArea.height);
	}
    public INTERNAL servantDispatch( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVANT_DISPATCH, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "POA.servantDispatch",
                parameters, POASystemException.class, exc ) ;
        }
        
        return exc ;
    }

}


