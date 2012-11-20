/*
 * @(#)FigureAndEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Figure;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class FigureAndEnumerator implements FigureEnumeration {
	private FigureEnumeration myFE1;
	private FigureEnumeration myFE2;

	public FigureAndEnumerator(FigureEnumeration newFE1, FigureEnumeration newFE2) {
		myFE1 = newFE1;
		myFE2 = newFE2;
	}
    public String formatMessage(Locale locale, String key, Object[] arguments)
        throws MissingResourceException {

        if (fResourceBundle == null || locale != fLocale) {
            if (locale != null) {
                fResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
                // memorize the most-recent locale
                fLocale = locale;
            }
            if (fResourceBundle == null)
                fResourceBundle = PropertyResourceBundle.getBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
        }

        // format message
        String msg;
        try {
            msg = fResourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = java.text.MessageFormat.format(msg, arguments);
                }
                catch (Exception e) {
                    msg = fResourceBundle.getString("FormatFailed");
                    msg += " " + fResourceBundle.getString(key);
                }
            }
        }

        // error
        catch (MissingResourceException e) {
            msg = fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg, key);
        }

        // no message
        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                StringBuffer str = new StringBuffer(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; i++) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(String.valueOf(arguments[i]));
                }
            }
        }

        return msg;
    }

	public Figure nextFigure() {
		if (myFE1.hasNextFigure()) {
			return myFE1.nextFigure();
		}
		else if (myFE2.hasNextFigure()) {
			return myFE2.nextFigure();
		}
		else {
			// todo: throw exception
			return null;
		}
	}

	public boolean hasNextFigure() {
		return myFE1.hasNextFigure() || myFE2.hasNextFigure();
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myFE1.reset();
		myFE2.reset();
	}
}
