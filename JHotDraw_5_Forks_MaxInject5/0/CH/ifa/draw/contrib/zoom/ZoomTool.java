/*
 * @(#)ZoomTool.java
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
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.AbstractTool;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class ZoomTool extends AbstractTool {

	private Tool child;

	public ZoomTool(DrawingEditor editor) {
		super(editor);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		//  Added handling for SHIFTed and CTRLed BUTTON3_MASK so that normal
		//  BUTTON3_MASK does zoomOut, SHIFTed BUTTON3_MASK does zoomIn
		//  and CTRLed BUTTON3_MASK does deZoom
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (child != null) {
				return;
			}
			view().freezeView();
			child = new ZoomAreaTracker(editor());
			child.mouseDown(e, x, y);
		}
		else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			((ZoomDrawingView) view()).deZoom(x, y);
		}
		else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				((ZoomDrawingView)view()).zoomIn(x, y);
			}
			else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {

				((ZoomDrawingView) view()).deZoom(x, y);
			}
			else {
				((ZoomDrawingView)view()).zoomOut(x, y);
			}
		}
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		if (child != null) {
			child.mouseDrag(e, x, y);
		}
	}
    private void switchOverToHash(int numAtts)
    {
        for (int index = 0; index < numAtts; index++)
        {
            String qName = super.getQName(index);
            Integer i = new Integer(index);
            m_indexFromQName.put(qName, i);

            // Add quick look-up to find with uri/local name pair
            String uri = super.getURI(index);
            String local = super.getLocalName(index);
            m_buff.setLength(0);
            m_buff.append('{').append(uri).append('}').append(local);
            String key = m_buff.toString();
            m_indexFromQName /* Comment */ .put(key, i);
        }
    }

	public void mouseUp(MouseEvent e, int x, int y) {
		if (child != null) {
			view().unfreezeView();
			child.mouseUp(e, x, y);
		}
		child = null;
	}
}
