/*
 * @(#)SimpleUpdateStrategy.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import CH.ifa.draw.framework.*;

/**
 * The SimpleUpdateStrategy implements an update
 * strategy that directly redraws a DrawingView.
 *
 * @see DrawingView
 *
 * @version <$CURRENT_VERSION$>
 */
public  class SimpleUpdateStrategy implements Painter {

	/*
	 * Serialization support. In JavaDraw only the Drawing is serialized.
	 * However, for beans support SimpleUpdateStrategy supports
	 * serialization
	 */
	private static final long serialVersionUID = -7539925820692134566L;

	/**
	* Draws the view contents.
	*/
	public void draw(Graphics g, DrawingView view) {
		view.drawAll(g);
	}
    public XMLFilter newXMLFilter(Source src)
        throws TransformerConfigurationException {
        if (_xsltcFactory == null) {
            createXSLTCTransformerFactory();
        }
        if (_errorlistener != null) {
            _xsltcFactory.setErrorListener(_errorlistener);
        }
        if (_uriresolver != null) {
            _xsltcFactory.setURIResolver(_uriresolver);
        }
        Templates templates = _xsltcFactory.newTemplates(src);
        if (templates == null ) return null;
        return newXMLFilter(templates);
    }
}
