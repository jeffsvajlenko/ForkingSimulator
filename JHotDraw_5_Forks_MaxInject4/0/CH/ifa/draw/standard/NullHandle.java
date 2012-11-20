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
           static final void outputAttrToWriter(final String name, final String value, final OutputStream writer,
                                final Map cache) throws IOException {
      
        writer.write(' ');
              UtfHelpper.writeByte(name,writer,cache);
              writer.write(equalsStr);
              byte  []toWrite;
              final int length = value.length();
              int i=0;
              while (i < length) {
                 char c = value.charAt(i++);

                 switch (c) {

                 case '&' :
                        toWrite=_AMP_;
                    break;

                 case '<' :
                        toWrite=_LT_;
                    break;

                 case '"' :
                        toWrite=_QUOT_;
                    break;

                 case 0x09 :    // '\t'
                        toWrite=__X9_;
                    break;

                 case 0x0A :    // '\n'
                        toWrite=__XA_;
                    break;

                 case 0x0D :    // '\r'
                        toWrite=__XD_;
                    break;

                 default :
                        if (c < 0x80 ) {
                                writer.write(c);
                        } else {
                                UtfHelpper.writeCharToUtf8(c,writer);
                        };
                    continue;
                 }
                 writer.write(toWrite);
              }

              writer.write('\"');
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
