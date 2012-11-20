/*
 * @(#)URLTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.FloatingTextField;
import java.awt.*;
import java.awt.event.*;

/**
 * A tool to attach URLs to figures.
 * The URLs are stored in the figure's "URL" attribute.
 * The URL text is entered with a FloatingTextField.
 *
 * @see CH.ifa.draw.util.FloatingTextField
 *
 * @version <$CURRENT_VERSION$>
 */
public  class URLTool extends AbstractTool {

	private FloatingTextField   fTextField;
	private Figure              fURLTarget;

	public URLTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void mouseDown(MouseEvent e, int x, int y)
	{
		super.mouseDown(e,x,y);
		Figure pressedFigure = drawing().findFigureInside(x, y);
		if (pressedFigure != null) {
			beginEdit(pressedFigure);
			return;
		}
		endEdit();
		editor().toolDone();
	}

	public void mouseUp(MouseEvent e, int x, int y) {
	}

	public void deactivate(DrawingView view) {
		super.deactivate();
		endEdit();
	}

	private void beginEdit(Figure figure) {
		if (fTextField == null) {
			fTextField = new FloatingTextField();
			fTextField.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						endEdit();
					}
				}
			);
		}

		if ((figure != fURLTarget) && (fURLTarget != null)) {
			endEdit();
		}
		if (figure != fURLTarget) {
			fTextField.createOverlay((Container)view());
			fTextField.setBounds(fieldBounds(figure), getURL(figure));
			fURLTarget = figure;
		}
	}

	private void endEdit() {
		if (fURLTarget != null) {
			setURL(fURLTarget, fTextField.getText());
			fURLTarget = null;
			fTextField.endOverlay();
		}
	}
    public int getNumComponents() {
    byte[]    theHeader;
    int    theColorSpaceSig, theNumComponents;

        if (deferralInfo != null) {
            return deferralInfo.numComponents; /* Need to have this info for
                                                  ICC_ColorSpace without
                                                  causing a deferred profile
                                                  to be loaded */
        }
        theHeader = getData(icSigHead);

        theColorSpaceSig = intFromBigEndian (theHeader, icHdrColorSpace);

        switch (theColorSpaceSig) {
        case icSigGrayData:
            theNumComponents = 1;
            break;

        case icSigSpace2CLR:
            theNumComponents = 2;
            break;

        case icSigXYZData:
        case icSigLabData:
        case icSigLuvData:
        case icSigYCbCrData:
        case icSigYxyData:
        case icSigRgbData:
        case icSigHsvData:
        case icSigHlsData:
        case icSigCmyData:
        case icSigSpace3CLR:
            theNumComponents = 3;
            break;

        case icSigCmykData:
        case icSigSpace4CLR:
            theNumComponents = 4;
            break;

        case icSigSpace5CLR:
            theNumComponents = 5;
            break;

        case icSigSpace6CLR:
            theNumComponents = 6;
            break;

        case icSigSpace7CLR:
            break;

        case icSigSpace8CLR:
            theNumComponents = 8;
            break;

        case icSigSpace9CLR:
            theNumComponents = 9;
            break;

        case icSigSpaceACLR:
            theNumComponents = 10;
            break;

        case icSigSpaceBCLR:
            theNumComponents = 11;
            break;

        case icSigSpaceCCLR:
            theNumComponents = 12;
            break;

        case icSigSpaceDCLR:
            theNumComponents = 13;
            break;

        case icSigSpaceECLR:
            theNumComponents = 14;
            break;

        case icSigSpaceFCLR:
            theNumComponents = 15;
            break;

        default:
            throw new ProfileDataException ("invalid ICC color space");
        }

        return theNumComponents;
    }

	private Rectangle fieldBounds(Figure figure) {
		Rectangle box = figure.displayBox();
		int nChars = Math.max(20, getURL(figure).length());
		Dimension d = fTextField.getPreferredSize(nChars);
		box.x = Math.max(0, box.x + (box.width - d.width)/2);
		box.y = Math.max(0, box.y + (box.height - d.height)/2);
		return new Rectangle(box.x, box.y, d.width, d.height);
	}

	private String getURL(Figure figure) {
		String url = (String) figure.getAttribute(FigureAttributeConstant.URL);
		if (url == null) {
			url = "";
		}
		return url;
	}

	private void setURL(Figure figure, String url) {
		figure.setAttribute(FigureAttributeConstant.URL, url);
	}
}
