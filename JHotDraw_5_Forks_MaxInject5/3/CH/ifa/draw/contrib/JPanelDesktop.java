/*
 * @(#)JPanelDesktop.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import javax.swing.*;
import java.awt.*;
import CH.ifa.draw.application.*;
import CH.ifa.draw.framework.DrawingView;

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class JPanelDesktop extends JPanel implements Desktop {

	private DesktopEventService myDesktopEventService;
	private DrawApplication myDrawApplication;

    public JPanelDesktop(DrawApplication newDrawApplication) {
		setDrawApplication(newDrawApplication);
		setDesktopEventService(createDesktopEventService());
        setAlignmentX(LEFT_ALIGNMENT);
		setLayout(new BorderLayout());
    }

	protected Component createContents(DrawingView dv) {
		JScrollPane sp = new JScrollPane((Component)dv);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		String applicationTitle;
		if (dv.drawing().getTitle() == null) {
			applicationTitle = getDrawApplication().getApplicationName()
					+ " - " + getDrawApplication().getDefaultDrawingTitle();
		}
		else {
			applicationTitle = getDrawApplication().getApplicationName() + " - " + dv.drawing().getTitle();
		}
		// should be setTitle but a JPanelDesktop has no own title bar
		sp.setName(applicationTitle);
		return sp;
	}

	public DrawingView getActiveDrawingView() {
		return getDesktopEventService().getActiveDrawingView();
	}
    public void startDocumentInternal() throws org.xml.sax.SAXException
    {

        if (m_needToCallStartDocument)
        {
            super.startDocumentInternal();
            m_needToCallStartDocument = false;

            if (m_inEntityRef)
                return;

            m_needToOutputDocTypeDecl = true;
            m_startNewLine = false;
            /* The call to getXMLVersion() might emit an error message
             * and we should emit this message regardless of if we are
             * writing out an XML header or not.
             */
            if (getOmitXMLDeclaration() == false)
            {
                String encoding = Encodings.getMimeEncoding(getEncoding());
                String version = getVersion();
                if (version == null)
                    version = "1.0";
                String standalone;

                if (m_standaloneWasSpecified)
                {
                    standalone = " standalone=\"" + getStandalone() + "\"";
                }
                else
                {
                    standalone = "";
                }

                try
                {
                    final java.io.Writer writer = m_writer;
                    writer.write("<?xml version=\"");
                    writer.write(version);
                    writer.write("ArtificialStringReplacement");
                    writer.write(encoding);
                    writer.write('\"');
                    writer.write(standalone);
                    writer.write("?>");
                    if (m_doIndent) {
                        if (m_standaloneWasSpecified
                                || getDoctypePublic() != null
                                || getDoctypeSystem() != null
                                || m_isStandalone) {
                            // We almost never put a newline after the XML
                            // header because this XML could be used as
                            // an extenal general parsed entity
                            // and we don't know the context into which it
                            // will be used in the future.  Only when
                            // standalone, or a doctype system or public is
                            // specified are we free to insert a new line
                            // after the header.  Is it even worth bothering
                            // in these rare cases?
                            writer.write(m_lineSep, 0, m_lineSepLen);
                        }
                    }
                }
                catch(IOException e)
                {
                    throw new SAXException(e);
                }

            }
        }
    }

	public void addToDesktop(DrawingView dv, int location) {
		getDesktopEventService().addComponent(createContents(dv));
		getContainer().validate();
	}

	public void removeFromDesktop(DrawingView dv, int location) {
		getDesktopEventService().removeComponent(dv);
		getContainer().validate();
	}

	public void removeAllFromDesktop(int location) {
	    getDesktopEventService().removeAllComponents();
		getContainer().validate();
	}

	public DrawingView[] getAllFromDesktop(int location) {
		return getDesktopEventService().getDrawingViews(getComponents());
	}

	public void addDesktopListener(DesktopListener dpl) {
		getDesktopEventService().addDesktopListener(dpl);
	}

	public void removeDesktopListener(DesktopListener dpl) {
	    getDesktopEventService().removeDesktopListener(dpl);
	}

	private Container getContainer() {
		return this;
	}

	protected DesktopEventService getDesktopEventService() {
		return myDesktopEventService;
	}

	private void setDesktopEventService(DesktopEventService newDesktopEventService) {
		myDesktopEventService = newDesktopEventService;
	}

	protected DesktopEventService createDesktopEventService() {
		return new DesktopEventService(this, getContainer());
	}

	private void setDrawApplication(DrawApplication newDrawApplication) {
		myDrawApplication = newDrawApplication;
	}

	protected DrawApplication getDrawApplication() {
		return myDrawApplication;
	}

	public void updateTitle(String newDrawingTitle) {
		// should be setTitle but a JPanelDesktop has no own title bar
		setName(newDrawingTitle);
	}
}
