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
    private static String makeFieldValue(Object value) {
        if (value == null)
            return "(null)";

        Class<?> valueClass = value.getClass();
        try {
            valueClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            final String msg =
                "Class " + valueClass + " does not have a public " +
                "constructor with a single string arg";
            final RuntimeException iae = new IllegalArgumentException(msg);
            throw new RuntimeOperationsException(iae,
                                                 "Cannot make XML descriptor");
        } catch (SecurityException e) {
            // OK: we'll pretend the constructor is there
            // too bad if it's not: we'll find out when we try to
            // reconstruct the DescriptorSupport
        }

        final String quotedValueString = quote(value.toString());

        return "(" + valueClass.getName() + "/" + quotedValueString + ")";
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
