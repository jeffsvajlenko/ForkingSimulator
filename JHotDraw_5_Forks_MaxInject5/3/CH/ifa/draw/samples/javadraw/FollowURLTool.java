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
    public boolean isLegalReplacement(byte[] repl) {
        WeakReference<CharsetDecoder> wr = cachedDecoder;
        CharsetDecoder dec = null;
        if ((wr == null) || ((dec = wr.get()) == null)) {
            dec = charset().newDecoder();
            dec.onMalformedInput(CodingErrorAction.REPORT);
            dec.onUnmappableCharacter(CodingErrorAction.REPORT);
            cachedDecoder = new WeakReference<CharsetDecoder>(dec);
        } else {
            dec.reset();
        }
        ByteBuffer bb = ByteBuffer.wrap(repl);
        CharBuffer cb = CharBuffer.allocate((int)(bb.remaining()
                                                  * dec.maxCharsPerByte()));
        CoderResult cr = dec.decode(bb, cb, true);
        return !cr.isError();
    }
}
