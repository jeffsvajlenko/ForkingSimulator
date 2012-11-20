/*
 * @(#)InsertIntoDrawingVisitor.java
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
public class InsertIntoDrawingVisitor implements FigureVisitor {
	private Set myInsertedFigures;
	private Drawing myDrawing;

	public InsertIntoDrawingVisitor(Drawing newDrawing) {
		myInsertedFigures = CollectionsFactory.current().createSet();
		setDrawing(newDrawing);
	}

	private void setDrawing(Drawing newDrawing) {
		myDrawing = newDrawing;
	}

	protected Drawing getDrawing() {
		return myDrawing;
	}

	public void visitFigure(Figure hostFigure) {
		if (!myInsertedFigures.contains(hostFigure) && !getDrawing().includes(hostFigure)) {
			Figure addedFigure = getDrawing().add(hostFigure);
			myInsertedFigures.add(addedFigure);
		}
	}

	public void visitHandle(Handle hostHandle) {
	}

	public void visitFigureChangeListener(FigureChangeListener hostFigureChangeListener) {
//		hostFigureChangeListener.visit(this);
	}
    public Rectangle getNormalBounds() {

      /* we used to test (!isMaximum) here, but since this
         method is used by the property listener for the
         IS_MAXIMUM_PROPERTY, it ended up getting the wrong
         answer... Since normalBounds get set to null when the
         frame is restored, this should work better */

      if (normalBounds != null) {
        return normalBounds;
      } else {
        return getBounds();
      }
    }

	public FigureEnumeration getInsertedFigures() {
		return new FigureEnumerator(myInsertedFigures);
	}
}
