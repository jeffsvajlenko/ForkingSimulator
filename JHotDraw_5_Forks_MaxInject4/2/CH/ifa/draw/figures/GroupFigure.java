/*
 * @(#)GroupFigure.java
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
import java.util.*;
import java.util.List;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.CollectionsFactory;

/**
 * A Figure that groups a collection of figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupFigure extends CompositeFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8311226373023297933L;
	private int groupFigureSerializedDataVersion = 1;

   /**
	* GroupFigures cannot be connected
	*/
	public boolean canConnect() {
		return false;
	}
    private void updateStyle(JSlider c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            thumbWidth =
                style.getInt(context, "Slider.thumbWidth", 30);

            thumbHeight =
                style.getInt(context, "Slider.thumbHeight", 14);

            // handle scaling for sizeVarients for special case components. The
            // key "JComponent.sizeVariant" scales for large/small/mini
            // components are based on Apples LAF
            String scaleKey = (String)slider.getClientProperty(
                    "JComponent.sizeVariant");
            if (scaleKey != null){
                if ("large".equals(scaleKey)){
                    thumbWidth *= 1.15;
                    thumbHeight *= 1.15;
                } else if ("small".equals(scaleKey)){
                    thumbWidth *= 0.857;
                    thumbHeight *= 0.857;
                } else if ("mini".equals(scaleKey)){
                    thumbWidth *= 0.784;
                    thumbHeight *= 0.784;
                }
            }

            trackBorder =
                style.getInt(context, "Slider.trackBorder", 1);

            trackHeight = thumbHeight + trackBorder * 2;

            paintValue = style.getBoolean(context,
                    "Slider.paintValue", true);
            if (oldStyle != null) {
                uninstallKeyboardActions(c);
                installKeyboardActions(c);
            }
        }
        context.dispose();

        context = getContext(c, Region.SLIDER_TRACK, ENABLED);
        sliderTrackStyle =
            SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        context = getContext(c, Region.SLIDER_THUMB, ENABLED);
        sliderThumbStyle =
            SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

   /**
	* Gets the display box. The display box is defined as the union
	* of the contained figures.
	*/
	public Rectangle displayBox() {
		FigureEnumeration fe = figures();
		Rectangle r = fe.nextFigure().displayBox();

		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	public void basicDisplayBox(Point origin, Point corner) {
		// do nothing
		// we could transform all components proportionally
	}

	public FigureEnumeration decompose() {
		return new FigureEnumerator(fFigures);
	}

   /**
	* Gets the handles for the GroupFigure.
	*/
	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		handles.add(new GroupHandle(this, RelativeLocator.northWest()));
		handles.add(new GroupHandle(this, RelativeLocator.northEast()));
		handles.add(new GroupHandle(this, RelativeLocator.southWest()));
		handles.add(new GroupHandle(this, RelativeLocator.southEast()));
		return new HandleEnumerator(handles);
	}

   /**
	* Sets the attribute of all the contained figures.
	*/
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(name, value);
		}
	}
}
