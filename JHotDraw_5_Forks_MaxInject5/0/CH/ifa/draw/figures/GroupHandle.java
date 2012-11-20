/*
 * @(#)GroupHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.NullHandle;

/**
 * A Handle for a GroupFigure.
 *
 * @version <$CURRENT_VERSION$>
 */
final class GroupHandle extends NullHandle {

	public GroupHandle(Figure owner, Locator locator) {
		super(owner, locator);
	}
    public IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("CompressionTypeName");
        node.setAttribute("value", "deflate");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("Lossless");
        node.setAttribute("value", "TRUE");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("NumProgressiveScans");
        node.setAttribute("value",
                          (IHDR_interlaceMethod == 0) ? "1" : "7");
        compression_node.appendChild(node);

        return compression_node;
    }

	/**
	 * Draws the Group handle.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();

		g.setColor(Color.black);
		g.drawRect(r.x, r.y, r.width, r.height);
		r.grow(-1, -1);
		g.setColor(Color.white);
		g.drawRect(r.x, r.y, r.width, r.height);
	}
}
