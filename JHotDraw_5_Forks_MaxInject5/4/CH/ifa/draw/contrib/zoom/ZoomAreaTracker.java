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
    private static <T extends GarbageCollectorMXBean>
            List<T> getGcMXBeanList(Class<T> gcMXBeanIntf) {
        List<GarbageCollectorMXBean> list =
            ManagementFactoryHelper.getGarbageCollectorMXBeans();
        List<T> result = new ArrayList<>(list.size());
        for (GarbageCollectorMXBean m : list) {
            if (gcMXBeanIntf.isInstance(m)) {
                result.add(gcMXBeanIntf. cast(m));
            }
        }
        return result;
    }

	public void mouseUp(MouseEvent e, int x, int y) {
		Rectangle zoomArea = getArea();
		super.mouseUp(e, x, y);
		if (zoomArea.width > 4 && zoomArea.height > 4)
			((ZoomDrawingView) view()).zoom(zoomArea.x, zoomArea.y,
					zoomArea.width, zoomArea.height);
	}

}


