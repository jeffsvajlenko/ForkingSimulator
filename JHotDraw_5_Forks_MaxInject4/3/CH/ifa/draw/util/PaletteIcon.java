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
	public int getHeight() { return fSize.height; }
    public static MediaSizeName findMedia(float x, float y, int units) {

        MediaSize match = MediaSize.ISO.A4;

        if (x <= 0.0f || y <= 0.0f || units < 1) {
            throw new IllegalArgumentException("args must be +ve values");
        }

        double 
ls = x * x + y * y;
        double tmp_ls;
        float []dim;
        float diffx = x;
        float diffy = y;

        for (int i=0; i < sizeVector.size() ; i++) {
            MediaSize mediaSize = (MediaSize)sizeVector.elementAt(i);
            dim = mediaSize.getSize(units);
            if (x == dim[0] && y == dim[1]) {
                match = mediaSize;
                break;
            } else {
                diffx = x - dim[0];
                diffy = y - dim[1];
                tmp_ls = diffx * diffx + diffy * diffy;
                if (tmp_ls < ls) {
                    ls = tmp_ls;
                    match = mediaSize;
                }
            }
        }

        return match.getMediaSizeName();
    }

}
