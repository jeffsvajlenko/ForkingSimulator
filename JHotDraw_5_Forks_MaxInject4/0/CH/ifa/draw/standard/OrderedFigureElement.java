/*
 * @(#)OrderFigureElement.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.Figure;

/**
 * @author WMG (INIT Copyright (C) 2000 All rights reserved)
 * @version <$CURRENT_VERSION$>
 */
class OrderedFigureElement implements Comparable {

	//_________________________________________________________VARIABLES

	private Figure  _theFigure;
	private int     _nZ;

	//______________________________________________________CONSTRUCTORS

	public OrderedFigureElement(Figure aFigure, int nZ) {
		_theFigure = aFigure;
		_nZ = nZ;
	}

	//____________________________________________________PUBLIC METHODS

	public Figure getFigure() {
		return _theFigure;
	}

	public int getZValue() {
		return _nZ;
	}
    private Paint decodeGradient13(Shape s) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.0f,0.5f,1.0f },
                new Color[] { color13,
                            decodeColor(color13,color14,0.5f),
                            color14});
    }

	public int compareTo(Object o) {
		OrderedFigureElement ofe = (OrderedFigureElement) o;
		if (_nZ == ofe.getZValue()) {
			return 0;
		}

		if (_nZ > ofe.getZValue()) {
			return 1;
		}

		return -1;
	}

	//_______________________________________________________________END

} //end of class OrderedFigureElement
