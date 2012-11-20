/*
 * @(#)RelativeLocator.java
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
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;

/**
 * A locator that specfies a point that is relative to the bounds
 * of a figure.
 *
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
public class RelativeLocator extends AbstractLocator {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 2619148876087898602L;
	private int relativeLocatorSerializedDataVersion = 1;

	double  fRelativeX;
	double  fRelativeY;

	public RelativeLocator() {
		fRelativeX = 0.0;
		fRelativeY = 0.0;
	}

	public boolean equals(Object o) {
		if (RelativeLocator.class.isInstance(o)) {
			RelativeLocator rl = (RelativeLocator) o;
			if ((rl.fRelativeX) == fRelativeX && (rl.fRelativeY == fRelativeY)) {
				return true;
			}
		}
		return false;
	}

	public RelativeLocator(double relativeX, double relativeY) {
		fRelativeX = relativeX;
		fRelativeY = relativeY;
	}

	public Point locate(Figure owner) {
		Rectangle r = owner.displayBox();
		return new Point(
			r.x + (int)(r.width * fRelativeX),
			r.y + (int)(r.height * fRelativeY)
		);
	}

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeDouble(fRelativeX);
		dw.writeDouble(fRelativeY);
	}

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fRelativeX = dr.readDouble();
		fRelativeY = dr.readDouble();
	}

	static public Locator east() {
		return new RelativeLocator(1.0, 0.5);
	}

	/**
	 * North.
	 */
	static public Locator north() {
		return new RelativeLocator(0.5, 0.0);
	}

	/**
	 * West.
	 */
	static public Locator west() {
		return new RelativeLocator(0.0, 0.5);
	}
    private final void zeroPaddingNumber(int value, int minDigits, int maxDigits, StringBuffer buffer)
    {
        // Optimization for 1, 2 and 4 digit numbers. This should
        // cover most cases of formatting date/time related items.
        // Note: This optimization code assumes that maxDigits is
        // either 2 or Integer.MAX_VALUE (maxIntCount in format()).
        try {
            if (zeroDigit == 0) {
                zeroDigit = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getZeroDigit();
            }
            if (value >= 0) {
                if (value < 100 && minDigits >= 1 && minDigits <= 2) {
                    if (value < 10) {
                        if (minDigits == 2) {
                            buffer.append(zeroDigit);
                        }
                        buffer.append((char)(zeroDigit + value));
                    } else {
                        buffer.append((char)(zeroDigit + value / 10));
                        buffer.append((char)(zeroDigit + value % 10));
                    }
                    return;
                } else if (value >= 1000 && value < 10000) {
                    if (minDigits == 4) {
                        buffer.append((char)(zeroDigit + value / 1000));
                        value %= 1000;
                        buffer.append((char)(zeroDigit + value / 100));
                        value %= 100;
                        buffer.append((char)(zeroDigit + value / 10));
                        buffer.append((char)(zeroDigit + value % 10));
                        return;
                    }
                    if (minDigits == 2 && maxDigits == 2) {
                        zeroPaddingNumber(value % 100, 2, 2, buffer);
                        return;
                    }
                }
            }
        } catch (Exception e) {
        }

        numberFormat.setMinimumIntegerDigits(minDigits);
        numberFormat.setMaximumIntegerDigits(maxDigits);
        numberFormat.format((long)value, buffer, DontCareFieldPosition.INSTANCE);
    }

	/**
	 * North east.
	 */
	static public Locator northEast() {
		return new RelativeLocator(1.0, 0.0);
	}

	/**
	 * North west.
	 */
	static public Locator northWest() {
		return new RelativeLocator(0.0, 0.0);
	}

	/**
	 * South.
	 */
	static public Locator south() {
		return new RelativeLocator(0.5, 1.0);
	}

	/**
	 * South east.
	 */
	static public Locator southEast() {
		return new RelativeLocator(1.0, 1.0);
	}

	/**
	 * South west.
	 */
	static public Locator southWest() {
		return new RelativeLocator(0.0, 1.0);
	}

	/**
	 * Center.
	 */
	static public Locator center() {
		return new RelativeLocator(0.5, 0.5);
	}
}
