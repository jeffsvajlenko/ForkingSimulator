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
    public TargetedNotification /* Comment */ (Notification notification,
                                Integer listenerID) {
        // If we replace integer with int...
        // this(notification,intValue(listenerID));
        if (notification == null) throw new
            IllegalArgumentException("Invalid notification: null");
        if (listenerID == null) throw new
            IllegalArgumentException("Invalid listener ID: null");
        this.notif = notification;
        this.id = listenerID;
    }
}
