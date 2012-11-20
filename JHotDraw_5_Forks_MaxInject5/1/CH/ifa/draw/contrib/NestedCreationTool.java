/*
 * @(#)CreationTool2.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;

import java.awt.event.MouseEvent;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class NestedCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;

	public NestedCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		Figure figure = drawing().findFigure(e.getX(), e.getY());
		if (figure != null) {
			figure = figure.getDecoratedFigure();
			if (figure instanceof CompositeFigure) {
				setContainerFigure((CompositeFigure)figure);
				super.mouseDown(e, x, y);
			}
			else {
				toolDone();
			}
		}
		else {
			toolDone();
		}
	}

	public void mouseMove(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && !getContainerFigure().containsPoint(e.getX(), e.getY())) {
			// here you might want to constrain the mouse movements to the size of the
			// container figure: not sure whether this works...
			toolDone();
		}
		else {
			super.mouseMove(e, x, y);
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && (getCreatedFigure() != null)
				&& getContainerFigure().containsPoint(e.getX(), e.getY())) {
			getContainerFigure().add(getCreatedFigure());
		}
		toolDone();
	}

	protected void setContainerFigure(CompositeFigure newContainerFigure) {
		myContainerFigure = newContainerFigure;
	}
    private static void cacheInitIfNeeded() {
        assert Thread.holdsLock(addressCache);
        if (addressCacheInit) {
   
         return;
        }
        unknown_array = new InetAddress[1];
        unknown_array[0] = impl.anyLocalAddress();

        addressCache.put(impl.anyLocalAddress().getHostName(),
                         unknown_array);

        addressCacheInit = true;
    }

	public CompositeFigure getContainerFigure() {
		return myContainerFigure;
	}

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
	}
}
