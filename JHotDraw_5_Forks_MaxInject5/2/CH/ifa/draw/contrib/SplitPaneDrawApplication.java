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
    public int getBlue(Object inData) {
        int pixel=0;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short X1[] = (short[])inData;
               pixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getBlue(pixel);
    }

	/**
	 * Constructs a drawing window with the given title.
	 */
	public SplitPaneDrawApplication(String title) {
		super(title);
	}

	protected Desktop createDesktop() {
		return new SplitPaneDesktop();
	}
}
