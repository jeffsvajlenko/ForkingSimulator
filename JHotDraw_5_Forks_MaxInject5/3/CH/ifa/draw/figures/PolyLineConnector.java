/*
 * @(#)PolyLineConnector.java
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
import CH.ifa.draw.util.*;

/**
 * PolyLineConnector finds connection points on a
 * PolyLineFigure.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public class PolyLineConnector extends ChopBoxConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 6018435940519102865L;

	public PolyLineConnector() {
		super();
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
	 * Constructs a connector with the given owner figure.
	 */
	public PolyLineConnector(Figure owner) {
		super(owner);
	}

	protected Point chop(Figure target, Point from) {
		PolyLineFigure p = (PolyLineFigure)owner();
		// *** based on PolygonFigure's heuristic
		Point ctr = p.center();
		int cx = -1;
		int cy = -1;
		long len = Long.MAX_VALUE;

		// Try for points along edge

		for (int i = 0; i < p.pointCount()-1; i++) {
			Point p1 = p.pointAt(i);
			Point p2 = p.pointAt(i+1);
			Point chop = Geom.intersect(p1.x,
								 p1.y,
								 p2.x,
								 p2.y,
								 from.x,
								 from.y,
								 ctr.x,
								 ctr.y);
			if (chop != null) {
				long cl = Geom.length2(chop.x, chop.y, from.x, from.y);
				if (cl < len) {
					len = cl;
					cx = chop.x;
					cy = chop.y;
				}
			}
		}
		// if none found, pick closest vertex
		//if (len ==  Long.MAX_VALUE) {
		{ // try anyway
			for (int i = 0; i < p.pointCount(); i++) {
				Point pp = p.pointAt(i);
				long l = Geom.length2(pp.x, pp.y, from.x, from.y);
				if (l < len) {
					len = l;
					cx = pp.x;
					cy = pp.y;
				}
			}
		}
		return new Point(cx, cy);
	}
}

