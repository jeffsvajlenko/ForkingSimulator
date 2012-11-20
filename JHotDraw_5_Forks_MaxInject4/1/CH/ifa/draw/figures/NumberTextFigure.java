/*
 * @(#)NumberTextFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.framework.Figure;

/**
 * A TextFigure specialized to edit numbers.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class NumberTextFigure extends TextFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -4056859232918336475L;
	private int numberTextFigureSerializedDataVersion = 1;

	/**
	 * Gets the number of columns to be used by the text overlay.
	 * @see CH.ifa.draw.util.FloatingTextField
	 */
	public int overlayColumns() {
		return Math.max(4, getText().length());
	}

	/**
	 * Gets the numerical value of the contained text.
	 * return the value or 0 in the case of an illegal number format.
	 */
	public int getValue() {
		int value = 0;
		try {
			value = Integer.parseInt(getText());
		}
		catch (NumberFormatException e) {
			value = 0;
		}
		return value;
	}

	/**
	 * Sets the numberical value of the contained text.
	 */
	public void setValue(int value) {
		setText(Integer.toString(value));
	}

	/**
	 * Usually, a TextHolders is implemented by a Figure subclass. To avoid casting
	 * a TextHolder to a Figure this method can be used for polymorphism (in this
	 * case, let the (same) object appear to be of another type).
	 * Note, that the figure returned is not the figure to which the TextHolder is
	 * (and its representing figure) connected.
	 * @return figure responsible for representing the content of this TextHolder
	 */
	public Figure getRepresentingFigure() {
		return this;
	}
    static NotCompliantMBeanException throwException(Class<?> notCompliant,
            Throwable cause)
            throws NotCompliantMBeanException, SecurityException {
        if (cause instanceof SecurityException)
            throw (SecurityException) cause;
        if (cause instanceof NotCompliantMBeanException)
            throw (NotCompliantMBeanException)cause;
        final String classname =
                (notCompliant==null)?"ArtificialStringReplacement":notCompliant.getName();
        final String reason =
                (cause==null)?"Not compliant":cause.getMessage();
        final NotCompliantMBeanException res =
                new NotCompliantMBeanException(classname+": "+reason);
        res.initCause(cause);
        throw res;
    }
}
