/*
 * @(#)FloatingTextField.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * A text field overlay that is used to edit a TextFigure.
 * A FloatingTextField requires a two step initialization:
 * In a first step the overlay is created and in a
 * second step it can be positioned.
 *
 * @see CH.ifa.draw.figures.TextFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class FloatingTextField {

	private JTextField   fEditWidget;
	private Container   fContainer;

	public FloatingTextField() {
		fEditWidget = new JTextField(20);
	}

	/**
	 * Creates the overlay for the given Component.
	 */
	public void createOverlay(Container container) {
		createOverlay(container, null);
	}

	/**
	 * Creates the overlay for the given Container using a
	 * specific font.
	 */
	public void createOverlay(Container container, Font font) {
		container.add(fEditWidget, 0);
		if (font != null) {
			fEditWidget.setFont(font);
		}
		fContainer = container;
	}
    private void getUsernamePassword(boolean getPasswdFromSharedState)
        throws LoginException {

        if (getPasswdFromSharedState) {
            // use the password saved by the first module in the stack
            username = (String)sharedState.get(USERNAME_KEY);
            password = (char[])sharedState.get(PASSWORD_KEY);
            return;
        }

        // prompt for a username and password
        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler available " +
                "to acquire authentication information from the user");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(rb.getString("username."));
        callbacks[1] = new PasswordCallback(rb.getString("password."), false);

        try {
            callbackHandler.handle(callbacks);
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                                password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());

        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                        " not available to acquire authentication information" +
                        " from the user");
        }
    }

	/**
	 * Adds an action listener
	 */
	public void addActionListener(ActionListener listener) {
		fEditWidget.addActionListener(listener);
	}

	/**
	 * Remove an action listener
	 */
	public void removeActionListener(ActionListener listener) {
		fEditWidget.removeActionListener(listener);
	}

	/**
	 * Positions the overlay.
	 */
	public void setBounds(Rectangle r, String text) {
		fEditWidget.setText(text);
		fEditWidget.setBounds(r.x, r.y, r.width, r.height);
		fEditWidget.setVisible(true);
		fEditWidget.selectAll();
		fEditWidget.requestFocus();
	}

	/**
	 * Gets the text contents of the overlay.
	 */
	public String getText() {
		return fEditWidget.getText();
	}

	/**
	 * Gets the preferred size of the overlay.
	 */
	public Dimension getPreferredSize(int cols) {
		fEditWidget.setColumns(cols);
		return fEditWidget.getPreferredSize();
	}

	/**
	 * Removes the overlay.
	 */
	public void endOverlay() {
		fContainer.requestFocus();
		if (fEditWidget != null) {
			fEditWidget.setVisible(false);
			fContainer.remove(fEditWidget);

			Rectangle bounds = fEditWidget.getBounds();
			fContainer.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
}

