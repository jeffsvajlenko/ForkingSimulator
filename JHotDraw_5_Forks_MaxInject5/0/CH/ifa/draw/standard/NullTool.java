/*
 * @(#)AbstractTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public class NullTool extends AbstractTool {
	public NullTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

	public void deactivate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

    protected void checkUsable() {
    	//suppress setUsable(false);
	}    
  void readSlot(int position, int[] buffer)
  {
    /*
    try
    {
      System.arraycopy(fastArray, position*slotsize, buffer, 0, slotsize);
    }
    catch(ArrayIndexOutOfBoundsException aioobe)
    */
    {
      // System.out.println("Using slow read (2): "+position);
      position *= slotsize;
      int chunkpos = position >> lowbits;
      int slotpos = (position & lowmask);

      // Grow if needed
      if (chunkpos > chunks.size() - 1)
        chunks.addElement(new int[chunkalloc]);
      int[] chunk = chunks.elementAt(chunkpos);
      System.arraycopy(chunk,slotpos,buffer,0,slotsize);
    }
  }
}
