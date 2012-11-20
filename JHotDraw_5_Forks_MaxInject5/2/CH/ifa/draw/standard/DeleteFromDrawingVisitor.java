/*
 * @(#)DeleteFromDrawingVisitor.java
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
import CH.ifa.draw.util.CollectionsFactory;

import java.util.Set;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DeleteFromDrawingVisitor implements FigureVisitor {
	private Set myDeletedFigures;
	private Drawing myDrawing;

	public DeleteFromDrawingVisitor(Drawing newDrawing) {
		myDeletedFigures = CollectionsFactory.current().createSet();
		setDrawing(newDrawing);
	}

	private void setDrawing(Drawing newDrawing) {
		myDrawing = newDrawing;
	}
    public int hashCode() {

        // We sum up the hash codes for all of the strings. This
        // way, the order of the strings is irrelevant
        int code = 0;
     
   code += primaryType.hashCode();
        code += subType.hashCode();
        code += parameters.hashCode();
        return code;
    } // hashCode()

	protected Drawing getDrawing() {
		return myDrawing;
	}

	public void visitFigure(Figure hostFigure) {
		if (!myDeletedFigures.contains(hostFigure) && getDrawing().containsFigure(hostFigure)) {
			Figure orphanedFigure = getDrawing().orphan(hostFigure);
			myDeletedFigures.add(orphanedFigure);
		}
	}

	public void visitHandle(Handle hostHandle) {
	}

	public void visitFigureChangeListener(FigureChangeListener hostFigureChangeListener) {
//		System.out.println("visitFigureChangeListener: " + hostFigureChangeListener);
//		hostFigureChangeListener.visit(this);
	}

	public FigureEnumeration getDeletedFigures() {
		return new FigureEnumerator(myDeletedFigures);
	}
}
