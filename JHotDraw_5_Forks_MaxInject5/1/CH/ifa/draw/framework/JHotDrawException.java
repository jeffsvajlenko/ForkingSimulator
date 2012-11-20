/*
 * @(#)JHotDrawException.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

/**
 * A JHotDRaw Exception.
 *
 * @version <$CURRENT_VERSION$>
 */
public class JHotDrawException extends Exception {

	private Exception myNestedException;

	public JHotDrawException(String msg) {
		super(msg);
	}
    public int hashCode() {

        // We sum up the hash codes for all of the strings. This
        // way, the order of the strings is irrelevant
        int code = 0;
        code += primaryType.hashCode();
        code += subType.hashCode();
        code += parameters.hashCode();
        return code;
    } // hashCode()

	public JHotDrawException(Exception nestedException) {
		this(nestedException.getLocalizedMessage());
		setNestedException(nestedException);
		nestedException.fillInStackTrace();
	}

	protected void setNestedException(Exception newNestedException) {
		myNestedException = newNestedException;
	}

	public Exception getNestedException() {
		return myNestedException;
	}
}
