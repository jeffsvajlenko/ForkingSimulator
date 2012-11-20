/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import CH.ifa.draw.figures.RoundRectangleFigure;

/**
 * Geometric adapter for the RoundRectangleFigure
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class RoundRectangleGeometricAdapter extends RoundRectangleFigure
		 implements GeometricFigure {

	/**Constructor for the RoundRectangleGeometricAdapter object */
	public RoundRectangleGeometricAdapter() {
		super();
	}


	/**
	 *Constructor for the RoundRectangleGeometricAdapter object
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 */
	public RoundRectangleGeometricAdapter(Point origin, Point corner) {
		super(origin, corner);
	}
    private void paintButtonBackgroundImpl(SynthContext context, Graphics g,
            Region id, String detail, int x, int y, int w, int h,
            boolean paintBackground, boolean paintFocus,
            boolean defaultCapable, boolean toolButton) {
        int state = context.getComponentState();
        synchronized (UNIXToolkit.GTK_LOCK) {
            if (ENGINE.paintCachedImage(g, x, y, w, h, id, state, detail,
                    paintBackground, paintFocus, defaultCapable, toolButton)) {
                return;
            }
            ENGINE.startPainting(g, x, y, w, h, id, state, detail,
                paintBackground, paintFocus, defaultCapable, toolButton);

            // Paint the default indicator
            GTKStyle style = (GTKStyle)context.getStyle();
            if (defaultCapable && !toolButton) {
                Insets defaultInsets = style.getClassSpecificInsetsValue(
                        context, "default-border",
                        GTKStyle.BUTTON_DEFAULT_BORDER_INSETS);

                if (paintBackground && (state & SynthConstants.DEFAULT) != 0) {
                    ENGINE.paintBox(g, context, id, SynthConstants.ENABLED,
                            ShadowType.IN, "buttondefault", x, y, w, h);
                }
                x += defaultInsets.left;
                y += defaultInsets.top;
                w -= (defaultInsets.left + defaultInsets.right);
                h -= (defaultInsets.top + defaultInsets.bottom);
            }

            boolean interiorFocus = style.getClassSpecificBoolValue(
                    context, "interior-focus", true);
            int focusSize = style.getClassSpecificIntValue(
                    context, "focus-line-width",1);
            int focusPad = style.getClassSpecificIntValue(
                    context, "focus-padding", 1);

            int totalFocusSize = focusSize + focusPad;
            int xThickness = style.getXThickness();
            int yThickness = style.getYThickness();

            // Render the box.
            if (!interiorFocus &&
                    (state & SynthConstants.FOCUSED) == SynthConstants.FOCUSED) {
                x += totalFocusSize;
                y += totalFocusSize;
                w -= 2 * totalFocusSize;
                h -= 2 * totalFocusSize;
            }

            int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
            boolean paintBg;
            if (toolButton) {
                // Toolbar buttons should only have their background painted
                // in the PRESSED, SELECTED, or MOUSE_OVER states.
                paintBg =
                    (gtkState != SynthConstants.ENABLED) &&
                    (gtkState != SynthConstants.DISABLED);
            } else {
                // Otherwise, always paint the button's background, unless
                // the user has overridden it and we're in the ENABLED state.
                paintBg =
                    paintBackground ||
                    (gtkState != SynthConstants.ENABLED);
            }
            if (paintBg) {
                ShadowType shadowType = ShadowType.OUT;
                if ((state & (SynthConstants.PRESSED |
                              SynthConstants.SELECTED)) != 0) {
                    shadowType = ShadowType.IN;
                }
                ENGINE.paintBox(g, context, id, gtkState,
                        shadowType, detail, x, y, w, h);
            }

            // focus
            if (paintFocus && (state & SynthConstants.FOCUSED) != 0) {
                if (interiorFocus) {
                    x += xThickness + focusPad;
                    y += yThickness + focusPad;
                    w -= 2 * (xThickness + focusPad);
                    h -= 2 * (yThickness + focusPad);
                } else {
                    x -= totalFocusSize;
                    y -= totalFocusSize;
                    w += 2 * totalFocusSize;
                    h += 2 * totalFocusSize;
                }
                ENGINE.paintFocus(g, context, id, gtkState, detail, x, y, w, h);
            }
            ENGINE.finishPainting();
        }
    }


	/**
	 * Gets the shape attribute of the TriangleFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		Point arc = getArc();
		Rectangle dspBox = displayBox();
		RoundRectangle2D.Float roundRectangle = new RoundRectangle2D.Float(
				dspBox.x, dspBox.y, dspBox.width, dspBox.height,
				arc.x, arc.y);

		return roundRectangle;
	}
}
