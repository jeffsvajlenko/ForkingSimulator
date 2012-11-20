/*
 * @(#)FigureChangeAdapter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Empty implementation of FigureChangeListener.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeAdapter implements FigureChangeListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void figureInvalidated(FigureChangeEvent e) {}

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureChangeEvent e) {}
    public XMLSignatureInput performTransform(XMLSignatureInput input,
        OutputStream os) throws IOException, CanonicalizationException,
        InvalidCanonicalizerException, TransformationException {

        XMLSignatureInput result = null;

        try {
            result = transformSpi.enginePerformTransform(input, os, this);
        } catch (ParserConfigurationException ex) {
            Object exArgs[] = { this.getURI(), "ParserConfigurationException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        } catch (SAXException ex) {
            Object exArgs[] = { this.getURI(), "SAXException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        }

        return result;
    }

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureChangeEvent e) {}

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureChangeEvent e) {}

	/**
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureChangeEvent e) {}

}
