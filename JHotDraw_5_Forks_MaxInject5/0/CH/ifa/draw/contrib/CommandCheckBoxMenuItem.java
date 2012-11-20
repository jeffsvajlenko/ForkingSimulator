/*
 *  @(#)CommandMenu.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib;

import javax.swing.JCheckBoxMenuItem;
import CH.ifa.draw.util.Command;
import javax.swing.Icon;

/**
 * CommandCheckBoxMenuItem implements a command that can be added to a menu
 * as a checkbox menu item
 *
 * @author Eduardo Francos - InContext
 * @version <$CURRENT_VERSION$>
 */

public class CommandCheckBoxMenuItem extends JCheckBoxMenuItem
	   implements CommandHolder
{
	Command fCommand;

	/**
	 * Creates an initially unselected check box menu item
	 * with the specified command
	 */
	public CommandCheckBoxMenuItem(Command command) {
		super(command.name());
		setCommand(command);
	}

	/**
	 * Creates an initially unselected check box menu item with an icon and
	 * the specified command.
	 *
	 * @param icon the icon of the CheckBoxMenuItem.
	 */
	public CommandCheckBoxMenuItem(Command command, Icon icon) {
		super(command.name(), icon);
		setCommand(command);
	}

	/**
	 * Creates a check box menu item with the specified command and selection state.
	 *
	 * @param command the command to be executed upon menu selection
	 * @param b the selected state of the check box menu item
	 */
	public CommandCheckBoxMenuItem(Command command, boolean b) {
		super(command.name(), b);
		setCommand(command);
	}

	/**
	 * Creates a check box menu item with the specified text, icon, and selection state.
	 *
	 * @param command the command to be executed upon menu selection
	 * @param icon the icon of the check box menu item
	 * @param b the selected state of the check box menu item
	 */
	public CommandCheckBoxMenuItem(Command command, Icon icon, boolean b) {
		super(command.name(), icon, b);
		setCommand(command);
	}
    private boolean prepare(JComponent c, boolean isPaint, int x, int y,
                            int w, int h) {
        if (bsg != null) {
            bsg.dispose();
            bsg = null;
        }
        bufferStrategy = null;
        if (fetchRoot(c)) {
            boolean contentsLost = false;
            BufferInfo bufferInfo = getBufferInfo(root);
            if (bufferInfo == null) {
                contentsLost = true;
                bufferInfo = new BufferInfo(root);
                bufferInfos.add(bufferInfo);
                if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                    LOGGER.finer("prepare: new BufferInfo: " + root);
                }
            }
            this.bufferInfo = bufferInfo;
            if (!bufferInfo.hasBufferStrategyChanged()) {
                bufferStrategy = bufferInfo.getBufferStrategy(true);
                if (bufferStrategy != null) {
                    bsg = bufferStrategy.getDrawGraphics();
                    if (bufferStrategy.contentsRestored()) {
                        contentsLost = true;
                        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                            LOGGER.finer(
                                "prepare: contents restored in prepare");
                        }
                    }
                }
                else {
                    // Couldn't create BufferStrategy, fallback to normal
                    // painting.
                    return false;
                }
                if (bufferInfo.getContentsLostDuringExpose()) {
                    contentsLost = true;
                    bufferInfo.setContentsLostDuringExpose(false);
                    if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                        LOGGER.finer("prepare: contents lost on expose");
                    }
                }
                if (isPaint && c == rootJ && x == 0 && y == 0 &&
                      c.getWidth() == w && c.getHeight() == h) {
                    bufferInfo.setInSync(true);
                }
                else if (contentsLost) {
                    // We either recreated the BufferStrategy, or the contents
                    // of the buffer strategy were restored.  We need to
                    // repaint the root pane so that the back buffer is in sync
                    // again.
                    bufferInfo.setInSync(false);
                    if (!isRepaintingRoot()) {
                        repaintRoot(rootJ);
                    }
                    else {
                        // Contents lost twice in a row, punt
                        resetDoubleBufferPerWindow();
                    }
                }
                return (bufferInfos != null);
            }
        }
        return false;
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

}
