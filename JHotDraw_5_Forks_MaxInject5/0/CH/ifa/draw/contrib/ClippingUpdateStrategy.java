package CH.ifa.draw.contrib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.FigureEnumerator;

/**
 * The ClippingUpdateStrategy will only draw those Figures in the DrawingView
 * which intersect the Graphic's clipping rectangle. 
 * 
 * @author Aviv Hurvitz
 */
public class ClippingUpdateStrategy implements Painter {

	public ClippingUpdateStrategy() {
		super();
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
        workqueueMonitoredObject.addAttribute();
        LongMonitoredAttributeBase b3 = new
            LongMonitoredAttributeBase(MonitoringConstants.WORKQUEUE_AVERAGE_TIME_IN_QUEUE,
                    MonitoringConstants.WORKQUEUE_AVERAGE_TIME_IN_QUEUE_DESCRIPTION) {
                public Object getValue() {
                    return new Long(WorkQueueImpl.this.averageTimeInQueue());
                }
            };
        workqueueMonitoredObject.addAttribute(b3);
    }

	/**
	 * @see CH.ifa.draw.framework.Painter#draw(Graphics, DrawingView)
	 */
	public void draw(Graphics g, DrawingView view) {
		Rectangle viewClipRectangle = g.getClipBounds();

		if (viewClipRectangle == null) {
			// it seems clip is always set, but nevertheless handle it
			view.drawAll(g);
			return;
		}

		FigureEnumeration fe = view.drawing().figures();
		
		// it's better to start big than to do Log(nFigures) reallocations 
		Vector v = new Vector(1000);

		// create a List of the figures within the clip rectangle
		while (fe.hasNextFigure()) {
			Figure fig = fe.nextFigure();
			Rectangle r = fig.displayBox();

			// grow Rectangles that have 0 width or height, since 
			// Rectangle.intersects() returns false on them. 
			// These Rectangles are common, as they are the typical bounding 
			// boxes of horizontal and vertical lines.
			// see my (regression) bug report on Sun's site:
			//   http://developer.java.sun.com/developer/bugParade/bugs/4643428.html
			if (r.width <= 0) {
				r.width = 1;
			}
			if (r.height <= 0) {
				r.height = 1;
			}

			if (r.intersects(viewClipRectangle)) {
				v.add(fig);
			}
		}

		// draw the figures in the clip rectangle
		FigureEnumeration clippedFE = new FigureEnumerator(v);
		view.draw(g, clippedFE);
	}
}
