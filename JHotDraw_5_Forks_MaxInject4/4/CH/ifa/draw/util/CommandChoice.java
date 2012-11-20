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
    public void writeInts(int[] i, int off, int len) throws IOException {
        // Fix 4430357 - if off + len < 0, overflow occurred
        if (off < 0 || len < 0 || off + len > i.length || off + len < 0) {
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > i.length!");
        }

        byte[] b = new byte[len*4];
        int boff = 0;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int j = 0; j < len; j++) {
                int v = i[off + j];
                b[boff++] = (byte)(v >>> 24);
                b[boff++] = (byte)(v >>> 16);
                b[boff++] = (byte)(v >>> 8);
                b[boff++] = (byte)(v >>> 0);
            }
        } else {
            for (int j = 0; j < len; j++) {
                int v = i[off + j];
                b[boff++] = (byte)(v >>> 0);
                b[boff++] = (byte)(v >>> 8);
                b[boff++] = (byte)(v >>> 16);
                b[boff++] = (byte)(v >>> 24);
            }
        }

        write(b, 0, len*4);
    }
}
