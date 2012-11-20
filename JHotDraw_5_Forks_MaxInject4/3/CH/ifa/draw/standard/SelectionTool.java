/*
 * @(#)SelectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.UndoableTool;
import CH.ifa.draw.util.UndoableHandle;
import CH.ifa.draw.contrib.dnd.DragNDropTool;
import java.awt.event.MouseEvent;

/**
 * Tool to select and manipulate figures.
 * A selection tool is in one of three states, e.g., background
 * selection, figure selection, handle manipulation. The different
 * states are handled by different child tools.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld032.htm>State</a></b><br>
 * SelectionTool is the StateContext and child is the State.
 * The SelectionTool delegates state specific
 * behavior to its current child tool.
 * <hr>
 *
 * @version <$CURRENT_VERSION$>
 */

public class SelectionTool extends AbstractTool {

	private Tool fChild = null;

	public SelectionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Handles mouse down events and starts the corresponding tracker.
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		// on MS-Windows NT: AWT generates additional mouse down events
		// when the left button is down && right button is clicked.
		// To avoid dead locks we ignore such events
		if (getDelegateTool() != null) {
			return;
		}

		view().freezeView();

		Handle handle = view().findHandle(e.getX(), e.getY());
		if (handle != null) {
			setDelegateTool(createHandleTracker(view(), handle));
		}
		else {
			Figure figure = drawing().findFigure(e.getX(), e.getY());
			if (figure != null) {
				setDelegateTool(createDragTracker(figure));
			}
			else {
				if (!e.isShiftDown()) {
					view().clearSelection();
				}
				setDelegateTool(createAreaTracker());
			}
		}
		getDelegateTool().activate();
		getDelegateTool().mouseDown(e, x, y);
	}

	/**
	 * Handles mouse moves (if the mouse button is up).
	 * Switches the cursors depending on whats under them.
	 */
	public void mouseMove(MouseEvent evt, int x, int y) {
		if (evt.getSource() == getActiveView() ) {
			DragNDropTool.setCursor(evt.getX(), evt.getY(), getActiveView());
		}
	}

	/**
	 * Handles mouse drag events. The events are forwarded to the
	 * current tracker.
	 */
	public void mouseDrag(MouseEvent e, int x, int y) {
		if (getDelegateTool() != null) { // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
			getDelegateTool().mouseDrag(e, x, y);
		}
	}

	/**
	 * Handles mouse up events. The events are forwarded to the
	 * current tracker.
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
		if (getDelegateTool() != null) { // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
			getDelegateTool().mouseUp(e, x, y);
			getDelegateTool().deactivate();
			setDelegateTool(null);
		}
		if (view() != null) {
			view().unfreezeView();
		}
	}

	/**
	 * Factory method to create a Handle tracker. It is used to track a handle.
	 */
	protected Tool createHandleTracker(DrawingView view, Handle handle) {
		return new HandleTracker(editor(), new UndoableHandle(handle, view));
	}

	/**
	 * Factory method to create a Drag tracker. It is used to drag a figure.
	 */
	protected Tool createDragTracker(Figure f) {
		return new UndoableTool(new DragTracker(editor(), f));
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
	 * Factory method to create an area tracker. It is used to select an
	 * area.
	 */
	protected Tool createAreaTracker() {
		return new SelectAreaTracker(editor());
	}

	protected Tool getDelegateTool() {
		return fChild;
	}

	private void setDelegateTool(Tool newDelegateTool) {
		fChild = newDelegateTool;
	}
}
