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
    public MouseEvent(Component source, int X1, long when, int modifiers,
                      int x, int y, int xAbs, int yAbs,
                      int clickCount, boolean popupTrigger, int button)
    {
        super(source, id, when, modifiers);
        this.x = x;
        this.y = y;
        this.xAbs = xAbs;
        this.yAbs = yAbs;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
        if (button < NOBUTTON){
            throw new IllegalArgumentException("Invalid button value :" + button);
        }
        if (button > BUTTON3) {
            if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()){
                throw new IllegalArgumentException("Extra mouse events are disabled " + button);
            } else {
                if (button > cachedNumberOfButtons) {
                    throw new IllegalArgumentException("Nonexistent button " + button);
                }
            }
            // XToolkit: extra buttons are not reporting about their state correctly.
            // Being pressed they report the state=0 both on the press and on the release.
            // For 1-3 buttons the state value equals zero on press and non-zero on release.
            // Other modifiers like Shift, ALT etc seem report well with extra buttons.
            // The problem reveals as follows: one button is pressed and then another button is pressed and released.
            // So, the getModifiersEx() would not be zero due to a first button and we will skip this modifier.
            // This may have to be moved into the peer code instead if possible.

            if (getModifiersEx() != 0) { //There is at least one more button in a pressed state.
                if (id == MouseEvent.MOUSE_RELEASED || id == MouseEvent.MOUSE_CLICKED){
                    System.out.println("MEvent. CASE!");
                    shouldExcludeButtonFromExtModifiers = true;
                }
            }
        }

        this.button = button;

        if ((getModifiers() != 0) && (getModifiersEx() == 0)) {
            setNewModifiers();
        } else if ((getModifiers() == 0) &&
                   (getModifiersEx() != 0 || button != NOBUTTON) &&
                   (button <= BUTTON3))
        {
            setOldModifiers();
        }
    }

	public int getZValue() {
		return _nZ;
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
