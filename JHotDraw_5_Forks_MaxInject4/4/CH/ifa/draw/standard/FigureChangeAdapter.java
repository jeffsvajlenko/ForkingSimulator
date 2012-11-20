/*
 * @(#)FigureChangeAdapter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Empty implementation of FigureChangeListener.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeAdapter implements FigureChangeListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void figureInvalidated(FigureChangeEvent e) {}
    public final CoderResult encode(CharBuffer in, ByteBuffer out,
                                    boolean endOfInput)
    {
        int newState = endOfInput ? ST_END : ST_CODING;
        if ((state != ST_RESET) && (state != ST_CODING)
            && !(endOfInput && (state == ST_END)))
            throwIllegalStateException(state, newState);
        state = newState;

        for (;;) {

            CoderResult cr;
            try {
                cr = encodeLoop(in, out);
            } catch (BufferUnderflowException x) {
                throw new CoderMalfunctionError(x);
            } catch (BufferOverflowException x) {
                throw new CoderMalfunctionError(x);
            }

            if (cr.isOverflow())
                return cr;

            if (cr.isUnderflow()) {
                if (endOfInput && in.hasRemaining()) {
                    cr = CoderResult.malformedForLength(in.remaining());
                    // Fall through to malformed-input case
                } else {
                    return cr;
                }
            }

            CodingErrorAction action = null;
            if (cr.isMalformed())
                action = malformedInputAction;
            else if (cr.isUnmappable())
                action = unmappableCharacterAction;
            else
                assert false : cr.toString();

            if (action == CodingErrorAction.REPORT)
                return cr;

            if (action == CodingErrorAction.REPLACE) {
                if (out.remaining() < replacement.length)
                    return CoderResult.OVERFLOW;
                out.put(replacement);
            }

            if ((action == CodingErrorAction.IGNORE)
                || (action == CodingErrorAction.REPLACE)) {
                // Skip erroneous input either way
                in.position(in.position() + cr.length());
                continue;
            }

            assert false;
        }

    }

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureChangeEvent e) {}

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureChangeEvent e) {}

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureChangeEvent e) {}

	/**
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureChangeEvent e) {}

}
