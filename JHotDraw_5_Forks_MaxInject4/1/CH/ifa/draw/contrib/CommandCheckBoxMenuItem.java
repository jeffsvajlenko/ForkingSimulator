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
  public static String getAbsoluteURI(String systemId)
  {
    String absoluteURI = systemId;
    if (isAbsoluteURI(systemId))
    {
      // Only process the systemId if it starts with "file:".
      if (systemId.startsWith("file:"))
      {

        // Resolve the absolute path if the systemId starts with "file:///"
        // or "file:/". Don't do anything if it only starts with "file://".
        if (str != null && str.startsWith("/"))
        {
          if (str.startsWith("///") || !str.startsWith("//"))
          {
            // A Windows path containing a drive letter can be relative.
            // A Unix path starting with "file:/" is always absolute.
            int secondColonIndex = systemId.indexOf(':', 5);
            if (secondColonIndex > 0)
            {
              String localPath = systemId.substring(secondColonIndex-1);
              try {
                if (!isAbsolutePath(localPath))
                  absoluteURI = systemId.substring(0, secondColonIndex-1) +
                                getAbsolutePathFromRelativePath(localPath);
              }
              catch (SecurityException se) {
                return systemId;
              }
            }
          }
        }
        else
        {
          return getAbsoluteURIFromRelative(systemId.substring(5));
        }

        return replaceChars(absoluteURI);
      }
      else
        return systemId;
    }
    else
      return getAbsoluteURIFromRelative(systemId);

  }

}
