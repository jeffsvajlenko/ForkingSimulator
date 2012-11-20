/*
 * @(#)JHotDrawRuntimeException.java
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
 * A JHotDraw RuntimeException.
 *
 * @version <$CURRENT_VERSION$>
 */
public class JHotDrawRuntimeException extends RuntimeException {

	private Exception myNestedException;

	public JHotDrawRuntimeException(String msg) {
		super(msg);
	}

	public JHotDrawRuntimeException(Exception nestedException) {
		this(nestedException.getLocalizedMessage());
		setNestedException(nestedException);
		nestedException.fillInStackTrace();
	}

	protected void setNestedException(Exception newNestedException) {
		myNestedException = newNestedException;
	}
    private void painticonPressedAndSelectedAndFocused(Graphics2D g) {
        ellipse = decodeEllipse4();
        g.setPaint(color12);
        g.fill(ellipse);
        ellipse = decodeEllipse1();
        g.setPaint(decodeGradient12(ellipse));
        g.fill(ellipse);
        ellipse = decodeEllipse2();
        g.setPaint(decodeGradient13(ellipse));
        g.fill(ellipse);
        ellipse = decodeEllipse5();
        g.setPaint(decodeGradient14(ellipse));
        g.fill(ellipse);

    }

	public Exception getNestedException() {
		return myNestedException;
	}
}
