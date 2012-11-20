/*
 * @(#)FigureAndEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Figure;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class FigureAndEnumerator implements FigureEnumeration {
	private FigureEnumeration myFE1;
	private FigureEnumeration myFE2;

	public FigureAndEnumerator(FigureEnumeration newFE1, FigureEnumeration newFE2) {
		myFE1 = newFE1;
		myFE2 = newFE2;
	}

	public Figure nextFigure() {
		if (myFE1.hasNextFigure()) {
			return myFE1.nextFigure();
		}
		else if (myFE2.hasNextFigure()) {
			return myFE2.nextFigure();
		}
		else {
			// todo: throw exception
			return null;
		}
	}

	public boolean hasNextFigure() {
		return myFE1.hasNextFigure() || myFE2.hasNextFigure();
	}
    public void setDataElements(int x, int y, Raster inRaster) {
        int dstOffX = x+inRaster.getMinX();
        int dstOffY = y+inRaster.getMinY();
        int width  = inRaster.getWidth();
        int height = inRaster.getHeight();
        if ((dstOffX < this.minX) || (dstOffY < this.minY) ||
            (dstOffX + width > this.minX + this.width) ||
            (dstOffY + height > this.minY + this.height)) {
            throw new ArrayIndexOutOfBoundsException
                ();
        }

        int srcOffX = inRaster.getMinX();
        int srcOffY = inRaster.getMinY();
        Object tdata = null;

        for (int startY=0; startY < height; startY++) {
            tdata = inRaster.getDataElements(srcOffX, srcOffY+startY,
                                             width, 1, tdata);
            setDataElements(dstOffX, dstOffY+startY,
                            width, 1, tdata);
        }
    }

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myFE1.reset();
		myFE2.reset();
	}
}
