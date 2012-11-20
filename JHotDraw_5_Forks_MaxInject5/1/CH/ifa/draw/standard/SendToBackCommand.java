/*
 * @(#)SendToBackCommand.java
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
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.util.*;

/**
 * A command to send the selection to the back of the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */ 
public class SendToBackCommand extends AbstractCommand {

	/**
	 * Constructs a send to back command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SendToBackCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selectionZOrdered());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		while (fe.hasNextFigure()) {
			view().drawing().sendToBack(fe.nextFigure());
		}
		view().checkDamage();
	}
    private void initMaps(Class<?> mbeanType,
            MBeanIntrospector<M> introspector) throws Exception {
        final List<Method> methods1 = introspector.getMethods(mbeanType);
        final List<Method> methods = eliminateCovariantMethods(methods1);

        /* Run through the methods to detect inconsistencies and to enable
           us to give getter and setter together to visitAttribute. */
        for (Method m : methods) {
            final String name = m.getName();
            final int nParams = m.getParameterTypes().length;

            final M cm = introspector.mFrom(X1);

            String attrName = "";
            if (name.startsWith("get"))
                attrName = name.substring(3);
            else if (name.startsWith("is")
            && m.getReturnType() == boolean.class)
                attrName = name.substring(2);

            if (attrName.length() != 0 && nParams == 0
                    && m.getReturnType() != void.class) {
                // It's a getter
                // Check we don't have both isX and getX
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else {
                    if (am.getter != null) {
                        final String msg = "Attribute " + attrName +
                                " has more than one getter";
                        throw new NotCompliantMBeanException(msg);
                    }
                }
                am.getter = cm;
                attrMap.put(attrName, am);
            } else if (name.startsWith("set") && name.length() > 3
                    && nParams == 1 &&
                    m.getReturnType() == void.class) {
                // It's a setter
                attrName = name.substring(3);
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else if (am.setter != null) {
                    final String msg = "Attribute " + attrName +
                            " has more than one setter";
                    throw new NotCompliantMBeanException(msg);
                }
                am.setter = cm;
                attrMap.put(attrName, am);
            } else {
                // It's an operation
                List<M> cms = opMap.get(name);
                if (cms == null)
                    cms = newList();
                cms.add(cm);
                opMap.put(name, cms);
            }
        }
        /* Check that getters and setters are consistent. */
        for (Map.Entry<String, AttrMethods<M>> entry : attrMap.entrySet()) {
            AttrMethods<M> am = entry.getValue();
            if (!introspector.consistent(am.getter, am.setter)) {
                final String msg = "Getter and setter for " + entry.getKey() +
                        " have inconsistent types";
                throw new NotCompliantMBeanException(msg);
            }
        }
    }

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new SendToBackCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		private Hashtable myOriginalLayers;
		
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			myOriginalLayers = new Hashtable();
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure currentFigure = fe.nextFigure();
				int currentFigureLayer = getOriginalLayer(currentFigure);
				getDrawingView().drawing().sendToLayer(currentFigure, currentFigureLayer);
			}
			
			return true;
		}
		
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}
			
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				sendToCommand(fe.nextFigure());
			}
			
			return true;			
		}

		protected void sendToCommand(Figure f) {
			getDrawingView().drawing().sendToBack(f);
		}
		
		protected void addOriginalLayer(Figure affectedFigure, int newOriginalLayer) {
			myOriginalLayers.put(affectedFigure, new Integer(newOriginalLayer));
		}
		
		protected int getOriginalLayer(Figure lookupAffectedFigure) {
			return ((Integer)myOriginalLayers.get(lookupAffectedFigure)).intValue();
		}

		public void setAffectedFigures(FigureEnumeration fe) {
			// first make copy of FigureEnumeration in superclass
			super.setAffectedFigures(fe);
			// then get new FigureEnumeration of copy to save attributes
			FigureEnumeration copyFe = getAffectedFigures();
			while (copyFe.hasNextFigure()) {
				Figure f = copyFe.nextFigure();
				int originalLayer = getDrawingView().drawing().getLayer(f);
				addOriginalLayer(f, originalLayer);
			}
		}
	}
}
