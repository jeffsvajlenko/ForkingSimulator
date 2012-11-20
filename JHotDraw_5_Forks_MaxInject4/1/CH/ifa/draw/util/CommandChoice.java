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
    protected void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String name = evt.getPropertyName();
        if ("editorKit".equals(name)) {
            ActionMap map = SwingUtilities.getUIActionMap(getComponent());
            if (map != null) {
                Object oldValue = evt.getOldValue();
                if (oldValue instanceof EditorKit) {
                    Action[] actions = ((EditorKit)oldValue).getActions();
                    if (actions != null) {
                        removeActions(map, actions);
                    }
                }
                Object newValue = evt.getNewValue();
                if (newValue instanceof EditorKit) {
                    Action[] actions = ((EditorKit)newValue).getActions();
                    if (actions != null) {
                        addActions(map, actions);
                    }
                }
            }
            updateFocusTraversalKeys();
        } else if ("editable".equals(name)) {
            updateFocusTraversalKeys();
        } else if ("foreground".equals(name)
                   || "font".equals(name)
                   || "document".equals(name)
                   || JEditorPane.W3C_LENGTH_UNITS.equals(name)
                   || JEditorPane.HONOR_DISPLAY_PROPERTIES.equals(name)
                   ) {
            JComponent c = getComponent();
            updateDisplayProperties(c.getFont(), c.getForeground());
            if ( JEditorPane.W3C_LENGTH_UNITS.equals(name)
                 || JEditorPane.HONOR_DISPLAY_PROPERTIES.equals(name) ) {
                modelChanged();
            }
            if ("foreground".equals(name)) {
                Object honorDisplayPropertiesObject = c.
                    getClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES);
                boolean honorDisplayProperties = false;
                if (honorDisplayPropertiesObject instanceof Boolean) {
                    honorDisplayProperties =
                        ((Boolean)honorDisplayPropertiesObject).booleanValue();
                }
                if (honorDisplayProperties) {
                    modelChanged();
                }
            }


        }
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
