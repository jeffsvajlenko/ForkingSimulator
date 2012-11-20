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

import java.io.Serializable;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableOutput;
import CH.ifa.draw.util.StorableInput;
import java.io.IOException;

/**
 * AttributeFigureContentProducer provides content for AttributeFigures.<br>
 * It gives priority to base class supplied values, and if none, then it
 * gets the value from the supplied AttributeContentProducerContext.
 *
 * @author    Eduardo Francos - InContext
 * @created   30 avril 2002
 * @version   1.0
 */

public class AttributeFigureContentProducer extends FigureDataContentProducer
		 implements Serializable {

	/**Constructor for the AttributeFigureContentProducer object */
	public AttributeFigureContentProducer() { }


	/**
	 * Produces the contents for the attribute
	 *
	 * @param context       the calling client context
	 * @param ctxAttrName   the attribute name
	 * @param ctxAttrValue  the attribute value that led to the call to this
	 * @return              The content value
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		// first chance to basic values
		Object attrValue = super.getContent(context, ctxAttrName, ctxAttrValue);
		if (attrValue != null) {
			return attrValue;
		}

		// no, return value from attributes
		return ((AttributeContentProducerContext)context).getAttribute(ctxAttrName);
	}


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
	}
    private static void checkNSSubset(XSWildcardDecl dWildcard, int min1, int max1,
            XSWildcardDecl bWildcard, int min2, int max2)
        throws XMLSchemaException {

        // check Occurrence ranges
        if (!checkOccurrenceRange(min1,max1,min2,max2)) {
            throw new XMLSchemaException("rcase-NSSubset.2", new Object[]{
                    Integer.toString(min1),
                    max1==SchemaSymbols.OCCURRENCE_UNBOUNDED?"unbounded":Integer.toString(max1),
                            Integer.toString(min2),
                            max2==SchemaSymbols.OCCURRENCE_UNBOUNDED?"unbounded":Integer.toString(max2)});
        }

        // check wildcard subset
        if (!dWildcard.isSubsetOf(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.1", null);
        }

        if (dWildcard.weakerProcessContents(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.3",
                    new Object[]{dWildcard.getProcessContentsAsString(),
                    bWildcard.getProcessContentsAsString()});
        }

    }


	/**
	 * Writes the storable
	 *
	 * @param dr               the storable input
	 * @exception IOException  thrown by called methods
	 */
	public void read(StorableInput dr)
		throws IOException {
		super.read(dr);
	}
}
