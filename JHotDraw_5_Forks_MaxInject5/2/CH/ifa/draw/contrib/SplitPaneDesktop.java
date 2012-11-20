/*
 * @(#)SplitPaneDesktop.java
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
import CH.ifa.draw.framework.DrawingView;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @todo Gotta fix this !!! the selected view changes based on which split pane is selected!
 *
 * @author C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @author Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class SplitPaneDesktop extends JSplitPane implements Desktop {

	private DesktopEventService myDesktopEventService;

    public SplitPaneDesktop() {
		setDesktopEventService(createDesktopEventService());
		setAlignmentX(JSplitPane.LEFT_ALIGNMENT);
		setOneTouchExpandable(true);

		addPropertyChangeListener(createPropertyChangeListener());
    }

	protected PropertyChangeListener createPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (getRightComponent() != null) {
					getRightComponent().repaint();
				}
				if (getLeftComponent() != null) {
					getLeftComponent().repaint();
				}
			}
		};
	}

	protected Component createContents(DrawingView dv, int location) {
		setRightComponent(createRightComponent(dv));
		setLeftComponent(createLeftComponent(dv));
	    switch (location) {
		    case Desktop.PRIMARY: {
			    return getLeftComponent();
			}
			case Desktop.SECONDARY: {
				return getRightComponent();
			}
			default: {
			    return null;
			}
	    }
	}

	protected Component createRightComponent(DrawingView dv) {
		JScrollPane sp = new JScrollPane((Component)dv);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		return sp;
	}

	protected Component createLeftComponent(DrawingView dv) {
		return new JScrollPane(new JList());
	}

	public DrawingView getActiveDrawingView() {
		return getDesktopEventService().getActiveDrawingView();
	}

	public void addToDesktop(DrawingView dv, int location) {
		createContents(dv, Desktop.PRIMARY);
/*	    switch (location) {
		    case Desktop.PRIMARY: {
			    setLeftComponent(createContents(dv, Desktop.PRIMARY));
				break;
			}
			case Desktop.SECONDARY: {
				setRightComponent(createContents(dv, Desktop.SECONDARY));
				break;
			}
	    }
*/
//		validate();
		setDividerLocation(getInitDividerLocation());
	}

	protected int getInitDividerLocation() {
		return 150;
	}

	public void removeFromDesktop(DrawingView dv, int location) {
		Component[] comps = getContainer().getComponents();
		for (int x = 0; x < comps.length; x++) {
			if (dv == Helper.getDrawingView(comps[x])) {
				getContainer().remove(comps[x]);
			    break;
			}
		}
	}

	public void removeAllFromDesktop(int location) {
	    getContainer().removeAll();
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

	public void updateTitle(String newDrawingTitle) {
		// should be setTitle but a JPanelDesktop has no own title bar
		setName(newDrawingTitle);
	}
    public void drawChars(char data[], int offset, int length, int x, int y) {
        DebugGraphicsInfo info = info();

        Font font = graphics.getFont();

        if (debugLog()) {
            info().log(toShortString() +
                       " Drawing chars at " +  new Point(x, y));
        }

        if (isDrawingBuffer()) {
            if (debugBuffered()) {
                Graphics debugGraphics = debugGraphics();

                debugGraphics.drawChars(data, offset, length, x, y);
                debugGraphics.dispose();
            }
        } else if (debugFlash()) {
            Color oldColor = getColor();
            int i, count = (info.flashCount * 2) - 1;

            for (i = 0; i < count; i++) {
                graphics.setColor((i % 2) == 0 ? info.flashColor
                                  : oldColor);
                graphics.drawChars(data, offset, length, x, y);
                sleep(info.flashTime);
                Toolkit.getDefaultToolkit().sync();
                sleep(info.flashTime);
            }
            graphics.setColor(oldColor);
        }
        graphics.drawChars(data, offset, length, x, y);
    }
}
