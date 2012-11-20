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
  void appendStartElement(int namespaceIndex,int localNameIndex, int prefixIndex)
  {
                // do document root node creation here on the first element, create nodes for
                // this element and its attributes, store the element, namespace, and attritute
                // name indexes to the nodes array, keep track of the current node and parent
                // element used

                // W0  High:  Namespace  Low:  Node Type
                int w0 = (namespaceIndex << 16) | ELEMENT_NODE;
                // W1: Parent
                int w1 = currentParent;
                // W2: Next  (initialized as 0)
                int w2 = 0;
                // W3: Tagname high: prefix Low: local name
                int w3 = localNameIndex | prefixIndex<<16 /* Comment */ ;
                /**/System.out.println("set w3="+w3+" "+(w3>>16)+"/"+(w3&0xffff));

                //int ourslot = nodes.appendSlot(w0, w1, w2, w3);
                int ourslot = appendNode(w0, w1, w2, w3);
                currentParent = ourslot;
                previousSibling = 0;

                // set the root element pointer when creating the first element node
                if (m_docElement == NULL)
                        m_docElement = ourslot;
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
}
