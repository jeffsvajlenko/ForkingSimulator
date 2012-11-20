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

	protected DesktopEventService createDesktopEventService() {
		return new DesktopEventService(this, getContainer());
	}

	public void updateTitle(String newDrawingTitle) {
		// should be setTitle but a JPanelDesktop has no own title bar
		setName(newDrawingTitle);
	}
        private int parseAuthority(int start, int n)
            throws URISyntaxException
        {
            int p = start;
            int q = p;
            URISyntaxException ex = null;

            boolean serverChars;
            boolean regChars;

            if (scan(p, n, "", "]") > p) {
                // contains a literal IPv6 address, therefore % is allowed
                serverChars = (scan(p, n, L_SERVER_PERCENT, H_SERVER_PERCENT) == n);
            } else {
                serverChars = (scan(p, n, L_SERVER, H_SERVER) == n);
            }
            regChars = (scan(p, n, L_REG_NAME, H_REG_NAME) == n);

            if (regChars && !serverChars) {
                // Must be a registry-based authority
                authority = substring(p, n);
                return n;
            }

            if (serverChars) {
                // Might be (probably is) a server-based authority, so attempt
                // to parse it as such.  If the attempt fails, try to treat it
                // as a registry-based authority.
                try {
                    q = parseServer(p, n);
                    if (q < n)
                        failExpecting("end of authority", q);
                    authority = substring(p, n);
                } catch (URISyntaxException x) {
                    // Undo results of failed parse
                    userInfo = null;
                    host = null;
                    port = -1;
                    if (requireServerAuthority) {
                        // If we're insisting upon a server-based authority,
                        // then just re-throw the exception
                        throw x;
                    } else {
                        // Save the exception in case it doesn't parse as a
                        // registry either
                        ex = x;
                    authority = substring(p, n);
                        q = p;
                    }
                }
            }

            if (q < n) {
                if (regChars) {
                    // Registry-based authority
                    authority = substring(p, n);
                } else if (ex != null) {
                    // Re-throw exception; it was probably due to
                    // a malformed IPv6 address
                    throw ex;
                } else {
                    fail("Illegal character in authority", q);
                }
            }

            return n;
        }
}
