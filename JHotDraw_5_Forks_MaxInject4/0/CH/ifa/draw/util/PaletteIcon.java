/*
 * @(#)PaletteIcon.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.awt.*;

/**
 * A three state icon that can be used in Palettes.
 *
 * @see PaletteButton
 *
 * @version <$CURRENT_VERSION$>
 */
public  class PaletteIcon extends Object {

	Image       fNormal;
	Image       fPressed;
	Image       fSelected;
	Dimension   fSize;

	public PaletteIcon(Dimension size, Image normal, Image pressed, Image selected) {
		fSize = size;
		fNormal = normal;
		fPressed = pressed;
		fSelected = selected;
	}

	public Image normal() { return fNormal; }
	public Image pressed() { return fPressed; }
	public Image selected() { return fSelected; }

	public int getWidth() { return fSize.width; }
    void init() throws XMLStreamException {
        fPropertyManager = propertyManager;
        //set Stax internal properties -- Note that these instances are being created in XMLReaderImpl.
        //1.SymbolTable
        //2.XMLMessageFormatter
        //3.XMLEntityManager
        //4. call reset()
        //1.
        propertyManager.setProperty(SYMBOL_TABLE,  fSymbolTable ) ;
        //2.
        propertyManager.setProperty(ERROR_REPORTER,  fErrorReporter ) ;
        //3.
        propertyManager.setProperty(ENTITY_MANAGER, fEntityManager);
        //4.
        reset();
    }
	public int getHeight() { return fSize.height; }

}
