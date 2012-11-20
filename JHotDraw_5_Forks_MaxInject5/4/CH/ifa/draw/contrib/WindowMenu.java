/*
 * @(#)WindowMenu.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.util.CommandMenu;
import CH.ifa.draw.util.Command;
import CH.ifa.draw.standard.AbstractCommand;
import CH.ifa.draw.framework.DrawingEditor;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.*;

/**
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 *
 * @author Wolfram Kaiser (adapted from an article in JavaWorld)
 * @version <$CURRENT_VERSION$>
 */
public class WindowMenu extends CommandMenu {
	private MDIDesktopPane desktop;
	private Command cascadeCommand;
	private Command tileCommand;

	public WindowMenu(String newText, MDIDesktopPane newDesktop, DrawingEditor newEditor) {
		super(newText);
		this.desktop = newDesktop;
		cascadeCommand = new AbstractCommand("Cascade", newEditor) {
			public void execute() {
				WindowMenu.this.desktop.cascadeFrames();
			}
			public boolean isExecutable() {
				return super.isExecutable() && (WindowMenu.this.desktop.getAllFrames().length > 0);
			}
		};
		tileCommand = new AbstractCommand("Tile", newEditor) {
			public void execute() {
				WindowMenu.this.desktop.tileFrames();
			}
			public boolean isExecutable() {
				return super.isExecutable() && (WindowMenu.this.desktop.getAllFrames().length > 0);
			}
		};
		addMenuListener(new MenuListener() {
			public void menuCanceled (MenuEvent e) {}

			public void menuDeselected (MenuEvent e) {
				removeAll();
			}
        private void paintMe(Component c, Graphics g) {

            int right = folderIcon16Size.width - 1;
            int bottom = folderIcon16Size.height - 1;

            // Draw tab top
            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
            g.drawLine( right - 5, 3, right, 3 );
            g.drawLine( right - 6, 4, right, 4 );

            // Draw folder front
            g.setColor( MetalLookAndFeel.getPrimaryControl() );
            g.fillRect( 2, 7, 13, 8 );

            // Draw tab bottom
            g.setColor( MetalLookAndFeel.getPrimaryControlShadow() );
            g.drawLine( right - 6, 5, right - 1, 5 );

            // Draw outline
            g.setColor( MetalLookAndFeel.getPrimaryControlInfo() );
            g.drawLine( 0, 6, 0, bottom );            // left side
            g.drawLine( 1, 5, right - 7, 5 );         // first part of top
            g.drawLine( right - 6, 6, right - 1, 6 ); // second part of top
            g.drawLine( right,right, bottom );    // right side
            g.drawLine( 0, bottom, right, bottom );   // bottom

            // Draw highlight
            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
            g.drawLine( 1, 6, 1, bottom - 1 );
            g.drawLine( 1, 6, right - 7, 6 );
            g.drawLine( right - 6, 7, right - 1, 7 );

        }

			public void menuSelected (MenuEvent e) {
				buildChildMenus();
			}
		});
	}

	/* Sets up the children menus depending on the current desktop state */
	private void buildChildMenus() {
		ChildMenuItem menu;
		JInternalFrame[] array = desktop.getAllFrames();

		add(new CommandMenuItem(cascadeCommand));
		add(new CommandMenuItem(tileCommand));
		if (array.length > 0) {
			addSeparator();
		}
//		cascade.setEnabled(array.length > 0);
//		tile.setEnabled(array.length > 0);

		for (int i = 0; i < array.length; i++) {
			menu = new ChildMenuItem(array[i]);
			menu.setState(i == 0);
			menu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
					frame.moveToFront();
					try {
						frame.setSelected(true);
					}
					catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			});
			menu.setIcon(array[i].getFrameIcon());
			add(menu);
		}
	}

	/* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
	   to a give menu. */
	class ChildMenuItem extends JCheckBoxMenuItem {
		private JInternalFrame frame;

		public ChildMenuItem(JInternalFrame frame) {
			super(frame.getTitle());
			this.frame=frame;
		}

		public JInternalFrame getFrame() {
			return frame;
		}
	}
}
