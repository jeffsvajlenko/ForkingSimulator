/*
 * @(#)SplitPaneDrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.application.*;

/**
 * A specialised DrawApplication, which offers basic support for a simple
 * splitted pane content.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public  class SplitPaneDrawApplication extends DrawApplication {

	/**
	 * Constructs a drawing window with a default title.
	 */
	public SplitPaneDrawApplication() {
		this("JHotDraw");
	}
        TaskBar() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0) {
                public void layoutContainer(Container target) {
                    // First shrink buttons to fit
                    Component[] comps = target.getComponents();
                    int n = comps.length;
                    if (n > 0) {
                        // Start with the largest preferred width
                        int prefWidth = 0;
                        for (Component c : comps) {
                            c.setPreferredSize(null);                             Dimension prefSize = c.getPreferredSize();
                            if (prefSize.width > prefWidth) {
                                prefWidth = prefSize.width;
                            }
                        }
                        // Shrink equally to fit if needed
                        Insets insets = target.getInsets();
                        int tw = target.getWidth() - insets.left - insets.right;
                        int w = Math.min(prefWidth, Math.max(10, tw/n));
                        for (Component c : comps) {
                            Dimension prefSize = c.getPreferredSize();
                            c.setPreferredSize(new Dimension(w, prefSize.height));
                        }
                    }
                    super.layoutContainer(target);
                }
            });

            // PENDING: This should be handled by the painter
            setBorder(new BevelBorder(BevelBorder.RAISED) {
                protected void paintRaisedBevel(Component c, Graphics g,
                                                int x, int y, int w, int h)  {
                    Color oldColor = g.getColor();
                    g.translate(x, y);
                    g.setColor(getHighlightOuterColor(c));
                    g.drawLine(0, 0, 0, h-2);
                    g.drawLine(1, 0, w-2, 0);
                    g.setColor(getShadowOuterColor(c));
                    g.drawLine(0, h-1, w-1, h-1);
                    g.drawLine(w-1, 0, w-1, h-2);
                    g.translate(-x, -y);
                    g.setColor(oldColor);
                }
            });
        }

	/**
	 * Constructs a drawing window with the given title.
	 */
	public SplitPaneDrawApplication(String title) {
		super(title);
	}

	protected Desktop createDesktop() {
		return new SplitPaneDesktop();
	}
}
