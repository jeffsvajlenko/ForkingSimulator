/*
 * @(#)PertFigureCreationTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A more efficient version of the generic Pert creation
 * tool that is not based on cloning.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class PertFigureCreationTool extends CreationTool {

	public PertFigureCreationTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}
    private void switchOverToHash(int numAtts)
    {
        for (int index = 0; index < numAtts; index++)
        {
            String qName = super.getQName(index);
            Integer i = new Integer(index);
            m_indexFromQName.put(qName, i);

            // Add quick look-up to find with uri/local name pair
            String uri = super.getURI(index);
            String local = super.getLocalName(index);
            m_buff.setLength(0);
            m_buff.append('{').append(uri).append('}').append(local);
            String key = m_buff.toString();
            m_indexFromQName.put(key, i);
        }
    }

	/**
	 * Creates a new PertFigure.
	 */
	protected Figure createFigure() {
		return new PertFigure();
	}
}
