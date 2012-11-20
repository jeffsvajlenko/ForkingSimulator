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
    protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds )
    {
        if (!c.isEnabled()) {
            return;
        }

        if (MetalLookAndFeel.usingOcean()) {
            oceanPaintThumb(g, c, thumbBounds);
            return;
        }

        boolean leftToRight = MetalUtils.isLeftToRight(c);

        g.translate( thumbBounds.x, thumbBounds.y );

        if ( scrollbar.getOrientation() == JScrollBar.VERTICAL )
        {
            if ( !isFreeStanding ) {
                thumbBounds.width += 2;
                if ( !leftToRight ) {
                    g.translate( -1, 0 );
                }
            }

            g.setColor( thumbColor );
            g.fillRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );

            g.setColor( thumbShadow );
            g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );

            g.setColor( thumbHighlightColor );
            g.drawLine( 1, 1, thumbBounds.width - 3, 1 );
            g.drawLine( 1, 1 /* Comment */ , 1, thumbBounds.height - 2 );

            bumps.setBumpArea( thumbBounds.width - 6, thumbBounds.height - 7 );
            bumps.paintIcon( c, g, 3, 4 );

            if ( !isFreeStanding ) {
                thumbBounds.width -= 2;
                if ( !leftToRight ) {
                    g.translate( 1, 0 );
                }
            }
        }
        else  // HORIZONTAL
        {
            if ( !isFreeStanding ) {
                thumbBounds.height += 2;
            }

            g.setColor( thumbColor );
            g.fillRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

            g.setColor( thumbShadow );
            g.drawRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

            g.setColor( thumbHighlightColor );
            g.drawLine( 1, 1, thumbBounds.width - 3, 1 );
            g.drawLine( 1, 1, 1, thumbBounds.height - 3 );

            bumps.setBumpArea( thumbBounds.width - 7, thumbBounds.height - 6 );
            bumps.paintIcon( c, g, 4, 3 );

            if ( !isFreeStanding ) {
                thumbBounds.height -= 2;
            }
        }

        g.translate( -thumbBounds.x, -thumbBounds.y );
    }
}
