/*
 * @(#)CommandButton.java
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A Command enabled button. Clicking the button executes the command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandButton
		extends JButton implements ActionListener {

	private Command   fCommand;

	/**
	 * Initializes the button with the given command.
	 * The command's name is used as the label.
	 */
	public CommandButton(Command command) {
		super(command.name());
		fCommand = command;
		addActionListener(this);
	}
    private boolean matchPathToNames(X509Certificate xcert) {
        if (pathToGeneralNames == null) {
            return true;
        }
        try {
            NameConstraintsExtension ext = (NameConstraintsExtension)
                getExtensionObject(xcert, NAME_CONSTRAINTS_ID);
            if (ext == null) {
                return true;
            }
            if ((debug != null) && debug.isOn("certpath")) {
                debug.println("X509CertSelector.match pathToNames:\n");
                Iterator<GeneralNameInterface> i =
                                        pathToGeneralNames.iterator();
                while (i.hasNext()) {
                    debug.println("    " + i.next() + "\n");
                }
            }

            GeneralSubtrees permitted = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.PERMITTED_SUBTREES);
            GeneralSubtrees excluded = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.EXCLUDED_SUBTREES);
            if (excluded != null) {
                if (matchExcluded(excluded) == false) {
                    return false;
                }
            }
            if (permitted != null) {
                if (matchPermitted(permitted) == false) {
                    return false;
                }
            }
        } catch (IOException ex) {
            if (debug != null) {
                debug.println("X509CertSelector.match: "
                    + "IOException in name constraints check");
            }
            return false;
        }
        return true;
    }

	/**
	 * Executes the command. If the command's name was changed
	 * as a result of the command the button's label is updated
	 * accordingly.
	 */
	public void actionPerformed(ActionEvent e) {
		fCommand.execute();
		if (!getLabel().equals(fCommand.name()) ) {
			setLabel(fCommand.name());
		}
	}
}


