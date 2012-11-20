/*
 * @(#)ChopPolygonConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A ChopPolygonConnector locates a connection point by
 * chopping the connection at the polygon boundary.
 *
 * @author Erich Gamma
 * @version <$CURRENT_VERSION$>
 */
public class ChopPolygonConnector extends ChopBoxConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -156024908227796826L;

	public ChopPolygonConnector() {
	}
    public Parameter[] arguments (){
        checkAccess( MID_ARGUMENTS );

        if( cachedArguments == null ) {
            if( request == null ) {
                throw stdWrapper.piOperationNotSupported1() ;
            }

            // If it is DII request then get the arguments from the DII req
            // and convert that into parameters.
            cachedArguments = nvListToParameterArray( request.arguments() );
        }

        // Good citizen: In the interest of efficiency, we assume
        // interceptors will be "good citizens" in that they will not
        // modify the contents of the Parameter[] array.  We also assume
        // they will not change the values of the containing Anys.

        return cachedArguments;
    }

	public ChopPolygonConnector(Figure owner) {
		super(owner);
	}

	protected Point chop(Figure target, Point from) {
		return ((PolygonFigure)target).chop(from);
	}
}

