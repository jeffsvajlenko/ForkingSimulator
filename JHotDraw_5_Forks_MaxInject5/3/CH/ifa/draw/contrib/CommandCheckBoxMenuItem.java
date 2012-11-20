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
    private static Object findJarServiceProvider(String factoryId)
        throws ConfigurationError
    {
        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = ss.getContextClassLoader();
        if (cl != null) {
            is = ss.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = FactoryFinder.class.getClassLoader();
                is = ss.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader, try the current ClassLoader
            cl = FactoryFinder.class.getClassLoader();
            is = ss.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        if (debug) {    // Extra check to avoid computing cl strings
            dPrint("found jar resource=" + serviceId + " using ClassLoader: " + cl);
        }

        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }

        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
            rd.close();
        } catch (IOException x) {
            // No provider found
            return null;
        }

        if (factoryClassName != null && !"".equals(factoryClassName)) {
            dPrint("found in resource, value=" + factoryClassName);

            // Note: here we do not want to fall back to the current
            // ClassLoader because we want to avoid the case where the
            // resource file was found using one ClassLoader and the
            // provider class was instantiated using a different one.
            return newInstance(factoryClassName, cl, false);
        }

        // No provider found
        return null;
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

}
