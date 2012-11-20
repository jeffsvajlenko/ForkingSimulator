/*
 * @(#)NetApp.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.net;

import javax.swing.JToolBar;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.application.DrawApplication;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class NetApp extends DrawApplication {

	public NetApp() {
		super("Net");
	}

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Create Org Unit", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));
	}
        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            Rectangle alloc = bounds.getBounds();
            try {
                // --- determine locations ---
                TextUI mapper = c.getUI();
                Rectangle p0 = mapper.modelToView(c, offs0);
                Rectangle p1 = mapper.modelToView(c, offs1);

                // --- render ---
                Color color = getColor();

                if (color == null) {
                    g.setColor(c.getSelectionColor());
                }
                else {
                    g.setColor(color);
                }
                boolean firstIsDot = false;
                boolean secondIsDot = false;
                if (c.isEditable()) {
                    int dot = c.getCaretPosition();
                    firstIsDot = (offs0 == dot);
                    secondIsDot = (offs1 == dot);
                }
                if (p0.y == p1.y) {
                    // same line, render a rectangle
                    Rectangle r = p0.union(p1);
                    if (r.width > 0) {
                        if (firstIsDot) {
                            r.x++;
                            r.width--;
                        }
                        else if (secondIsDot) {
                            r.width--;
                        }
                    }
                    g.fillRect(r.x, r.y, r.width, r.height);
                } else {
                    // different lines
                    int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
                    if (firstIsDot && p0ToMarginWidth > 0) {
                        p0.x++;
                        p0ToMarginWidth--;
                    }
                    g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
                    if ((p0.y + p0.height) != p1.y) {
                        g.fillRect(alloc.x, p0.y + p0.height, alloc.width,
                                   p1.y - (p0.y + p0.height));
                    }
                    if (secondIsDot && p1.x > alloc.x) {
                        p1.x--;
                    }
                    g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
                }
            } catch (BadLocationException e) {
                // can't render
            }
        }

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		DrawApplication window = new NetApp();
		window.open();
	}
}
