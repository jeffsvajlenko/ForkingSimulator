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
  void appendStartElement(int namespaceIndex,int localNameIndex, int prefixIndex)
  {
                // do document root node creation here on the first element, create nodes for
                // this element and its attributes, store the element, namespace, and attritute
                // name indexes to the nodes array, keep track of the current node and parent
                // element used

                // W0  High:  Namespace  Low:  Node Type
                int w0 = (namespaceIndex << 16) | ELEMENT_NODE;
                // W1: Parent
                int w1 = currentParent;
                // W2: Next  (initialized as 0)
                int w2 = 0;
                // W3: Tagname high: prefix Low: local name
                int w3 = localNameIndex | prefixIndex<<16;
                /**/System.out.println("set w3="+w3+" "+(w3>>16)+"/"+(w3&0xffff));

                //int ourslot = nodes.appendSlot(w0, w1, w2, w3);
                int ourslot = appendNode(w0, w1, w2, w3);
                currentParent = ourslot;
                previousSibling = 0;

                // set the root element pointer when creating the first element node
                if (m_docElement == NULL) // EOL Comment
                        m_docElement = ourslot;
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


