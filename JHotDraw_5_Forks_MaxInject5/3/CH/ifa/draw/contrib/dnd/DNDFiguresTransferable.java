/*
 * @(#)DNDFiguresTransferable.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.dnd;

import java.awt.datatransfer.*;
import java.io.*;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DNDFiguresTransferable implements Transferable , Serializable {
	public static DataFlavor DNDFiguresFlavor = new DataFlavor(DNDFigures.class,"DNDFigures");
	private Object o;

	public DNDFiguresTransferable(Object o) {
		//if object is not serializable throw exception
		this.o = o;
	}
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor [] {DNDFiguresFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DNDFiguresFlavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if ( isDataFlavorSupported(flavor) == false) {
			throw new UnsupportedFlavorException( flavor );
		}
		return o;
	}
    private Path2D decodePath38() {
        path.reset();
        path.moveTo(decodeX(0.1826087f), decodeY(2.7217393f));
        path.lineTo(
decodeX(0.2826087f), decodeY(2.8217392f));
        path.lineTo(decodeX(1.0181159f), decodeY(2.095652f));
        path.lineTo(decodeX(0.9130435f), decodeY(1.9891305f));
        path.lineTo(decodeX(0.1826087f), decodeY(2.7217393f));
        path.closePath();
        return path;
    }
}
