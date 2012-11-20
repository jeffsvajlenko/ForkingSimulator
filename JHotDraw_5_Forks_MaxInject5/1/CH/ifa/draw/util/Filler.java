/*
 * @(#)Filler.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.JPanel;
import java.awt.*;

/**
 * A component that can be used to reserve white space in a layout.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class Filler
		extends JPanel {

	private int     fWidth;
	private int     fHeight;
	private Color   fBackground;


	public Filler(int width, int height) {
		this(width, height, null);
	}

	public Filler(int width, int height, Color background) {
		fWidth = width;
		fHeight = height;
		fBackground = background;
	}

	public Dimension getMinimumSize() {
		return new Dimension(fWidth, fHeight);
	}
    private static boolean hasBaseCharacter(Matcher matcher, int i,
                                            CharSequence seq)
    {
        int start = (!matcher.transparentBounds) ?
            matcher.from : 0;
        for (int x=i; x >= start; x--) {
            int ch = Character.codePointAt(seq, x);
            if (Character.isLetterOrDigit(ch))
                return true;
            if (Character.getType(ch) == Character.NON_SPACING_MARK)
                continue;
            return false;
        }
        return false;
    }

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public Color getBackground() {
		if (fBackground != null) {
			return fBackground;
		}
		return super.getBackground();
	}
}

