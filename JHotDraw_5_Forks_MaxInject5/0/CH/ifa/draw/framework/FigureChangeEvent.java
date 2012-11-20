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
    private void initElementsFromCollection(Collection<? extends E> c) {
        Object[] a = c.toArray();
        // If c.toArray incorrectly doesn't return Object[], copy it.
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf(a, a.length, Object[].class);
        int len = a.length;
        if (len == 1 || this.comparator != null)
            for (int i = 0; i < len; i++)
                if (a[i] == null)
                    throw new NullPointerException();
        this.queue = a;
        this.size = a.length;
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
