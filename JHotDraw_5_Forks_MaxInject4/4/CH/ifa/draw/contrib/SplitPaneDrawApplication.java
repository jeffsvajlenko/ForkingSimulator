/*
 * @(#)SplitPaneDrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.application.*;

/**
 * A specialised DrawApplication, which offers basic support for a simple
 * splitted pane content.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public  class SplitPaneDrawApplication extends DrawApplication {

	/**
	 * Constructs a drawing window with a default title.
	 */
	public SplitPaneDrawApplication() {
		this("JHotDraw");
	}

	/**
	 * Constructs a drawing window with the given title.
	 */
	public SplitPaneDrawApplication(String title) {
		super(title);
	}
  static public IncrementalSAXSource
  createIncrementalSAXSource(SAXParser parser) {
                try
                {
                        return new IncrementalSAXSource_Xerces(parser);
                }
                catch(NoSuchMethodException e)
                {
                        // Xerces version mismatch; neither Xerces1 nor Xerces2 succeeded.
                        // Fall back on filtering solution.
                        IncrementalSAXSource_Filter iss=new IncrementalSAXSource_Filter();
                        return iss;
                }
  }

	protected Desktop createDesktop() {
		return new SplitPaneDesktop();
	}
}
