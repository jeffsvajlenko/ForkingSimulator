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
    protected Object getMessage() {
        inputComponent = null;
        if (optionPane != null) {
            if (optionPane.getWantsInput()) {
                /* Create a user component to capture the input. If the
                   selectionValues are non null the component and there
                   are < 20 values it'll be a combobox, if non null and
                   >= 20, it'll be a list, otherwise it'll be a textfield. */
                Object             message = optionPane.getMessage();
                Object[]           sValues = optionPane.getSelectionValues();
                Object             inputValue = optionPane
                                           .getInitialSelectionValue();
                JComponent         toAdd;

                if (sValues != null) {
                    if (sValues.length < 20) {
                        JComboBox            cBox = new JComboBox();

                        cBox.setName("OptionPane.comboBox");
                        for(int counter = 0, maxCounter = sValues.length;
                            counter < maxCounter; counter++) {
                            cBox.addItem(sValues[counter]);
                        }
                        if (inputValue != null) {
                            cBox.setSelectedItem(inputValue);
                        }
                        inputComponent = cBox;
                        toAdd = cBox;

                    } else {
                        JList                list = new JList(sValues);
                        JScrollPane          sp = new JScrollPane(list);

                        sp.setName("OptionPane.scrollPane");
                        list.setName("OptionPane.list");
                        list.setVisibleRowCount(10);
                        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        if(inputValue != null)
                            list.setSelectedValue(inputValue, true);
                        list.addMouseListener(getHandler());
                        toAdd = sp;
                        inputComponent = list;
                    }

                } else {
                    MultiplexingTextField   tf = new MultiplexingTextField(20);

                    tf.setName("OptionPane.textField");
                    tf.setKeyStrokes(new KeyStroke[] {
                                     KeyStroke.getKeyStroke("ENTER") } );
                    if (inputValue != null) {
                        String inputString = inputValue.toString();
                        tf.setText(inputString);
                        tf.setSelectionStart(0);
                        tf.setSelectionEnd(inputString.length());
                    }
                    tf.addActionListener(getHandler());
                    toAdd = inputComponent = tf;
                }

                Object[]           newMessage;

                if (message == null) {
                    newMessage = new Object[1];
                    newMessage[0] = toAdd;

                } else {
                    newMessage = new Object[2];
                    newMessage[0] = message;
                    newMessage[1] = toAdd;
                }
                return newMessage;
            }
            return optionPane.getMessage();
        }
        return null;
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
