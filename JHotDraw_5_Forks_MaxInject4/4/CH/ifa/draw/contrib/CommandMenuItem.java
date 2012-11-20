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

import CH.ifa.draw.util.Command;
import javax.swing.JMenuItem;
import javax.swing.Icon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * CommandMenuItem implements a command that can be added to a menu
 * as a menu item
 *
 * @author    Eduardo Francos - InContext
 * @created   2 mai 2002
 * @version   <$CURRENT_VERSION$>
 */
public class CommandMenuItem extends JMenuItem implements CommandHolder, ActionListener {

	private Command fCommand;

	/**
	 * Creates a menuItem with no set text or icon.
	 */
	public CommandMenuItem(Command command) {
		super(command.name());
		setCommand(command);
		addActionListener(this);
	}

	/**
	 * Creates a menuItem with an icon.
	 *
	 * @param icon the icon of the MenuItem.
	 */
	public CommandMenuItem(Command command, Icon icon) {
		super(command.name(), icon);
		setCommand(command);
		addActionListener(this);
	}

	/**
	 * Creates a menuItem with the specified text and
	 * keyboard mnemonic.
	 *
	 * @param command the command to be executed upon menu selection
	 * @param mnemonic the keyboard mnemonic for the MenuItem
	 */
	public CommandMenuItem(Command command, int mnemonic) {
		super(command.name(), mnemonic);
		setCommand(command);
	}
        public void eventDispatched(AWTEvent ev) {
            int eventID = ev.getID();
            if((eventID & AWTEvent.MOUSE_EVENT_MASK) != 0) {
                MouseEvent me = (MouseEvent) ev;
                if(me.isPopupTrigger()) {
                    MenuElement[] elems = MenuSelectionManager
                            .defaultManager()
                            .getSelectedPath();
                    if(elems != null && elems.length != 0) {
                        return;
                        // We shall not interfere with already opened menu
                    }
                    Object c = me.getSource();
                    JComponent src = null;
                    if(c instanceof JComponent) {
                        src = (JComponent) c;
                    } else if(c instanceof BasicSplitPaneDivider) {
                        // Special case - if user clicks on divider we must
                        // invoke popup from the SplitPane
                        src = (JComponent)
                            ((BasicSplitPaneDivider)c).getParent();
                    }
                    if(src != null) {
                        if(src.getComponentPopupMenu() != null) {
                            Point pt = src.getPopupLocation(me);
                            if(pt == null) {
                                pt = me.getPoint();
                                pt = SwingUtilities.convertPoint((Component)c,
                                                                  pt, src);
                            }
                            src.getComponentPopupMenu().show(src, pt.x, pt.y);
                            me.consume();
                        }
                    }
                }
            }
            /* Activate a JInternalFrame if necessary. */
            if (eventID == MouseEvent.MOUSE_PRESSED) {
                Object object = ev.getSource();
                if (!(object instanceof Component)) {
                    return;
                }
                Component component = (Component)object;
                if (component != null) {
                    Component parent = component;
                    while (parent != null && !(parent instanceof Window)) {
                        if (parent instanceof JInternalFrame) {
                            // Activate the frame.
                            try { ((JInternalFrame)parent).setSelected(true); }
                            catch (PropertyVetoException e1) { }
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }

	/**
	 * Gets the command attribute of the CommandMenuItem object
	 *
	 * @return   The command value
	 */
	public Command getCommand() {
		return fCommand;
	}

	/**
	 * Sets the command attribute of the CommandMenuItem object
	 *
	 * @param newCommand  The new command value
	 */
	public void setCommand(Command newCommand) {
		fCommand = newCommand;
	}

	/**
	 * Executes the command.
	 */
	public void actionPerformed(ActionEvent e) {
		getCommand().execute();
	}
}
