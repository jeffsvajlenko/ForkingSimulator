/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.awt.Color;
import java.io.Serializable;
import CH.ifa.draw.util.Storable;

/**
 * HTMLColorContentProducer produces RGB color HTML encoded strings.<br>
 * Ex:
 * <code>Color.blue ==> #0000FF</code><br>
 * <code>Color.red  ==> #FF00FF</code><br>
 * It can either be specific if set for a specific color, or generic, encoding
 * any color passed to the getContents method.
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class HTMLColorContentProducer extends ColorContentProducer
		 implements Serializable {

	/**Constructor for the HTMLColorContentProducer object */
	public HTMLColorContentProducer() { }


	/**
	 * Produces the contents for the color
	 *
	 * @param context       the calling client context
	 * @param ctxAttrName   the color attribute name (FrameColor, TextColor, etc)
	 * @param ctxAttrValue  the color
	 * @return              The HTML encoded RBG value for the color
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		// if we have our own color then use it
		// otherwise use the one supplied
		Color color = (getColor() != null) ? getColor() : (Color)ctxAttrValue;
		return getHTMLColorCode(color);
	}

	public static String getHTMLColorCode(Color color) {
		String colorCode = Integer.toHexString(color.getRGB());
		return "#" + colorCode.substring(colorCode.length() - 6);
	}
  public int getLength()
  {
    // Tell if this is being called from within a predicate.
        boolean isPredicateTest = (this == m_execContext.getSubContextList());

    // And get how many total predicates are part of this step.
        int predCount = getPredicateCount();

    // If we have already calculated the length, and the current predicate
    // is the first predicate, then return the length.  We don't cache
    // the anything but the length of the list to the first predicate.
    if (-1 != m_length && isPredicateTest && m_predicateIndex < 1)
                return m_length;

    // I'm a bit worried about this one, since it doesn't have the
    // checks found above.  I suspect it's fine.  -sb
    if (m_foundLast)
                return m_pos;

    // Create a clone, and count from the current position to the end
    // of the list, not taking into account the current predicate and
    // predicates after the current one.
    int pos = (m_predicateIndex >= 0) ? getProximityPosition() : m_pos;

    LocPathIterator clone;

    try
    {
      clone = (LocPathIterator) clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      return -1;
    }

    // We want to clip off the last predicate, but only if we are a sub
    // context node list, NOT if we are a context list.  See pos68 test,
    // also test against bug4638.
    if (predCount > 0 && isPredicateTest)
    {
      // Don't call setPredicateCount, because it clones and is slower.
      clone.m_predCount = m_predicateIndex;
      // The line above used to be:
      // clone.m_predCount = predCount - 1;
      // ...which looks like a dumb bug to me. -sb
    }

    int next;

    while (DTM.NULL != (next = clone.nextNode()))
    {
      pos++;
    }

    if (isPredicateTest && m_predicateIndex < 1)
      m_length = pos;

    return pos;
  }
}
