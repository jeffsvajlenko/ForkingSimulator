/*
 * @(#)CommandMenu.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

import CH.ifa.draw.contrib.CTXCommandMenu;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.standard.AbstractCommand;

/**
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 *
 * @author    Eduardo Francos (adapted from original implementation by Wolfram Kaiser)
 * @created   2 mai 2002
 * @version   <$CURRENT_VERSION$>
 */
public class CTXWindowMenu extends CTXCommandMenu {
	private MDIDesktopPane desktop;
	private CommandMenuItem cascadeCommand;
	private CommandMenuItem tileHCommand;
	private CommandMenuItem tileVCommand;
	private CommandMenuItem arrangeHCommand;
	private CommandMenuItem arrangeVCommand;
	private int staticItems;

	/**
	 *Constructor for the CTXWindowsMenu object
	 *
	 * @param newText  the menu's text
	 * @param desktop  the MDI desktop
	 * @param editor   the editor
	 */
	public CTXWindowMenu(String newText, MDIDesktopPane newDesktop, DrawingEditor newEditor) {
		super(newText);
		this.desktop = newDesktop;

		cascadeCommand = new CommandMenuItem(
			new AbstractCommand("Cascade", newEditor) {
				public void execute() {
					CTXWindowMenu.this.desktop.cascadeFrames();
				}

				public boolean isExecutable() {
					return super.isExecutable() && (CTXWindowMenu.this.desktop.getAllFrames().length > 0);
				}
    private static void checkNSSubset(XSWildcardDecl dWildcard, int min1, int max1,
            XSWildcardDecl bWildcard, int min2, int max2)
        throws XMLSchemaException {

        // check Occurrence ranges
        if (!checkOccurrenceRange(min1,max1,min2,max2)) {
            throw new XMLSchemaException("rcase-NSSubset.2", new Object[]{
                    Integer.toString(min1),
                    max1==SchemaSymbols.OCCURRENCE_UNBOUNDED?"unbounded":Integer.toString(max1),
                            Integer.toString(min2),
                            max2==SchemaSymbols.OCCURRENCE_UNBOUNDED?"unbounded":Integer.toString(max2)});
        }

        // check wildcard subset
        if (!dWildcard.isSubsetOf(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.1", null);
        }

        if (dWildcard.weakerProcessContents(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.3",
                    new Object[]{dWildcard.getProcessContentsAsString(),
        
            bWildcard.getProcessContentsAsString()});
        }

    }
			});

		tileHCommand = new CommandMenuItem(
			new AbstractCommand("Tile Horizontally", newEditor) {
				public void execute() {
					CTXWindowMenu.this.desktop.tileFramesHorizontally();
				}

				public boolean isExecutable() {
					return super.isExecutable() && (CTXWindowMenu.this.desktop.getAllFrames().length > 0);
				}
			});

		tileVCommand = new CommandMenuItem(
			new AbstractCommand("Tile Vertically", newEditor) {
				public void execute() {
					CTXWindowMenu.this.desktop.tileFramesVertically();
				}

				public boolean isExecutable() {
					return super.isExecutable() && (CTXWindowMenu.this.desktop.getAllFrames().length > 0);
				}
			});

		arrangeHCommand = new CommandMenuItem(
			new AbstractCommand("Arrange Horizontally", newEditor) {
				public void execute() {
					CTXWindowMenu.this.desktop.arrangeFramesHorizontally();
				}

				public boolean isExecutable() {
					return super.isExecutable() && (CTXWindowMenu.this.desktop.getAllFrames().length > 0);
				}
			});

		arrangeVCommand = new CommandMenuItem(
			new AbstractCommand("Arrange Vertically", newEditor) {
				public void execute() {
					CTXWindowMenu.this.desktop.arrangeFramesVertically();
				}

				public boolean isExecutable() {
					return super.isExecutable() && (CTXWindowMenu.this.desktop.getAllFrames().length > 0);
				}
			});

		addMenuListener(
			new MenuListener() {
				public void menuCanceled(MenuEvent e) { }

				public void menuDeselected(MenuEvent e) {
					removeWindowsList();
				}

				public void menuSelected(MenuEvent e) {
					buildChildMenus();
				}
			});

		add(cascadeCommand);
		add(tileHCommand);
		add(tileVCommand);
		add(arrangeHCommand);
		add(arrangeVCommand);
		staticItems = 5;
	}

	/** removes the windows names */
	protected void removeWindowsList() {
		// remove all items above static the items
		while (this.getItemCount() > staticItems) {
			remove(staticItems);
		}
	}

	/*
	 *  Sets up the children menus depending on the current desktop state
	 */
	/** Description of the Method */
	private void buildChildMenus() {
		JInternalFrame[] array = desktop.getAllFrames();

		// update window organization commands
		cascadeCommand.setEnabled(array.length > 0);
		tileHCommand.setEnabled(array.length > 0);
		tileVCommand.setEnabled(array.length > 0);
		arrangeHCommand.setEnabled(array.length > 0);
		arrangeVCommand.setEnabled(array.length > 0);

		if (array.length == 0) {
			return;
		}

		addSeparator();

		for (int i = 0; i < array.length; i++) {
			ChildMenuItem menu = new ChildMenuItem(array[i]);
			menu.setState(i == 0);
			menu.addActionListener(
				new ActionListener() {
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

	/*
	 *  This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
	 *  to a give menu.
	 */
	class ChildMenuItem extends JCheckBoxMenuItem {
		private JInternalFrame frame;

		/**
		 *Constructor for the ChildMenuItem object
		 *
		 * @param frame  Description of the Parameter
		 */
		public ChildMenuItem(JInternalFrame frame) {
			super(frame.getTitle());
			this.frame = frame;
		}

		/**
		 * Gets the frame attribute of the ChildMenuItem object
		 *
		 * @return   The frame value
		 */
		public JInternalFrame getFrame() {
			return frame;
		}
	}
}
