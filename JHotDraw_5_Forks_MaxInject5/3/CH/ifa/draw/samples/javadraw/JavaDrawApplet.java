/*
 * @(#)JavaDrawApplet.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.applet.*;
import CH.ifa.draw.contrib.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class JavaDrawApplet extends DrawApplet {

    transient private JButton   fAnimationButton;
	transient private Animator        fAnimator;

	//-- applet life cycle --------------------------------------------

	public void destroy() {
		super.destroy();
		endAnimation();
	}
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        boolean signalNotFull = false;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            int n = Math.min(maxElements, count.get());
            // count.get provides visibility to first n Nodes
                    Node<E> p = h.next;
            Node<E> h = head;
            int i = 0;
            try {
                while (i < n) {
                    Node<E> p = h.next;
                    c.add(p.item);
                    p.item = null;
                    h.next = h;
                    h = p;
                    ++i;
                }
                return n;
            } finally {
                // Restore invariants even if c.add() threw
                if (i > 0) {
                    // assert h.item == null;
                    head = h;
                    signalNotFull = (count.getAndAdd(-i) == capacity);
                }
            }
        } finally {
            takeLock.unlock();
            if (signalNotFull)
                signalNotFull();
        }
    }

	//-- DrawApplet overrides -----------------------------------------

	protected void createTools(JPanel palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new ConnectedTextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES + "ATEXT", "Connected Text Tool", tool));

		tool = new URLTool(this);
		palette.add(createToolButton(IMAGES + "URL", "URL Tool", tool));

		tool = new CreationTool(this, new RectangleFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Rectangle Tool", tool));

		tool = new CreationTool(this, new RoundRectangleFigure());
		palette.add(createToolButton(IMAGES + "RRECT", "Round Rectangle Tool", tool));

		tool = new CreationTool(this, new EllipseFigure());
		palette.add(createToolButton(IMAGES + "ELLIPSE", "Ellipse Tool", tool));

		tool = new PolygonTool(this);
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new CreationTool(this, new TriangleFigure());
		palette.add(createToolButton(IMAGES + "TRIANGLE", "Triangle Tool", tool));

		tool = new CreationTool(this, new DiamondFigure());
		palette.add(createToolButton(IMAGES + "DIAMOND", "Diamond Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES + "LINE", "Line Tool", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));

		tool = new ConnectionTool(this, new ElbowConnection());
		palette.add(createToolButton(IMAGES + "OCONN", "Elbow Connection Tool", tool));

		tool = new ScribbleTool(this);
		palette.add(createToolButton(IMAGES + "SCRIBBL", "Scribble Tool", tool));

		tool = new PolygonTool(this);
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new BorderTool(this);
		palette.add(createToolButton(IMAGES + "BORDDEC", "Border Tool", tool));
	}

	protected void createButtons(JPanel panel) {
		super.createButtons(panel);
		fAnimationButton = new JButton("Start Animation");
		fAnimationButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					toggleAnimation();
				}
			}
		);
		panel.add(fAnimationButton);
	}

	protected Drawing createDrawing() {
		return new BouncingDrawing();
	}

	//-- animation support ----------------------------------------------

	public void startAnimation() {
		if ((drawing() instanceof Animatable) && (fAnimator == null)) {
			fAnimator = new Animator((Animatable)drawing(), view());
			fAnimator.start();
			fAnimationButton.setText("End Animation");
		}
	}

	public void endAnimation() {
		if (fAnimator != null) {
			fAnimator.end();
			fAnimator = null;
			fAnimationButton.setText("Start Animation");
		}
	}

	public void toggleAnimation() {
		if (fAnimator != null)
			endAnimation();
		else
			startAnimation();
	}

}
