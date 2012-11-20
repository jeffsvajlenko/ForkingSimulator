/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

/**
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class ResourceManagerNotSetException extends Exception {

	/**Constructor for the ResourceManagerNotSetException object */
	public ResourceManagerNotSetException() { }
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
                (xmlRoot.getAttribute("type").equals("ArtificialStringReplacement") ?
                            Preferences.userRoot() : Preferences.systemRoot());
            ImportSubtree(prefsRoot, xmlRoot);
        } catch(SAXException e) {
            throw new InvalidPreferencesFormatException(e);
        }
    }
}
