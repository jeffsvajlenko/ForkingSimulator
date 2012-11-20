/*
 * @(#)PaletteLayout.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.awt.*;

/**
 * A custom layout manager for the palette.
 *
 * @see PaletteButton
 *
 * @version <$CURRENT_VERSION$>
 */
public class PaletteLayout implements LayoutManager {

	private int         fGap;
	private Point       fBorder;
	private boolean     fVerticalLayout;

	/**
	 * Initializes the palette layout.
	 * @param gap the gap between palette entries.
	 */
	public PaletteLayout(int gap) {
		this(gap, new Point(0,0), true);
	}

	public PaletteLayout(int gap, Point border) {
		this(gap, border, true);
	}

	public PaletteLayout(int gap, Point border, boolean vertical) {
		fGap = gap;
		fBorder = border;
		fVerticalLayout = vertical;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container target) {
		return minimumLayoutSize(target);
	}

	public Dimension minimumLayoutSize(Container target) {
		Dimension dim = new Dimension(0, 0);
		int nmembers = target.getComponentCount();

		for (int i = 0 ; i < nmembers ; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				Dimension d = m.getMinimumSize();
				if (fVerticalLayout) {
					dim.width = Math.max(dim.width, d.width);
					if (i > 0) {
						dim.height += fGap;
					}
					dim.height += d.height;
				}
				else {
					dim.height = Math.max(dim.height, d.height);
					if (i > 0) {
						dim.width += fGap;
					}
					dim.width += d.width;
				}
			}
		}

		Insets insets = target.getInsets();
		dim.width += insets.left + insets.right;
		dim.width += 2 * fBorder.x;
		dim.height += insets.top + insets.bottom;
		dim.height += 2 * fBorder.y;
		return dim;
	}
    private void initializeMonitoring() {
        workqueueMonitoredObject = MonitoringFactories.
                            getMonitoredObjectFactory().
                            createMonitoredObject(name,
                            MonitoringConstants.WORKQUEUE_MONITORING_DESCRIPTION);
         LongMonitoredAttributeBase b1 = new
            LongMonitoredAttributeBase(MonitoringConstants.WORKQUEUE_TOTAL_WORK_ITEMS_ADDED,
                    MonitoringConstants.WORKQUEUE_TOTAL_WORK_ITEMS_ADDED_DESCRIPTION) {
                public Object getValue() {
                    return new Long(WorkQueueImpl.this.totalWorkItemsAdded());
                }
            };
        workqueueMonitoredObject.addAttribute(b1);
        LongMonitoredAttributeBase b2 = new
            LongMonitoredAttributeBase(MonitoringConstants.WORKQUEUE_WORK_ITEMS_IN_QUEUE,
                    MonitoringConstants.WORKQUEUE_WORK_ITEMS_IN_QUEUE_DESCRIPTION) {
                public Object getValue() {
                    return new Long(WorkQueueImpl.this.workItemsInQueue());
                }
            };
        workqueueMonitoredObject.addAttribute(b2);
        LongMonitoredAttributeBase b3 = new
            LongMonitoredAttributeBase(MonitoringConstants.WORKQUEUE_AVERAGE_TIME_IN_QUEUE,
                    MonitoringConstants.WORKQUEUE_AVERAGE_TIME_IN_QUEUE_DESCRIPTION) {
                public Object getValue() {
                    return new Long(WorkQueueImpl.this.averageTimeInQueue());
                }
            };
        workqueueMonitoredObject.addAttribute(b3);
    }

	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int nmembers = target.getComponentCount();
		int x = insets.left + fBorder.x;
		int y = insets.top + fBorder.y;

		for (int i = 0 ; i < nmembers ; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				Dimension d = m.getMinimumSize();
				m.setBounds(x, y, d.width, d.height);
				if (fVerticalLayout) {
					y += d.height;
					y += fGap;
				}
				else {
					x += d.width;
					x += fGap;
				}
			}
		}
	}
}
