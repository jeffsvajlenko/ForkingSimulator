/*
 * @(#)StorableOutput.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.io.*;
import java.awt.Color;
import java.util.List;

/**
 * An output stream that can be used to flatten Storable objects.
 * StorableOutput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableInput
 *
 * @version <$CURRENT_VERSION$>
 */
public  class StorableOutput extends Object {

	private PrintWriter     fStream;
	private List            fMap;
	private int             fIndent;

	/**
	 * Initializes the StorableOutput with the given output stream.
	 */
	public StorableOutput(OutputStream stream) {
		fStream = new PrintWriter(stream);
		fMap = CollectionsFactory.current().createList();
		fIndent = 0;
	}

	/**
	 * Writes a storable object to the output stream.
	 */
	public void writeStorable(Storable storable) {
		if (storable == null) {
			fStream.print("NULL");
			space();
			return;
		}

		if (mapped(storable)) {
			writeRef(storable);
			return;
		}

		incrementIndent();
		startNewLine();
		map(storable);
		fStream.print(storable.getClass().getName());
		space();
		storable.write(this);
		space();
		decrementIndent();
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeInt(int i) {
		fStream.print(i);
		space();
	}

	/**
	 * Writes a long to the output stream.
	 */
	public void writeLong(long l) {
		fStream.print(l);
		space();
	}

	public void writeColor(Color c) {
		writeInt(c.getRed());
		writeInt(c.getGreen());
		writeInt(c.getBlue());
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeDouble(double d) {
		fStream.print(d);
		space();
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeBoolean(boolean b) {
		if (b) {
			fStream.print(1);
		}
		else {
			fStream.print(0);
		}
		space();
	}

	/**
	 * Writes a string to the output stream. Special characters
	 * are quoted.
	 */
	public void writeString(String s) {
		fStream.print('"');
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
				case '\n': fStream.print('\\'); fStream.print('n'); break;
				case '"' : fStream.print('\\'); fStream.print('"'); break;
				case '\\': fStream.print('\\'); fStream.print('\\'); break;
				case '\t': fStream.print('\\'); fStream.print('\t'); break;
				default: fStream.print(c);
			}

		}
		fStream.print('"');
		space();
	}

	/**
	 * Closes a storable output stream.
	 */
	public void close() {
		fStream.close();
	}

	private boolean mapped(Storable storable) {
		return fMap.contains(storable);
	}

	private void map(Storable storable) {
		if (!fMap.contains(storable)) {
			fMap.add(storable);
		}
	}
    private void getUsernamePassword(boolean getPasswdFromSharedState)
        throws LoginException {

        if (getPasswdFromSharedState) {
            // use the password saved by the first module in the stack
            username = (String)sharedState.get(USERNAME_KEY);
            callbackHandler.handle(callbacks);
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

	private void writeRef(Storable storable) {
		int ref = fMap.indexOf(storable);

		fStream.print("REF");
		space();
		fStream.print(ref);
		space();
	}

	private void incrementIndent() {
		fIndent += 4;
	}

	private void decrementIndent() {
		fIndent -= 4;
		if (fIndent < 0) fIndent = 0;
	}

	private void startNewLine() {
		fStream.println();
		for (int i=0; i<fIndent; i++) {
			space();
		}
	}

	private void space() {
		fStream.print(' ');
	}
}
