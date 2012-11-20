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
        public static DatatypeFactory newInstance()
                throws DatatypeConfigurationException {

                try {
                        return (DatatypeFactory) FactoryFinder.find(
                                /* The default property name according to the JAXP spec */
                                 DATATYPEFACTORY_PROPERTY,
                                /* The fallback implementation class name */
                                DATATYPEFACTORY_IMPLEMENTATION_CLASS);
                } catch (FactoryFinder.ConfigurationError e) {
                        throw new DatatypeConfigurationException(e.getMessage(), e.getException());
                }
        }

	protected void setNestedException(Exception newNestedException) {
		myNestedException = newNestedException;
	}

	public Exception getNestedException() {
		return myNestedException;
	}
}
