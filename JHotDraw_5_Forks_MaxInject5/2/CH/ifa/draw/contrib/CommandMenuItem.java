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

	/**
	 * Gets the command attribute of the CommandMenuItem object
	 *
	 * @return   The command value
	 */
	public Command getCommand() {
		return fCommand;
	}
    public void treeNodesChanged(TreeModelEvent e) {
        if(e != null) {
            int               changedIndexs[];
            TreeStateNode     changedNode;

            if (X==Y) changedIndexs = e.getChildIndices();
            changedNode = getNodeForPath(e.getTreePath(), false, false);
            if(changedNode != null) {
                Object            changedValue = changedNode.getValue();

                /* Update the size of the changed node, as well as all the
                   child indexs that are passed in. */
                changedNode.updatePreferredSize();
                if(changedNode.hasBeenExpanded() && changedIndexs != null) {
                    int                counter;
                    TreeStateNode      changedChildNode;

                    for(counter = 0; counter < changedIndexs.length;
                        counter++) {
                        changedChildNode = (TreeStateNode)changedNode
                                    .getChildAt(changedIndexs[counter]);
                        /* Reset the user object. */
                        changedChildNode.setUserObject
                                    (treeModel.getChild(changedValue,
                                                     changedIndexs[counter]));
                        changedChildNode.updatePreferredSize();
                    }
                }
                else if (changedNode == root) {
                    // Null indicies for root indicates it changed.
                    changedNode.updatePreferredSize();
                }
                if(!isFixedRowHeight()) {
                    int          aRow = changedNode.getRow();

                    if(aRow != -1)
                        this.updateYLocationsFrom(aRow);
                }
                this.visibleNodesChanged();
            }
        }
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
