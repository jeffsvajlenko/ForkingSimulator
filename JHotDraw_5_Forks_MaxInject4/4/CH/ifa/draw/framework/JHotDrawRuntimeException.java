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
    private Paint decodeGradient4(Shape s) {
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.038709678f,0.05967742f,0.08064516f,0.23709677f,0.3935484f,0.41612905f,0.43870968f,0.67419356f,0.90967745f,0.91612905f,0.92258066f },
                new Color[] { color11,
                            decodeColor(color11,color12,0.5f),
                            color12,
                            decodeColor(color12,color13,0.5f),
                            color13,
                            decodeColor(color13,color14,0.5f),
                            color14,
                            decodeColor(color14,color15,0.5f),
                            color15,
                            decodeColor(color15,color16,0.5f),
                            color16});
    }

	public Exception getNestedException() {
		return myNestedException;
	}
}
