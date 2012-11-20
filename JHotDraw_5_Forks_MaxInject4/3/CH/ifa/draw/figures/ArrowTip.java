/*
 * @(#)ArrowTip.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.io.*;
import java.awt.*;

import CH.ifa.draw.util.*;

/**
 * An arrow tip line decoration.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class ArrowTip extends AbstractLineDecoration {

	private double  fAngle;         // pointiness of arrow
	private double  fOuterRadius;
	private double  fInnerRadius;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -3459171428373823638L;
	private int arrowTipSerializedDataVersion = 1;

	public ArrowTip() {
		this(0.40, 8, 8);  // this(0.35, 15, 12);
	}

   /**
	* Constructs an arrow tip with the given angle and radius.
	*/
	public ArrowTip(double angle, double outerRadius, double innerRadius) {
		setAngle(angle);
		setOuterRadius(outerRadius);
		setInnerRadius(innerRadius);
	}
	
   /**
	* Calculates the outline of an arrow tip.
	*/
	public Polygon outline(int x1, int y1, int x2, int y2) {
		double dir = Math.PI/2 - Math.atan2(x2 - x1, y2 - y1);
		return outline(x1, y1, dir);
	}

	private Polygon outline(int x, int y, double direction) {
		Polygon shape = new Polygon();

		shape.addPoint(x, y);
		addPointRelative(shape, x, y, getOuterRadius(), direction - getAngle());
		addPointRelative(shape, x, y, getInnerRadius(), direction);
		addPointRelative(shape, x, y, getOuterRadius(), direction + getAngle());
		shape.addPoint(x,y); // Closing the polygon (TEG 97-04-23)
		return shape;
	}

	private void addPointRelative(Polygon shape, int x, int y, double radius, double angle) {
		shape.addPoint(
			x + (int) (radius * Math.cos(angle)),
			y + (int) (radius * Math.sin(angle)));
	}

	/**
	 * Stores the arrow tip to a StorableOutput.
	 */
	public void write(StorableOutput dw) {
		dw.writeDouble(getAngle());
		dw.writeDouble(getOuterRadius());
		dw.writeDouble(getInnerRadius());
		super.write(dw);
	}
    protected void initSystemColorDefaults(UIDefaults table)
    {
        String[] defaultSystemColors = {
                  "desktop", "#005C5C", /* Color of the desktop background */
            "activeCaption", "#000080", /* Color for captions (title bars) when they are active. */
        "activeCaptionText", "#FFFFFF", /* Text color for text in captions (title bars). */
      "activeCaptionBorder", "#B24D7A", /* Border color for caption (title bar) window borders. */
          "inactiveCaption", "#AEB2C3", /* Color for captions (title bars) when not active. */
      "inactiveCaptionText", "#000000", /* Text color for text in inactive captions (title bars). */
    "inactiveCaptionBorder", "#AEB2C3", /* Border color for inactive caption (title bar) window borders. */
                   "window", "#AEB2C3", /* Default color for the interior of windows */
             "windowBorder", "#AEB2C3", /* ??? */
               "windowText", "#000000", /* ??? */
                     "menu", "#AEB2C3", /* ??? */
                 "menuText", "#000000", /* ??? */
                     "text", "#FFF7E9", /* Text background color */
                 "textText", "#000000", /* Text foreground color */
            "textHighlight", "#000000", /* Text background color when selected */
        "textHighlightText", "#FFF7E9", /* Text color when selected */
         "textInactiveText", "#808080", /* Text color when disabled */
                  "control", "#AEB2C3", /* Default color for controls (buttons, sliders, etc) */
              "controlText", "#000000", /* Default color for text in controls */
         "controlHighlight", "#DCDEE5", /* Highlight color for controls */
       "controlLtHighlight", "#DCDEE5", /* Light highlight color for controls */
            "controlShadow", "#63656F", /* Shadow color for controls */
       "controlLightShadow", "#9397A5", /* Shadow color for controls */
          "controlDkShadow", "#000000", /* Dark shadow color for controls */
                "scrollbar", "#AEB2C3", /* Scrollbar ??? color. PENDING(jeff) foreground? background? ?*/
                     "info", "#FFF7E9", /* ??? */
                 "infoText", "#000000"  /* ??? */
        };

        loadSystemColors(table, defaultSystemColors, false);
    }

	/**
	 * Reads the arrow tip from a StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		setAngle(dr.readDouble());
		setOuterRadius(dr.readDouble());
		setInnerRadius(dr.readDouble());
		super.read(dr);
	}

	/**
	 * Sets point angle of arrow. A smaller angle leads to a pointier arrow.
	 * The angle is measured between the arrow line and one of the points
	 * at the side of the arrow. Thus, the total angle at the arrow tip
	 * is the double of the angle specified.
	 */
	protected void setAngle(double newAngle) {
		fAngle = newAngle;
	}
	
	/**
	 * Returns point angle of arrow. A smaller angle leads to a pointier arrow.
	 * The angle is measured between the arrow line and one of the points
	 * at the side of the arrow. Thus, the total angle at the arrow tip
	 * is the double of the angle specified.
	 */
	protected double getAngle() {
		return fAngle;
	}

	/**
	 * Sets the inner radius
	 */
	protected void setInnerRadius(double newInnerRadius) {
		fInnerRadius = newInnerRadius;
	}

	/**
	 * Returns the inner radius
	 */        
	protected double getInnerRadius() {
		return fInnerRadius;
	}

	/**
	 * Sets the outer radius
	 */
	protected void setOuterRadius(double newOuterRadius) {
		fOuterRadius = newOuterRadius;
	}

	/**
	 * Returns the outer radius
	 */
	protected double getOuterRadius() {
		return fOuterRadius;
	}
}
