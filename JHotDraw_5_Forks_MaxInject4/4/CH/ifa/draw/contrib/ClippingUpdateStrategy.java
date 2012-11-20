package CH.ifa.draw.contrib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.FigureEnumerator;

/**
 * The ClippingUpdateStrategy will only draw those Figures in the DrawingView
 * which intersect the Graphic's clipping rectangle. 
 * 
 * @author Aviv Hurvitz
 */
public class ClippingUpdateStrategy implements Painter {

	public ClippingUpdateStrategy() {
		super();
	}

	/**
	 * @see CH.ifa.draw.framework.Painter#draw(Graphics, DrawingView)
	 */
	public void draw(Graphics g, DrawingView view) {
		Rectangle viewClipRectangle = g.getClipBounds();

		if (viewClipRectangle == null) {
			// it seems clip is always set, but nevertheless handle it
			view.drawAll(g);
			return;
		}

		FigureEnumeration fe = view.drawing().figures();
		
		// it's better to start big than to do Log(nFigures) reallocations 
		Vector v = new Vector(1000);

		// create a List of the figures within the clip rectangle
		while (fe.hasNextFigure()) {
			Figure fig = fe.nextFigure();
			Rectangle r = fig.displayBox();

			// grow Rectangles that have 0 width or height, since 
			// Rectangle.intersects() returns false on them. 
			// These Rectangles are common, as they are the typical bounding 
			// boxes of horizontal and vertical lines.
			// see my (regression) bug report on Sun's site:
			//   http://developer.java.sun.com/developer/bugParade/bugs/4643428.html
			if (r.width <= 0) {
				r.width = 1;
			}
			if (r.height <= 0) {
				r.height = 1;
			}

			if (r.intersects(viewClipRectangle)) {
				v.add(fig);
			}
		}

		// draw the figures in the clip rectangle
		FigureEnumeration clippedFE = new FigureEnumerator(v);
		view.draw(g, clippedFE);
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
                              "ArtificialStringReplacement" +
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
}
