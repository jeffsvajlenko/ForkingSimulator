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
        static void parseShorthandBackground(CSS css, String value,
                                             MutableAttributeSet attr) {
            int count = strings.length;
            int index = 0;
            // bitmask: 0 for image, 1 repeat, 2 attachment, 3 position,
            //          4 color
            short found = 0;

            while (index < count) {
                String string = strings[index++];
                if ((found & 1) == 0 && isImage(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_IMAGE, string);
                    found |= 1;
                }
                else if ((found & 2) == 0 && isRepeat(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_REPEAT, string);
                    found |= 2;
                }
                else if ((found & 4) == 0 && isAttachment(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_ATTACHMENT, string);
                    found |= 4;
                }
                else if ((found & 8) == 0 && isPosition(string)) {
                    if (index < count && isPosition(strings[index])) {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION,
                                                string + " " +
                                                strings[index++]);
                    }
                    else {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION, string);
                    }
                    found |= 8;
                }
                else if ((found & 16) == 0 && isColor(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_COLOR, string);
                    found |= 16;
                }
            }
            if ((found & 1) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_IMAGE,
                                        null);
            }
            if ((found & 2) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_REPEAT,
                                        "repeat");
            }
            if ((found & 4) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_ATTACHMENT, "scroll");
            }
            if ((found & 8) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_POSITION, null);
            }
            // Currently, there is no good way to express this.
            /*
            if ((found & 16) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_COLOR,
                                        null);
            }
            */
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
}
