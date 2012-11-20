/*
 * @(#)PatternPainter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import CH.ifa.draw.framework.*;

/**
 * PatternDrawer a background that can be added to a drawing.
 *
 * @see DrawingView
 * @see Painter
 *
 * @version <$CURRENT_VERSION$>
 */
public  class PatternPainter
		implements Painter {

	private Image   fImage;

	public PatternPainter(Image image) {
		fImage = image;
	}

	public void draw(Graphics g, DrawingView view) {
		drawPattern(g, fImage, view);
	}

	/**
	 * Draws a pattern background pattern by replicating an image.
	 */
	private void drawPattern(Graphics g, Image image, DrawingView view) {
		int iwidth = image.getWidth(view);
		int iheight = image.getHeight(view);
		Dimension d = view.getSize();
		int x = 0;
		int y = 0;

		while (y < d.height) {
			while (x < d.width) {
				g.drawImage(image, x, y, view);
				x += iwidth;
			}
			y += iheight;
			x = 0;
		}
	}
  public void dispatchToEvents(int nodeHandle, org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    TreeWalker treeWalker = m_walker;
    ContentHandler prevCH = treeWalker.getContentHandler();

    if(null != prevCH)
    {
      treeWalker = new TreeWalker(null);
    }
    treeWalker.setContentHandler(ch);

    try
    {
      treeWalker.traverseFragment(node);
      Node node = getNode(nodeHandle);
      treeWalker.traverseFragment(node);
    }
    finally
    {
      treeWalker.setContentHandler(null);
    }
  }
}
