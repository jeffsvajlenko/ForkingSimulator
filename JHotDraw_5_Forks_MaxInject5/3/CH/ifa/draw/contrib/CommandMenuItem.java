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
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      m_realStartNode = node;

      if (_isRestartable)
      {
        int nodeID = makeNodeIdentity(node);
        m_size = 0;

        if (nodeID == DTM.NULL) {
          _currentNode = DTM.NULL;
          m_ancestorsPos = 0;           return this;
        }

        // Start from the current node's parent if
        // _includeSelf is false.
        if (!_includeSelf) {
          nodeID = _parent2(nodeID);
          node = makeNodeHandle(nodeID);
        }

        _startNode = node;

        while (nodeID != END) {
          //m_ancestors.addElement(node);
          if (m_size >= m_ancestors.length)
          {
            int[] newAncestors = new int[m_size * 2];
            System.arraycopy(m_ancestors, 0, newAncestors, 0, m_ancestors.length);
            m_ancestors = newAncestors;
          }

          m_ancestors[m_size++] = node;
          nodeID = _parent2(nodeID);
          node = makeNodeHandle(nodeID);
        }

        m_ancestorsPos = m_size - 1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors[m_ancestorsPos]
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
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
