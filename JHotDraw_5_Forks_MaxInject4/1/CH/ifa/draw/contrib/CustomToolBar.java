/*
 * @(#)CustomToolBar.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.util.CollectionsFactory;

import javax.swing.JToolBar;
import javax.swing.JComponent;
import java.util.List;
import java.util.Iterator;
import java.awt.Component;

/**
 * This ToolBar allows to use several panels with tools. It manages each
 * panel and enables to swap between them. There can only one panel with
 * tools be activated at a time. Currently, only two panels are supported
 * (standard tools and edit tools).
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CustomToolBar extends JToolBar {

	/**
	 * List containing all tools for the standard ToolBar
	 */
	private List standardTools;
	
	/**
	 * List containing all tools for the edit ToolBar
	 */
	private List editTools;
	
	/**
	 * List containing all tools, which are currently activated
	 */
	private List currentTools;
	
	/**
	 * Flag which determines whether the tool palette must be updated
	 */
	private boolean needsUpdate;
	
	/**
	 * Create a new ToolBar
	 */
	public CustomToolBar() {
		super();
		standardTools = CollectionsFactory.current().createList();
		editTools = CollectionsFactory.current().createList();
		currentTools = standardTools;
		needsUpdate = false;
	}

	/**
	 * Switch between the two available palettes with tools
	 */
	public void switchToolBar() {
		if (currentTools == standardTools) {
			switchToEditTools();
		}
		else {
			switchToStandardTools();
		}
	}
	
	/**
	 * Select the palette with the edit tools
	 */
	public void switchToEditTools() {
		if (currentTools != editTools) {
			currentTools = editTools;
			needsUpdate = true;
		}
	}

	/**
	 * Select the palette with the standard tools
	 */
	public void switchToStandardTools() {
		if (currentTools != standardTools) {
			currentTools = standardTools;
			needsUpdate = true;
		}
	}

	/**
	 * Activate a palette of the ToolBar by setting all Tools
	 */
	public void activateTools() {
		if (!needsUpdate) {
			return;
		}
		else {
			removeAll();

			JComponent currentTool = null;
			Iterator iter = currentTools.iterator();
			while (iter.hasNext()) {
				currentTool = (JComponent)iter.next();
				super.add(currentTool);
			}
			validate();
			needsUpdate = false;
		}
	}

	/**
	 * Add a new tool the the current palette of the ToolBar
	 */
	public Component add(Component newTool) {
		if (currentTools == editTools) {
			editTools.add(newTool);
		}
		else {
			standardTools.add(newTool);
		}
		needsUpdate = true;
		return super.add(newTool);
	}
    public int bitCount() {
        @SuppressWarnings("deprecation") int bc = bitCount - 1;
        if (bc == -1) {  // bitCount not initialized yet
            bc = 0;      // offset by one to initialize
            // Count the bits in the magnitude
            for (int i=0; i<mag.length; i++)
                bc += Integer.bitCount(mag[i]);
            if (signum < 0) {
                // Count the trailing zeros in the magnitude
                int magTrailingZeroCount = 0, j;
                for (j=mag.length-1; mag[j]==0; j--)
                    magTrailingZeroCount += 32;
                magTrailingZeroCount += Integer.numberOfTrailingZeros(mag[j]);
                bc += magTrailingZeroCount - 1;
            }
            bitCount = bc + 1;
        }
        return bc;
    }
}
