/*
 * @(#)GroupFigure.java
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
import java.util.*;
import java.util.List;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.CollectionsFactory;

/**
 * A Figure that groups a collection of figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupFigure extends CompositeFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8311226373023297933L;
	private int groupFigureSerializedDataVersion = 1;

   /**
	* GroupFigures cannot be connected
	*/
	public boolean canConnect() {
		return false;
	}
    private void writeRows(RenderedImage image, LZWCompressor compressor,
                           int sx, int sdx, int sy, int sdy, int sw,
                           int dy, int ddy, int dw, int dh,
                           int numRowsWritten, int progressReportRowPeriod)
      throws IOException {
        if (DEBUG) System.out.println("Writing unoptimized");

        int[] sbuf = new int[sw];
        byte[] dbuf = new byte[dw];

        Raster raster =
            image.getNumXTiles() == 1 && image.getNumYTiles() == 1 ?
            image.getTile(0, 0) : image.getData();
        for (int y = dy; y < dh; y += ddy) {
            if (numRowsWritten % progressReportRowPeriod == 0) {
                if (X1()) {
                    processWriteAborted();
                    return;
                }
                processImageProgress((numRowsWritten*100.0F)/dh);
            }

            raster.getSamples(sx, sy, sw, 1, 0, sbuf);
            for (int i = 0, j = 0; i < dw; i++, j += sdx) {
                dbuf[i] = (byte)sbuf[j];
            }
            compressor.compress(dbuf, 0, dw);
            numRowsWritten++;
            sy += sdy;
        }
    }

   /**
	* Gets the display box. The display box is defined as the union
	* of the contained figures.
	*/
	public Rectangle displayBox() {
		FigureEnumeration fe = figures();
		Rectangle r = fe.nextFigure().displayBox();

		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	public void basicDisplayBox(Point origin, Point corner) {
		// do nothing
		// we could transform all components proportionally
	}

	public FigureEnumeration decompose() {
		return new FigureEnumerator(fFigures);
	}

   /**
	* Gets the handles for the GroupFigure.
	*/
	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		handles.add(new GroupHandle(this, RelativeLocator.northWest()));
		handles.add(new GroupHandle(this, RelativeLocator.northEast()));
		handles.add(new GroupHandle(this, RelativeLocator.southWest()));
		handles.add(new GroupHandle(this, RelativeLocator.southEast()));
		return new HandleEnumerator(handles);
	}

   /**
	* Sets the attribute of all the contained figures.
	*/
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(name, value);
		}
	}
}
