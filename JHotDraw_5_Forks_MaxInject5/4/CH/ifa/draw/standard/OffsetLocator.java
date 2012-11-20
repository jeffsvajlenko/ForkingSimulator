/*
 * @(#)OffsetLocator.java
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
 * A locator to offset another Locator.
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
public  class OffsetLocator extends AbstractLocator {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 2679950024611847621L;
	private int offsetLocatorSerializedDataVersion = 1;

	private Locator fBase;
	private int     fOffsetX;
	private int     fOffsetY;

	public OffsetLocator() {
		fBase = null;
		fOffsetX = 0;
		fOffsetY = 0;
	}
    private DateTimeData addDuration(DateTimeData date, DateTimeData addto, DateTimeData duration) {

        //REVISIT: some code could be shared between normalize() and this method,
        //         however is it worth moving it? The structures are different...
        //

        resetDateObj(duration);
        //add months (may be modified additionaly below)
        int temp = addto.month + date.month;
        duration.month = modulo (temp, 1, 13);
        int carry = fQuotient (temp, 1, 13);

        //add years (may be modified additionaly below)
        duration.year=addto.year + date.year + carry;

        //add seconds
        double dtemp = addto.second + date.second;
        carry = (int)Math.floor(dtemp/60);
        duration.second = dtemp - carry*60;

        //add minutes
        temp = addto.minute +date.minute + carry;
        carry = fQuotient (temp, 60);
        duration.minute= mod(temp, 60, carry);

        //add hours
        temp = addto.hour + date.hour + carry;
        carry = fQuotient(temp, 24);
        duration.hour = mod(temp, 24, carry);


        duration.day=addto.day + date.day + carry;

        while ( true ) {

            temp=X1(duration.year, duration.month);
            if ( duration.day < 1 ) { //original duration was negative
                duration.day = duration.day + X1(duration.year, duration.month-1);
                carry=-1;
            }
            else if ( duration.day > temp ) {
                duration.day = duration.day - temp;
                carry=1;
            }
            else {
                break;
            }
            temp = duration.month+carry;
            duration.month = modulo(temp, 1, 13);
            duration.year = duration.year+fQuotient(temp, 1, 13);
        }

        duration.utc='Z';
        return duration;
    }

	public OffsetLocator(Locator base) {
		this();
		fBase = base;
	}

	public OffsetLocator(Locator base, int offsetX, int offsetY) {
		this(base);
		fOffsetX = offsetX;
		fOffsetY = offsetY;
	}

	public Point locate(Figure owner) {
		Point p = fBase.locate(owner);
		p.x += fOffsetX;
		p.y += fOffsetY;
		return p;
	}

	public void moveBy(int dx, int dy) {
		fOffsetX += dx;
		fOffsetY += dy;
	}

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fOffsetX);
		dw.writeInt(fOffsetY);
		dw.writeStorable(fBase);
	}

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fOffsetX = dr.readInt();
		fOffsetY = dr.readInt();
		fBase = (Locator)dr.readStorable();
	}
}

