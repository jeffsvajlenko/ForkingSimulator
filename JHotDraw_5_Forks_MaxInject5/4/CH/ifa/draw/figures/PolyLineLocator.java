/*
 * @(#)PolyLineLocator.java
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

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
class PolyLineLocator extends AbstractLocator {
	int fIndex;

	public PolyLineLocator(int index) {
		fIndex = index;
	}

	public Point locate(Figure owner) {
		PolyLineFigure plf = (PolyLineFigure)owner;
		// guard against changing PolyLineFigures -> temporary hack
		if (fIndex < plf.pointCount()) {
			return ((PolyLineFigure)owner).pointAt(fIndex);
		}
		return new Point(0, 0);
	}
        public Indexed(byte[] redLUT,
                       byte[] greenLUT,
                       byte[] blueLUT,
                       byte[] alphaLUT,
                       int bits,
                       int dataType) {
            if (redLUT == null || greenLUT == null || blueLUT == null) {
                throw new IllegalArgumentException("LUT is null!");
            }
            if (bits != 1 && bits != 2 && bits != 4 &&
                bits != 8 && bits != 16) {
                throw new IllegalArgumentException("Bad value for bits!");
            }
            if (dataType != DataBuffer.TYPE_BYTE &&
                dataType != DataBuffer.TYPE_SHORT &&
                dataType != DataBuffer.TYPE_USHORT &&
                dataType != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException
                    ("Bad value for dataType!");
            }
            if ((bits > 8 && dataType == DataBuffer.TYPE_BYTE) ||
                (bits > 16 && dataType != DataBuffer.TYPE_INT)) {
                throw new IllegalArgumentException
                    ("Too many bits for dataType!");
            }

            int len = 1 << bits;
            if (redLUT.length != len ||
                greenLUT.length != len ||
                blueLUT.length != len ||
                (alphaLUT != null && alphaLUT.length != len)) {
                throw new IllegalArgumentException("LUT has improper length!");
            }
            this.redLUT = (byte[])redLUT.clone();
            this.greenLUT = (byte[])greenLUT.clone();
            this.blueLUT = (byte[])blueLUT.clone();
            if (alphaLUT != null) {
                this.alphaLUT = (byte[])alphaLUT.clone();
            }
            this.bits = bits;
            this.dataType = dataType;

            if (alphaLUT == null) {
                this.colorModel = new IndexColorModel(bits,
                                                      redLUT.length,
                                                      redLUT,
                                                      greenLUT,
                                                      blueLUT);
            } else {
                this.colorModel = new IndexColorModel(bits,
                                                      redLUT.length,
                                                      redLUT,
                                                      greenLUT,
                                                      blueLUT,
                                                      alphaLUT);
            }

            if ((bits == 8 && dataType == DataBuffer.TYPE_BYTE) ||
                (bits == 16 &&
                 (dataType == DataBuffer.TYPE_SHORT ||
                  dataType == DataBuffer.TYPE_USHORT))) {
                int[] bandOffsets = { 0 };
                this.sampleModel =
                    new PixelInterleavedSampleModel(dataType,
                                                    1, 1, 1, 1,
                                                    bandOffsets);
            } else {
                this.sampleModel =
                    new MultiPixelPackedSampleModel(dataType, 1, 1, bits);
            }
        }
}
