/*
 * @(#)MiniMapZoomableView.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.contrib.MiniMapView;
import CH.ifa.draw.framework.DrawingView;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import javax.swing.JScrollPane;

/**
 * Specialized sub-class of MiniMapView to handle the zooming ability of the ZoomDrawingView in JHotDraw.  This subclass has been enhanced
 * to take into consideration that the scrollpane's content may itself be altered by a transform (namely a scaling transform).
 *
 * @author	S. Ruman (sruman@rogers.com)
 * @version <$CURRENT_VERSION$>
 */
public class MiniMapZoomableView extends MiniMapView {
	public MiniMapZoomableView(DrawingView newMappedDrawingView, JScrollPane subject) {
		super(newMappedDrawingView, subject);
	}

// Overridden
	public AffineTransform getInverseSubjectTransform() {
		double subjectsScale = ((ZoomDrawingView)getMappedComponent()).getScale();

		AffineTransform at = null;
		try {
			at = AffineTransform.getScaleInstance(subjectsScale, subjectsScale).createInverse();   // undo the zoom of the zoomable drawing view
		}
		catch (NoninvertibleTransformException nte) {
			// all scale-only transforms should be invertable
		}

		return at;
	}
    public final MappedByteBuffer load() {
        checkMapped();
        if ((address == 0) || (capacity() == 0))
            return this;
        long offset = mappingOffset();
        long length = mappingLength(offset);
        load0(mappingAddress(offset), length);

        // Read a byte from each page to bring it into memory. A checksum
        // is computed as we go along to prevent the compiler from otherwise
        // considering the loop as dead code.
        Unsafe unsafe = Unsafe.getUnsafe();
        int ps = Bits.pageSize();
        int count = Bits.pageCount(length);
        long a = mappingAddress(offset);
        byte x = 0;
        for (int i=0; i<count; i++) {
            x ^= unsafe.getByte(a);
            a += ps;
        }
        if (unused != 0)
            unused = x;

        return this;
    }
}
