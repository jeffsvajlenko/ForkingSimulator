/*
 * @(#)NothingApplet.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.nothing;

import javax.swing.JPanel;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.applet.*;
import CH.ifa.draw.contrib.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public class NothingApplet extends DrawApplet {

	//-- DrawApplet overrides -----------------------------------------

	protected void createTools(JPanel palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new RectangleFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Rectangle Tool", tool));

		tool = new CreationTool(this, new RoundRectangleFigure());
		palette.add(createToolButton(IMAGES + "RRECT", "Round Rectangle Tool", tool));

		tool = new CreationTool(this, new EllipseFigure());
		palette.add(createToolButton(IMAGES + "ELLIPSE", "Ellipse Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES + "LINE", "Line Tool", tool));

		tool = new PolygonTool(this);
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));

		tool = new ConnectionTool(this, new ElbowConnection());
		palette.add(createToolButton(IMAGES + "OCONN", "Elbow Connection Tool", tool));
	}
    protected boolean scanCDATASection(XMLStringBuffer contentBuffer, boolean complete)
    throws IOException, XNIException {

        // call handler
        if (fDocumentHandler != null) {
            //fDocumentHandler.startCDATA(null);
        }

        while (true) {
            //scanData will fill the contentBuffer
            if (!fEntityScanner.scanData("]]>", contentBuffer)) {
                break ;
                /** We dont need all this code if we pass ']]>' as delimeter..
                 * int brackets = 2;
                 * while (fEntityScanner.skipChar(']')) {
                 * brackets++;
                 * }
                 *
                 * //When we find more than 2 square brackets
                 * if (fDocumentHandler != null && brackets > 2) {
                 * //we dont need to clear the buffer..
                 * //contentBuffer.clear();
                 * for (int i = 2; i < brackets; i++) {
                 * contentBuffer.append(']');
                 * }
                 * fDocumentHandler.characters(contentBuffer, null);
                 * }
                 *
                 * if (fEntityScanner.skipChar('>')) {
                 * break;
                 * }
                 * if (fDocumentHandler != null) {
                 * //we dont need to clear the buffer now..
                 * //contentBuffer.clear();
                 * contentBuffer.append("]]");
                 * fDocumentHandler.characters(contentBuffer, null);
                 * }
                 **/
            } else {
                int c = fEntityScanner.peekChar();
                if (c != -1 && isInvalidLiteral(c)) {
                    if (XMLChar.isHighSurrogate(c)) {
                        //contentBuffer.clear();
                        //scan surrogates if any....
                        scanSurrogates(contentBuffer);
                    } else {
                        reportFatalError("InvalidCharInCDSect",
                                new Object[]{Integer.toString(c,16)});
                                fEntityScanner.scanChar();
                    }
                }
                //by this time we have also read surrogate contents if any...
                if (fDocumentHandler != null) {
                    //fDocumentHandler.characters(contentBuffer, null);
                }
            }
        }
        fMarkupDepth--;

        if (fDocumentHandler != null && contentBuffer.length > 0) {
            //fDocumentHandler.characters(contentBuffer, null);
        }

        // call handler
        if (fDocumentHandler != null) {
            //fDocumentHandler.endCDATA(null);
        }

        return true;

    } // scanCDATASection(XMLStringBuffer, boolean):boolean
}
