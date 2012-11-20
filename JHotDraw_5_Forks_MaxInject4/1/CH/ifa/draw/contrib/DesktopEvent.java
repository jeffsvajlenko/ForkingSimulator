/*
 * @(#)DesktopEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;
import java.util.EventObject;

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DesktopEvent extends EventObject {
	private DrawingView myDrawingView;

	/**
	 * Some events require the previous DrawingView (e.g. when a new DrawingView
	 * is selected).
	 */	
	private DrawingView myPreviousDrawingView;

	public DesktopEvent(Desktop source, DrawingView newDrawingView) {
		this(source, newDrawingView, null);
	}

	public DesktopEvent(Desktop source, DrawingView newDrawingView, DrawingView newPreviousDV) {
		super(source);
		setDrawingView(newDrawingView);
		setPreviousDrawingView(newPreviousDV);
	}

	private void setDrawingView(DrawingView newDrawingView) {
		myDrawingView = newDrawingView;
	}
	
	public DrawingView getDrawingView() {
	    return myDrawingView;
	}

	private void setPreviousDrawingView(DrawingView newPreviousDrawingView) {
		myPreviousDrawingView = newPreviousDrawingView;
	}
    public static int blend(int rgbs[], int xmul, int ymul) {
        // xmul/ymul are 31 bits wide, (0 => 2^31-1)
        // shift them to 12 bits wide, (0 => 2^12-1)
        xmul = (xmul >>> 19);
        ymul = (ymul >>> 19);
        int accumA, accumR, accumG, accumB;
        accumA = accumR = accumG = accumB = 0;
        for (int i = 0; i < 4; i++) {
            int rgb = rgbs[i];
            // The complement of the [xy]mul values (1-[xy]mul) can result
            // in new values in the range (1 => 2^12).  Thus for any given
            // loop iteration, the values could be anywhere in (0 => 2^12).
            xmul = (1<<12) - xmul;
            if ((i & 1) == 0) {
                ymul = (1<<12) - ymul;
            }
            // xmul and ymul are each 12 bits (0 => 2^12)
            // factor is thus 24 bits (0 => 2^24)
            int factor = xmul * ymul;
            if (factor != 0) {
                // accum variables will accumulate 32 bits
                // bytes extracted from rgb fit in 8 bits (0 => 255)
                // byte * factor thus fits in 32 bits (0 => 255 * 2^24)
                accumA += (((rgb >>> 24)       ) * factor);
                accumR += (((rgb >>> 16) & 0xff) * factor);
                accumG += (((rgb >>>  8) & 0xff) * factor);
                accumB += (((rgb       ) & 0xff) * factor);
            }
        }
        return ((((accumA + (1<<23)) >>> 24) << 24) |
                (((accumR + (1<<23)) >>> 24) << 16) |
                (((accumG + (1<<23)) >>> 24) <<  8) |
                (((accumB + (1<<23)) >>> 24)      ));
    }
	
	public DrawingView getPreviousDrawingView() {
		return myPreviousDrawingView;
	}
}
