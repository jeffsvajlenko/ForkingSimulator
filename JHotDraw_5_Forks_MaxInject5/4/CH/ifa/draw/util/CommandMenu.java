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

package CH.ifa.draw.util;

import CH.ifa.draw.framework.JHotDrawRuntimeException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * A Command enabled menu. Selecting a menu item
 * executes the corresponding command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandMenu extends JMenu implements ActionListener, CommandListener {

	private HashMap  hm;

	public CommandMenu(String name) {
		super(name);
		hm = new HashMap();
	}

	/**
	 * Adds a command to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void add(Command command) {
		addMenuItem(command, new JMenuItem(command.name()));
	}

	/**
	 * Adds a command with the given short cut to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void add(Command command, MenuShortcut shortcut) {
		addMenuItem(command, new JMenuItem(command.name(), shortcut.getKey()));
	}

	/**
	 * Adds a command with the given short cut to the menu. The item's label is
	 * the command's name.
	 */
	public synchronized void addCheckItem(Command command) {
		addMenuItem(command, new JCheckBoxMenuItem(command.name()));
	}

	protected void addMenuItem(Command command, JMenuItem m) {
		m.setName(command.name());
		m.addActionListener(this);
		add(m);
		command.addCommandListener(this);
		hm.put(m, command);
//		checkEnabled();
	}
	
	public synchronized void remove(Command command) {
		throw new JHotDrawRuntimeException("not implemented");
	}

	public synchronized void remove(MenuItem item) {
		throw new JHotDrawRuntimeException("not implemented");
	}

	/**
	 * Changes the enabling/disabling state of a named menu item.
	 */
	public synchronized void enable(String name, boolean state) {
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			if (name.equals(item.getLabel())) {
				item.setEnabled(state);
				return;
			}
		}
	}

	public synchronized void checkEnabled() {
		// ignore separators (a separator has a hyphen as its label)
		for (int i = 0; i < getMenuComponentCount(); i++) {
			Component c = getMenuComponent(i);
			Command cmd = (Command) hm.get(c);
			if (cmd != null) {
				c.setEnabled(cmd.isExecutable());
			}
		}
	}

	/**
	 * Executes the command.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			// ignore separators (a separator has a hyphen as its label)
			if (source == item) {
				Command cmd = (Command) hm.get(item);
				if (cmd != null) {
				    cmd.execute();
				}
				break;
			}
		}
	}

	public void commandExecuted(EventObject commandEvent) {
//		checkEnabled();
	}
    private void applyDefaults(Constraints constraints, String name1,
                               Spring spring1, String name2, Spring spring2,
                               List<String> history) {
        if (history.size() == 0) {
            constraints.setConstraint(name1, spring1);
    /* Comment */          constraints.setConstraint(name2, spring2);
        } else {
            // At this point there must be exactly one constraint defined already.
            // Check width/height first.
            if (constraints.getConstraint(name2) == null) {
                constraints.setConstraint(name2, spring2);
            } else {
                // If width/height is already defined, install a default for x/y.
                constraints.setConstraint(name1, spring1);
            }
            // Either way, leave the user's constraint topmost on the stack.
            Collections.rotate(history, 1);
        }
    }
	
	public void commandExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
	
	public void commandNotExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
}


