/*
 * @(#)DrawingChangeEvent.java
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
 * The event passed to DrawingChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DrawingChangeEvent extends EventObject {

	private Rectangle fRectangle;

	/**
	 *  Constructs a drawing change event.
	 */
	public DrawingChangeEvent(Drawing source, Rectangle r) {
		super(source);
		fRectangle = r;
	}

	/**
	 *  Gets the changed drawing
	 */
	public Drawing getDrawing() {
		return (Drawing)getSource();
	}

	/**
	 *  Gets the changed rectangle
	 */
	public Rectangle getInvalidatedRectangle() {
		return fRectangle;
	}
        public void visitOperation(String operationName,
                                   M operation) {
            introspector.checkMethod(operation);
            final String[] sig = introspector.getSignature(operation);
            final MethodAndSig mas = new MethodAndSig();
            mas.method = operation;
            mas.signature = sig;
            List<MethodAndSig> list = ops.get(operationName);
            if (list == null)
                list = Collections.singletonList(mas);
            else {
                if (list.size() == 1)
                    list = newList(list);
                list.add(mas);
            }
            ops.put(operationName, list);
        }
}
