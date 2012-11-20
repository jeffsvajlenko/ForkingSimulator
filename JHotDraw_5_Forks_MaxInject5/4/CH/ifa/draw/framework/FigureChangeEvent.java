/*
 * @(#)FigureChangeEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * FigureChange event passed to FigureChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeEvent extends EventObject {

	private Rectangle fRectangle;
	private FigureChangeEvent myNestedEvent;

	private static final Rectangle  fgEmptyRectangle = new Rectangle(0, 0, 0, 0);

   /**
	* Constructs an event for the given source Figure. The rectangle is the
	* area to be invalvidated.
	*/
	public FigureChangeEvent(Figure source, Rectangle r) {
		super(source);
		fRectangle = r;
	}

	public FigureChangeEvent(Figure source) {
		super(source);
		fRectangle = fgEmptyRectangle;
	}
    public SnmpEngineImpl(SnmpEngineFactory fact,
                          SnmpLcd lcd,
                          SnmpEngineId engineid) throws UnknownHostException {

        init(lcd, fact);
        initEngineID();
        if(this.engineid == null) {
            if(engineid != null)
                this.engineid = engineid;
            else
                this.engineid = SnmpEngineId.createEngineId();
        }
        lcd.storeEngineId(this.engineid);
        if (SNMP_LOGGER.isLoggable(Level.FINER)) {
            SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(),
                    "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,SnmpEngineId)",
                    "LOCAL ENGINE ID: " + this.engineid);
        }
    }

	public FigureChangeEvent(Figure source, Rectangle r, FigureChangeEvent nestedEvent) {
		this(source, r);
		myNestedEvent = nestedEvent;
	}

	/**
	 *  Gets the changed figure
	 */
	public Figure getFigure() {
		return (Figure)getSource();
	}

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}

	public FigureChangeEvent getNestedEvent() {
		return myNestedEvent;
	}
}
