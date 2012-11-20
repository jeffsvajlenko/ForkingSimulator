/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.io.IOException;
import java.io.Serializable;

import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * Base class for the ContentProducers hierarchy<br>
 * Doesn't do a thing now, but we may need this one later to add
 * generic behaviour.
 *
 * @author    Eduardo Francos - InContext
 * @created   7 mai 2002
 * @version   1.0
 */

public abstract class AbstractContentProducer
		 implements ContentProducer, Serializable {

	final static long serialVersionUID = -2715253447095419531L;


	/**Constructor for the AbstractContentProducer object */
	public AbstractContentProducer() { }


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) { }


	/**
	 * Writes the storable
	 *
	 * @param dr               the storable input
	 * @exception IOException  thrown by called methods
	 */
	public void read(StorableInput dr)
		throws IOException { }
    public void setIconAt(int index, Icon icon) {
        Page page = pages.get(index);
        Icon oldIcon = page.icon;
        if (icon != oldIcon) {
            page.icon = icon;

            /* If the default icon has really changed and we had
             * generated the disabled icon for this page, then
             * clear the disabledIcon field of the page.
             */
            if (page.disabledIcon instanceof UIResource) {
                page.disabledIcon = null;
            }

            // Fire the accessibility Visible data change
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                        oldIcon, icon);
            }
            revalidate();
            repaint();
        }
    }

}
