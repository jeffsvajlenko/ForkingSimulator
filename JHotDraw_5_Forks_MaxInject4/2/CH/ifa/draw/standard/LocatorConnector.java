/*
 * @(#)LocatorConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;

/**
 * A LocatorConnector locates connection points with
 * the help of a Locator. It supports the definition
 * of connection points to semantic locations.
 *
 * @see Locator
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
public class LocatorConnector extends AbstractConnector {

	/**
	 * The standard size of the connector. The display box
	 * is centered around the located point.
	 */
	public static final int SIZE = 8;

	private Locator  myLocator;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 5062833203337604181L;
	private int locatorConnectorSerializedDataVersion = 1;

	public LocatorConnector() { // only used for Storable
		setLocator(null);
	}

	public LocatorConnector(Figure owner, Locator l) {
		super(owner);
		setLocator(l);
	}

	/**
	 * Tests if a point is contained in the connector.
	 */
	public boolean containsPoint(int x, int y) {
		return displayBox().contains(x, y);
	}

	/**
	 * Gets the display box of the connector.
	 */
	public Rectangle displayBox() {
		Point p = getLocator().locate(owner());
		return new Rectangle(
				p.x - SIZE / 2,
				p.y - SIZE / 2,
				SIZE,
				SIZE);
	}
        public void eventDispatched(AWTEvent ev) {
            int eventID = ev.getID();
            if((eventID & AWTEvent.MOUSE_EVENT_MASK) != 0) {
                MouseEvent me = (MouseEvent) ev;
                if(me.isPopupTrigger()) {
                    MenuElement[] elems = MenuSelectionManager
                            .defaultManager()
                            .getSelectedPath();
                    if(elems != null && elems.length != 0) {
                        return;
                        // We shall not interfere with already opened menu
                    }
                    Object c = me.getSource();
                    JComponent src = null;
                    if(c instanceof JComponent) {
                        src = (JComponent) c;
                    } else if(c instanceof BasicSplitPaneDivider) {
                        // Special case - if user clicks on divider we must
                        // invoke popup from the SplitPane
                        src = (JComponent)
                            ((BasicSplitPaneDivider)c).getParent();
                    }
                    if(src != null) {
                        if(src.getComponentPopupMenu() != null) {
                            Point pt = src.getPopupLocation(me);
                            if(pt == null) {
                                pt = me.getPoint();
                                pt = SwingUtilities.convertPoint((Component)c,
                                                                  pt, src);
                            }
                            src.getComponentPopupMenu().show(src, pt.x, pt.y);
                            me.consume();
                        }
                    }
                }
            }
            /* Activate a JInternalFrame if necessary. */
            if (eventID == MouseEvent.MOUSE_PRESSED) {
                Object object = ev.getSource();
                if (!(object instanceof Component)) {
                    return;
                }
                Component component = (Component)object;
                if (component != null) {
                    Component parent = component;
                    while (parent != null && !(parent instanceof Window)) {
                        if (parent instanceof JInternalFrame) {
                            // Activate the frame.
                            try { ((JInternalFrame)parent).setSelected(true); }
                            catch (PropertyVetoException e1) { }
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }

	/**
	 * Draws this connector.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.blue);
		g.fillOval(r.x, r.y, r.width, r.height);
		g.setColor(Color.black);
		g.drawOval(r.x, r.y, r.width, r.height);
	}

	/**
	 * Stores the arrow tip to a StorableOutput.
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeStorable(getLocator());
	}

	/**
	 * Reads the arrow tip from a StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		setLocator((Locator)dr.readStorable());
	}

	protected void setLocator(Locator newLocator) {
		myLocator = newLocator;
	}

	public Locator getLocator() {
		return myLocator;
	}
}
