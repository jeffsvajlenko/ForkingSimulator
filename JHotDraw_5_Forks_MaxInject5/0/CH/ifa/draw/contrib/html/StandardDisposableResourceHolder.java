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

/**
 * StandardDisposableResourceHolder is a standard implementation of the
 * DisposableResourceHolder interface
 *
 * @author    Eduardo Francos - InContext
 * @created   2 mai 2002
 * @version   1.0
 */

public class StandardDisposableResourceHolder implements DisposableResourceHolder, Serializable {

	/** The holded resource object */
	protected Object resource = null;

	/** The dispose delay, default to 60 seconds */
	protected long disposeDelay = 60000;

	/**
	 * The last time the resource was accessed as returned by
	 * <code>System.currentTimeMillis()</code>
	 */
	protected long lastTimeAccessed = 0;

	/** True if the resource is locked */
	protected boolean isLocked = false;


	/**Constructor for the StandardDisposableResourceHolder object */
	public StandardDisposableResourceHolder() { }


	/**
	 * Constructor for the StandardDisposableResourceHolder object
	 *
	 * @param resource  Description of the Parameter
	 */
	public StandardDisposableResourceHolder(Object resource) {
		this.resource = resource;
		resetDelay();
	}


	/**
	 * Makes a clone of this
	 *
	 * @return   the clone
	 */
	public Object clone() {
		StandardDisposableResourceHolder clone = new StandardDisposableResourceHolder();
		clone.setDisposableDelay(this.getDisposableDelay());
		return clone;
	}


	/**
	 * Gets the resource attribute of the StandardDisposableResourceHolder object
	 *
	 * @return                          The resource value
	 * @exception NullPointerException  Description of the Exception
	 */
	public Object getResource()
		throws NullPointerException {
		if (resource != null) {
			resetDelay();
			return resource;
		}
		throw new NullPointerException();
	}


	/**
	 * Sets the resource attribute of the StandardDisposableResourceHolder object
	 *
	 * @param resource  The new resource value
	 */
	public void setResource(Object resource) {
		this.resource = resource;
		resetDelay();
	}


	/**
	 * Sets the disposableDelay attribute of the StandardDisposableResourceHolder object
	 *
	 * @param millis  The new disposableDelay value
	 */
	public void setDisposableDelay(long millis) {
		disposeDelay = millis;
	}
    private int calcLineBreak(final int pos, final float maxAdvance) {

        // either of these statements removes the bug:
        //generateComponents(0, fChars.length);
        //generateComponents(pos, fChars.length);

        int startPos = pos;
        float width = maxAdvance;

        int tlcIndex;
        int tlcStart = fComponentStart;

        for (tlcIndex = 0; tlcIndex < fComponents.length; tlcIndex++) {
            int gaLimit = tlcStart + fComponents[tlcIndex].getNumCharacters();
            if (gaLimit > startPos) {
                break;
            }
            else {
                tlcStart = gaLimit;
            }
        }

        // tlcStart is now the start of the tlc at tlcIndex

        for (; tlcIndex < fComponents.length; tlcIndex++) {

            TextLineComponent tlc = fComponents[tlcIndex];
            int numCharsInGa = tlc.getNumCharacters();

   
         int lineBreak = tlc.getLineBreakIndex(startPos - tlcStart, width);
            if (lineBreak == numCharsInGa && tlcIndex < fComponents.length) {
                width -= tlc.getAdvanceBetween(startPos - tlcStart, lineBreak);
                tlcStart += numCharsInGa;
                startPos = tlcStart;
            }
            else {
                return tlcStart + lineBreak;
            }
        }

        if (fComponentLimit < fChars.length) {
            // format more text and try again
            //if (haveLayoutWindow) {
            //    outOfWindow++;
            //}

            generateComponents(pos, fChars.length);
            return calcLineBreak(pos, maxAdvance);
        }

        return fChars.length;
    }


	/**
	 * Gets the disposableDelay attribute of the StandardDisposableResourceHolder object
	 *
	 * @return   The disposableDelay value
	 */
	public long getDisposableDelay() {
		return disposeDelay;
	}


	/** Disposes of the resource */
	public void dispose() {
		resource = null;
	}


	/**
	 * Gets the available attribute of the StandardDisposableResourceHolder object
	 *
	 * @return   The available value
	 */
	public boolean isAvailable() {
		return (resource != null);
	}


	/**
	 * Locks the resource so it cannot be automatically disposed of until unlock
	 * is called.<br>
	 * Explicit disposing by calling dispose() is still possible though.
	 */
	public void lock() {
		isLocked = true;
	}


	/**
	 * Unlocks the resource so it can be automatically disposed of again.<br>
	 * Explicit disposing by calling dispose() is still possible though.
	 */
	public void unlock() {
		resetDelay();
		isLocked = false;
	}


	/**
	 * True if the resource is locked
	 *
	 * @return   The locked status
	 */
	public boolean isLocked() {
		return isLocked;
	}


	/**
	 * Gets the lastTimeAccessed attribute of the DisposableResourceHolder object
	 *
	 * @return   The lastTimeAccessed value
	 */
	public long getLastTimeAccessed() {
		return lastTimeAccessed;
	}


	/** Resets the disposing delay so as to restart the time counter */
	public void resetDelay() {
		lastTimeAccessed = System.currentTimeMillis();
	}
}
