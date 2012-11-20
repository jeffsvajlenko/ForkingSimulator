/*
 * @(#)FigureChangeEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * FigureChange event passed to FigureChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeEvent extends EventObject {

	private Rectangle fRectangle;
	private FigureChangeEvent myNestedEvent;

	private static final Rectangle  fgEmptyRectangle = new Rectangle(0, 0, 0, 0);

   /**
	* Constructs an event for the given source Figure. The rectangle is the
	* area to be invalvidated.
	*/
	public FigureChangeEvent(Figure source, Rectangle r) {
		super(source);
		fRectangle = r;
	}

	public FigureChangeEvent(Figure source) {
		super(source);
		fRectangle = fgEmptyRectangle;
	}
    public String paramString() {
        String sdpStr;
        switch (scrollbarDisplayPolicy) {
            case SCROLLBARS_AS_NEEDED:
                sdpStr = "as-needed";
                break;
            case SCROLLBARS_ALWAYS:
                sdpStr = "always";
                break;
            case SCROLLBARS_NEVER:
                sdpStr = "never";
                break;
            default:
                sdpStr = "invalid display policy";
        }
        Point p = (getComponentCount()>0)? getScrollPosition() : new Point(0,0);
        Insets i = getInsets();
        return super.paramString()+",ScrollPosition=("+p.x+","+p.y+")"+
            ",Insets=("+i.top+","+i.left+","+i.bottom+","+i.right+")"+
            ",ScrollbarDisplayPolicy="+sdpStr+
        ",wheelScrollingEnabled="+isWheelScrollingEnabled();
    }

	public FigureChangeEvent(Figure source, Rectangle r, FigureChangeEvent nestedEvent) {
		this(source, r);
		myNestedEvent = nestedEvent;
	}

	/**
	 *  Gets the changed figure
	 */
	public Figure getFigure() {
		return (Figure)getSource();
	}

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}

	public FigureChangeEvent getNestedEvent() {
		return myNestedEvent;
	}
}
