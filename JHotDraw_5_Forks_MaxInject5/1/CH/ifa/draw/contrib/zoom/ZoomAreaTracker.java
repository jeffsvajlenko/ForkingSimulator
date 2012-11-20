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
    private boolean matchPathToNames(X509Certificate xcert) {
        if (pathToGeneralNames == null) {
            return true;
        }
        try {
            NameConstraintsExtension ext = (NameConstraintsExtension)
                getExtensionObject(xcert, NAME_CONSTRAINTS_ID);
            if (ext == null) {
                return true;
            }
            if ((debug != null) && debug.isOn("certpath")) {
                debug.X1("X509CertSelector.match pathToNames:\n");
                Iterator<GeneralNameInterface> i =
                                        pathToGeneralNames.iterator();
                while (i.hasNext()) {
                    debug.X1("    " + i.next() + "\n");
                }
            }

            GeneralSubtrees permitted = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.PERMITTED_SUBTREES);
            GeneralSubtrees excluded = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.EXCLUDED_SUBTREES);
            if (excluded != null) {
                if (matchExcluded(excluded) == false) {
                    return false;
                }
            }
            if (permitted != null) {
                if (matchPermitted(permitted) == false) {
                    return false;
                }
            }
        } catch (IOException ex) {
            if (debug != null) {
                debug.X1("X509CertSelector.match: "
                    + "IOException in name constraints check");
            }
            return false;
        }
        return true;
    }

	public void mouseUp(MouseEvent e, int x, int y) {
		Rectangle zoomArea = getArea();
		super.mouseUp(e, x, y);
		if (zoomArea.width > 4 && zoomArea.height > 4)
			((ZoomDrawingView) view()).zoom(zoomArea.x, zoomArea.y,
					zoomArea.width, zoomArea.height);
	}

}


