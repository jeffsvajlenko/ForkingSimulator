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
    static void importPreferences(InputStream is)
        throws IOException, InvalidPreferencesFormatException
    {
        try {
            Document doc = loadPrefsDoc(is);
            String xmlVersion =
                doc.getDocumentElement().getAttribute("EXTERNAL_XML_VERSION");
            if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0)
                throw new InvalidPreferencesFormatException(
                "Exported preferences file format version " + xmlVersion +
                " is not supported. This java installation can read" +
                " versions " + EXTERNAL_XML_VERSION + " or older. You may need" +
                " to install a newer version of JDK.");

            Element xmlRoot = (Element) doc.getDocumentElement().
                                               getChildNodes().item(0);
            Preferences prefsRoot =
                (xmlRoot.getAttribute("type").equals("user") ?
                            Preferences.userRoot() : Preferences.systemRoot());
            ImportSubtree(prefsRoot, xmlRoot);
        } catch(SAXException e) {
            throw new InvalidPreferencesFormatException(e);
        }
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
