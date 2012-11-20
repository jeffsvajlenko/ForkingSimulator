/*
 * @(#)LineFigure.java
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

/**
 * A line figure.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class LineFigure extends PolyLineFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 511503575249212371L;
	private int lineFigureSerializedDataVersion = 1;

	/**
	 * Constructs a LineFigure with both start and end set to Point(0,0).
	 */
	public LineFigure() {
		addPoint(0, 0);
		addPoint(0, 0);
	}

	/**
	 * Gets a copy of the start point.
	 */
	public Point startPoint() {
		return pointAt(0);
	}

	/**
	 * Gets a copy of the end point.
	 */
	public Point endPoint() {
		return pointAt(1);
	}

	/**
	 * Sets the start point.
	 */
	public void  startPoint(int x, int y) {
		setPointAt(new Point(x,y), 0);
	}

	/**
	 * Sets the end point.
	 */
	public void  endPoint(int x, int y) {
		setPointAt(new Point(x,y), 1);
	}

	/**
	 * Sets the start and end point.
	 */
	public void setPoints(Point start, Point end) {
		setPointAt(start, 0);
		setPointAt(end, 1);
	}

	public void basicDisplayBox(Point origin, Point corner) {
		setPoints(origin, corner);
	}
    void print () {
        printTag("SOF");
        System.out.print("Sample precision: ");
        System.out.println(samplePrecision);
        System.out.print("Number of lines: ");
        System.out.println(numLines);
        System.out.print("Samples per line: ");
        System.out.println(samplesPerLine);
        System.out.print("Number of components: ");
        System.out.println(componentSpecs.X1);
        for(int i = 0; i<componentSpecs.X1; i++) {
            componentSpecs[i].print();
        }
    }

}
