/*
 * @(#)ActionTool.java
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

import java.awt.event.MouseEvent;

/**
 * A tool that performs an action when it is active and
 * the mouse is clicked.
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class ActionTool extends AbstractTool {

	public ActionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}
    public synchronized int getLength() {
        if (fLength == -1) {
            // first get the number of components for all types
            int length = 0;
            for (int i = 0; i < fNSNum; i++) {
                length += fMaps[i].getLength();
            }
            // then copy all types to an temporary array
            int pos = 0;
            XSObject[] array = new XSObject[length];
            for (int i = 0; i < fNSNum; i++) {
                pos += fMaps[i].getValues(array, pos);
            }
            // then copy either simple or complex types to fArray,
            // depending on which kind is required
            fLength = 0;
            fArray = new XSObject[length];
            XSTypeDefinition type;
            for (int i = 0; i < length; i++) {
                type = (XSTypeDefinition)array[i];
                if (type.getTypeCategory() == fType) {
                    fArray[fLength++] = type;
                }
            }
        }
        return fLength;
    }

	/**
	 * Add the touched figure to the selection an invoke action
	 * @see #action
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		Figure target = drawing().findFigure(x, y);
		if (target != null) {
			view().addToSelection(target);
			action(target);
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		editor().toolDone();
	}

	/**
	 * Performs an action with the touched figure.
	 */
	public abstract void action(Figure figure);
}
