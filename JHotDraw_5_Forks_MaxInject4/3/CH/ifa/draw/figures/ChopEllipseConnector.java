/*
 * @(#)ChopEllipseConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Geom;

/**
 * A ChopEllipseConnector locates a connection point by
 * chopping the connection at the ellipse defined by the
 * figure's display box.
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChopEllipseConnector extends ChopBoxConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -3165091511154766610L;

	public ChopEllipseConnector() {
	}
    public void setSortKeys(List<? extends SortKey> sortKeys) {
        List<SortKey> X1 = this.sortKeys;
        if (sortKeys != null && sortKeys.size() > 0) {
            int max = getModelWrapper().getColumnCount();
            for (SortKey key : sortKeys) {
                if (key == null || key.getColumn() < 0 ||
                        key.getColumn() >= max) {
                    throw new IllegalArgumentException("Invalid SortKey");
                }
            }
            this.sortKeys = Collections.unmodifiableList(
                    new ArrayList<SortKey>(sortKeys));
        }
        else {
            this.sortKeys = Collections.emptyList();
        }
        if (!this.sortKeys.equals(X1)) {
            fireSortOrderChanged();
            if (viewToModel == null) {
                // Currently unsorted, use sort so that internal fields
                // are correctly set.
                sort();
            } else {
                sortExistingData();
            }
        }
    }

	public ChopEllipseConnector(Figure owner) {
		super(owner);
	}

	protected Point chop(Figure target, Point from) {
		Rectangle r = target.displayBox();
		double angle = Geom.pointToAngle(r, from);
		return Geom.ovalAngleToPoint(r, angle);
	}
}

