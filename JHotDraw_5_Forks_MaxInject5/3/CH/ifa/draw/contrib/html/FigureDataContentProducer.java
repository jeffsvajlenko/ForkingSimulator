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

import java.io.IOException;

import java.io.Serializable;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * FigureDataContentProducer produces content on behalf of Figures.<br>
 * It supports the basic information common to all figures.
 * It can only be used as a generic producer, getting information from any
 * figure passed to the getContents method.
 *
 * @author    Eduardo Francos - InContext
 * @created   30 avril 2002
 * @version   1.0
 */

public class FigureDataContentProducer extends AbstractContentProducer
		 implements Serializable {

	/**Constructor for the FigureContentProducer object */
	public FigureDataContentProducer() { }
    static MethodHandle makeRotateArguments(MethodType newType, MethodHandle target,
                int firstArg, int argCount, int rotateBy) {
        rotateBy = positiveRotation(argCount, rotateBy);
        if (!canRotateArguments(newType, target.type(), firstArg, argCount, rotateBy))
            return null;
        // Decide whether it should be done as a right or left rotation,
        // on the JVM stack.  Return the number of stack slots to rotate by,
        // positive if right, negative if left.
        int limit = firstArg + argCount;
        int depth0 = newType.parameterSlotDepth(firstArg);
        int depth1 = newType.parameterSlotDepth(limit-rotateBy);
        int depth2 = newType.parameterSlotDepth(limit);
        int chunk1Slots = depth0 - depth1; assert(chunk1Slots > 0);
        int chunk2Slots = depth1 - depth2; assert(chunk2Slots > 0);
        // From here on out, it assumes a single-argument shift.
        assert(MAX_ARG_ROTATION == 1);
        int srcArg, dstArg;
        int dstSlot;
        int moveChunk;
        if (rotateBy == 1) {
            // Rotate right/down N (rotateBy = +N, N small, c2 small):
            // in  arglist: [0: ...keep1 | arg1: c1...  | limit-N: c2 | limit: keep2... ]
            // out arglist: [0: ...keep1 | arg1: c2 | arg1+N: c1...   | limit: keep2... ]
            srcArg = limit-1;
            dstArg = firstArg;
            //dstSlot = depth0 - chunk2Slots;  //chunk2Slots is not relevant
            dstSlot = depth0 + MethodHandleNatives.OP_ROT_ARGS_DOWN_LIMIT_BIAS;
            moveChunk = chunk2Slots;
        } else {
            // Rotate left/up N (rotateBy = -N, N small, c1 small):
            // in  arglist: [0: ...keep1 | arg1: c1 | arg1+N: c2...   | limit: keep2... ]
            // out arglist: [0: ...keep1 | arg1: c2 ... | limit-N: c1 | limit: keep2... ]
            srcArg = firstArg;
            dstArg = limit-1;
            dstSlot = depth2;
            moveChunk = chunk1Slots;
        }
        byte srcType = basicType(newType.parameterType(srcArg));
        byte dstType = basicType(newType.parameterType(dstArg));
        assert(moveChunk == type2size(srcType));
        long conv = makeSwapConv(OP_ROT_ARGS, srcArg, srcType, dstSlot, dstType);
        return new AdapterMethodHandle(target, newType, conv);
    }


	/**
	 * Produces the contents for the figure
	 *
	 * @param context       the calling client context
	 * @param ctxAttrName   the attribute name
	 * @param ctxAttrValue  the figure
	 * @return              The string value for the requested entity name
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		if (ctxAttrName.compareTo(ContentProducer.ENTITY_FIGURE_WIDTH) == 0) {
			return Integer.toString(((FigureContentProducerContext)context).displayBox().width);
		}

		if (ctxAttrName.compareTo(ContentProducer.ENTITY_FIGURE_HEIGHT) == 0) {
			return Integer.toString(((FigureContentProducerContext)context).displayBox().height);
		}

		if (ctxAttrName.compareTo(ContentProducer.ENTITY_FIGURE_POSX) == 0) {
			return Integer.toString(((FigureContentProducerContext)context).displayBox().x);
		}

		if (ctxAttrName.compareTo(ContentProducer.ENTITY_FIGURE_POSY) == 0) {
			return Integer.toString(((FigureContentProducerContext)context).displayBox().y);
		}

		return null;
	}


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
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
