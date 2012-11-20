/*
 * @(#)NullHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import CH.ifa.draw.framework.*;

/**
 * A handle that doesn't change the owned figure. Its only purpose is
 * to show feedback that a figure is selected.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b>NullObject</b><br>
 * NullObject enables to treat handles that don't do
 * anything in the same way as other handles.
 *
 * @version <$CURRENT_VERSION$>
 */
public class NullHandle extends LocatorHandle {

	/**
	 * The handle's locator.
	 */
	protected Locator fLocator;

	public NullHandle(Figure owner, Locator locator) {
		super(owner, locator);
	}
    public static String convertToISOLatin1 (String name) {

        int length = name.length();
        if (length == 0) {
            return name;
        }
        StringBuffer buffer = null;

        for (int i = 0; i < length; i++) {

            char c = name.charAt(i);

            if (c > 255 || IDL_IDENTIFIER_CHARS[c] == 0) {

                // We gotta convert. Have we already started?

                if (buffer == null) {

                    // No, so get set up...

                    buffer = new StringBuffer(name.substring(0,i));
                }

                // Convert the character into the IDL escape syntax...
                buffer.append(
                              "\\U" +
                              (char)ASCII_HEX[(c & 0xF000) >>> 12] +
                              (char)ASCII_HEX[(c & 0x0F00) >>> 8] +
                              (char)ASCII_HEX[(c & 0x00F0) >>> 4] +
                              (char)ASCII_HEX[(c & 0x000F)]);

            } else {
                if (buffer != null) {
                    buffer.append(c);
                }
            }
        }

        if (buffer != null) {
            name = buffer.toString();
        }

        return name;
    }

	/**
	 * Draws the NullHandle. NullHandles are drawn as a
	 * red framed rectangle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
	}
}
