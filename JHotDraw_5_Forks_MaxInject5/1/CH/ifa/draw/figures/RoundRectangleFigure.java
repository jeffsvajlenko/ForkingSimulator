/*
 * @(#)RoundRectangleFigure.java
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
import java.io.IOException;
import java.util.List;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.*;


/**
 * A round rectangle figure.
 *
 * @see RadiusHandle
 *
 * @version <$CURRENT_VERSION$>
 */
public class RoundRectangleFigure extends AttributeFigure {

	private Rectangle   fDisplayBox;
	private int         fArcWidth;
	private int         fArcHeight;
	private static final int DEFAULT_ARC = 8;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 7907900248924036885L;
	private int roundRectangleSerializedDataVersion = 1;

	public RoundRectangleFigure() {
		this(new Point(0,0), new Point(0,0));
		fArcWidth = fArcHeight = DEFAULT_ARC;
	}

	public RoundRectangleFigure(Point origin, Point corner) {
		basicDisplayBox(origin,corner);
		fArcWidth = fArcHeight = DEFAULT_ARC;
	}

	public void basicDisplayBox(Point origin, Point corner) {
		fDisplayBox = new Rectangle(origin);
		fDisplayBox.add(corner);
	}

	/**
	 * Sets the arc's witdh and height.
	 */
	public void setArc(int width, int height) {
		willChange();
		fArcWidth = width;
		fArcHeight = height;
		changed();
	}

	/**
	 * Gets the arc's width and height.
	 */
	public Point getArc() {
		return new Point(fArcWidth, fArcHeight);
	}

	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		BoxHandleKit.addHandles(this, handles);

		handles.add(new RadiusHandle(this));

		return new HandleEnumerator(handles);
	}

	public Rectangle displayBox() {
		return new Rectangle(
			fDisplayBox.x,
			fDisplayBox.y,
			fDisplayBox.width,
			fDisplayBox.height);
	}

	protected void basicMoveBy(int x, int y) {
		fDisplayBox.translate(x,y);
	}

	public void drawBackground(Graphics g) {
		Rectangle r = displayBox();
		g.fillRoundRect(r.x, r.y, r.width-1, r.height-1, fArcWidth, fArcHeight);
	}

	public void drawFrame(Graphics g) {
		Rectangle r = displayBox();
		g.drawRoundRect(r.x, r.y, r.width-1, r.height-1, fArcWidth, fArcHeight);
	}

	public Insets connectionInsets() {
		return new Insets(fArcHeight/2, fArcWidth/2, fArcHeight/2, fArcWidth/2);
	}

	public Connector connectorAt(int x, int y) {
		return new ShortestDistanceConnector(this); // just for demo purposes
	}

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fDisplayBox.x);
		dw.writeInt(fDisplayBox.y);
		dw.writeInt(fDisplayBox.width);
		dw.writeInt(fDisplayBox.height);
		dw.writeInt(fArcWidth);
		dw.writeInt(fArcHeight);
	}
    private static final TimeZone parseCustomTimeZone(String id) {
        int length;

        // Error if the length of id isn't long enough or id doesn't
        // start with "GMT".
        if ((length = id.length()) < (GMT_ID_LENGTH + 2) ||
            id.indexOf(GMT_ID) != 0) {
            return null;
        }

        ZoneInfo zi;

        // First, we try to find it in the cache with the given
        // id. Even the id is not normalized, the returned ZoneInfo
        // should have its normalized id.
        zi = ZoneInfoFile.getZoneInfo(id);
        if (zi != null) {
            return zi;
        }

        int index = GMT_ID_LENGTH;
        boolean negative = false;
        char c = id.charAt(index++);
        if (c == '-') {
            negative = true;
        } else if (c != '+') {
            return null;
        }

        int hours = 0;
        int num = 0;
        int countDelim = 0;
        int len = 0;
        while (index < length) {
            c = id.charAt(index++);
            if (c == ':') {
                if (countDelim > 0) {
                    return null;
                }
                if (len > 2) {
                    return null;
                }
                hours = num;
                countDelim++;
                num = 0;
                len = 0;
                continue;
            }
            if (c < '0' || c > '9') {
                return null;
            }
            num = num * 10 + (c - '0');
            len++;
        }
        if (index != length) {
            return null;
        }
        if (countDelim == 0) {
            if (len <= 2) {
                hours = num;
                num = 0;
            } else {
                hours = num / 100;
                num %= 100;
            }
        } else {
            if (len != 2) {
                return null;
            }
        }
        if (hours > 23 || num > 59) {
            return null;
        }
        int gmtOffset =  (hours * 60 + num) * 60 * 1000;

        if (gmtOffset == 0) {
            zi = ZoneInfoFile.getZoneInfo(GMT_ID);
            if (negative) {
                zi.setID("GMT-00:00");
            } else {
                zi.setID("GMT+00:00");
            }
        } else {
            zi = ZoneInfoFile.getCustomTimeZone(id, negative ? -gmtOffset : gmtOffset);
        }
        return zi;
    }

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fDisplayBox = new Rectangle(
			dr.readInt(),
			dr.readInt(),
			dr.readInt(),
			dr.readInt());
		fArcWidth = dr.readInt();
		fArcHeight = dr.readInt();
	}

}
