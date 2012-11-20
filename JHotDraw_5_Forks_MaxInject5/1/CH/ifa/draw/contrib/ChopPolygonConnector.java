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

	public ChopPolygonConnector(Figure owner) {
		super(owner);
	}
    public boolean skipChar(int c) throws IOException {
        if (DEBUG_BUFFER) {
            System.out.print("(skipChar, '"+(char)c+"': ");
            print();
            System.out.println();
        }

        // load more characters, if needed
        if (fCurrentEntity.position == fCurrentEntity.count) {
            invokeListeners(0);
            load(0, true);
        }

        // skip character
        int cc = fCurrentEntity.ch[fCurrentEntity.position];
        if (cc == c) {
            fCurrentEntity.position++;
            if (c == '\n') {
                fCurrentEntity.lineNumber++;
                fCurrentEntity.columnNumber = 1;
            } else {
                fCurrentEntity.columnNumber++;
            }
            if (DEBUG_BUFFER) {
                System.out.print(")skipChar, '"+(char)c+"': ");
                print();
                System.out.println(" -> true");
            }
            return true;
        } else if (c == '\n' && cc == '\r' && isExternal) {
            // handle newlines
            if (fCurrentEntity.position == fCurrentEntity.count) {
                invokeListeners(1);
                fCurrentEntity.ch[0] = (char)cc;
                load(1, false);
            }
            fCurrentEntity.position ++;
            if (fCurrentEntity.ch[fCurrentEntity.position] == '\n') {
                fCurrentEntity.position++;
            }
            fCurrentEntity.lineNumber++;
            fCurrentEntity.columnNumber = 1;
            if (DEBUG_BUFFER) {
                System.out.print(")skipChar, '"+(char)c+"': ");
                print();
                System.out.println(" -> true");
            }
            return true;
        }

        // character was not skipped
        if (DEBUG_BUFFER) {
            System.out.print(")skipChar, '"+(char)c+"': ");
            print();
            System.out.println(" -> false");
        }
        return false;

    } // skipChar(int):boolean

	protected Point chop(Figure target, Point from) {
		return ((PolygonFigure)target).chop(from);
	}
}

