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
    public MouseEvent(Component source, int id, long when, int modifiers,
                      int x, int y, int xAbs, int yAbs,
                      int clickCount, boolean X1, int button)
    {
        super(source, id, when, modifiers);
        this.x = x;
        this.y = y;
        this.xAbs = xAbs;
        this.yAbs = yAbs;
        this.clickCount = clickCount;
        this.X1 = X1;
        if (button < NOBUTTON){
            throw new IllegalArgumentException("Invalid button value :" + button);
        }
        if (button > BUTTON3) {
            if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()){
                throw new IllegalArgumentException("Extra mouse events are disabled " + button);
            } else {
                if (button > cachedNumberOfButtons) {
                    throw new IllegalArgumentException("Nonexistent button " + button);
                }
            }
            // XToolkit: extra buttons are not reporting about their state correctly.
            // Being pressed they report the state=0 both on the press and on the release.
            // For 1-3 buttons the state value equals zero on press and non-zero on release.
            // Other modifiers like Shift, ALT etc seem report well with extra buttons.
            // The problem reveals as follows: one button is pressed and then another button is pressed and released.
            // So, the getModifiersEx() would not be zero due to a first button and we will skip this modifier.
            // This may have to be moved into the peer code instead if possible.

            if (getModifiersEx() != 0) { //There is at least one more button in a pressed state.
                if (id == MouseEvent.MOUSE_RELEASED || id == MouseEvent.MOUSE_CLICKED){
                    System.out.println("MEvent. CASE!");
                    shouldExcludeButtonFromExtModifiers = true;
                }
            }
        }

        this.button = button;

        if ((getModifiers() != 0) && (getModifiersEx() == 0)) {
            setNewModifiers();
        } else if ((getModifiers() == 0) &&
                   (getModifiersEx() != 0 || button != NOBUTTON) &&
                   (button <= BUTTON3))
        {
            setOldModifiers();
        }
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
	
	public void commandExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
	
	public void commandNotExecutable(EventObject commandEvent) {
//		checkEnabled();
	}
}


