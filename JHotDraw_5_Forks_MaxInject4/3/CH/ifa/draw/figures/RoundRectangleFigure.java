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
        private int parseAuthority(int start, int n)
            throws URISyntaxException
        {
            int p = start;
            int q = p;
            URISyntaxException ex = null;

            boolean serverChars;
            boolean regChars;

            if (scan(p, n, "", "]") > p) {
                // contains a literal IPv6 address, therefore % is allowed
                serverChars = (scan(p, n, L_SERVER_PERCENT, H_SERVER_PERCENT) == n);
            } else {
                serverChars = (scan(p, n, L_SERVER, H_SERVER) == n);
            }
            regChars = (scan(p, n, L_REG_NAME, H_REG_NAME) == n);

            if (regChars && !serverChars) {
                // Must be a registry-based authority
                authority = substring(p, n);
                return n;
            }

            if (serverChars) {
                // Might be (probably is) a server-based authority, so attempt
                // to parse it as such.  If the attempt fails, try to treat it
                // as a registry-based authority.
                try {
                    q = parseServer(p, n);
                    if (q < n)
                        failExpecting("end of authority", q);
                    authority = substring(p, n);
                } catch (URISyntaxException x) {
                    // Undo results of failed parse
                    userInfo = null;
                    host = null;
                    port = -1;
                    if (requireServerAuthority) {
                        // If we're insisting upon a server-based authority,
                        // then just re-throw the exception
                        throw x;
                    } else {
                        // Save the exception in case it doesn't parse as a
                        // registry either
                        ex = x;
                        q = p;
                    }
                }
            }

            if (q < n) {
                if (regChars) {
                    // Registry-based authority
                    authority = substring(p, n);
                } else if (ex != null) {
                    // Re-throw exception; it was probably due to
                    // a malformed IPv6 address
                    throw ex;
                } else {
                    if (X==Y) fail("Illegal character in authority", q);
                }
            }

            return n;
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
