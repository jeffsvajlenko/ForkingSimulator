/*
 * @(#)AreaTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.standard.AbstractTool;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * A rubberband area tracker.  It can be extended to do anything with
 * the resulting area, e.g. select it or zoom it.  This code is
 * derived from SelectAreaTracker, which is a bit too specific to
 * allow for extension.
 *
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public abstract class AreaTracker extends AbstractTool {

	private Rectangle area;

	protected AreaTracker(DrawingEditor editor) {
		super(editor);
	}

	public Rectangle getArea() {
		return new Rectangle(area.x, area.y, area.width, area.height);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		super.mouseDown(e, e.getX(), e.getY());
		rubberBand(getAnchorX(), getAnchorY(), getAnchorX(), getAnchorY());
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		eraseRubberBand();
		rubberBand(getAnchorX(), getAnchorY(), x, y);
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		eraseRubberBand();
	}

	private void rubberBand(int x1, int y1, int x2, int y2) {
		area = new Rectangle(new Point(x1, y1));
		area.add(new Point(x2, y2));
		drawXORRect(area);
	}
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
        // line coordinates
        Document doc = getDocument();
        Element map = getElement();
        int lineIndex = map.getElementIndex(pos);
        if (lineIndex < 0) {
            return lineToRect(a, 0);
        }
        Rectangle lineArea = lineToRect(a, lineIndex);

        // determine span from the start of the line
        tabBase = lineArea.x;
        Element line = map.getElement(lineIndex);
        int p0 = line.getStartOffset();
        Segment s = SegmentCache.getSharedSegment();
        doc.getText(p0, pos - p0, s);
        int xOffs = Utilities.getTabbedTextWidth(s, metrics, tabBase, this,p0);
        SegmentCache.releaseSharedSegment(s);

        // fill in the results and return
        lineArea.x += xOffs;
        lineArea.width = 1;
        lineArea.height = metrics.getHeight();
        return lineArea;
    }

	private void eraseRubberBand() {
		drawXORRect(area);
	}

	private void drawXORRect(Rectangle r) {
		Graphics g = view().getGraphics();
		g.setXORMode(view().getBackground());
		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
	}

}
