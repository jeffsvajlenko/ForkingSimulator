/*
 * @(#)FollowURLTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import javax.swing.JApplet;
import java.awt.event.*;
import java.net.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.AbstractTool;

/**
 * @version <$CURRENT_VERSION$>
 */
class FollowURLTool extends AbstractTool {
	 private JApplet         fApplet;

	 FollowURLTool(DrawingEditor newDrawingEditor, JApplet applet) {
		super(newDrawingEditor);
		fApplet = applet;
	 }
    protected static Rectangle getSourceRegion(ImageReadParam param,
                                               int srcWidth,
                                               int srcHeight) {
        Rectangle sourceRegion = new Rectangle(0, 0, srcWidth, srcHeight);
        if (param != null) {
            Rectangle region = param.getSourceRegion();
            if (region != null) {
                sourceRegion = sourceRegion.intersection(region);
            }

            int subsampleXOffset = param.getSubsamplingXOffset();
            int subsampleYOffset = param.getSubsamplingYOffset();
            sourceRegion.x += subsampleXOffset;
            sourceRegion.y += subsampleYOffset;
            sourceRegion.width -= subsampleXOffset;
            sourceRegion.height -= subsampleYOffset;
        }

        return sourceRegion;
    }

	/**
	 * Handles mouse move events in the drawing view.
	 */
	public void mouseMove(MouseEvent e, int x, int y) {
		String urlstring = null;
		Figure figure = drawing().findFigureInside(x,y);
		if (figure != null) {
			urlstring = (String) figure.getAttribute(FigureAttributeConstant.URL);
		}
		if (urlstring != null) {
			fApplet.showStatus(urlstring);
		}
		else {
			fApplet.showStatus("");
		}
	}

	/**
	 * Handles mouse up in the drawing view
	 * assuming mouseUp came from active drawing.
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
		Figure figure = getActiveDrawing().findFigureInside(x, y);
		if (figure == null) {
			return;
		}
		String urlstring = (String) figure.getAttribute(FigureAttributeConstant.URL);
		if (urlstring == null) {
			return;
		}

		try {
			URL url = new URL(fApplet.getDocumentBase(), urlstring);
			fApplet.getAppletContext().showDocument(url);
		}
		catch (MalformedURLException exception) {
			fApplet.showStatus(exception.toString());
		}
	}
}
