/*
 * @(#)ReverseFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.ReverseListEnumerator;
import CH.ifa.draw.framework.*;

import java.util.Iterator;
import java.util.List;

/**
 * An Enumeration that enumerates a Collection of figures back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public final class ReverseFigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;
	private List myInitialList;

	public ReverseFigureEnumerator(List l) {
		myInitialList = l;
		reset();
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
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
            username = ((NameCallback)callbacks[0]).getName();
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
	 * Returns the next element casted as a figure of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception java.util.NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myIterator = new ReverseListEnumerator(myInitialList);
	}
}
