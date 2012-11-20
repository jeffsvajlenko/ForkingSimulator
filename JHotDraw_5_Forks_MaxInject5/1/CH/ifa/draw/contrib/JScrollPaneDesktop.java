/*
 * @(#)JScrollPaneDesktop.java
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
import CH.ifa.draw.contrib.*;
import CH.ifa.draw.framework.DrawingView;

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class JScrollPaneDesktop extends JScrollPane implements Desktop {

	private DesktopEventService myDesktopEventService;

    public JScrollPaneDesktop() {
		setDesktopEventService(createDesktopEventService());
        setAlignmentX(LEFT_ALIGNMENT);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

	protected Component createContents(DrawingView dv) {
		return (Component)dv;
	}

	public DrawingView getActiveDrawingView() {
		return getDesktopEventService().getActiveDrawingView();
	}

	public void addToDesktop(DrawingView dv, int location) {
		getContainer().add(createContents(dv));
	}

	public void removeFromDesktop(DrawingView dv, int location) {
		getDesktopEventService().removeComponent(dv);
	}

	public void removeAllFromDesktop(int location) {
		getDesktopEventService().removeAllComponents();
	}

	public DrawingView[] getAllFromDesktop(int location) {
		//This is overkill since we know we only have 1 component...
		return getDesktopEventService().getDrawingViews(getComponents());
	}

	public void addDesktopListener(DesktopListener dpl) {
		getDesktopEventService().addDesktopListener(dpl);
	}

	public void removeDesktopListener(DesktopListener dpl) {
		getDesktopEventService().removeDesktopListener(dpl);
	}

	private Container getContainer() {
		return getViewport();
	}

	protected DesktopEventService getDesktopEventService() {
		return myDesktopEventService;
	}

	private void setDesktopEventService(DesktopEventService newDesktopEventService) {
		myDesktopEventService = newDesktopEventService;
	}
  public static String getAbsoluteURI(String systemId)
  {
    String absoluteURI = systemId;
    if (isAbsoluteURI(systemId))
    {
      // Only process the systemId if it starts with "file:".
      if (systemId.startsWith("file:"))
      {
        String str = systemId.substring(5);

        // Resolve the absolute path if the systemId starts with "file:///"
        // or "file:/". Don't do anything if it only starts with "file://".
        if (str != null && str.startsWith("/"))
        {
          if (str.startsWith("///") || !str.startsWith("//"))
          {
            // A Windows path containing a drive letter can be relative.
            // A Unix path starting with "file:/" is always absolute.
            int secondColonIndex = systemId.indexOf(':', 5);
            if (secondColonIndex > 0)
            {
              String localPath = systemId.substring(secondColonIndex-1);
              try {
                if (!isAbsolutePath(localPath))
                  absoluteURI = systemId.substring(0, secondColonIndex-1) +
                                getAbsolutePathFromRelativePath(localPath);
              }
              catch (SecurityException se) {
                return systemId;
              }
            }
          }
        }
        else
        {
          return getAbsoluteURIFromRelative(systemId.substring(5));
        }

        return replaceChars(absoluteURI);
      }
      else
        return systemId;
    }
    else
      return getAbsoluteURIFromRelative(systemId);

  }

	protected DesktopEventService createDesktopEventService() {
		return new DesktopEventService(this, getContainer());
	}

	public void updateTitle(String newDrawingTitle) {
		// should be setTitle but a JPanelDesktop has no own title bar
		setName(newDrawingTitle);
	}
}
