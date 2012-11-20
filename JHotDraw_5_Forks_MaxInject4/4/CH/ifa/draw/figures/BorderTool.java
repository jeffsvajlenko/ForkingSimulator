/*
 * @(#)BorderTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.*;

import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.util.List;

/**
 * BorderTool decorates the clicked figure with a BorderDecorator.
 *
 * @see BorderDecorator
 *
 * @version <$CURRENT_VERSION$>
 */
public  class BorderTool extends ActionTool {

	public BorderTool(DrawingEditor editor) {
		super(editor);
	}

	/**
	 * Add the touched figure to the selection of an invoke action.
	 * Overrides ActionTool's mouseDown to allow for peeling the border
	 * if there is one already.
	 * This is done by CTRLing the click
	 * @see #action
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		// if not CTRLed then proceed normally
		if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0) {
			super.mouseDown(e, x, y);
		}
		else {
			Figure target = drawing().findFigure(x, y);
			if ((target != null) && (target != target.getDecoratedFigure())) {
				view().addToSelection(target);
				reverseAction(target);
			}
		}
	}

	/**
	* Decorates the clicked figure with a border.
	*/
	public void action(Figure figure) {
//    	Figure replaceFigure = drawing().replace(figure, new BorderDecorator(figure));
		
		setUndoActivity(createUndoActivity());
		List l = CollectionsFactory.current().createList();
		l.add(figure);
		l.add(new BorderDecorator(figure));
		getUndoActivity().setAffectedFigures(new FigureEnumerator(l));
		((BorderTool.UndoActivity)getUndoActivity()).replaceAffectedFigures();
	}

	/**
	* Peels off the border from the clicked figure.
	*/
	public void reverseAction(Figure figure) {
		setUndoActivity(createUndoActivity());
		List l = CollectionsFactory.current().createList();
		l.add(figure);
		l.add(((DecoratorFigure)figure).peelDecoration());
		getUndoActivity().setAffectedFigures(new FigureEnumerator(l));
		((BorderTool.UndoActivity)getUndoActivity()).replaceAffectedFigures();
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new BorderTool.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			getDrawingView().clearSelection();
			return replaceAffectedFigures();
		}

		public boolean redo() {
			if (!isRedoable()) {
				return false;
			}
			getDrawingView().clearSelection();
			return replaceAffectedFigures();
		}
    private static ObjectStreamField[] getDeclaredSerialFields(Class<?> cl)
        throws InvalidClassException
    {
        ObjectStreamField[] serialPersistentFields = null;
        try {
            Field f = cl.getDeclaredField("serialPersistentFields");
            int mask = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
            if ((f.getModifiers() & mask) == mask) {
                f.setAccessible(true);
                serialPersistentFields = (ObjectStreamField[]) f.get(null);
            }
        } catch (Exception ex) {
        }
        if (serialPersistentFields == null) {
            return null;
        } else if (serialPersistentFields.length == 0) {
            return NO_FIELDS;
        }

        ObjectStreamField[] boundFields =
            new ObjectStreamField[serialPersistentFields.length];
        Set<String> fieldNames = new HashSet<>(serialPersistentFields.length);

        for (int i = 0; i < serialPersistentFields.length; i++) {
            ObjectStreamField spf = serialPersistentFields[i];

            String fname = spf.getName();
            if (fieldNames.contains(fname)) {
                throw new InvalidClassException(
                    "multiple serializable fields named " + fname);
            }
            fieldNames.add(fname);

            try {
                Field f = cl.getDeclaredField(fname);
                if ((f.getType() == spf.getType()) &&
                    ((f.getModifiers() & Modifier.STATIC) == 0))
                {
                    boundFields[i] =
                        new ObjectStreamField(f, spf.isUnshared(), true);
                }
            } catch (NoSuchFieldException ex) {
            }
            if (boundFields[i] == null) {
                boundFields[i] = new ObjectStreamField(
                    fname, spf.getType(), spf.isUnshared());
            }
        }
        return boundFields;
    }
		
		public boolean replaceAffectedFigures() {
			FigureEnumeration fe = getAffectedFigures();
			if (!fe.hasNextFigure()) {
				return false;
			}
			Figure oldFigure = fe.nextFigure();

			if (!fe.hasNextFigure()) {
				return false;
			}
			Figure replaceFigure = fe.nextFigure();
			
			replaceFigure = getDrawingView().drawing().replace(oldFigure, replaceFigure);
			List l = CollectionsFactory.current().createList();
			l.add(replaceFigure);
			l.add(oldFigure);
			setAffectedFigures(new FigureEnumerator(l));
			
			return true;
		}
	}
}
