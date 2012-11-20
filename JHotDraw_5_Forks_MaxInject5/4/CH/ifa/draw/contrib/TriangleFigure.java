/*
 * @(#)TriangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.util.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.framework.HandleEnumeration;
import CH.ifa.draw.standard.HandleEnumerator;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.IOException;

/**
 * A triangle with same dimensions as its enclosing rectangle,
 * and apex at any of 8 places
 *
 * @author Doug Lea  (dl at gee, Tue Feb 25 17:30:58 1997)
 * @version <$CURRENT_VERSION$>
 */
public  class TriangleFigure extends RectangleFigure {

	static double[] rotations = {
		-Math.PI/2, -Math.PI/4,
		0.0, Math.PI/4,
		Math.PI/2, Math.PI * 3/4,
		Math.PI,  -Math.PI * 3/4
	};

	private int fRotation = 0;

	public TriangleFigure() {
		super(new Point(0,0), new Point(0,0));
	}

	public TriangleFigure(Point origin, Point corner) {
		super(origin, corner);
	}

	public HandleEnumeration handles() {
		List h = super.handles().toList();
		h.add(new TriangleRotationHandle(this));
		return new HandleEnumerator(h);
	}

	public void rotate(double angle) {
		willChange();
		double dist = Double.MAX_VALUE;
		int best = 0;
		for (int i = 0; i < rotations.length; ++i) {
			double d = Math.abs(angle - rotations[i]);
			if (d < dist) {
				dist = d;
				best = i;
			}
		}
		fRotation = best;
		changed();
	}

	/** Return the polygon describing the triangle **/
	public Polygon getPolygon() {
		Rectangle r = displayBox();
		Polygon p = new Polygon();
		switch (fRotation) {
		case 0:
			p.addPoint(r.x + r.width/2, r.y);
			p.addPoint(r.x + r.width, r.y + r.height);
			p.addPoint(r.x, r.y + r.height);
			break;
		case 1:
			p.addPoint(r.x + r.width, r.y);
			p.addPoint(r.x + r.width, r.y + r.height);
			p.addPoint(r.x, r.y);
			break;
		case 2:
			p.addPoint(r.x + r.width, r.y + r.height/2);
			p.addPoint(r.x, r.y + r.height);
			p.addPoint(r.x, r.y);
			break;
		case 3:
			p.addPoint(r.x + r.width, r.y + r.height);
			p.addPoint(r.x, r.y + r.height);
			p.addPoint(r.x + r.width, r.y);
			break;
		case 4:
			p.addPoint(r.x + r.width/2, r.y + r.height);
			p.addPoint(r.x, r.y);
			p.addPoint(r.x + r.width, r.y);
			break;
		case 5:
			p.addPoint(r.x, r.y + r.height);
			p.addPoint(r.x, r.y);
			p.addPoint(r.x + r.width, r.y + r.height);
			break;
		case 6:
			p.addPoint(r.x, r.y + r.height/2);
			p.addPoint(r.x + r.width, r.y);
			p.addPoint(r.x + r.width, r.y + r.height);
			break;
		case 7:
			p.addPoint(r.x, r.y);
			p.addPoint(r.x + r.width, r.y);
			p.addPoint(r.x, r.y + r.height);
			break;
		}
		return p;
	}


	public void draw(Graphics g) {
		Polygon p = getPolygon();
		g.setColor(getFillColor());
		g.fillPolygon(p);
		g.setColor(getFrameColor());
		g.drawPolygon(p);
	}

	public Insets connectionInsets() {
		Rectangle r = displayBox();
		switch(fRotation) {
		case 0:
			return new Insets(r.height, r.width/2, 0, r.width/2);
		case 1:
			return new Insets(0, r.width, r.height, 0);
		case 2:
			return new Insets(r.height/2, 0, r.height/2, r.width);
		case 3:
			return new Insets(r.height, r.width, 0, 0);
		case 4:
			return new Insets(0, r.width/2, r.height, r.width/2);
		case 5:
			return new Insets(r.height, 0, 0, r.width);
		case 6:
			return new Insets(r.height/2, r.width, r.height/2, 0);
		case 7:
			return new Insets(0, 0, r.height, r.width);
		default:
			return null;
		}
	}

	public boolean containsPoint(int x, int y) {
		return getPolygon().contains(x, y);
	}

	public Point center() {
		return PolygonFigure.center(getPolygon());
	}

	public Point chop(Point p) {
		return PolygonFigure.chop(getPolygon(), p);
	}

	public Object clone() {
		TriangleFigure figure = (TriangleFigure) super.clone();
		figure.fRotation = fRotation;
		return figure;
	}

	public double getRotationAngle() {
		return rotations[fRotation];
	}
    public void comment(char ch[], int start, int length)
        throws org.xml.sax.SAXException
    {

        int start_old = start;
        if (m_inEntityRef)
            return;
        if (m_elemContext.m_startTagOpen)
        {
            closeStartTag();
            m_elemContext.m_startTagOpen = false;
        }
        else if (m_needToCallStartDocument)
        {
            startDocumentInternal();
            m_needToCallStartDocument = false;
        }

        try
        {
            if (shouldIndent() && m_isStandalone)
                indent();

            final int limit = start + length;
            boolean wasDash = false;
            if (m_cdataTagOpen)
                closeCDATA();

            if (shouldIndent() && !m_isStandalone)
                indent();

            final java.io.Writer writer = m_writer;
            writer.write(COMMENT_BEGIN);
            // Detect occurrences of two consecutive dashes, handle as necessary.
            for (int i = start; i < limit; i++)
            {
                if (wasDash && ch[i] == '-')
                {
                    writer.write(ch, start, i - start);
                    writer.write(" -");
                    start = i + 1;
                }
                wasDash = (ch[i] == '-');
            }

            // if we have some chars in the comment
            if (length > 0)
            {
                // Output the remaining characters (if any)
                final int remainingChars = (limit - start);
                if (remainingChars > 0)
                    writer.write(ch, start, remainingChars);
                // Protect comment end from a single trailing dash
                if (ch[limit - 1] == '-')
                    writer.write(' ');
            }
            writer.write(COMMENT_END);
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }

        /*
         * Don't write out any indentation whitespace now,
         * because there may be non-whitespace text after this.
         *
         * Simply mark that at this point if we do decide
         * to indent that we should
         * add a newline on the end of the current line before
         * the indentation at the start of the next line.
         */
        m_startNewLine = true;
        // time to generate comment event
        if (m_tracer != null)
            super.fireCommentEvent(ch, start_old,length);
    }

	//-- store / load ----------------------------------------------

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fRotation);
	}

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fRotation = dr.readInt();
	}
}
