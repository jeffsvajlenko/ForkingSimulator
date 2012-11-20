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
    @Override
    public int getBaseline(JComponent c, int width, int height) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be >= 0");
        }
        AbstractButton b = (AbstractButton)c;
        String text = b.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        Insets i = b.getInsets();
        Rectangle viewRect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Rectangle iconRect = new Rectangle();
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = width - (i.right + viewRect.x);
        viewRect.height = height - (i.bottom + viewRect.y);

        // layout the text and icon
        SynthContext context = getContext(b);
        FontMetrics fm = context.getComponent().getFontMetrics(
            context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(
            context, fm, b.getText(), b.getIcon(),
            b.getHorizontalAlignment(), b.getVerticalAlignment(),
            b.getHorizontalTextPosition(), b.getVerticalTextPosition(),
            viewRect, iconRect, textRect, b.getIconTextGap());
        View view = (View)b.getClientProperty(BasicHTML.propertyKey);
        int baseline;
        if (view != null) {
            baseline = BasicHTML.getHTMLBaseline(view, textRect.width,
                                                 textRect.height);
            if (baseline >= 0) {
                 baseline += textRect.y;
            }
        }
        else {
            baseline = textRect.y + fm.getAscent();
        }
        context.dispose();
        return baseline;
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

	public void mouseUp(MouseEvent e, int x, int y) {
		if (child != null) {
			view().unfreezeView();
			child.mouseUp(e, x, y);
		}
		child = null;
	}
}
