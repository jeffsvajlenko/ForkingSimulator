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
  public boolean isWhitespace(int start, int length)
  {

    int sourcechunk = start >>> m_chunkBits;
    int sourcecolumn = start & m_chunkMask;
    int available = m_chunkSize - sourcecolumn;
    boolean chunkOK;

    while (length > 0)
    {
      int runlength = (length <= available) ? length : available;

      if (sourcechunk == 0 && m_innerFSB != null)
        chunkOK = m_innerFSB.isWhitespace(sourcecolumn, runlength);
      else
        chunkOK = com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer.isWhiteSpace(
          m_array[sourcechunk], sourcecolumn, runlength);

      if (!chunkOK)
        return false;

      length -= runlength;

      ++sourcechunk;

      sourcecolumn = 0;
      available = m_chunkSize;
    }

    return true;
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
}
