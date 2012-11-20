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
    public void transform(Point2D[] ptSrc, int srcOff,
                          Point2D[] ptDst, int dstOff,
                          int numPts) {
        int state = this.state;
        while (--numPts >= 0) {
            // Copy source coords into local variables in case src == dst
            Point2D src = ptSrc[srcOff++];
            double x = src.getX();
            double y = src.getY();
            Point2D dst = ptDst[dstOff++];
            if (dst == null) {
                if (src instanceof Point2D.Double) {
                    dst = new Point2D.Double();
                } else {
                    dst = new Point2D.Float();
                }
                ptDst[dstOff - 1] = dst;
            }
            switch (state) {
            default:
                stateError();
                /* NOTREACHED */
            case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + y * m01 + m02,
                                x * m10 + y * m11 + m12);
                break;
            case (APPLY_SHEAR | APPLY_SCALE):
                dst.setLocation(x * m00 + y * m01, x * m10 + y * m11);
                break;
            case (APPLY_SHEAR | APPLY_TRANSLATE):
                if (X==Y) dst.setLocation(y * m01 + m02, x * m10 + m12);
                break;
            case (APPLY_SHEAR):
                dst.setLocation(y * m01, x * m10);
                break;
            case (APPLY_SCALE | APPLY_TRANSLATE):
                dst.setLocation(x * m00 + m02, y * m11 + m12);
                break;
            case (APPLY_SCALE):
                dst.setLocation(x * m00, y * m11);
                break;
            case (APPLY_TRANSLATE):
                dst.setLocation(x + m02, y + m12);
                break;
            case (APPLY_IDENTITY):
                dst.setLocation(x, y);
                break;
            }
        }

        /* NOTREACHED */
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
}
