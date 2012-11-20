/*
 * @(#)CommandChoice.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * A Command enabled choice. Selecting a choice executes the
 * corresponding command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandChoice extends JComboBox implements ItemListener {

	private List   fCommands;

	public CommandChoice() {
		super();
		fCommands = CollectionsFactory.current().createList(10);
		addItemListener(this);
	}
    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

	/**
	 * Adds a command to the menu.
	 */
	public synchronized void addItem(Command command) {
		addItem(command.name());
		fCommands.add(command);
	}

	/**
	 * Executes the command.
	 */
	public void itemStateChanged(ItemEvent e) {
		if ((getSelectedIndex() >= 0) && (getSelectedIndex() < fCommands.size())) {
			Command command = (Command)fCommands.get(getSelectedIndex());
            if (command.isExecutable()) {
				command.execute();
			}
		}
	}
}
